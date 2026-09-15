package main

import (
	"context"
	"database/sql"
	"log"
	"net"
	"net/http"
	"os"
	"strings"

	ikafka "inventory-service/internal/kafka"

	igrpc "inventory-service/internal/grpc"
	pb "inventory-service/internal/grpc/inventorypb"
	iotel "inventory-service/internal/otel"
	"inventory-service/internal/store"

	"github.com/jackc/pgx/v5/pgxpool"
	_ "github.com/jackc/pgx/v5/stdlib"
	"github.com/pressly/goose/v3"
	"go.opentelemetry.io/contrib/instrumentation/google.golang.org/grpc/otelgrpc"
	"google.golang.org/grpc"
	"google.golang.org/grpc/reflection"
)

func getenv(key, def string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return def
}

func main() {
	ctx := context.Background()

	otelShutdown, err := iotel.Init(ctx)
	if err != nil {
		log.Fatalf("failed to init otel: %v", err)
	}
	defer otelShutdown(ctx)

	dbURL := getenv("DATABASE_URL", "postgres://ecommerce:ecommerce@localhost:54320/ecommerce?sslmode=disable")
	redisAddr := getenv("REDIS_ADDR", "localhost:63790")
	kafkaBrokers := strings.Split(getenv("KAFKA_BROKERS", "localhost:9092"), ",")

	// Run migrations via goose (stdlib *sql.DB).
	sqlDB, err := sql.Open("pgx", dbURL)
	if err != nil {
		log.Fatalf("failed to open db for migrations: %v", err)
	}
	goose.SetBaseFS(nil)
	if err := goose.SetDialect("postgres"); err != nil {
		log.Fatalf("goose dialect: %v", err)
	}
	if err := goose.Up(sqlDB, "migrations"); err != nil {
		log.Fatalf("migrations failed: %v", err)
	}
	sqlDB.Close()

	pool, err := pgxpool.New(ctx, dbURL)
	if err != nil {
		log.Fatalf("failed to connect to postgres: %v", err)
	}
	defer pool.Close()

	pgStore := store.NewPgStore(pool)

	cache := store.NewCache(redisAddr)
	if err := cache.Ping(ctx); err != nil {
		log.Printf("warning: redis not reachable at %s: %v (continuing without cache)", redisAddr, err)
		cache = nil
	}

	// Kafka consumer for order.placed (best-effort background loop).
	go ikafka.RunConsumer(ctx, kafkaBrokers, pgStore, cache)

	// gRPC server
	lis, err := net.Listen("tcp", ":9090")
	if err != nil {
		log.Fatalf("failed to listen on :9090: %v", err)
	}
	grpcSrv := grpc.NewServer(grpc.StatsHandler(otelgrpc.NewServerHandler()))
	pb.RegisterInventoryServiceServer(grpcSrv, igrpc.NewServer(pgStore, cache))
	reflection.Register(grpcSrv) // enables grpcurl without a local .proto copy
	go func() {
		log.Printf("inventory-service gRPC listening on :9090")
		if err := grpcSrv.Serve(lis); err != nil {
			log.Fatal(err)
		}
	}()

	// HTTP server (healthz)
	mux := http.NewServeMux()
	mux.HandleFunc("/healthz", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("ok"))
	})

	addr := ":8081"
	log.Printf("inventory-service HTTP listening on %s", addr)
	if err := http.ListenAndServe(addr, mux); err != nil {
		log.Fatal(err)
	}
}
