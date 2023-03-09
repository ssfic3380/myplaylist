package com.mypli.myplaylist.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AddMusicDto {

    @NotNull
    private Long playlistId;

    @NotNull
    private String youtubePlaylistId;

    @NotNull
    private String youtubePlaylistName;

    @NotNull
    private String youtubePlaylistImg;

    @NotNull
    private Long lastMusicOrder;

    @Builder
    public AddMusicDto(Long playlistId, String youtubePlaylistId, String youtubePlaylistName, String youtubePlaylistImg, Long lastMusicOrder) {
        this.playlistId = playlistId;
        this.youtubePlaylistId = youtubePlaylistId;
        this.youtubePlaylistName = youtubePlaylistName;
        this.youtubePlaylistImg = youtubePlaylistImg;
        this.lastMusicOrder = lastMusicOrder;
    }
}
