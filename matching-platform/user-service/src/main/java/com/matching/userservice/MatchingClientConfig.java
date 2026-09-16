package com.matching.userservice;

import com.matching.grpc.MatchingEngineGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchingClientConfig {

    @Bean
    public ManagedChannel matchingChannel(
            @Value("${matching.engine.host:localhost}") String host,
            @Value("${matching.engine.grpc-port:9095}") int port) {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    }

    @Bean
    public MatchingEngineGrpc.MatchingEngineBlockingStub matchingStub(ManagedChannel matchingChannel) {
        return MatchingEngineGrpc.newBlockingStub(matchingChannel);
    }
}
