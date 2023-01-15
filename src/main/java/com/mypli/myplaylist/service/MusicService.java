package com.mypli.myplaylist.service;

import com.mypli.myplaylist.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    //==생성==//
    /**
     * 노래 생성
     */


    //==조회==//
    /**
     * 전체 노래 조회(N개)
     */


    /**
     * "플레이리스트 번호"로 노래 조회(1개 -> n개)
     */


    /**
     * "노래 제목"으로 노래 조회(m개)
     */


    /**
     * "가수 이름"으로 노래 조회(m개)
     */


    /**
     * "노래 앨범명"으로 노래 조회(m개)
     */


    /**
     * "유튜브 url"로 노래 조회(1개)
     */


    //==변경==//
    /**
     * 노래 순서 변경
     */


    /**
     * 노래 정보 변경(제목, 이름, 앨범명)
     */
    
    
    //==삭제==//
    /**
     * 노래 삭제
     */
}
