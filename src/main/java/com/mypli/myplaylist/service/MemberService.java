package com.mypli.myplaylist.service;

import com.mypli.myplaylist.domain.Authority;
import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.SocialCode;
import com.mypli.myplaylist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 임시용
     */
    @Transactional
    public Long join() {
        Member member = Member.builder()
                .authority(Authority.ROLE_USER)
                .socialCode(SocialCode.GOOGLE)
                .socialId("Google-abc123")
                .socialAccessToken("a1b2c3d4e5f6g7")
                .jwtRefreshToken("0z9y8x7w6p5v4u").build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

}
