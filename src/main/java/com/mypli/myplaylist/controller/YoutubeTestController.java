package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.dto.youtube.YoutubeSearchDto;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.service.youtube.YoutubeSearchService;
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
    private final YoutubeSearchService youtubeSearchService;

    @GetMapping("youtube1")
    public List<YoutubePlaylistDto> playlist_list() {
        return youtubePlaylistService.getPlaylists("111511732184187189491");
    }

    @GetMapping("youtube2")
    public List<YoutubePlaylistItemDto> playlistItems_list() {
        return youtubePlaylistItemsService.getPlaylistItems("111511732184187189491", "PL1DG6X8jmc76tqriNylTHfqDZVRDAyYgK");
    }

    @GetMapping("youtube3")
    public List<YoutubeSearchDto> search() {
        return youtubeSearchService.getSearchResult("어떻게 이별까지 사랑하겠어");
    }

}
