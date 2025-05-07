package com.example.siidemo.service;

import com.example.siidemo.api.dto.TransactionDto;
import com.example.siidemo.persistence.entity.Transaction;
import com.example.siidemo.persistence.entity.TransactionData;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Map;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface TransactionMapper
{
    @Mapping(target = "id", source = "foreignId")
    TransactionDto toTransactionDto(Transaction transaction);

    default Map<String,String> map(Set<TransactionData> input) {
        return input.stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                TransactionData::getDataKey,
                                TransactionData::getDataValue
                        )
                );
    }
}
