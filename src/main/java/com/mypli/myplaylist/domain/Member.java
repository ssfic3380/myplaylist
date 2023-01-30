package com.mypli.myplaylist.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    //@NotNull = @Column(nullable = false) + Spring Validation
    //@Size(max = 64) = @Column(length = 64) + Spring Validation
    @Enumerated(EnumType.STRING)
    private Role role; //[ROLE_GUEST, ROLE_USER(Default), ROLE_ADMIN]

    @Enumerated(EnumType.STRING)
    private SocialCode socialCode; //[LOCAL, GOOGLE(Default), NAVER, KAKAO]

    @NotNull @Size(max = 64)
    private String socialId;

    @NotNull @Size(max = 255)
    private String socialAccessToken;

    @NotNull @Size(max = 255)
    private String socialRefreshToken;

    @NotNull @Size(max = 255)
    private String jwtRefreshToken;

    //==연관관계==//
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MemberProfile memberProfile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Playlist> playlists = new ArrayList<>();


    //==연관관계 메서드==//
    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
        memberProfile.setMember(this);
    }

    //==생성 메서드==//
    @Builder
    public Member(Role role, SocialCode socialCode, String socialId, String socialAccessToken, String socialRefreshToken, String jwtRefreshToken, MemberProfile memberProfile) {
        this.role = role;
        this.socialCode = socialCode;
        this.socialId = socialId;
        this.socialAccessToken = socialAccessToken;
        this.socialRefreshToken = socialRefreshToken;
        this.jwtRefreshToken = jwtRefreshToken;
        this.memberProfile = memberProfile;
    }

    //==비즈니스 로직==//
    /**
     * Social Access Token 변경
     */
    public void updateSocialAccessToken(String socialAccessToken) {
        this.socialAccessToken = socialAccessToken;
    }

    /**
     * Social Refresh Token 변경
     */
    public void updateSocialRefreshToken(String socialRefreshToken) {
        this.socialRefreshToken = socialRefreshToken;
    }

    /**
     * JWT Refresh Token 변경
     */
    public void updateJwtRefreshToken(String jwtRefreshToken) {
        this.jwtRefreshToken = jwtRefreshToken;
    }

    /**
     * 회원정보 변경
     */
    public Member updateProfile(String name, String email, String picture) {
        memberProfile.updateProfile(name, email, picture);
        return this;
    }

}
