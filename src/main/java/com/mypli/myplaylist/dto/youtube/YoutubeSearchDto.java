package com.mypli.myplaylist.dto.youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class YoutubeSearchDto {

    private String videoId;
    private String title;
    private String channelTitle;
    private String thumbnail;

    @Builder
    public YoutubeSearchDto(String videoId, String title, String channelTitle, String thumbnail) {
        this.videoId = videoId;
        this.title = title;
        this.channelTitle = channelTitle;
        this.thumbnail = thumbnail;
    }
}
