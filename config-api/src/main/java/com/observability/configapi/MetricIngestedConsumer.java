package com.observability.configapi;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MetricIngestedConsumer {

    @KafkaListener(topics = "metric.ingested", groupId = "config-api")
    public void onMessage(String payload) {
        System.out.println("metric.ingested received: " + payload);
    }
}
