package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.utils.HeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final YoutubePlaylistsService youtubePlaylistsService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;

    @GetMapping("/")
    public String getAllPlaylists(Principal principal) {
        //마플리 홈페이지 (유저의 모든 플레이리스트 보여주기)

        String socialId = principal.getName();
        Optional<Playlist> playlists;

        if (socialId != null) playlists = playlistService.findBySocialId(socialId);
        else {
            //TODO: 로그인을 안했다면 플레이리스트가 없음
        }
        //TODO: 플레이리스트 이름, img만 보내주면 될듯? DTO로

        return "home";
    }

    @GetMapping("/playlist/{playlistId}")
    public String getPlaylist(@PathVariable String playlistId) {
        //플레이리스트 상세내용

        Optional<Playlist> playlist = playlistService.findByPlaylistId(playlistId);
        //TODO: 플레이리스트 DTO를 넘겨서 관련된 Music까지 싹 보여줘야함

        return "home";
    }

    @GetMapping("/youtube/playlists")
    public String getYoutubePlaylists(Principal principal, Model model) {
        //유튜브에서 불러오기를 클릭했을 경우

        String socialId = principal.getName();

        List<YoutubePlaylistDto> youtubePlaylistDtoList = youtubePlaylistsService.getPlaylists(socialId);
        model.addAttribute("youtubePlaylists", youtubePlaylistDtoList);

        //TODO: return 뭐로해야할지(팝업창에 띄워주고싶음)
        return "home";
    }

    @PostMapping("/youtube/playlists")
    public String createPlaylistFromYoutube(Principal principal,
                                            @ModelAttribute("YoutubePlaylist") YoutubePlaylistDto youtubePlaylistDto) {
        //유튜브 플레이리스트를 골랐을 경우

        String socialId = principal.getName();

        List<YoutubePlaylistItemDto> youtubePlaylistItemDtoList = youtubePlaylistItemsService.getPlaylistItems(socialId, youtubePlaylistDto.getPlaylistId());
        Long playlistId = playlistService.createPlaylistFromYoutube(socialId, youtubePlaylistDto, youtubePlaylistItemDtoList);

        log.info("New playlistId = {}", playlistId);

        return "redirect:/playlist/{playlistId}";
    }
}
