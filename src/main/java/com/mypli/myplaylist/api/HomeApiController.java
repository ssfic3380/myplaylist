package com.mypli.myplaylist.api;

import com.mypli.myplaylist.api.response.ApiResponse;
import com.mypli.myplaylist.dto.PlaylistDto;
import com.mypli.myplaylist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeApiController {

    private final PlaylistService playlistService;

    /**
     * 메인페이지 - 플레이리스트 추가 (누르면 바로 새 플레이리스트의 상세페이지로 이동)
     */
    @PostMapping("/")
    public ApiResponse<Long> createPlaylist(Principal principal) {
        //"추가" 버튼을 클릭했을 경우

        String socialId = null;
        if (principal != null) socialId = principal.getName();

        PlaylistDto playlistDto = PlaylistDto.builder()
                .playlistName("새 플레이리스트")
                .playlistImg("/img/no-image.jpg")
                .build();
        Long playlistId = playlistService.create(socialId, playlistDto);

        return ApiResponse.success("playlistId", playlistId);
    }
}
