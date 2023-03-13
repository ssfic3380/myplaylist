package com.mypli.myplaylist.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class UpdateMusicDto {

    @NotNull
    private Long playlistId;

    @NotNull
    private Long musicId;

    @Size(max = 128)
    private String title;

    @Size(max = 128)
    private String artist;

    @Size(max = 128)
    private String album;

    @Size(max = 255)
    private String videoId;

    private Long musicOrder;

    @Builder
    public UpdateMusicDto(Long playlistId, Long musicId, String title, String artist, String album, String videoId, Long musicOrder) {
        this.playlistId = playlistId;
        this.musicId = musicId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.videoId = videoId;
        this.musicOrder = musicOrder;
    }
}
