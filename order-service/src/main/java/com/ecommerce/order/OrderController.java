package com.ecommerce.order;

import com.ecommerce.inventory.grpc.InventoryServiceGrpc;
import com.ecommerce.inventory.grpc.ReserveStockRequest;
import com.ecommerce.inventory.grpc.ReserveStockResponse;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class OrderController {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Tracer tracer;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    public OrderController(InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub,
                            KafkaTemplate<String, String> kafkaTemplate,
                            Tracer tracer) {
        this.inventoryStub = inventoryStub;
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public record CreateOrderRequest(String sku, int quantity) {}

    public record CreateOrderResponse(String orderId, boolean reserved, int remainingStock, String message) {}

    @PostMapping("/orders")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest req) {
        String orderId = UUID.randomUUID().toString();

        Span span = tracer.spanBuilder("order-service -> inventory-service ReserveStock")
                .setAttribute("order.id", orderId)
                .setAttribute("order.sku", req.sku())
                .setAttribute("order.quantity", req.quantity())
                .startSpan();
        ReserveStockResponse resp;
        try (Scope scope = span.makeCurrent()) {
            resp = inventoryStub.reserveStock(
                    ReserveStockRequest.newBuilder()
                            .setSku(req.sku())
                            .setQuantity(req.quantity())
                            .setOrderId(orderId)
                            .build());
            span.setAttribute("order.reserved", resp.getReserved());
        } finally {
            span.end();
        }

        if (resp.getReserved()) {
            publishOrderPlaced(orderId, req.sku(), req.quantity());
        }

        return new CreateOrderResponse(orderId, resp.getReserved(), resp.getRemainingStock(), resp.getMessage());
    }

    private void publishOrderPlaced(String orderId, String sku, int quantity) {
        try {
            String payload = objectMapper.writeValueAsString(new OrderPlacedEvent(orderId, sku, quantity));
            kafkaTemplate.send("order.placed", orderId, payload);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public record OrderPlacedEvent(String order_id, String sku, int quantity) {
    }
}
