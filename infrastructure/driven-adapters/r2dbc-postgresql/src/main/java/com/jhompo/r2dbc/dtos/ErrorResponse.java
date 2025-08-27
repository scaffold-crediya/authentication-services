package com.jhompo.r2dbc.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {}