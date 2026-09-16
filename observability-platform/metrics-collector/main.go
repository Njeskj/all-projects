package main

import (
	"context"
	"log"
	"net"
	"net/http"
	"os"
	"strings"

	pb "metrics-collector/proto"

	"go.opentelemetry.io/otel/trace"
	"google.golang.org/grpc"
)

type server struct {
	pb.UnimplementedMetricsCollectorServiceServer
	kafkaBrokers []string
}

func (s *server) IngestMetric(ctx context.Context, req *pb.IngestMetricRequest) (*pb.IngestMetricResponse, error) {
	if tracer != nil {
		var span trace.Span
		ctx, span = tracer.Start(ctx, "IngestMetric")
		defer span.End()
	}
	log.Printf("ingested metric name=%s value=%f source=%s", req.MetricName, req.Value, req.Source)
	if len(s.kafkaBrokers) > 0 {
		publishMetricIngested(ctx, s.kafkaBrokers, req.MetricName, req.Value)
	}
	return &pb.IngestMetricResponse{Accepted: true, Id: req.MetricName + "-1"}, nil
}

func main() {
	shutdown := initTracing()
	defer shutdown(context.Background())

	var brokers []string
	if b := os.Getenv("KAFKA_BROKERS"); b != "" {
		brokers = strings.Split(b, ",")
	}

	go func() {
		lis, err := net.Listen("tcp", ":28090")
		if err != nil {
			log.Fatalf("grpc listen failed: %v", err)
		}
		s := grpc.NewServer()
		pb.RegisterMetricsCollectorServiceServer(s, &server{kafkaBrokers: brokers})
		log.Printf("metrics-collector grpc listening on :28090")
		if err := s.Serve(lis); err != nil {
			log.Fatalf("grpc serve failed: %v", err)
		}
	}()

	http.HandleFunc("/healthz", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("ok"))
	})

	addr := ":28081"
	log.Printf("metrics-collector http listening on %s", addr)
	if err := http.ListenAndServe(addr, nil); err != nil {
		log.Fatal(err)
	}
}
