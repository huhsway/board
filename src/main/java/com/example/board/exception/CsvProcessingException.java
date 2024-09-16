package com.example.board.exception;

public class CsvProcessingException extends RuntimeException{
    public CsvProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
