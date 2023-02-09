package com.mypli.myplaylist.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class MusicDto {

    private Long musicId;

    @NotNull @Size(max = 128)
    private String title;

    @NotNull @Size(max = 128)
    private String artist;

    @Size(max = 128)
    private String album;

    @NotNull @Size(max = 255)
    private String videoId;

    @Size(max = 255)
    private String musicImg;

    @NotNull
    private Long musicOrder;

    @Builder
    public MusicDto(Long musicId, String title, String artist, String album, String videoId, String musicImg, Long musicOrder) {
        this.musicId = musicId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.videoId = videoId;
        this.musicImg = musicImg;
        this.musicOrder = musicOrder;
    }
}
