package com.mypli.myplaylist.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_id")
    private Long id;

    @Column(unique = true)
    @Size(max = 12)
    private String name;

    @Size(max = 128)
    private String email;

    private String picture;

    private LocalDateTime updateDate; // Default = CURRENT_TIMESTAMP

    private LocalDateTime joinDate;

    //==연관관계==//
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
    }

    //==생성 메서드==//
    @Builder
    public MemberProfile(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    //==비즈니스 메서드==//
    public void updateProfile(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }
}
