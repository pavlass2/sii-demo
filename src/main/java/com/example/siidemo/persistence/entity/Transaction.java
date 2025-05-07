package com.example.siidemo.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
public class Transaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false) // We may want to set "unique = true" but it wasn't explicitly specified.
    private Integer foreignId;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String actor;

    @OneToMany(
            mappedBy = "transaction",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<TransactionData> transactionData;


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getForeignId()
    {
        return foreignId;
    }

    public void setForeignId(final Integer foreignId)
    {
        this.foreignId = foreignId;
    }

    public OffsetDateTime getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final OffsetDateTime timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getActor()
    {
        return actor;
    }

    public void setActor(final String actor)
    {
        this.actor = actor;
    }

    public Set<TransactionData> getTransactionData()
    {
        return transactionData;
    }

    public void setTransactionData(final Set<TransactionData> transactionData)
    {
        this.transactionData = transactionData;
    }
}
