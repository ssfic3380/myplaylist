package com.mypli.myplaylist.exception;

public class MusicNotFoundException extends RuntimeException {

    public MusicNotFoundException() {
    }

    public MusicNotFoundException(String message) {
        super(message);
    }

    public MusicNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MusicNotFoundException(Throwable cause) {
        super(cause);
    }
}
