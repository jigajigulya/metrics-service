package dev.matveev.metricsservice.service;

import dev.matveev.metricsservice.config.RateLimitProperties;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {



    private final ProxyManager<String> proxyManager;


    private final BucketConfiguration globalConfig;
    private final BucketConfiguration clientConfig;


    private static final String GLOBAL_LIMIT_KEY = "ratelimit:global";
    private static final String CLIENT_LIMIT_PREFIX = "ratelimit:client:";

    public RateLimiterService(ProxyManager<String> proxyManager, RateLimitProperties properties) {
        this.proxyManager = proxyManager;


        this.globalConfig = BucketConfiguration.builder()
                .addLimit(io.github.bucket4j.Bandwidth.builder()
                        .capacity(properties.global().capacity())
                        .refillIntervally(properties.global().capacity(), properties.global().duration())
                        .build())
                .build();


        this.clientConfig = BucketConfiguration.builder()
                .addLimit(io.github.bucket4j.Bandwidth.builder()
                        .capacity(properties.client().capacity())
                        .refillIntervally(properties.client().capacity(), properties.client().duration())
                        .build())
                .build();
    }


    public boolean tryConsume(String clientId) {
        String clientKey = CLIENT_LIMIT_PREFIX + clientId;
        boolean globalAllowed = proxyManager.getProxy(GLOBAL_LIMIT_KEY, () -> globalConfig).tryConsume(1);
        if (!globalAllowed) {
            return false;
        }


        return proxyManager.getProxy(clientKey, () -> clientConfig).tryConsume(1);
    }
}
