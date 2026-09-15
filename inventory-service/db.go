package main

import (
	"database/sql"
	"fmt"
	"log"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/postgres"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "github.com/lib/pq"
)

func connectDB(dsn string) *sql.DB {
	db, err := sql.Open("postgres", dsn)
	if err != nil {
		log.Fatalf("open db: %v", err)
	}
	if err := db.Ping(); err != nil {
		log.Fatalf("ping db: %v", err)
	}
	return db
}

func runMigrations(db *sql.DB, migrationsPath string) {
	driver, err := postgres.WithInstance(db, &postgres.Config{})
	if err != nil {
		log.Fatalf("migrate driver: %v", err)
	}
	m, err := migrate.NewWithDatabaseInstance(fmt.Sprintf("file://%s", migrationsPath), "postgres", driver)
	if err != nil {
		log.Fatalf("migrate init: %v", err)
	}
	if err := m.Up(); err != nil && err != migrate.ErrNoChange {
		log.Fatalf("migrate up: %v", err)
	}
	log.Println("migrations applied (or already up to date)")
}

func insertSlot(db *sql.DB, id string, capacity int) error {
	_, err := db.Exec(`INSERT INTO slots (id, total_capacity) VALUES ($1, $2)
		ON CONFLICT (id) DO NOTHING`, id, capacity)
	return err
}

func getSlot(db *sql.DB, id string) (capacity int, reserved int, err error) {
	err = db.QueryRow(`SELECT total_capacity, reserved FROM slots WHERE id = $1`, id).Scan(&capacity, &reserved)
	return
}

func incrementReserved(db *sql.DB, id string, qty int) error {
	_, err := db.Exec(`UPDATE slots SET reserved = reserved + $1 WHERE id = $2`, qty, id)
	return err
}
