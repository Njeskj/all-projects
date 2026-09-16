package com.observability.configapi;

import com.observability.metricscollector.grpc.IngestMetricRequest;
import com.observability.metricscollector.grpc.IngestMetricResponse;
import com.observability.metricscollector.grpc.MetricsCollectorServiceGrpc;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

    private final MetricsCollectorServiceGrpc.MetricsCollectorServiceBlockingStub stub;
    private final Tracer tracer;

    public MetricsController(MetricsCollectorServiceGrpc.MetricsCollectorServiceBlockingStub stub, Tracer tracer) {
        this.stub = stub;
        this.tracer = tracer;
    }

    public record IngestResult(boolean accepted, String id) {}

    @PostMapping("/metrics/ingest")
    public IngestResult ingest(@RequestParam String name, @RequestParam double value, @RequestParam(defaultValue = "config-api") String source) {
        Span span = tracer.spanBuilder("IngestMetric-grpc-call").startSpan();
        try (Scope scope = span.makeCurrent()) {
            IngestMetricRequest req = IngestMetricRequest.newBuilder()
                    .setMetricName(name)
                    .setValue(value)
                    .setSource(source)
                    .build();
            IngestMetricResponse resp = stub.ingestMetric(req);
            return new IngestResult(resp.getAccepted(), resp.getId());
        } finally {
            span.end();
        }
    }
}
