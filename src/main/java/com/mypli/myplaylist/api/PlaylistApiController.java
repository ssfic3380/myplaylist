package com.mypli.myplaylist.api;

import com.mypli.myplaylist.api.response.ApiResponse;
import com.mypli.myplaylist.api.response.ApiResponseHeader;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.dto.youtube.YoutubeSearchDto;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.service.youtube.YoutubeSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistApiController {

    private final PlaylistService playlistService;
    private final YoutubePlaylistsService youtubePlaylistsService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;
    private final YoutubeSearchService youtubeSearchService;

    /**
     * 유튜브에서 불러오기 모달 - 플레이리스트 검색 API
     */
    @GetMapping("/youtube/playlists")
    public ApiResponse<List<YoutubePlaylistDto>> getYoutubePlaylists(Principal principal) {

        String socialId = null;
        if (principal != null) socialId = principal.getName();

        List<YoutubePlaylistDto> youtubePlaylistResultList = youtubePlaylistsService.getPlaylists(socialId);

        return ApiResponse.success("data", youtubePlaylistResultList);
    }


    /**
     * 노래 추가 모달 - 노래 검색 API
     * List는 비어있을 수 있음
     */
    @GetMapping("/youtube/search")
    public ApiResponse<List<YoutubeSearchDto>> getYoutubeItems(@RequestParam(value = "q", required = false) String searchQuery, Principal principal) {

        String socialId = null;
        if (principal != null) socialId = principal.getName();

        List<YoutubeSearchDto> youtubeSearchResultList = new ArrayList<>();
        if (searchQuery != null) youtubeSearchResultList = youtubeSearchService.getSearchResult(socialId, searchQuery);

        return ApiResponse.success("data", youtubeSearchResultList);
    }
}
