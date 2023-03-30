package com.example.lab4.domain.exceptions;

public class UsersAlreadyFriends extends RuntimeException{
    public UsersAlreadyFriends() {
    }

    public UsersAlreadyFriends(String message) {
        super(message);
    }

    public UsersAlreadyFriends(String message, Throwable cause) {
        super(message, cause);
    }

    public UsersAlreadyFriends(Throwable cause) {
        super(cause);
    }

    public UsersAlreadyFriends(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
