FROM golang:1.25-alpine AS build
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -o /out/inventory-service .

FROM alpine:3.20
RUN apk add --no-cache ca-certificates
WORKDIR /app
COPY --from=build /out/inventory-service /app/inventory-service
COPY --from=build /src/migrations /app/migrations
EXPOSE 8080 9090
ENTRYPOINT ["/app/inventory-service"]
