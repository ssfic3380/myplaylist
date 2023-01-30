package com.mypli.myplaylist.dto.youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter @Setter
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
