package com.mypli.myplaylist.oauth2.exception;

//가입 실패 예외
public class OAuth2RegistrationException extends RuntimeException {
    public OAuth2RegistrationException() {
    }

    public OAuth2RegistrationException(String message) {
        super(message);
    }

    public OAuth2RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2RegistrationException(Throwable cause) {
        super(cause);
    }
}
