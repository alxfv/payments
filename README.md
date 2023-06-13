# Quick start

./run.sh

# Improvements


- Observability: logs and metrics, traces, alerts. 
- How to handle potentially massive numbers of concurrent connections? Using reactive to build non-blocking, asynchronous, low-latency, high-throughput services
- Retry queue: when we think that we can just retry (e. g. downstream is dead), so just try to process the message again.
- The Dead Letter Queue (DLQ): message is completely broken, no reason to process it. Instead of passively dumping the message, move it to a Dead Letter Queue. DLQ can be durable (retation is higher that for general queues), so we will not loose any messages.
- Store idempotency key in header
- Load testing 
- Chaos-testing: simulate network issues, randomly kill pods, etc
- Add meta information in events
- Kafka registry for schema events
