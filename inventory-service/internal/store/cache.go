package store

import (
	"context"
	"strconv"
	"time"

	"github.com/redis/go-redis/v9"
)

// Cache is a thin read-through/write-through Redis cache for stock levels.
// Keys: "stock:<sku>" -> available quantity (string int32), TTL 30s.
type Cache struct {
	rdb *redis.Client
}

func NewCache(addr string) *Cache {
	return &Cache{rdb: redis.NewClient(&redis.Options{Addr: addr})}
}

func (c *Cache) Ping(ctx context.Context) error {
	return c.rdb.Ping(ctx).Err()
}

func (c *Cache) Get(ctx context.Context, sku string) (int32, bool) {
	val, err := c.rdb.Get(ctx, "stock:"+sku).Result()
	if err != nil {
		return 0, false
	}
	n, err := strconv.Atoi(val)
	if err != nil {
		return 0, false
	}
	return int32(n), true
}

func (c *Cache) Set(ctx context.Context, sku string, qty int32) {
	c.rdb.Set(ctx, "stock:"+sku, qty, 30*time.Second)
}
