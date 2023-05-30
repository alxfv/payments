import random
import time
import uuid

import pytest
from aiohttp import ClientSession
import json
import pytest
import logging

import requests

from confluent_kafka import Consumer, KafkaException

PAYMENTS_TOPIC = 'new_payments'

logger = logging.getLogger(__name__)


def test_payment_methods():
    response = requests.get("http://localhost:8080/payment-methods")

    assert response.status_code == 200


def test_payment_creation_failure():
    # Create payment
    payment = {
        "userId": "e8af92bd-1910-421e-8de0-cb3dcf9bf44d",
        "payeeId": "4c3e304e-ce79-4f53-bb26-4e198e6c780a",
        "paymentMethod": "CREDIT_CARD",
        "idempotencyKey": "fef4c7e2-584f-4bc4-8673-192810940edc",
    }
    response = requests.post('http://localhost:8080/payments', json=payment)

    # expect 400, we didn't specify amount
    assert response.status_code == 400


def test_integration_payment_creation_success():
    # Create payment
    key = str(uuid.uuid4())

    payment = {
        "userId": "e8af92bd-1910-421e-8de0-cb3dcf9bf44d",
        "amount": random.randint(10, 100),
        "currency": "USD",
        "payeeId": "4c3e304e-ce79-4f53-bb26-4e198e6c780a",
        "paymentMethod": "CREDIT_CARD",
        "idempotencyKey": key,
    }
    response = requests.post('http://localhost:8080/payments', json=payment)
    assert response.status_code == 200, response.json()

    logger.info("created payment: %s", payment)


def test_integration_payment_creation_with_retries():
    idempotency_key = str(uuid.uuid4())

    payment = {
        "userId": "e8af92bd-1910-421e-8de0-cb3dcf9bf44d",
        "amount": random.randint(10, 100),
        "currency": "USD",
        "payeeId": "4c3e304e-ce79-4f53-bb26-4e198e6c780a",
        "paymentMethod": "CREDIT_CARD",
        "idempotencyKey": idempotency_key,
    }
    response = requests.post('http://localhost:8080/payments', json=payment)
    assert response.status_code == 200, response.json()

    logger.info("created payment: %s", payment)

    # simulate retry
    response = requests.post('http://localhost:8080/payments', json=payment)
    assert response.status_code == 200, response.json()

    payments = []

    for attempt in range(10):
        response = requests.get('http://localhost:8081/payments')
        all_payments = response.json()

        payments = [payment for payment in all_payments if payment["idempotencyKey"] == idempotency_key]
        if payments:
            break

        time.sleep(1)

    assert len(payments) == 1, f"number of payments with idempotencyKey={idempotency_key}: {len(payments)}"


@pytest.mark.skip("flaky")
def test_integration_payment_creation_with_kafka():
    KAFKA_CONF = {'bootstrap.servers': 'localhost:9092',
                  'group.id': 'test_group',
                  'auto.offset.reset': 'earliest'}

    # Create payment
    key = str(uuid.uuid4())
    payment = {
        "userId": "e8af92bd-1910-421e-8de0-cb3dcf9bf44d",
        "amount": random.randint(10, 100),
        "payeeId": "4c3e304e-ce79-4f53-bb26-4e198e6c780a",
        "paymentMethod": "8e28af1b-a3a0-43a9-96cc-57d66dd68294",
        "idempotencyKey": key,
    }
    response = requests.post('http://localhost:8080/payments', json=payment)
    assert response.status_code == 200, response.json()

    logger.info("created payment: %s", payment)

    # Consume from Kafka
    c = Consumer(KAFKA_CONF)
    c.subscribe([PAYMENTS_TOPIC])
    while True:
        msg = c.poll(60.0)
        if msg is None:
            raise Exception('No message received from Kafka')
        elif not msg.error():
            kafka_payment = json.loads(msg.value())
            logger.info("received payment: %s", kafka_payment)

            if "idempotencyKey" not in kafka_payment:
                continue

            if not isinstance(kafka_payment, dict):
                continue

            if kafka_payment["idempotencyKey"] == key:
                logger.info("assertion!")
                assert kafka_payment == payment
                break
        elif msg.error().code() != KafkaException._PARTITION_EOF:
            print(msg.error())
            assert False
