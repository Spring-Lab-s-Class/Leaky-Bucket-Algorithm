package com.systemdesign.leakybucket.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "from")
public class ExceptionResponse {

    private final String code;
}

