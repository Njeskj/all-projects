FROM golang:1.27-alpine AS build
WORKDIR /src
COPY go.mod go.sum* ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -o /out/matching-engine ./cmd/server

FROM alpine:3.20
COPY --from=build /out/matching-engine /matching-engine
EXPOSE 8091 9095
ENTRYPOINT ["/matching-engine"]
