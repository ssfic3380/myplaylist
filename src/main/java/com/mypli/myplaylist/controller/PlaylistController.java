package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.service.PlaylistService;
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

    private final PlaylistService playlistService;

    @GetMapping("/youtube")
    public String createPlaylistFromYoutube(HttpServletRequest request, @ModelAttribute List<Playlist> playlists) {


        return null;
    }

}
