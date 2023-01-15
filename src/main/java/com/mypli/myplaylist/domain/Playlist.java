package com.mypli.myplaylist.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long id;

    @NotNull @Size(max = 128)
    private String playlistName;

    @Size(max = 255)
    private String playlistImg;

    private LocalDateTime updateDate;

    private LocalDateTime createDate;

    //==연관관계==//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL)
    private List<Music> musicList = new ArrayList<>();


    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
    }

    public void addMusic(Music music) {
        musicList.add(music);
        music.setPlaylist(this);
    }

    //==생성 메서드==//
    @Builder
    public Playlist(String playlistName, String playlistImg) {
        this.playlistName = playlistName;
        this.playlistImg = playlistImg;
    }
}
