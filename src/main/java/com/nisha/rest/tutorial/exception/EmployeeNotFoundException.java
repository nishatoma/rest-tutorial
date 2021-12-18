package com.nisha.rest.tutorial.exception;

public class EmployeeNotFoundException extends RuntimeException {

    private EmployeeNotFoundException(){}

    public EmployeeNotFoundException(Long id) {
        super("Could not find employee with ID: " + id);
    }
}
