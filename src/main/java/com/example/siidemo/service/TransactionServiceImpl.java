package com.example.siidemo.service;

import com.example.siidemo.api.controller.SearchTransactionResponse;
import com.example.siidemo.api.dto.TransactionResponse;
import com.example.siidemo.api.dto.TransactionDto;
import com.example.siidemo.api.dto.TransactionRequest;
import com.example.siidemo.exception.NotFoundException;
import com.example.siidemo.persistence.entity.Transaction;
import com.example.siidemo.persistence.entity.TransactionData;
import com.example.siidemo.persistence.repository.TransactionRepository;
import com.example.siidemo.persistence.spec.TransactionSpecifications;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService
{
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionServiceImpl(
            final TransactionRepository transactionRepository,
            final TransactionMapper transactionMapper
    )
    {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public void createTransaction(final TransactionRequest request)
    {
        final Transaction transaction = new Transaction();
        copyTransactionRequestToEntity(request, transaction);

        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse readTransaction(final Integer foreignId)
    {
        final Transaction transaction = readTransactionFromRepository(foreignId);

        Hibernate.initialize(transaction.getTransactionData());

        final TransactionDto transactionDto = transactionMapper.toTransactionDto(transaction);

        return new TransactionResponse(transactionDto);
    }

    @Override
    @Transactional
    public void updateTransaction(
            final int foreignId,
            final TransactionRequest request
    )
    {
        final Transaction transaction = readTransactionFromRepository(foreignId);
        copyTransactionRequestToEntity(request, transaction);

        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(final int foreignId)
    {
        final Transaction transaction = readTransactionFromRepository(foreignId);

        transactionRepository.delete(transaction);
    }

    @Override
    public SearchTransactionResponse searchTransaction(
            final Integer foreignId,
            final OffsetDateTime timestampFrom,
            final OffsetDateTime timestampUntil,
            final String type,
            final String actor
    )
    {
        // Build the composite Specification
        Specification<Transaction> spec = Specification.where(
                        TransactionSpecifications.hasForeignId(foreignId))
                .and(TransactionSpecifications.timestampAfter(timestampFrom))
                .and(TransactionSpecifications.timestampBefore(timestampUntil))
                .and(TransactionSpecifications.hasType(type))
                .and(TransactionSpecifications.hasActor(actor));

        // Execute the query
        List<Transaction> results = transactionRepository.findAll(spec);

        // Map to your API/DTO model
        Set<TransactionDto> dtos =
                results.stream()
                        .map(transactionMapper::toTransactionDto)
                        .collect(Collectors.toSet());

        return new SearchTransactionResponse(dtos);
    }

    private static void addTransactionDataToTransaction(
            final String key,
            final String value,
            final Transaction transaction
    )
    {
        final TransactionData transactionData = new TransactionData();
        transactionData.setTransaction(transaction);
        transactionData.setDataKey(key);
        transactionData.setDataValue(value);
        transaction.getTransactionData()
                .add(transactionData);
    }

    private static void copyTransactionRequestToEntity(
            final TransactionRequest request,
            final Transaction transaction
    )
    {
        transaction.setForeignId(request.id());
        transaction.setTimestamp(request.timestamp());
        transaction.setType(request.type());
        transaction.setActor(request.actor());

        if (transaction.getTransactionData() == null) {
            transaction.setTransactionData(new HashSet<>());
        } else {
            transaction.getTransactionData()
                    .clear();
        }

        request.transactionData()
                .forEach((key, value) -> addTransactionDataToTransaction(key, value, transaction));
    }

    private Transaction readTransactionFromRepository(final int foreignId)
    {
        return transactionRepository.getTransactionsByForeignId(foreignId)
                .stream()
                .max(Comparator.comparing(Transaction::getTimestamp))
                .orElseThrow(() -> new NotFoundException(foreignId));
    }
}
