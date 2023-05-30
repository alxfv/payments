#!/bin/bash

(cd payments-service && ./gradlew -stop)
(cd risk-engine-service  && ./gradlew -stop)

(cd payments-service && ./gradlew bootRun &)
(cd risk-engine-service && ./gradlew bootRun &)

docker-compose up -d