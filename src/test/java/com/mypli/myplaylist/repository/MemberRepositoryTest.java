package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Authority;
import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.SocialCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void save() {
        //given
        Member member = Member.builder()
                .authority(Authority.ROLE_USER)
                .socialCode(SocialCode.GOOGLE)
                .socialId("Google-abc123")
                .socialAccessToken("a1b2c3d4e5f6g7")
                .jwtRefreshToken("0z9y8x7w6p5v4u").build();

        //when
        Member savedMember = memberRepository.save(member);

        //then
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember).isEqualTo(member);
    }
}