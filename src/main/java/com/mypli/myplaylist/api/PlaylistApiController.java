package com.mypli.myplaylist.api;

import com.mypli.myplaylist.api.response.ApiResponse;
import com.mypli.myplaylist.dto.UpdateMusicDto;
import com.mypli.myplaylist.dto.UpdateMusicOrderDto;
import com.mypli.myplaylist.dto.UpdatePlaylistNameDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubeSearchDto;
import com.mypli.myplaylist.service.MusicService;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.service.youtube.YoutubeSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistApiController {

    private final PlaylistService playlistService;
    private final MusicService musicService;
    private final YoutubePlaylistsService youtubePlaylistsService;
    private final YoutubeSearchService youtubeSearchService;

    /**
     * 플레이리스트 상세정보 페이지 - 플레이리스트 이름 변경
     */
    @PatchMapping("/{playlistId}")
    public ApiResponse updatePlaylist(@PathVariable Long playlistId, @RequestBody UpdatePlaylistNameDto updatePlaylistNameDto,
                                 Principal principal) {

        String socialId = null;
        if (principal != null) socialId = principal.getName();

        playlistService.updateName(socialId, playlistId, updatePlaylistNameDto);

        return ApiResponse.noContent();
    }

    /**
     * 플레이리스트 상세정보 페이지 - 노래 순서 변경
     */
    @PatchMapping("/{playlistId}/order")
    public ApiResponse updateMusicOrder(@PathVariable Long playlistId, @RequestBody UpdateMusicOrderDto updateMusicOrderDto, Principal principal) {

        String socialId = null;
        if (principal != null) socialId = principal.getName();

        musicService.updateOrder(socialId, playlistId, updateMusicOrderDto);

        return ApiResponse.noContent();
    }

    /**
     * 플레이리스트 상세정보 페이지 - 노래 정보 변경
     */
    @PatchMapping("/{playlistId}/{musicId}")
    public ApiResponse updateMusic(@PathVariable Long playlistId, @PathVariable Long musicId, @RequestBody UpdateMusicDto updateMusicDto, Principal principal) {

        String socialId = null;
        if (principal != null) socialId = principal.getName();

        musicService.update(socialId, playlistId, musicId, updateMusicDto);

        return ApiResponse.noContent();
    }


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
