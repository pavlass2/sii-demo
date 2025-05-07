package com.example.siidemo.service;

import com.example.siidemo.api.controller.SearchTransactionResponse;
import com.example.siidemo.api.dto.TransactionResponse;
import com.example.siidemo.api.dto.TransactionRequest;

import java.time.OffsetDateTime;

public interface TransactionService
{
    void createTransaction(TransactionRequest request);

    TransactionResponse readTransaction(Integer id);

    void updateTransaction(
            int id,
            TransactionRequest request
    );

    void deleteTransaction(int id);

    SearchTransactionResponse searchTransaction(
            Integer id,
            OffsetDateTime timestampFrom,
            OffsetDateTime timestampUntil,
            String type,
            String actor
    );
}
