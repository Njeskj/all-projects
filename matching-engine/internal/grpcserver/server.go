package grpcserver

import (
	"context"
	"log"

	"matching-engine/internal/engine"
	"matching-engine/internal/kafka"
	pb "matching-engine/proto"
)

// Server implements the MatchingEngine gRPC service.
type Server struct {
	pb.UnimplementedMatchingEngineServer
	Engine    *engine.Engine
	Publisher *kafka.Publisher
}

func (s *Server) SubmitOffer(ctx context.Context, req *pb.SubmitOfferRequest) (*pb.SubmitOfferResponse, error) {
	count := s.Engine.SubmitOfferAccepted(req.GetOfferId(), req.GetPrice())

	// ponytail: every submitted offer is treated as an immediate match for this skeleton;
	// a real matching algorithm (order book crossing) is out of scope for phase 6.
	if s.Publisher != nil {
		if err := s.Publisher.PublishMatchExecuted(ctx, kafka.MatchExecutedEvent{
			OfferID: req.GetOfferId(),
			Price:   req.GetPrice(),
		}); err != nil {
			log.Printf("failed to publish match.executed: %v", err)
		}
	}

	return &pb.SubmitOfferResponse{Accepted: true, PendingCount: int32(count)}, nil
}
