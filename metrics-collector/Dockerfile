FROM golang:1.27-alpine AS build
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -o /out/metrics-collector .

FROM alpine:3.20
RUN apk add --no-cache ca-certificates
WORKDIR /app
COPY --from=build /out/metrics-collector /app/metrics-collector
EXPOSE 28081 28090
ENTRYPOINT ["/app/metrics-collector"]
