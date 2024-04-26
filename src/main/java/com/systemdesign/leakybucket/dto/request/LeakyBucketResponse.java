package com.systemdesign.leakybucket.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeakyBucketResponse {

    private String key;
    private Long requestCount;

    public static LeakyBucketResponse from(String key, Long requestCount) {
        return LeakyBucketResponse.builder()
                .key(key)
                .requestCount(requestCount)
                .build();
    }
}
