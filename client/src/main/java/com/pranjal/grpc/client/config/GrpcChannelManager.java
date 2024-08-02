package com.pranjal.grpc.client.config;

import com.pranjal.grpc.client.interceptor.GrpcClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

@Getter
@Component
public class GrpcChannelManager {

    private final ManagedChannel serverChannel;


    public GrpcChannelManager(@Value("${grpc-server.url}") String url,
                              @Value("${grpc-server.port}") int port) {
        this.serverChannel = ManagedChannelBuilder.forAddress(url, port)
                                                  .intercept(new GrpcClientInterceptor())
                                                  .usePlaintext()
                                                  .defaultLoadBalancingPolicy("pick_first")  // tenant_lb, round_robin
                                                  .defaultServiceConfig(getConfig())
                                                  .enableRetry()
                                                  .build();
    }


    private Map<String, Object> getConfig() {
        Map<String, Object> name = new HashMap<>();
        name.put("service", "");
        name.put("method", "");

        Map<String, Object> retryPolicy = new HashMap<>();
        retryPolicy.put("maxAttempts", 5.0);
        retryPolicy.put("initialBackoff", "0.1s");
        retryPolicy.put("maxBackoff", "1s");
        retryPolicy.put("backoffMultiplier", 2.0);
        retryPolicy.put("retryableStatusCodes", singletonList("UNAVAILABLE"));

        Map<String, Object> methodConfig = new HashMap<>();
        methodConfig.put("name", singletonList(name));
//        methodConfig.put("timeout", "5s");
        methodConfig.put("retryPolicy", retryPolicy);
        methodConfig.put("wait_for_ready", true);

        Map<String, Object> config = new HashMap<>();
        config.put("methodConfig", singletonList(methodConfig));

        return config;
    }

}
