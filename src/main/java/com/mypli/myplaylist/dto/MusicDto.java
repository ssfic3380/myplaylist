package com.mypli.myplaylist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class MusicDto {

    @NotNull
    private Long musicId;

    @NotNull @Size(max = 128)
    private String title;

    @NotNull @Size(max = 255)
    private String videoId;
}
