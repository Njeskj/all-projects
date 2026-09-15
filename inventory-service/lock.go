package main

import (
	"context"
	"fmt"
	"time"

	"github.com/google/uuid"
	"github.com/redis/go-redis/v9"
)

// slotLock is a Redis-based distributed lock (SET NX PX) scoped to one slot ID.
// ponytail: single-instance Redis, no Redlock quorum; fine for one-node dev/kind,
// upgrade to Redlock across replicas if Redis is ever clustered for HA.
type slotLock struct {
	rdb   *redis.Client
	key   string
	token string
}

func newSlotLock(rdb *redis.Client, slotID string) *slotLock {
	return &slotLock{
		rdb:   rdb,
		key:   fmt.Sprintf("lock:slot:%s", slotID),
		token: uuid.NewString(),
	}
}

// acquire tries to take the lock for ttl. Returns false if another holder has it.
func (l *slotLock) acquire(ctx context.Context, ttl time.Duration) (bool, error) {
	ok, err := l.rdb.SetNX(ctx, l.key, l.token, ttl).Result()
	if err != nil {
		return false, err
	}
	return ok, nil
}

// release only deletes the key if it still holds our token (avoids releasing
// a lock some other holder acquired after our TTL expired).
var releaseScript = redis.NewScript(`
if redis.call("GET", KEYS[1]) == ARGV[1] then
	return redis.call("DEL", KEYS[1])
else
	return 0
end
`)

func (l *slotLock) release(ctx context.Context) error {
	return releaseScript.Run(ctx, l.rdb, []string{l.key}, l.token).Err()
}
