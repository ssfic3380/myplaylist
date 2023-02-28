package com.mypli.myplaylist.oauth2.userdetails;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberService.findBySocialId(username);
        if (member == null) throw new UsernameNotFoundException("No Found Member: " + username);

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setMember(member);

        return userDetails;
    }
}
