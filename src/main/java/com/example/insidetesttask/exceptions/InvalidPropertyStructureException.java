package com.example.insidetesttask.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidPropertyStructureException extends RuntimeException {

    public InvalidPropertyStructureException(String message, String validStructure) {
        super(message + " ." + validStructure);
    }

    @NoArgsConstructor
    public static class InvalidMessageStructureException extends InvalidPropertyStructureException {
        public InvalidMessageStructureException(String message) {
            super("Invalid message structure. " + message,"The right example: Bearer_zk25g46uktgk3...");
        }
    }

    @NoArgsConstructor
    public static class InvalidHeaderStructureException extends InvalidPropertyStructureException {

        public InvalidHeaderStructureException(String header) {
            super("Invalid header structure: " + header, "The right example: history 10");
        }
    }
}