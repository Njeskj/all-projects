// Package store holds the in-process stock table shared by the gRPC and HTTP layers.
package store

import "sync"

// Store is a simple thread-safe stock table: sku -> available quantity.
// ponytail: in-memory + mutex, swap for Postgres-backed repo in the postgres phase.
type Store struct {
	mu    sync.Mutex
	stock map[string]int32
}

func New() *Store {
	return &Store{stock: map[string]int32{
		"SKU-1": 100,
		"SKU-2": 50,
	}}
}

// Reserve decrements stock by qty if enough is available. Returns the remaining stock.
func (s *Store) Reserve(sku string, qty int32) (reserved bool, remaining int32) {
	s.mu.Lock()
	defer s.mu.Unlock()
	cur := s.stock[sku]
	if cur < qty {
		return false, cur
	}
	s.stock[sku] = cur - qty
	return true, s.stock[sku]
}

func (s *Store) Get(sku string) int32 {
	s.mu.Lock()
	defer s.mu.Unlock()
	return s.stock[sku]
}

func (s *Store) Set(sku string, qty int32) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.stock[sku] = qty
}
