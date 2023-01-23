package com.mypli.myplaylist.oauth2.userdetails;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class UserDetailsImpl implements UserDetails {

    private Member member;

    public Member getMember() {
        return this.member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * UserDetails
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(member.getRole().getKey()));
        return auth;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return member.getSocialId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
