package grpc

import (
	"context"
	"fmt"

	pb "inventory-service/internal/grpc/inventorypb"
	"inventory-service/internal/store"
)

// Server implements the InventoryService gRPC API backed by a Postgres-backed store.
type Server struct {
	pb.UnimplementedInventoryServiceServer
	Store *store.PgStore
	Cache *store.Cache // optional Redis cache, nil-safe
}

func NewServer(s *store.PgStore, cache *store.Cache) *Server {
	return &Server{Store: s, Cache: cache}
}

// ReserveStock is a synchronous availability check (does NOT decrement). The real decrement
// happens when inventory-service later consumes the order.placed Kafka event for this order,
// keeping the sync gRPC path and the async Kafka path from double-decrementing the same order.
func (s *Server) ReserveStock(ctx context.Context, req *pb.ReserveStockRequest) (*pb.ReserveStockResponse, error) {
	ok, available, err := s.Store.CanReserve(ctx, req.Sku, req.Quantity)
	if err != nil {
		return nil, err
	}
	msg := "reserved"
	if !ok {
		msg = fmt.Sprintf("insufficient stock for %s: have %d, want %d", req.Sku, available, req.Quantity)
	}
	return &pb.ReserveStockResponse{
		Reserved:       ok,
		RemainingStock: available,
		Message:        msg,
	}, nil
}

func (s *Server) GetStock(ctx context.Context, req *pb.GetStockRequest) (*pb.GetStockResponse, error) {
	if s.Cache != nil {
		if cached, ok := s.Cache.Get(ctx, req.Sku); ok {
			return &pb.GetStockResponse{Sku: req.Sku, Available: cached}, nil
		}
	}
	available, err := s.Store.Get(ctx, req.Sku)
	if err != nil {
		return nil, err
	}
	if s.Cache != nil {
		s.Cache.Set(ctx, req.Sku, available)
	}
	return &pb.GetStockResponse{
		Sku:       req.Sku,
		Available: available,
	}, nil
}
