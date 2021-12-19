package com.nisha.rest.tutorial.exception;

public class OrderNotFoundException extends RuntimeException {

    private OrderNotFoundException() {
    }

    public OrderNotFoundException(Long id) {
        super("Could not find order with ID: " + id);
    }
}
