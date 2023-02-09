package com.mypli.myplaylist.oauth2.exception;

/**
 * 나중에 Advice 전역 예외 처리 컨트롤러를 만들어서
 * OAuth2NotFoundException은 NotFoundException 혹은 ResourceNotFoundException
 * OAuth2RegistrationException은 BadRequestException을 구현하면 됨
 */
//자원 없음 예외
public class OAuth2NotFoundException extends RuntimeException {

    public OAuth2NotFoundException() {
    }

    public OAuth2NotFoundException(String message) {
        super(message);
    }

    public OAuth2NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2NotFoundException(Throwable cause) {
        super(cause);
    }
}
