package com.mypli.myplaylist.service;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.Music;
import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.*;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.exception.MusicNotFoundException;
import com.mypli.myplaylist.exception.NoPermissionException;
import com.mypli.myplaylist.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final MemberService memberService;
    private final PlaylistService playlistService;

    //==생성==//
    /**
     * 노래 생성
     */
    @Transactional
    public Long create(String socialId, CreateMusicDto createMusicDto) {

        // 1. 권한 체크
        Member member = memberService.findBySocialId(socialId);
        Playlist playlist = playlistService.findById(createMusicDto.getPlaylistId());
        checkAuthority(member, playlist); // TODO: 404 NOT FOUND가 날아가고, errorMessage가 NoPermission인지 확인

        // 2. 노래 생성
        Music newMusic = Music.createMusic(
                playlist,
                createMusicDto.getTitle(),
                createMusicDto.getArtist(),
                createMusicDto.getAlbum(),
                createMusicDto.getVideoId(),
                createMusicDto.getMusicImg(),
                createMusicDto.getMusicOrder());

        return musicRepository.save(newMusic).getId();
    }

    /**
     * 유튜브 플레이리스트의 아이템을 추가
     */
    @Transactional
    public Long importFromYoutube(String socialId, AddMusicDto addMusicDto, List<YoutubePlaylistItemDto> youtubePlaylistItemDtoList) {

        // 1. 권한 체크
        Member member = memberService.findBySocialId(socialId);
        Playlist playlist = playlistService.findById(addMusicDto.getPlaylistId());
        checkAuthority(member, playlist);

        // 2. 플레이리스트의 이름, 이미지 변경
        playlist.updatePlaylistName(addMusicDto.getYoutubePlaylistName());
        playlist.updatePlaylistImg(addMusicDto.getYoutubePlaylistImg());

        Long musicOrder = addMusicDto.getLastMusicOrder();
        for (YoutubePlaylistItemDto youtubePlaylistItemDto : youtubePlaylistItemDtoList) {
            Music.createMusic(
                    playlist,
                    youtubePlaylistItemDto.getTitle(),
                    youtubePlaylistItemDto.getArtist(),
                    youtubePlaylistItemDto.getAlbum(),
                    youtubePlaylistItemDto.getVideoId(),
                    youtubePlaylistItemDto.getThumbnail(),
                    musicOrder);

            musicOrder++;
        }

        return playlist.getId();
    }


    //==조회==//
    /**
     * "노래 아이디"로 조회(1개)
     */
    public Music findById(Long musicId) {
        return musicRepository.findById(musicId).orElseThrow(MusicNotFoundException::new);
    }
    
    /**
     * "플레이리스트 아이디"로 조회(1개 -> n개)
     */
    public List<Music> findByPlaylistId(Long playlistId) {
        return musicRepository.findByPlaylistIdOrderByMusicOrder(playlistId);
    }

    /**
     * "노래 제목"으로 조회(m개)
     */
    public List<Music> findByTitle(String title) {
        return musicRepository.findByTitle(title);
    }

    /**
     * "가수 이름"으로 조회(m개)
     */
    public List<Music> findByArtist(String artist) {
        return musicRepository.findByArtist(artist);
    }

    /**
     * "노래 앨범명"으로 조회(m개)
     */
    public List<Music> findByAlbum(String album) {
        return musicRepository.findByArtist(album);
    }

    /**
     * "노래 아이디"로 플레이리스트 아이디 조회(1개)
     */
    public Long findPlaylistIdById(Long musicId) {
        Music music = musicRepository.findById(musicId).orElseThrow(MusicNotFoundException::new);
        return music.getPlaylist().getId();
    }


    //==변경==//
    /**
     * 노래 정보 변경
     */
    @Transactional
    public Long update(String socialId, UpdateMusicDto updateMusicDto) {
        // 1. 권한 체크
        Member member = memberService.findBySocialId(socialId);
        Playlist playlist = playlistService.findById(updateMusicDto.getPlaylistId());
        checkAuthority(member, playlist);

        // 2. 노래 정보 변경
        Music music = findById(updateMusicDto.getMusicId());

        music.updateTitle(updateMusicDto.getTitle());
        music.updateArtist(updateMusicDto.getArtist());
        music.updateAlbum(updateMusicDto.getAlbum());
        music.updateVideoId(updateMusicDto.getVideoId());
        music.updateMusicOrder(updateMusicDto.getMusicOrder());

        return music.getId();
    }

    /**
     * 노래 순서 변경
     */
    @Transactional
    public void updateOrder(String socialId, UpdateMusicOrderDto updateMusicOrderDto) {
        // 1. 권한 체크
        Member member = memberService.findBySocialId(socialId);
        Playlist playlist = playlistService.findById(updateMusicOrderDto.getPlaylistId());
        checkAuthority(member, playlist);

        // 2. 노래 순서 변경
        Long musicIds[] = updateMusicOrderDto.getMusicIds();
        for (int i = 0; i < musicIds.length; i++) { //TODO: 시간복잡도 개선 필요
            Music music = findById(musicIds[i]);
            music.updateMusicOrder((long) i + 1);
        }
    }


    /**
     * 노래 순서 변경(노래 2개 맞바꾸기)
     */
    /*@Transactional
    public Long[] exchangeOrder(String socialId, UpdateMusicOrderDto updateMusicOrderDto) {
        // 1. 권한 체크
        Member member = memberService.findBySocialId(socialId);
        Playlist playlist = playlistService.findById(updateMusicOrderDto.getPlaylistId());
        checkAuthority(member, playlist);

        // 2. 노래 순서 변경
        Music music1 = findById(updateMusicOrderDto.getMusicId1());
        Music music2 = findById(updateMusicOrderDto.getMusicId2());

        music1.updateMusicOrder(updateMusicOrderDto.getMusicOrder1());
        music2.updateMusicOrder(updateMusicOrderDto.getMusicOrder2());

        return new Long[] {music1.getId(), music2.getId()};
    }*/


    //==삭제==//
    /**
     * 노래 삭제
     */
    @Transactional
    public void deleteById(Long musicId) {
        Music music = findById(musicId);
        music.deleteMusic();
        musicRepository.deleteById(musicId);
    }


    //==권한 체크==//
    private void checkAuthority(Member member, Playlist playlist) {
        if (member.getId() != playlist.getMember().getId()) throw new NoPermissionException();
    }
}
