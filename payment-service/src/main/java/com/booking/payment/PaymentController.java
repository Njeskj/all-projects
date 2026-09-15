package com.booking.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final BookingConfirmedPublisher publisher;

    public PaymentController(PaymentRepository paymentRepository, BookingConfirmedPublisher publisher) {
        this.paymentRepository = paymentRepository;
        this.publisher = publisher;
    }

    @PostMapping("/payments")
    public Payment create(
            @RequestParam String bookingId,
            @RequestParam String slotId,
            @RequestParam long amountCents) {
        return paymentRepository.save(new Payment(bookingId, slotId, amountCents));
    }

    @GetMapping("/payments/{id}")
    public Payment get(@PathVariable UUID id) {
        return paymentRepository.findById(id).orElseThrow();
    }

    @PostMapping("/payments/{id}/confirm")
    public Payment confirm(@PathVariable UUID id) {
        Payment payment = paymentRepository.findById(id).orElseThrow();
        payment.setStatus("PAID");
        paymentRepository.save(payment);
        publisher.publishConfirmed(payment.getBookingId(), payment.getSlotId());
        return payment;
    }
}
