package com.mypli.myplaylist.repository;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findBySocialId(String socialId);

    @Query("select m from Member m join fetch m.memberProfile mp where mp.email like :email")
    Optional<Member> findByEmail(@Param("email") String email);
}
