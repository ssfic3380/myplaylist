package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.MemberProfile;
import com.mypli.myplaylist.domain.Role;
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
    void saveMember() {
        //given
        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .socialCode(SocialCode.GOOGLE)
                .socialId("Google-abc123")
                .socialAccessToken("a1b2c3d4e5f6g7")
                .jwtRefreshToken("0z9y8x7w6p5v4u").build();

        MemberProfile memberProfile = MemberProfile.builder()
                .name("user1")
                .email("abc123@gmail.com").build();

        member.setMemberProfile(memberProfile);

        //when
        Long savedId = memberRepository.save(member).getId();

        //then
        Member findMember = memberRepository.findById(savedId).get();
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void findByEmail() {

        //given
        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .socialCode(SocialCode.GOOGLE)
                .socialId("Google-abc123")
                .socialAccessToken("a1b2c3d4e5f6g7")
                .jwtRefreshToken("0z9y8x7w6p5v4u").build();

        MemberProfile memberProfile = MemberProfile.builder()
                .name("user1")
                .email("abc123@gmail.com").build();

        member.setMemberProfile(memberProfile);

        //when
        String savedEmail = memberRepository.save(member).getMemberProfile().getEmail();

        //then
        Member findMember = memberRepository.findByEmail(savedEmail).get();
        assertThat(findMember).isEqualTo(member);
    }

}