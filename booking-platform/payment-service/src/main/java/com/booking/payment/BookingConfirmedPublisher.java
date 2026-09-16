package com.booking.payment;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingConfirmedPublisher {

    private static final String TOPIC = "booking.confirmed";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BookingConfirmedPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishConfirmed(String bookingId, String slotId) {
        String payload = "{\"bookingId\":\"" + bookingId + "\",\"slotId\":\"" + slotId + "\"}";
        kafkaTemplate.send(TOPIC, bookingId, payload);
    }
}
