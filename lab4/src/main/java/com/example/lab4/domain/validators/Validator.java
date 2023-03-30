package com.example.lab4.domain.validators;

import com.example.lab4.domain.exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}