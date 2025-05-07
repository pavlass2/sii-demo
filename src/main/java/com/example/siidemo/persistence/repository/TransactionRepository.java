package com.example.siidemo.persistence.repository;

import com.example.siidemo.persistence.entity.Transaction;
import com.example.siidemo.persistence.entity.TransactionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction>
{
    List<Transaction> getTransactionsByForeignId(Integer foreignId);
}
