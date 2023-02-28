package com.mypli.myplaylist.service;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.exception.MemberNotFoundException;
import com.mypli.myplaylist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * socialId로 회원 1명 조회
     */
    public Member findBySocialId(String socialId) {
        Member member = memberRepository.findBySocialId(socialId).orElseThrow(MemberNotFoundException::new);
        return member;
    }

    //==변경==//
    /**
     * JwtRefreshToken 변경 => TODO: token refresh를 JWTFilter에서만 해주면 필요 없는 메서드
     */
    @Transactional
    public void updateJwtRefreshToken(String socialId, String refreshToken) {
        Member member = memberRepository.findBySocialId(socialId).orElseThrow(MemberNotFoundException::new);
        member.updateJwtRefreshToken(refreshToken);
    }


    //==삭제==//
    /**
     * 회원 탈퇴 (Cascade에 의해 memberProfile, Playlist, Music 모두 삭제됨)
     */
    @Transactional
    public void deleteById(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
