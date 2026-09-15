module inventory-service

go 1.25.11

require (
	github.com/golang-migrate/migrate/v4 v4.20.1
	github.com/google/uuid v1.6.0
	github.com/lib/pq v1.12.3
	github.com/redis/go-redis/v9 v9.22.0
	github.com/segmentio/kafka-go v0.4.51
	go.opentelemetry.io/otel v1.46.0
	go.opentelemetry.io/otel/exporters/stdout/stdouttrace v1.46.0
	go.opentelemetry.io/otel/sdk v1.46.0
	go.opentelemetry.io/otel/trace v1.46.0
	google.golang.org/grpc v1.83.2
	google.golang.org/protobuf v1.36.12
)

require (
	github.com/cespare/xxhash/v2 v2.3.0 // indirect
	github.com/go-logr/logr v1.4.4 // indirect
	github.com/go-logr/stdr v1.2.2 // indirect
	github.com/klauspost/compress v1.16.7 // indirect
	github.com/pierrec/lz4/v4 v4.1.16 // indirect
	go.opentelemetry.io/auto/sdk v1.2.1 // indirect
	go.opentelemetry.io/otel/metric v1.46.0 // indirect
	go.uber.org/atomic v1.11.0 // indirect
	golang.org/x/net v0.58.0 // indirect
	golang.org/x/sys v0.47.0 // indirect
	golang.org/x/text v0.41.0 // indirect
	google.golang.org/genproto/googleapis/rpc v0.0.0-20260630182238-925bb5da69e7 // indirect
)
