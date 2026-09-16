package main

import (
	"context"
	"log"

	kafka "github.com/segmentio/kafka-go"
)

// startBookingConfirmedConsumer consumes booking.confirmed and finalizes the
// reservation (in a fuller system this would mark reserved seats as final vs.
// provisional; here it just logs, proving the wire end-to-end for phase 6).
func startBookingConfirmedConsumer(ctx context.Context, brokers []string) {
	r := kafka.NewReader(kafka.ReaderConfig{
		Brokers: brokers,
		Topic:   "booking.confirmed",
		GroupID: "inventory-service",
	})
	defer r.Close()

	log.Println("kafka consumer listening on booking.confirmed")
	for {
		msg, err := r.ReadMessage(ctx)
		if err != nil {
			log.Printf("kafka consumer stopped: %v", err)
			return
		}
		log.Printf("booking.confirmed received: key=%s value=%s", string(msg.Key), string(msg.Value))
	}
}
