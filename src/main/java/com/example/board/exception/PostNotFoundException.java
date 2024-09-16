package com.example.board.exception;

public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(Long id) {
        super("Post with ID " + id + " not found.");
    }
}
