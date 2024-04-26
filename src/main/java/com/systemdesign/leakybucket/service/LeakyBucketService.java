package com.systemdesign.leakybucket.service;

import com.systemdesign.leakybucket.dto.request.LeakyBucketProfileResponse;
import com.systemdesign.leakybucket.dto.request.LeakyBucketResponse;
import com.systemdesign.leakybucket.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Date;

import static com.systemdesign.leakybucket.exception.RateExceptionCode.COMMON_TOO_MANY_REQUESTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeakyBucketService {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final static String LEAKY_BUCKET_KEY = "LeakyBucket:"; // 키
    private final static long LEAKY_BUCKET_MAX_TOKEN = 1000; // 최대 요청 허용 수
    private final static long LEAKY_BUCKET_REFILL_DURATION = 60; //토큰 리필 시간(1분)

    public Mono<LeakyBucketProfileResponse> createLeakyBucket() {
        String key = generateRedisKey("requests");
        return reactiveRedisTemplate.opsForList().size(key)
                .flatMap(size -> {
                    if (size < LEAKY_BUCKET_MAX_TOKEN) {
                        // 큐가 가득 차지 않았다면 요청 추가
                        log.info("Adding a request to the leaky bucket {}", size);
                        return reactiveRedisTemplate.opsForList().rightPush(key, new Date().toString())
                                .flatMap(index -> Mono.just(LeakyBucketProfileResponse.from(
                                        Collections.singletonList(LeakyBucketResponse.from(key, size + 1)))));
                    } else {
                        // 큐가 가득 차면 요청 거부
                        log.warn("Leaky bucket is full {}", size);
                        return Mono.error(new RateLimitExceededException(COMMON_TOO_MANY_REQUESTS, size));
                    }
                });
    }

    public Flux<LeakyBucketResponse> findAllLeakyBucket() {
        String key = generateRedisKey("requests");
        return reactiveRedisTemplate.opsForList().size(key)
                .flatMapMany(size -> Flux.just(LeakyBucketResponse.from(key, size)));
    }

    // 토큰 리필 로직
    @Scheduled(fixedRate = LEAKY_BUCKET_REFILL_DURATION * 1000)
    public void refillTokenBuckets() {
        String key = generateRedisKey("requests");
        log.info("Refilling token buckets for {}", key);
        reactiveRedisTemplate.opsForList().size(key)
                .flatMapMany(size -> {
                    if (size > 0) {
                        // 1번 리필 시 최대 5개의 토큰을 소비(1분 후 5개의 토큰 소비)
                        int tokensToConsume = (int) Math.min(5, size);
                        return Flux.range(0, tokensToConsume)
                                .flatMap(i -> {
                                    log.info("Consuming a token from the leaky bucket {}. Requests before consuming: {}", key, size - i);
                                    return reactiveRedisTemplate.opsForList().leftPop(key);
                                });
                    } else {
                        log.info("No requests to consume from the leaky bucket {}.", key);
                        return Flux.empty();
                    }
                }).subscribe();
    }

    private String generateRedisKey(String requestType) {
        return LEAKY_BUCKET_KEY + requestType;
    }
}
