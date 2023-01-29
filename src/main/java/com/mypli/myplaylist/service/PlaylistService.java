package com.mypli.myplaylist.service;

import com.mypli.myplaylist.dto.PlaylistDto;
import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.repository.PlaylistRepository;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.service.youtube.YoutubeSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final YoutubePlaylistsService youtubePlaylistsService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;
    private final YoutubeSearchService youtubeSearchService;

    //==생성==//
    /**
     * 유튜브에서 플레이리스트 가져오기
     * 1. 프론트에서 "유튜브에서 불러오기" 클릭
     * 2. Controller에서 GetMapping
     *    2-1. Request에서 JwtAccessToken을 추출
     *    2-2. JwtAccessToken에서 socialId 추출
     *    2-3. socialId를 이용하여 YoutubePlaylistsService의 getPlaylists(socialId)를 호출해서 DTO를 만들어서 modelAttribute로 넘김
     * 3. 프론트에서 팝업창에 <table>로 내 유튜브 플레이리스트 목록을 그려주고, 클릭하면 해당 플레이리스트의 id, 이름, 썸네일을 PostMapping으로 넘김
     * 4. Controller에서 PostMapping
     *    4-1. Reqeust에서 JwtAccessToken을 추출
     *    4-2. JwtAccessToken에서 socialId 추출
     *    4-3. Post할 때 playlistId를 query parameter로 전달
     *    4-4. socialId를 이용하여 getPlaylistItems(socialId, youtubePlaylistId)를 호출해서 DTO를 만들어서
     *         PlaylistService의 createPlaylistFromYoutube(...)로 넘김
     *    4-5. PlaylistService는 MusicService를 호출해서 title, artist, album=null, videoId, order(1부터시작)으로 각각을 Music으로 만들고,
     *         최종적으로 Playlist 하나를 만들어서 그 안에 MusicId를 받아와서 넣어줌
     *    4-6. PlaylistId를 return해주면 Member.addPlaylist(playlist) 호출
     * 5. 프론트에서 해당 플레이리스트를 그려주고 끝
     *
     */
    public Long createPlaylistFromYoutube(String socialId, String title, String videoOwnerChannelTitle, String resourceId) {

        Playlist newPlaylist = Playlist.builder()
                .playlistName("asdf")
                .playlistImg("zxcv")
                .build();

        return newPlaylist.getId();
    }

    public void savePlaylistToYoutube(String socialId) {

    }


    //==조회==//
    /**
     * 전체 플레이리스트 조회 (N개)
     */
    public List<Playlist> findPlaylists() {
        return playlistRepository.findAll();
    }

    /**
     * "회원 번호"로 플레이리스트 조회 (1명 -> n개)
     */
    public Optional<Playlist> findBySocialId(String socialId) {
        return playlistRepository.findBySocialId(socialId);
    }

    /**
     * "플레이리스트 이름"으로 조회 (1개)
     */
    public Playlist findByPlaylistName(String playlistName) {
        return playlistRepository.findByPlaylistName(playlistName);
    }


    //==삭제==//
    /**
     * 플레이리스트 삭제
     */
    @Transactional
    public void deletePlaylist(Long playlistId) {
        playlistRepository.deleteById(playlistId);
    }
}
