#!/usr/bin/env bash
# Smoke test: submit offer -> match executed -> history recorded, exits 0 on success.
set -euo pipefail

OFFER_ID="smoke-$(date +%s)"
PRICE="12.34"

echo "waiting for user-service health..."
for i in $(seq 1 60); do
  if curl -sf http://localhost:8092/actuator/health > /dev/null 2>&1; then
    echo "user-service up after ${i}s"
    break
  fi
  sleep 1
  if [ "$i" = "60" ]; then echo "user-service never became healthy"; exit 1; fi
done

echo "submitting offer $OFFER_ID..."
resp=$(curl -sf -X POST "http://localhost:8092/offers?offerId=${OFFER_ID}&price=${PRICE}")
echo "submit response: $resp"
echo "$resp" | grep -q '"accepted":true' || { echo "offer was not accepted"; exit 1; }

echo "waiting for history to record the match (kafka consume + db insert)..."
for i in $(seq 1 30); do
  hist=$(curl -sf http://localhost:8092/history)
  if echo "$hist" | grep -q "\"${OFFER_ID}\""; then
    echo "history contains offer: $hist"
    echo "SMOKE TEST PASSED"
    exit 0
  fi
  sleep 1
done

echo "SMOKE TEST FAILED: offer never appeared in history"
exit 1
