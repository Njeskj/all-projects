package main

import (
	"context"
	"log"
	"net"
	"net/http"
	"os"

	"matching-engine/internal/engine"
	"matching-engine/internal/grpcserver"
	"matching-engine/internal/kafka"
	matchotel "matching-engine/internal/otel"
	pb "matching-engine/proto"

	"github.com/redis/go-redis/v9"
	"go.opentelemetry.io/contrib/instrumentation/google.golang.org/grpc/otelgrpc"
	"google.golang.org/grpc"
	"google.golang.org/grpc/reflection"
)

func main() {
	shutdown, err := matchotel.Init(context.Background())
	if err != nil {
		log.Printf("otel init failed (continuing without tracing): %v", err)
	} else {
		defer shutdown(context.Background())
	}

	port := os.Getenv("HTTP_PORT")
	if port == "" {
		port = "8091"
	}
	grpcPort := os.Getenv("GRPC_PORT")
	if grpcPort == "" {
		grpcPort = "9095"
	}
	redisAddr := os.Getenv("REDIS_ADDR")
	if redisAddr == "" {
		redisAddr = "localhost:54321"
	}

	kafkaAddr := os.Getenv("KAFKA_ADDR")
	if kafkaAddr == "" {
		kafkaAddr = "localhost:54322"
	}

	rdb := redis.NewClient(&redis.Options{Addr: redisAddr})
	eng := engine.NewEngine(rdb)
	publisher := kafka.NewPublisher(kafkaAddr)

	// gRPC server
	lis, err := net.Listen("tcp", ":"+grpcPort)
	if err != nil {
		log.Fatal(err)
	}
	grpcSrv := grpc.NewServer(grpc.StatsHandler(otelgrpc.NewServerHandler()))
	pb.RegisterMatchingEngineServer(grpcSrv, &grpcserver.Server{Engine: eng, Publisher: publisher})
	reflection.Register(grpcSrv)
	go func() {
		log.Printf("matching-engine gRPC listening on :%s", grpcPort)
		if err := grpcSrv.Serve(lis); err != nil {
			log.Fatal(err)
		}
	}()

	mux := http.NewServeMux()
	mux.HandleFunc("/healthz", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("ok"))
	})

	log.Printf("matching-engine HTTP listening on :%s", port)
	if err := http.ListenAndServe(":"+port, mux); err != nil {
		log.Fatal(err)
	}
}
