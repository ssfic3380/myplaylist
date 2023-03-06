package com.mypli.myplaylist.service;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.Music;
import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.CreateMusicDto;
import com.mypli.myplaylist.dto.MusicDto;
import com.mypli.myplaylist.exception.MusicNotFoundException;
import com.mypli.myplaylist.exception.NoPermissionException;
import com.mypli.myplaylist.exception.PlaylistNotFoundException;
import com.mypli.myplaylist.repository.MusicRepository;
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
        return musicRepository.findByPlaylistId(playlistId);
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
     * 노래 순서 변경(노래 2개 맞바꾸기)
     */
    @Transactional
    public Long[] exchangeOrder(Long musicId1, Long musicId2) {
        Music music1 = findById(musicId1);
        Music music2 = findById(musicId2);

        Long temp = music1.getMusicOrder();
        music1.updateMusicOrder(music2.getMusicOrder());
        music2.updateMusicOrder(temp);

        return new Long[] {music1.getId(), music2.getId()};
    }

    /**
     * 노래 순서 변경(맨 위 or 맨 아래로 옮기기)
     */
    @Transactional
    public Long updateOrderById(Long musicId, Long newOrder) {
        Music music = findById(musicId);
        music.updateMusicOrder(newOrder);

        return music.getId();
    }

    /**
     * 노래 정보 변경(제목)
     */
    @Transactional
    public Long updateTitleById(Long musicId, String newTitle) {
        Music music = findById(musicId);
        music.updateTitle(newTitle);

        return music.getId();
    }

    /**
     * 노래 정보 변경(가수)
     */
    @Transactional
    public Long updateArtistById(Long musicId, String newArtist) {
        Music music = findById(musicId);
        music.updateArtist(newArtist);

        return music.getId();
    }

    /**
     * 노래 정보 변경(앨범)
     */
    @Transactional
    public Long updateAlbumById(Long musicId, String newAlbum) {
        Music music = findById(musicId);
        music.updateAlbum(newAlbum);

        return music.getId();
    }

    /**
     * 노래 정보 변경(유튜브 주소)
     */
    @Transactional
    public Long updateVideoIdById(Long musicId, String newVideoId) {
        Music music = findById(musicId);
        music.updateVideoId(newVideoId);

        return music.getId();
    }

    /**
     * 노래 정보 변경(이미지)
     */
    @Transactional
    public Long updateImgById(Long musicId, String newImg) {
        Music music = findById(musicId);
        music.updateMusicImg(newImg);

        return music.getId();
    }


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
