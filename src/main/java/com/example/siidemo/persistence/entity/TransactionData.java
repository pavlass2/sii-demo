package com.example.siidemo.persistence.entity;

import jakarta.persistence.*;

@Entity
public class TransactionData
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(nullable = false)
    private String dataKey;

    @Column(nullable = false)
    private String dataValue;


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Transaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }

    public String getDataKey()
    {
        return dataKey;
    }

    public void setDataKey(final String dataKey)
    {
        this.dataKey = dataKey;
    }

    public String getDataValue()
    {
        return dataValue;
    }

    public void setDataValue(final String value)
    {
        this.dataValue = value;
    }
}
