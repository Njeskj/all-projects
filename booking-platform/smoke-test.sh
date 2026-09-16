#!/usr/bin/env bash
# End-to-end smoke test against the docker-compose stack:
# create reservation (gRPC via payment-service) -> pay -> confirm (Kafka) ->
# inventory-service consumes booking.confirmed.
#
# Usage: ./smoke-test.sh   (run after `docker compose up -d` and services are healthy)
# Exits 0 on success, nonzero on failure.
set -uo pipefail

PAYMENT_URL="http://localhost:18081"
INVENTORY_URL="http://localhost:18080"
SLOT_ID="SMOKE-SLOT-$$"
BOOKING_ID="SMOKE-BOOKING-$$"

fail() {
  echo "SMOKE TEST FAILED: $1" >&2
  exit 1
}

echo "== 1. healthchecks =="
[ "$(curl -s -o /dev/null -w '%{http_code}' "$INVENTORY_URL/healthz")" = "200" ] || fail "inventory-service /healthz not 200"
[ "$(curl -s -o /dev/null -w '%{http_code}' "$PAYMENT_URL/actuator/health")" = "200" ] || fail "payment-service /actuator/health not 200"
echo "OK"

echo "== 2. create slot with capacity 1 =="
code=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$INVENTORY_URL/slots?id=${SLOT_ID}&capacity=1")
[ "$code" = "201" ] || fail "slot creation returned $code"
echo "OK"

echo "== 3. create reservation (payment-service -> gRPC -> inventory-service) =="
reserve_resp=$(curl -s -X POST "$PAYMENT_URL/reservations?slotId=${SLOT_ID}&bookingId=${BOOKING_ID}&quantity=1")
echo "$reserve_resp" | grep -q "^OK:" || fail "reservation failed: $reserve_resp"
echo "OK: $reserve_resp"

echo "== 4. create payment record =="
payment_resp=$(curl -s -X POST "$PAYMENT_URL/payments?bookingId=${BOOKING_ID}&slotId=${SLOT_ID}&amountCents=1000")
payment_id=$(echo "$payment_resp" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
[ -n "$payment_id" ] || fail "no payment id in response: $payment_resp"
echo "OK: payment_id=$payment_id"

echo "== 5. confirm payment (publishes booking.confirmed to Kafka) =="
confirm_resp=$(curl -s -X POST "$PAYMENT_URL/payments/${payment_id}/confirm")
echo "$confirm_resp" | grep -q '"status":"PAID"' || fail "confirm did not return PAID: $confirm_resp"
echo "OK: $confirm_resp"

echo "== 6. verify inventory-service consumed booking.confirmed for this booking =="
found=0
for i in $(seq 1 10); do
  if docker compose logs inventory-service 2>/dev/null | grep -q "booking.confirmed received: key=${BOOKING_ID}"; then
    found=1
    break
  fi
  sleep 1
done
[ "$found" = "1" ] || fail "inventory-service never logged consuming booking.confirmed for ${BOOKING_ID}"
echo "OK"

echo
echo "SMOKE TEST PASSED"
exit 0
