package com.example.insidetesttask.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(String message) {
        super(message);
    }
}
