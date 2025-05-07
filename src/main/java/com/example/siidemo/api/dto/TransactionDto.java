package com.example.siidemo.api.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public record TransactionDto(
        Integer id,
        OffsetDateTime timestamp,
        String type,
        String actor,
        Map<String, String> transactionData
)
{
}
