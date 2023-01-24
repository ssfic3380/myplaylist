package com.mypli.myplaylist.service;

import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.repository.PlaylistRepository;
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

    //==생성==//
    /**
     * 유튜브에서 플레이리스트 가져와서 저장하기
     * 1. 프론트에서 "유튜브에서 불러오기" 클릭
     * 2. Controller에서 GetMapping
     *    2-1. Request에서 JwtAccessToken을 추출
     *    2-2. JwtAccessToken에서 socialId 추출
     *    2-3. MemberService에서 socialId를 이용하여 getSocialAccessToken을 호출하여 SocialAccessToken을 받아옴
     *    2-4.
     * 3. PlaylistService에서
     *    3-1.
     *    3-2. SocialAccessToken을 이용하여 유튜브 API(playlist.list)를 호출해서
     */
    public void createPlaylistFromYoutube(String socialId) {
        //유튜브에서 플레이리스트 가져오기

        //Playlist 생성


        

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
