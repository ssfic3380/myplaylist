package com.mypli.myplaylist.api.exhandler;

import com.mypli.myplaylist.api.response.ApiResponse;
import com.mypli.myplaylist.exception.MemberNotFoundException;
import com.mypli.myplaylist.exception.MusicNotFoundException;
import com.mypli.myplaylist.exception.NoPermissionException;
import com.mypli.myplaylist.exception.PlaylistNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    public ApiResponse memberNotFoundExHandler(MemberNotFoundException e) {
        return ApiResponse.unauthorized("Member Not Found");
    }

    @ExceptionHandler(NoPermissionException.class)
    public ApiResponse noPermissionExHandler(NoPermissionException e) {
        return ApiResponse.notFound("No Permission");
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ApiResponse playlistNotFoundExHandler(PlaylistNotFoundException e) {
        return ApiResponse.notFound("Playlist Not Found");
    }

    @ExceptionHandler(MusicNotFoundException.class)
    public ApiResponse musicNotFoundExHandler(MusicNotFoundException e) {
        return ApiResponse.notFound("Music Not Found");
    }
}
