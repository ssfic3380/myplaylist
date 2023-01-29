package com.mypli.myplaylist.dto.youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class YoutubePlaylistDto {

    private String playlistId;
    private String title;
    private String thumbnail;

    @Builder
    public YoutubePlaylistDto(String playlistId, String title, String thumbnail) {
        this.playlistId = playlistId;
        this.title = title;
        this.thumbnail = thumbnail;
    }
}
