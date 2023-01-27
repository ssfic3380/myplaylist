package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.dto.youtube.YoutubePlaylistsDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemsDto;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class YoutubeTestController {

    private final YoutubePlaylistsService youtubePlaylistService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;

    @GetMapping("youtube1")
    public List<YoutubePlaylistsDto> playlist_list() {
        return youtubePlaylistService.get("111511732184187189491");
    }

    @GetMapping("youtube2")
    public List<YoutubePlaylistItemsDto> playlistItems_list() {
        return youtubePlaylistItemsService.get("111511732184187189491", "PL1DG6X8jmc76tqriNylTHfqDZVRDAyYgK");
    }

}
