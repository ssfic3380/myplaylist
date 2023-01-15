package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {
}
