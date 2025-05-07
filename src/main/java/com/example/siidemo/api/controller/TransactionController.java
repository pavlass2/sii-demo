package com.example.siidemo.api.controller;

import com.example.siidemo.api.dto.TransactionRequest;
import com.example.siidemo.api.dto.TransactionResponse;
import com.example.siidemo.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/api/transaction")
public class TransactionController
{
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(final TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid final TransactionRequest request)
    {
        transactionService.createTransaction(request);

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> read(@PathVariable final int id)
    {
        final TransactionResponse transaction = transactionService.readTransaction(id);

        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable final int id,
            @RequestBody @Valid final TransactionRequest request
    )
    {
        transactionService.updateTransaction(id, request);

        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id)
    {
        transactionService.deleteTransaction(id);

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping
    public ResponseEntity<SearchTransactionResponse> search(
            @RequestParam(required = false) final Integer id,
            @RequestParam(required = false) final OffsetDateTime timestampFrom,
            @RequestParam(required = false) final OffsetDateTime timestampUntil,
            @RequestParam(required = false) final String type,
            @RequestParam(required = false) final String actor
    )
    {
        final SearchTransactionResponse transaction = transactionService.searchTransaction(
                id,
                timestampFrom,
                timestampUntil,
                type,
                actor
        );

        return ResponseEntity.ok(transaction);
    }
}
