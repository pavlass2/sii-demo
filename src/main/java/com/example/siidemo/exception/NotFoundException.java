package com.example.siidemo.exception;

public class NotFoundException extends RuntimeException
{
    public NotFoundException(final int id)
    {
        super("Transaction with id " + id + " was not found.");
    }
}
