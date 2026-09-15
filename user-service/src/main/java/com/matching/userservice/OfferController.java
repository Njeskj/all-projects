package com.matching.userservice;

import com.matching.grpc.MatchingEngineGrpc;
import com.matching.grpc.SubmitOfferRequest;
import com.matching.grpc.SubmitOfferResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class OfferController {

    private final MatchingEngineGrpc.MatchingEngineBlockingStub matchingStub;
    private final JdbcTemplate jdbcTemplate;

    public OfferController(MatchingEngineGrpc.MatchingEngineBlockingStub matchingStub, JdbcTemplate jdbcTemplate) {
        this.matchingStub = matchingStub;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/offers")
    public SubmitOfferResult submitOffer(@RequestParam String offerId, @RequestParam double price) {
        SubmitOfferResponse resp = matchingStub.submitOffer(
                SubmitOfferRequest.newBuilder().setOfferId(offerId).setPrice(price).build());
        // history row is written by MatchExecutedConsumer once matching-engine
        // publishes the match.executed Kafka event for this offer.
        return new SubmitOfferResult(resp.getAccepted(), resp.getPendingCount());
    }

    @GetMapping("/history")
    public List<Map<String, Object>> history() {
        return jdbcTemplate.queryForList("SELECT id, offer_id, price, created_at FROM match_history ORDER BY id");
    }

    public record SubmitOfferResult(boolean accepted, int pendingCount) {}
}
