package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.service.MusicService;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PlaylistControllerTest {

    @InjectMocks
    private PlaylistController playlistController;

    @Mock
    private PlaylistService playlistService;
    @Mock
    private MusicService musicService;
    @Mock
    private YoutubePlaylistItemsService youtubePlaylistItemService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController).build();
    }



}