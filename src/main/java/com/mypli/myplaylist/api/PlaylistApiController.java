package com.mypli.myplaylist.api;

import com.mypli.myplaylist.api.response.ApiResponse;
import com.mypli.myplaylist.api.response.ApiResponseHeader;
import com.mypli.myplaylist.dto.youtube.YoutubeSearchDto;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.service.youtube.YoutubeSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistApiController {

    private final YoutubePlaylistsService youtubePlaylistsService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;
    private final YoutubeSearchService youtubeSearchService;

    /**
     * 플레이리스트 상세정보 페이지 - 노래 추가 (모달창에서 검색 버튼)
     * List는 비어있을 수 있음
     */
    @GetMapping("/search")
    public ApiResponse<List<YoutubeSearchDto>> openYoutubeSearchPopup(@RequestParam(value = "q", required = false) String searchQuery) {
        
        List<YoutubeSearchDto> youtubeSearchResultList = new ArrayList<>();
        if (searchQuery != null) youtubeSearchResultList = youtubeSearchService.getSearchResult(searchQuery);

        return ApiResponse.success("data", youtubeSearchResultList);
    }

    @PostMapping("/search")
    public ApiResponse getYoutubeItems(@ModelAttribute("youtubeSearchResult") YoutubeSearchDto youtubeSearchDto,
                                  Model model) {
        //TODO: 선택한 youtubeItem을 Music에 추가하려고 하는데, playlistId가 필요함: 모달창으로 띄웠으니까 가져올 수 있을듯

        /*MusicDto musicDto = MusicDto.builder()
                .title(youtubeSearchDto.getTitle())
                .artist(youtubeSearchDto.getChannelTitle())
                .videoId(youtubeSearchDto.getVideoId())
                .musicImg(youtubeSearchDto.getThumbnail())
                .musicOrder();
        musicService.create();*/

        //TODO: 여기서 리턴을 하면 팝업창에 대해서 렌더링을 할텐데, 플레이리스트 페이지를 새로고침하려면 어떻게 해야할지?
        return ApiResponse.OK();
    }
}
