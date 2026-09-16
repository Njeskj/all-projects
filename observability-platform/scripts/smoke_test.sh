#!/bin/bash
# End-to-end smoke test: brings up docker-compose stack (if not already up),
# exercises gRPC ingest -> Kafka -> Postgres -> Redis, exits 0 on success.
set -e
cd "$(dirname "$0")/.."

BASE=http://localhost:28080
MC=http://localhost:28081

echo "== waiting for services =="
for i in $(seq 1 30); do
  curl -sf "$MC/healthz" >/dev/null 2>&1 && curl -sf "$BASE/actuator/health" 2>/dev/null | grep -q UP && break
  sleep 2
done
curl -sf "$MC/healthz" >/dev/null || { echo "metrics-collector not healthy"; exit 1; }
curl -sf "$BASE/actuator/health" | grep -q UP || { echo "config-api not healthy"; exit 1; }
echo "OK: both services healthy"

echo "== gRPC ingest via config-api =="
RESP=$(curl -sf -X POST "$BASE/metrics/ingest?name=smoke_metric&value=1.23&source=smoke")
echo "$RESP" | grep -q '"accepted":true' || { echo "FAIL: ingest response=$RESP"; exit 1; }
echo "OK: $RESP"

echo "== postgres: create + read rule =="
CREATED=$(curl -sf -X POST "$BASE/rules?name=smoke-rule&metricName=smoke_metric&threshold=99")
echo "$CREATED" | grep -q "smoke-rule" || { echo "FAIL: create rule response=$CREATED"; exit 1; }
LISTED=$(curl -sf "$BASE/rules")
echo "$LISTED" | grep -q "smoke-rule" || { echo "FAIL: list rules missing smoke-rule: $LISTED"; exit 1; }
echo "OK: rule persisted and read back"

echo "== redis: cache populated by /rules =="
KEYS=$(docker compose exec -T redis redis-cli KEYS '*')
echo "$KEYS" | grep -q "rules" || { echo "FAIL: redis cache key missing"; exit 1; }
echo "OK: redis cache key present"

echo "== kafka: metric.ingested consumable =="
MSG=$(docker compose exec -T kafka //opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic metric.ingested --from-beginning --max-messages 1 --timeout-ms 8000 2>/dev/null | tail -1)
echo "$MSG" | grep -q "metricName" || { echo "FAIL: no kafka message consumed"; exit 1; }
echo "OK: kafka message consumed: $MSG"

echo "== ALL SMOKE CHECKS PASSED =="
exit 0
