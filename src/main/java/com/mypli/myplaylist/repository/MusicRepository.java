package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {


    List<Music> findByPlaylistId(Long playlistId);

    List<Music> findByPlaylistIdOrderByMusicOrder(Long playlistId);

    List<Music> findByTitle(String title);

    List<Music> findByArtist(String artist);

    List<Music> findByAlbum(String album);
}
