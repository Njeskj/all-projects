package com.booking.payment;

import com.booking.inventory.grpc.ReserveSlotResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private final InventoryGrpcClient inventoryGrpcClient;

    public ReservationController(InventoryGrpcClient inventoryGrpcClient) {
        this.inventoryGrpcClient = inventoryGrpcClient;
    }

    @PostMapping("/reservations")
    public String reserve(
            @RequestParam String slotId,
            @RequestParam String bookingId,
            @RequestParam int quantity) {
        ReserveSlotResponse response = inventoryGrpcClient.reserveSlot(slotId, bookingId, quantity);
        return response.getSuccess() ? "OK: " + response.getMessage() : "FAILED: " + response.getMessage();
    }
}
