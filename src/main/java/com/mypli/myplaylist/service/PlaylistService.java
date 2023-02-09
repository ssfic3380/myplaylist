package com.mypli.myplaylist.service;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.Music;
import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.PlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.exception.MemberNotFoundException;
import com.mypli.myplaylist.exception.PlaylistNotFoundException;
import com.mypli.myplaylist.repository.MemberRepository;
import com.mypli.myplaylist.repository.MusicRepository;
import com.mypli.myplaylist.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistService {

    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;

    //==생성==//
    @Transactional
    public Long create(String socialId, PlaylistDto playlistDto) {

        Member member = memberRepository.findBySocialId(socialId).orElseThrow(MemberNotFoundException::new);

        if (playlistDto.getPlaylistImg() == null) {
            //TODO: 이미지가 없을 때, 기본 이미지를 넣어줘야 함
        }
        Playlist newPlaylist = Playlist.createPlaylist(member, playlistDto.getPlaylistName(), playlistDto.getPlaylistImg());

        return playlistRepository.save(newPlaylist).getId();
    }

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
     *    4-3. Post할 때 YoutubePlaylistDTO를 query parameter로 전달
     *    4-4. socialId를 이용하여 getPlaylistItems(socialId, youtubePlaylistId)를 호출해서 DTO를 만들어서
     *         PlaylistService의 createPlaylistFromYoutube(...)로 넘김
     *    4-5. PlaylistService는 MusicService를 호출해서 title, artist, album=null, videoId, order(1부터시작)으로 각각을 Music으로 만들고,
     *         최종적으로 Playlist 하나를 만들어서 그 안에 MusicId를 받아와서 넣어줌
     *    4-6. PlaylistId를 return해주면 Member.addPlaylist(playlist) 호출
     * 5. 프론트에서 해당 플레이리스트를 그려주고 끝
     *
     */
    @Transactional
    public Long importFromYoutube(String socialId, YoutubePlaylistDto youtubePlaylistDto, List<YoutubePlaylistItemDto> youtubePlaylistItemDtoList) {

        Member member = memberRepository.findBySocialId(socialId).orElseThrow(MemberNotFoundException::new);

        Playlist newPlaylist = Playlist.createPlaylist(member, youtubePlaylistDto.getTitle(), youtubePlaylistDto.getThumbnail());

        Long musicOrder = 1L;
        for (YoutubePlaylistItemDto youtubePlaylistItemDto : youtubePlaylistItemDtoList) {
            Music.createMusic(newPlaylist,
                    youtubePlaylistItemDto.getTitle(),
                    youtubePlaylistItemDto.getArtist(),
                    youtubePlaylistItemDto.getAlbum(),
                    youtubePlaylistItemDto.getVideoId(),
                    youtubePlaylistItemDto.getThumbnail(),
                    musicOrder);

            musicOrder++;
        }

        return playlistRepository.save(newPlaylist).getId();
    }

    public void exportToYoutube(String socialId, YoutubePlaylistDto youtubePlaylistDto, List<YoutubePlaylistItemDto> youtubePlaylistItemDtoList) {

    }


    //==조회==//
    /**
     * 전체 플레이리스트 조회 (N개) => TODO: 자기꺼만 보기때문에 필요 없는 메서드로 보임
     */
    public List<Playlist> findAll() {
        return playlistRepository.findAll();
    }

    /**
     * "플레이리스트 아이디"로 조회(1개)
     */
    public Playlist findById(String playlistId) {
        return playlistRepository.findById(Long.parseLong(playlistId)).orElseThrow(PlaylistNotFoundException::new);
    }

    /**
     * "회원 번호"로 플레이리스트 조회(1명 -> n개)
     */
    public List<Playlist> findBySocialId(String socialId) {
        return playlistRepository.findBySocialId(socialId);
    }

    /**
     * "플레이리스트 이름"으로 조회(n개) => 중복 이름 있으니까
     */
    public List<Playlist> findByName(String playlistName) {
        return playlistRepository.findByPlaylistName(playlistName);
    }


    //==삭제==//
    /**
     * 플레이리스트 삭제
     */
    @Transactional
    public void deleteById(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(PlaylistNotFoundException::new);
        playlist.deletePlaylist();
        playlistRepository.deleteById(playlistId);
    }
}
