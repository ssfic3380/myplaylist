package com.mypli.myplaylist.service;

import com.mypli.myplaylist.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    //==생성==//
    /**
     * 플레이리스트 생성
     */

    /**
     * 유튜브에서 플레이리스트 가져오기
     */


    //==조회==//
    /**
     * 전체 플레이리스트 조회 (N개)
     */


    /**
     * "회원 번호"로 플레이리스트 조회 (1명 -> n개)
     */


    /**
     * "플레이리스트 이름"으로 조회 (1개)
     */


    //==변경==//
    /**
     * 플레이리스트 정보 변경(이름, 이미지)
     */
    
    
    //==삭제==//
    /**
     * 플레이리스트 삭제
     */
}
