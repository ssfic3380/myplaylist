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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_profile_id")
    private Long id;

    @Column(unique = true)
    @Size(max = 12)
    private String nickname;

    @Size(max = 128)
    private String email;

    private LocalDateTime updateDate; // Default = CURRENT_TIMESTAMP

    private LocalDateTime joinDate;

    //==연관관계==//
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;


    //==생성 메서드==//
    @Builder
    public MemberProfile(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
