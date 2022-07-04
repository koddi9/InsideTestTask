package com.example.insidetesttask.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotCompatibleNameWithTokenSubject extends RuntimeException {
    public NotCompatibleNameWithTokenSubject(String message) {
        super(message);
    }
}
