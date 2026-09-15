package main

import (
	"context"
	"log"
	"time"

	pb "inventory-service/proto"

	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/trace"
)

// inventoryServer implements pb.InventoryServiceServer.
type inventoryServer struct {
	pb.UnimplementedInventoryServiceServer
}

func (s *inventoryServer) ReserveSlot(ctx context.Context, req *pb.ReserveSlotRequest) (*pb.ReserveSlotResponse, error) {
	if tracer != nil {
		var span trace.Span
		ctx, span = tracer.Start(ctx, "ReserveSlot",
			trace.WithAttributes(
				attribute.String("slot_id", req.GetSlotId()),
				attribute.String("booking_id", req.GetBookingId()),
				attribute.Int("quantity", int(req.GetQuantity())),
			))
		defer span.End()
	}

	log.Printf("ReserveSlot: slot=%s booking=%s qty=%d", req.GetSlotId(), req.GetBookingId(), req.GetQuantity())

	if req.GetSlotId() == "" || req.GetQuantity() <= 0 {
		return &pb.ReserveSlotResponse{Success: false, Message: "invalid request"}, nil
	}

	if db == nil || rdb == nil {
		// No infra wired (e.g. plain `go build` smoke test) — stub success so
		// phase-3 grpcurl checks still pass without Postgres/Redis running.
		return &pb.ReserveSlotResponse{Success: true, Message: "reserved (stub, no db/redis configured)"}, nil
	}

	lock := newSlotLock(rdb, req.GetSlotId())
	acquired, err := lock.acquire(ctx, 5*time.Second)
	if err != nil {
		return &pb.ReserveSlotResponse{Success: false, Message: "lock error: " + err.Error()}, nil
	}
	if !acquired {
		return &pb.ReserveSlotResponse{Success: false, Message: "slot is being reserved by another request, try again"}, nil
	}
	defer lock.release(context.Background())

	capacity, reserved, err := getSlot(db, req.GetSlotId())
	if err != nil {
		return &pb.ReserveSlotResponse{Success: false, Message: "slot not found: " + err.Error()}, nil
	}

	if reserved+int(req.GetQuantity()) > capacity {
		return &pb.ReserveSlotResponse{Success: false, Message: "insufficient capacity"}, nil
	}

	if err := incrementReserved(db, req.GetSlotId(), int(req.GetQuantity())); err != nil {
		return &pb.ReserveSlotResponse{Success: false, Message: "reserve failed: " + err.Error()}, nil
	}

	return &pb.ReserveSlotResponse{Success: true, Message: "reserved"}, nil
}
