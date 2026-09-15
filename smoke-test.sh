#!/usr/bin/env bash
# Smoke test: create order -> reserve stock (gRPC check) -> confirm (kafka decrement).
# Exits 0 on success, non-zero otherwise. Run after `docker compose up -d`.
set -euo pipefail

fail() { echo "SMOKE TEST FAILED: $1" >&2; exit 1; }

echo "== healthz checks =="
[ "$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8081/healthz)" = "200" ] || fail "inventory-service /healthz not 200"
[ "$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/actuator/health)" = "200" ] || fail "order-service /actuator/health not 200"
echo "OK: both services healthy"

echo "== baseline stock =="
BEFORE=$(docker exec ecommerce-platform-postgres-1 psql -U ecommerce -d ecommerce -tAc "SELECT available FROM inventory.stock WHERE sku='SKU-1'")
echo "SKU-1 available before: $BEFORE"

echo "== create order (reserve check via gRPC) =="
RESP=$(curl -s -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"sku":"SKU-1","quantity":3}')
echo "response: $RESP"
echo "$RESP" | grep -q '"reserved":true' || fail "order was not reserved"

echo "== wait for async kafka decrement =="
sleep 3
AFTER=$(docker exec ecommerce-platform-postgres-1 psql -U ecommerce -d ecommerce -tAc "SELECT available FROM inventory.stock WHERE sku='SKU-1'")
echo "SKU-1 available after: $AFTER"

EXPECTED=$((BEFORE - 3))
[ "$AFTER" -eq "$EXPECTED" ] || fail "stock not decremented as expected (before=$BEFORE after=$AFTER expected=$EXPECTED)"

echo "SMOKE TEST PASSED: order placed, gRPC reservation check OK, Kafka order.placed consumed, stock decremented $BEFORE -> $AFTER"
exit 0
