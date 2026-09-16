package kafka

import (
	"context"
	"encoding/json"

	kafkago "github.com/segmentio/kafka-go"
)

const MatchExecutedTopic = "match.executed"

// MatchExecutedEvent is published whenever the engine matches (accepts) an offer.
type MatchExecutedEvent struct {
	OfferID string  `json:"offer_id"`
	Price   float64 `json:"price"`
}

type Publisher struct {
	writer *kafkago.Writer
}

func NewPublisher(brokerAddr string) *Publisher {
	return &Publisher{
		writer: &kafkago.Writer{
			Addr:     kafkago.TCP(brokerAddr),
			Topic:    MatchExecutedTopic,
			Balancer: &kafkago.LeastBytes{},
		},
	}
}

func (p *Publisher) PublishMatchExecuted(ctx context.Context, e MatchExecutedEvent) error {
	body, err := json.Marshal(e)
	if err != nil {
		return err
	}
	return p.writer.WriteMessages(ctx, kafkago.Message{
		Key:   []byte(e.OfferID),
		Value: body,
	})
}
