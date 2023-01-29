package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistsDto;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.utils.HeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final JwtTokenProvider tokenProvider;
    private final PlaylistService playlistService;
    private final YoutubePlaylistsService youtubePlaylistsService;

    @GetMapping("/youtube")
    public String createPlaylistFromYoutube(HttpServletRequest request, @ModelAttribute List<Playlist> playlists) {

        String accessToken = HeaderUtils.getAccessToken(request);
        String socialId = tokenProvider.parseClaims(accessToken).getSubject();

        List<YoutubePlaylistsDto> youtubePlaylistsDtoList = youtubePlaylistsService.getPlaylists(socialId);

        return null;
    }

}
