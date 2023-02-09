package com.mypli.myplaylist.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class PlaylistDto {

    private Long playlistId;

    @NotNull @Size(max = 128)
    private String playlistName;

    @Size(max = 255)
    private String playlistImg;

    @Builder
    public PlaylistDto(Long playlistId, String playlistName, String playlistImg) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistImg = playlistImg;
    }
}
