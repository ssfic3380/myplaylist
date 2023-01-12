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
    private Authority authority; //[ROLE_USER(Default), ROLE_ADMIN]

    @Enumerated(EnumType.STRING)
    private SocialCode socialCode; //[IDPW, GOOGLE(Default), NAVER, KAKAO]

    @NotNull @Size(max = 64)
    private String socialId;

    @NotNull @Size(max = 255)
    private String socialAccessToken;

    @NotNull @Size(max = 255)
    private String jwtRefreshToken;

    //==연관관계==//
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberProfile memberProfile;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Playlist> playlists = new ArrayList<>();


    //==생성 메서드==//
    @Builder
    public Member(Authority authority, SocialCode socialCode, String socialId, String socialAccessToken, String jwtRefreshToken) {
        this.authority = authority;
        this.socialCode = socialCode;
        this.socialId = socialId;
        this.socialAccessToken = socialAccessToken;
        this.jwtRefreshToken = jwtRefreshToken;
    }
}
