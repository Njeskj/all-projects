// Package store also provides a Postgres-backed implementation used as the source of truth.
package store

import (
	"context"

	"github.com/jackc/pgx/v5/pgxpool"
)

// PgStore is the Postgres-backed stock repository (inventory.stock table).
type PgStore struct {
	pool *pgxpool.Pool
}

func NewPgStore(pool *pgxpool.Pool) *PgStore {
	return &PgStore{pool: pool}
}

func (s *PgStore) Get(ctx context.Context, sku string) (int32, error) {
	var available int32
	err := s.pool.QueryRow(ctx, `SELECT available FROM inventory.stock WHERE sku = $1`, sku).Scan(&available)
	return available, err
}

// CanReserve is a read-only check used by the synchronous gRPC path: does the SKU have
// enough stock right now? The actual decrement happens asynchronously when inventory-service
// consumes the order.placed Kafka event, so a gRPC check and a Kafka-driven decrement never
// double-count the same reservation.
func (s *PgStore) CanReserve(ctx context.Context, sku string, qty int32) (ok bool, available int32, err error) {
	available, err = s.Get(ctx, sku)
	if err != nil {
		return false, 0, err
	}
	return available >= qty, available, nil
}

// Reserve atomically decrements stock if enough is available, returning the resulting remaining stock.
func (s *PgStore) Reserve(ctx context.Context, sku string, qty int32) (reserved bool, remaining int32, err error) {
	err = s.pool.QueryRow(ctx, `
		UPDATE inventory.stock
		SET available = available - $2
		WHERE sku = $1 AND available >= $2
		RETURNING available
	`, sku, qty).Scan(&remaining)
	if err != nil {
		// no rows updated -> insufficient stock; fetch current value for the response.
		cur, gerr := s.Get(ctx, sku)
		if gerr != nil {
			return false, 0, gerr
		}
		return false, cur, nil
	}
	return true, remaining, nil
}
