package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;


}
