package com.ecommerce.order.otel;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.Collection;

/** Minimal span exporter that prints directly to stdout (bypasses JUL/Logback routing quirks). */
public class StdoutSpanExporter implements SpanExporter {

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
        for (SpanData span : spans) {
            System.out.println("[otel-trace] name=" + span.getName()
                    + " traceId=" + span.getTraceId()
                    + " spanId=" + span.getSpanId()
                    + " durationNs=" + (span.getEndEpochNanos() - span.getStartEpochNanos())
                    + " attributes=" + span.getAttributes());
        }
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }
}
