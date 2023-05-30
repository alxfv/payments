package com.example.riskengineservice;

import org.springframework.data.repository.CrudRepository;

interface PaymentRepository extends CrudRepository<Payment, String> {

}
