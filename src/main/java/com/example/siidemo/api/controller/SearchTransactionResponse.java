package com.example.siidemo.api.controller;

import com.example.siidemo.api.dto.TransactionDto;

import java.util.Set;

public record SearchTransactionResponse(
        Set<TransactionDto> transactions
)
{
}
