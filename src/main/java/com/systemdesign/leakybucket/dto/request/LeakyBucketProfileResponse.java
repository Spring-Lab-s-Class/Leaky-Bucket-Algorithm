package com.systemdesign.leakybucket.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LeakyBucketProfileResponse {

    private List<LeakyBucketResponse> counters;

    public static LeakyBucketProfileResponse from(List<LeakyBucketResponse> counters) {
        return LeakyBucketProfileResponse.builder()
                .counters(counters)
                .build();
    }
}
