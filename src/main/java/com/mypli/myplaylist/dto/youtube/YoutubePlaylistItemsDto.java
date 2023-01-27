package com.mypli.myplaylist.dto.youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class YoutubePlaylistItemsDto {

    private String title;
    private String artist;
    private String album;
    private String videoId;
    private String thumbnail;

    @Builder
    public YoutubePlaylistItemsDto(String title, String artist, String album, String videoId, String thumbnail) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.videoId = videoId;
        this.thumbnail = thumbnail;
    }
}
