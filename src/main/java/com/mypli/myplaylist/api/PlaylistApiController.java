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

import java.security.Principal;
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
     * 노래 추가 모달 - 노래 검색 API
     * List는 비어있을 수 있음
     */
    @GetMapping("/search")
    public ApiResponse<List<YoutubeSearchDto>> getYoutubeItems(@RequestParam(value = "q", required = false) String searchQuery,
                                                                      Principal principal) {

        String socialId = null;
        if (principal != null) socialId = principal.getName(); //TODO: HomeController 참고

        List<YoutubeSearchDto> youtubeSearchResultList = new ArrayList<>();
        if (searchQuery != null) youtubeSearchResultList = youtubeSearchService.getSearchResult(socialId, searchQuery);

        return ApiResponse.success("data", youtubeSearchResultList);
    }

    /**
     * 노래 추가 세부설정 모달 - 노래 추가 API
     */
    @PostMapping("/search")
    public ApiResponse insertYoutubeItem(@ModelAttribute("youtubeSearchResult") YoutubeSearchDto youtubeSearchDto,
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
