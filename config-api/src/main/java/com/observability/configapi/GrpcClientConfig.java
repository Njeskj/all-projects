package com.observability.configapi;

import com.observability.metricscollector.grpc.MetricsCollectorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel metricsCollectorChannel(
            @Value("${metrics-collector.grpc.host:localhost}") String host,
            @Value("${metrics-collector.grpc.port:28090}") int port) {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    }

    @Bean
    public MetricsCollectorServiceGrpc.MetricsCollectorServiceBlockingStub metricsCollectorStub(ManagedChannel metricsCollectorChannel) {
        return MetricsCollectorServiceGrpc.newBlockingStub(metricsCollectorChannel);
    }
}
