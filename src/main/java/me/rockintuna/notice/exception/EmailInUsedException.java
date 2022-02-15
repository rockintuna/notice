package me.rockintuna.notice.exception;

public class EmailInUsedException extends RuntimeException {
    public EmailInUsedException(String message) {
        super(message);
    }
}
