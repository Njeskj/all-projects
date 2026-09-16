package com.matching.userservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MatchExecutedConsumer {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MatchExecutedConsumer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @KafkaListener(topics = "match.executed", groupId = "user-service")
    public void onMatchExecuted(String message) throws Exception {
        JsonNode node = objectMapper.readTree(message);
        String offerId = node.get("offer_id").asText();
        double price = node.get("price").asDouble();
        jdbcTemplate.update(
                "INSERT INTO match_history (offer_id, price) VALUES (?, ?)", offerId, price);
    }
}
