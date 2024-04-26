package com.systemdesign.leakybucket.controller;

import com.systemdesign.leakybucket.dto.request.LeakyBucketProfileResponse;
import com.systemdesign.leakybucket.dto.request.LeakyBucketResponse;
import com.systemdesign.leakybucket.service.LeakyBucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("leaky-bucket")
public class LeakyBucketController {

    private final LeakyBucketService leakyBucketService;

    @GetMapping
    public Mono<ResponseEntity<Flux<LeakyBucketResponse>>> findAllLeakyBucket() {
        return Mono.just(
                ResponseEntity.status(OK)
                        .body(leakyBucketService.findAllLeakyBucket()));
    }

    @PostMapping
    public Mono<ResponseEntity<LeakyBucketProfileResponse>> createLeakyBucket() {
        return leakyBucketService.createLeakyBucket()
                .map(response -> ResponseEntity.status(CREATED).body(response));
    }
}
