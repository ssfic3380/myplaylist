package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.MemberProfile;
import com.mypli.myplaylist.domain.Role;
import com.mypli.myplaylist.domain.SocialCode;
import com.mypli.myplaylist.exception.MemberNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 저장 확인")
    void saveMember() {

        //given
        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .socialCode(SocialCode.GOOGLE)
                .socialId("socialid")
                .socialAccessToken("socialaccesstoken")
                .socialRefreshToken("socialrefreshtoken")
                .jwtRefreshToken("jwtrefreshtoken").build();

        MemberProfile memberProfile = MemberProfile.builder()
                .name("가나다")
                .picture("https://image.com")
                .email("email@email.com")
                .build();

        member.setMemberProfile(memberProfile);

        //when
        Member savedMember = memberRepository.save(member);

        //then
        assertThat(savedMember).isEqualTo(member);
        assertThat(savedMember.getId()).isEqualTo(member.getId());
        assertThat(savedMember.getRole()).isEqualTo(member.getRole());
        assertThat(savedMember.getSocialCode()).isEqualTo(member.getSocialCode());
        assertThat(savedMember.getSocialId()).isEqualTo(member.getSocialId());
        assertThat(savedMember.getSocialAccessToken()).isEqualTo(member.getSocialAccessToken());
        assertThat(savedMember.getSocialRefreshToken()).isEqualTo(member.getSocialRefreshToken());
        assertThat(savedMember.getJwtRefreshToken()).isEqualTo(member.getJwtRefreshToken());
        assertThat(savedMember.getMemberProfile()).isEqualTo(memberProfile);

        assertThat(savedMember.getMemberProfile().getId()).isEqualTo(member.getMemberProfile().getId());
        assertThat(savedMember.getMemberProfile().getName()).isEqualTo(member.getMemberProfile().getName());
        assertThat(savedMember.getMemberProfile().getEmail()).isEqualTo(member.getMemberProfile().getEmail());
        assertThat(savedMember.getMemberProfile().getPicture()).isEqualTo(member.getMemberProfile().getPicture());
        assertThat(savedMember.getMemberProfile().getUpdateDate()).isEqualTo(member.getMemberProfile().getUpdateDate());
        assertThat(savedMember.getMemberProfile().getJoinDate()).isEqualTo(member.getMemberProfile().getJoinDate());
        assertThat(savedMember.getMemberProfile().getMember()).isEqualTo(member);
    }

    @DisplayName("멤버 socialId로 조회 확인")
    @Test
    void findBySocialId() {

        //given
        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .socialCode(SocialCode.GOOGLE)
                .socialId("socialid")
                .socialAccessToken("socialaccesstoken")
                .socialRefreshToken("socialrefreshtoken")
                .jwtRefreshToken("jwtrefreshtoken").build();

        MemberProfile memberProfile = MemberProfile.builder()
                .name("가나다")
                .picture("https://image.com")
                .email("email@email.com").build();

        member.setMemberProfile(memberProfile);
        Member savedMember = memberRepository.save(member);

        //when
        Member foundMemberBySocialId = memberRepository.findBySocialId(savedMember.getSocialId())
                .orElseThrow(() -> new MemberNotFoundException("Member Not found: socialId = " + savedMember.getSocialId()));

        //then
        assertThat(memberRepository.count()).isEqualTo(1);
        assertThat(foundMemberBySocialId.getId()).isEqualTo(1);
        assertThat(foundMemberBySocialId.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(foundMemberBySocialId.getSocialCode()).isEqualTo(SocialCode.GOOGLE);
        assertThat(foundMemberBySocialId.getSocialId()).isEqualTo("socialid");
        assertThat(foundMemberBySocialId.getSocialAccessToken()).isEqualTo("socialaccesstoken");
        assertThat(foundMemberBySocialId.getSocialRefreshToken()).isEqualTo("socialrefreshtoken");
        assertThat(foundMemberBySocialId.getJwtRefreshToken()).isEqualTo("jwtrefreshtoken");

        assertThat(foundMemberBySocialId.getMemberProfile().getId()).isEqualTo(1);
        assertThat(foundMemberBySocialId.getMemberProfile().getName()).isEqualTo("가나다");
        assertThat(foundMemberBySocialId.getMemberProfile().getEmail()).isEqualTo("email@email.com");
        assertThat(foundMemberBySocialId.getMemberProfile().getPicture()).isEqualTo("https://image.com");
        assertThat(foundMemberBySocialId.getMemberProfile().getUpdateDate()).isNull();
        assertThat(foundMemberBySocialId.getMemberProfile().getJoinDate()).isNull();
    }

    @DisplayName("멤버 잘못된 socialId로 조회 에러 확인")
    @Test
    void findBySocialIdError() {
        //given
        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .socialCode(SocialCode.GOOGLE)
                .socialId("socialid")
                .socialAccessToken("socialaccesstoken")
                .socialRefreshToken("socialrefreshtoken")
                .jwtRefreshToken("jwtrefreshtoken").build();

        MemberProfile memberProfile = MemberProfile.builder()
                .name("가나다")
                .picture("https://image.com")
                .email("email@email.com").build();

        member.setMemberProfile(memberProfile);
        Member savedMember = memberRepository.save(member);

        //when
        //then
        Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberRepository.findBySocialId("wrongid").orElseThrow(MemberNotFoundException::new);
        });
    }
}