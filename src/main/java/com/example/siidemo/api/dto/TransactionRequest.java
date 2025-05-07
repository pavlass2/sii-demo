package com.example.siidemo.api.dto;

import jakarta.annotation.Nonnull;

import java.time.OffsetDateTime;
import java.util.Map;

public record TransactionRequest(

        int id,

        @Nonnull
        OffsetDateTime timestamp,

        @Nonnull
        String type,

        @Nonnull
        String actor,

        @Nonnull
        Map<String, String> transactionData
)
{
}
