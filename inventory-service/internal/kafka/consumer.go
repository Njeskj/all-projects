// Package kafka wires the order.placed consumer that decrements stock when an order is created.
package kafka

import (
	"context"
	"encoding/json"
	"log"

	"inventory-service/internal/store"

	"github.com/segmentio/kafka-go"
)

type OrderPlacedEvent struct {
	OrderID  string `json:"order_id"`
	Sku      string `json:"sku"`
	Quantity int32  `json:"quantity"`
}

// RunConsumer blocks, consuming order.placed events and decrementing stock via st.
// ponytail: at-least-once, no dedup/outbox — add idempotency key check if double-decrement matters.
func RunConsumer(ctx context.Context, brokers []string, st *store.PgStore, cache *store.Cache) {
	r := kafka.NewReader(kafka.ReaderConfig{
		Brokers: brokers,
		Topic:   "order.placed",
		GroupID: "inventory-service",
	})
	defer r.Close()

	log.Printf("kafka consumer started for topic order.placed, brokers=%v", brokers)
	for {
		m, err := r.ReadMessage(ctx)
		if err != nil {
			if ctx.Err() != nil {
				return
			}
			log.Printf("kafka read error: %v", err)
			continue
		}
		var evt OrderPlacedEvent
		if err := json.Unmarshal(m.Value, &evt); err != nil {
			log.Printf("bad order.placed payload: %v", err)
			continue
		}
		reserved, remaining, err := st.Reserve(ctx, evt.Sku, evt.Quantity)
		if err != nil {
			log.Printf("failed to apply order.placed for order %s: %v", evt.OrderID, err)
			continue
		}
		if cache != nil {
			cache.Set(ctx, evt.Sku, remaining)
		}
		log.Printf("consumed order.placed order=%s sku=%s qty=%d reserved=%v remaining=%d",
			evt.OrderID, evt.Sku, evt.Quantity, reserved, remaining)
	}
}
