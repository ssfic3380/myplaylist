package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public Member findBySocialId(String socialId);

    //public Member findByEmail(String email);
}
