package com.mypli.myplaylist.dto.youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class YoutubePlaylistItemsDto {

    private String videoId;
    private String title;
    private String artist;
    private String album;
    private String thumbnail;

    @Builder
    public YoutubePlaylistItemsDto(String videoId, String title, String artist, String album, String thumbnail) {
        this.videoId = videoId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.thumbnail = thumbnail;
    }
}
