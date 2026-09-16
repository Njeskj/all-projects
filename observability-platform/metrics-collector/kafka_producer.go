package main

import (
	"context"
	"fmt"
	"log"

	kafka "github.com/segmentio/kafka-go"
)

// publishMetricIngested publishes to metric.ingested after a successful
// IngestMetric gRPC call, proving the Kafka wire end-to-end for phase 6.
func publishMetricIngested(ctx context.Context, brokers []string, name string, value float64) {
	w := &kafka.Writer{
		Addr:     kafka.TCP(brokers...),
		Topic:    "metric.ingested",
		Balancer: &kafka.LeastBytes{},
	}
	defer w.Close()

	payload := fmt.Sprintf(`{"metricName":"%s","value":%f}`, name, value)
	if err := w.WriteMessages(ctx, kafka.Message{Key: []byte(name), Value: []byte(payload)}); err != nil {
		log.Printf("kafka publish failed: %v", err)
	}
}
