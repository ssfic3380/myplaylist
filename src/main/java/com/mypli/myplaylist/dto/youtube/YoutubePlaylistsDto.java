package com.mypli.myplaylist.dto.youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class YoutubePlaylistsDto {

    private String id;
    private String title;
    private String thumbnail;

    @Builder
    public YoutubePlaylistsDto(String id, String title, String thumbnail) {
        this.id = id;
        this.title = title;
        this.thumbnail = thumbnail;
    }
}
