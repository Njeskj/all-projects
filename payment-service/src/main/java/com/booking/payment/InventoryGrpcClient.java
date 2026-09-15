package com.booking.payment;

import com.booking.inventory.grpc.InventoryServiceGrpc;
import com.booking.inventory.grpc.ReserveSlotRequest;
import com.booking.inventory.grpc.ReserveSlotResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InventoryGrpcClient {

    private final ManagedChannel channel;
    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;
    private final Tracer tracer;

    public InventoryGrpcClient(
            @Value("${inventory.grpc.host}") String host,
            @Value("${inventory.grpc.port}") int port,
            Tracer tracer) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = InventoryServiceGrpc.newBlockingStub(channel);
        this.tracer = tracer;
    }

    public ReserveSlotResponse reserveSlot(String slotId, String bookingId, int quantity) {
        Span span = tracer.spanBuilder("InventoryGrpcClient.ReserveSlot")
                .setAttribute("slot_id", slotId)
                .setAttribute("booking_id", bookingId)
                .setAttribute("quantity", quantity)
                .startSpan();
        try (Scope scope = span.makeCurrent()) {
            ReserveSlotRequest request = ReserveSlotRequest.newBuilder()
                    .setSlotId(slotId)
                    .setBookingId(bookingId)
                    .setQuantity(quantity)
                    .build();
            return stub.reserveSlot(request);
        } finally {
            span.end();
        }
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
