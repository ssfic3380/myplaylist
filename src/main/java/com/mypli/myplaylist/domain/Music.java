package com.mypli.myplaylist.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;

    //테이블 자체가 Music에 대한 정보라서 임베디드 타입을 사용하지 않음
    @NotNull @Size(max = 128)
    private String title;

    @NotNull @Size(max = 128)
    private String artist;

    @Size(max = 128)
    private String album;

    @NotNull @Size(max = 255)
    private String videoId;

    @Size(max = 255)
    private String musicImg;

    @NotNull
    private Long musicOrder; //플레이리스트에서의 출력 순서 정보

    //==연관관계==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;


    //==연관관계 메서드==//
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        playlist.getMusicList().add(this);
    }

    //==생성 메서드==//
    @Builder
    public Music(String title, String artist, String album, String videoId, String musicImg, Long musicOrder) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.videoId = videoId;
        this.musicImg = musicImg;
        this.musicOrder = musicOrder;
    }

    public static Music createMusic(Playlist playlist, String title, String artist, String album, String videoId, String musicImg, Long musicOrder) {
        Music music = Music.builder()
                .title(title)
                .artist(artist)
                .album(album)
                .videoId(videoId)
                .musicImg(musicImg)
                .musicOrder(musicOrder)
                .build();

        music.setPlaylist(playlist);

        return music;
    }

    //==비즈니스 로직==//
    /**
     * Music 삭제
     */
    public void deleteMusic() {
        this.getPlaylist().getMusicList().remove(this);
    }

    /**
     * Title 변경
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    /**
     * Artist 변경
     */
    public void updateArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Album 변경
     */
    public void updateAlbum(String album) {
        this.album = album;
    }

    /**
     * VideoId 변경
     */
    public void updateVideoId(String videoId) {
        this.videoId = videoId;
    }

    /**
     * MusicImg 변경
     */
    public void updateMusicImg(String musicImg) {
        this.musicImg = musicImg;
    }

    /**
     * MusicOrder 변경
     */
    public void updateMusicOrder(Long musicOrder) {
        this.musicOrder = musicOrder;
    }
}
