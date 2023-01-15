package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
