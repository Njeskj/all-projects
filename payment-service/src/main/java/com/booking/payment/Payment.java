package com.booking.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @Column(name = "slot_id", nullable = false)
    private String slotId;

    @Column(name = "amount_cents", nullable = false)
    private long amountCents;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    protected Payment() {
    }

    public Payment(String bookingId, String slotId, long amountCents) {
        this.bookingId = bookingId;
        this.slotId = slotId;
        this.amountCents = amountCents;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getSlotId() {
        return slotId;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
