package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("select p from Playlist p join fetch p.member m where m.socialId like :socialId")
    Optional<Playlist> findBySocialId(@Param("socialId") String socialId);

    Playlist findByPlaylistName(String playlistName);
}
