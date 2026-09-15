package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"log"
	"net"
	"net/http"
	"os"
	"strconv"

	pb "inventory-service/proto"

	"github.com/redis/go-redis/v9"
	"google.golang.org/grpc"
)

var db *sql.DB
var rdb *redis.Client

func healthzHandler(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusOK)
	w.Write([]byte("ok"))
}

func slotsHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case http.MethodPost:
		id := r.URL.Query().Get("id")
		capacity, _ := strconv.Atoi(r.URL.Query().Get("capacity"))
		if id == "" || capacity <= 0 {
			http.Error(w, "id and capacity required", http.StatusBadRequest)
			return
		}
		if err := insertSlot(db, id, capacity); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusCreated)
	case http.MethodGet:
		id := r.URL.Query().Get("id")
		capacity, reserved, err := getSlot(db, id)
		if err != nil {
			http.Error(w, err.Error(), http.StatusNotFound)
			return
		}
		json.NewEncoder(w).Encode(map[string]any{
			"id": id, "total_capacity": capacity, "reserved": reserved,
		})
	default:
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
	}
}

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	grpcPort := os.Getenv("GRPC_PORT")
	if grpcPort == "" {
		grpcPort = "9090"
	}

	shutdownTracing := initTracing()
	defer shutdownTracing(context.Background())

	dsn := os.Getenv("DATABASE_URL")
	if dsn != "" {
		db = connectDB(dsn)
		runMigrations(db, "migrations")
	} else {
		log.Println("DATABASE_URL not set, skipping DB connect (healthz/grpc still available)")
	}

	if redisAddr := os.Getenv("REDIS_ADDR"); redisAddr != "" {
		rdb = redis.NewClient(&redis.Options{Addr: redisAddr})
		if err := rdb.Ping(context.Background()).Err(); err != nil {
			log.Fatalf("redis ping: %v", err)
		}
		log.Println("connected to redis at", redisAddr)
	} else {
		log.Println("REDIS_ADDR not set, skipping redis connect")
	}

	if brokers := os.Getenv("KAFKA_BROKERS"); brokers != "" {
		go startBookingConfirmedConsumer(context.Background(), []string{brokers})
	} else {
		log.Println("KAFKA_BROKERS not set, skipping kafka consumer")
	}

	go startGRPCServer(grpcPort)

	mux := http.NewServeMux()
	mux.HandleFunc("/healthz", healthzHandler)
	mux.HandleFunc("/slots", slotsHandler)

	log.Printf("inventory-service HTTP listening on :%s", port)
	if err := http.ListenAndServe(":"+port, mux); err != nil {
		log.Fatal(err)
	}
}

func startGRPCServer(port string) {
	lis, err := net.Listen("tcp", ":"+port)
	if err != nil {
		log.Fatalf("failed to listen on grpc port %s: %v", port, err)
	}

	s := grpc.NewServer()
	pb.RegisterInventoryServiceServer(s, &inventoryServer{})

	log.Printf("inventory-service gRPC listening on :%s", port)
	if err := s.Serve(lis); err != nil {
		log.Fatalf("grpc serve error: %v", err)
	}
}
