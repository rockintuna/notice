package me.rockintuna.notice.exception;

public class NoticeNotFoundException extends RuntimeException{
    public NoticeNotFoundException(String message) {
        super(message);
    }
}
