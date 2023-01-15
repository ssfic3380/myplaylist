package com.mypli.myplaylist.service;

import com.mypli.myplaylist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //==조회==//
    /**
     * 전체 회원 조회
     */

    /**
     * 이메일로 회원 1명 조회
     */


    //==변경==//
    /**
     * 회원 정보 변경(닉네임, 이메일)
     * (*) 변경 감지를 활용해야 함
     */
    
    
    //==삭제==//
    /**
     * 회원 탈퇴
     */
}
