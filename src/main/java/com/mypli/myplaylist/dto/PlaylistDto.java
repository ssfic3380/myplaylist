package com.mypli.myplaylist.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class PlaylistDto {

    @NotNull
    private Long playlistId;

    @NotNull @Size(max = 128)
    private String playlistName;

    @Size(max = 255)
    private String playlistImg;
}
