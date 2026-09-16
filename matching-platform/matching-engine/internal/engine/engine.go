package engine

import (
	"context"
	"sync"

	"github.com/redis/go-redis/v9"
)

const pendingOffersKey = "matching:pending_offers"

// Offer is a pending offer/request in the matching book.
type Offer struct {
	ID    string
	Price float64
}

// Engine is the in-memory matching engine (hot path), backed by a Redis
// sorted set (offer id -> price) so the pending book is inspectable/shared.
// ponytail: single mutex over a map, fine at this scale; shard by symbol if throughput matters.
type Engine struct {
	mu     sync.Mutex
	offers map[string]Offer
	rdb    *redis.Client
}

func NewEngine(rdb *redis.Client) *Engine {
	return &Engine{offers: make(map[string]Offer), rdb: rdb}
}

func (e *Engine) SubmitOffer(o Offer) {
	e.mu.Lock()
	e.offers[o.ID] = o
	e.mu.Unlock()

	if e.rdb != nil {
		e.rdb.ZAdd(context.Background(), pendingOffersKey, redis.Z{
			Score:  o.Price,
			Member: o.ID,
		})
	}
}

func (e *Engine) Count() int {
	e.mu.Lock()
	defer e.mu.Unlock()
	return len(e.offers)
}

// SubmitOfferAccepted stores the offer and reports the new pending count.
func (e *Engine) SubmitOfferAccepted(id string, price float64) int {
	e.SubmitOffer(Offer{ID: id, Price: price})
	return e.Count()
}
