package com.mypli.myplaylist.oauth2.jwt;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.utils.CookieUtils;
import com.mypli.myplaylist.oauth2.user.UserDetailsServiceImpl;
import com.mypli.myplaylist.repository.MemberRepository;
import com.mypli.myplaylist.utils.HeaderUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtTokenService {

    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.secret-key}")
    private String secretKey;
    private Key key;
    
    private final Long ACCESS_PERIOD = 1000L * 60L * 10L;
    private final Long REFRESH_PERIOD = 1000L * 60L * 60L * 24L * 30L * 3L;
    private final int COOKIE_PERIOD = 60 * 60 * 24; //하루

    private final String REFRESH_TOKEN = "refresh-token";
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken generateToken(String uid, String role) {
        Claims claims = Jwts.claims().setSubject(uid);
        claims.put("role", role);

        Date now = new Date();

        return new JwtToken(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + ACCESS_PERIOD))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact(),
                Jwts.builder() //TODO: refresh token은 claims 다른걸로
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + REFRESH_PERIOD))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact());
    }

    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build().parseClaimsJws(token);

            return claims.getBody()
                    .getExpiration()
                    .after(new Date()); //만료 기간이 현재시간 이후여야 유효
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean verifyRefreshToken(String token) {
        if (!verifyToken(token)) return false; //만료 기간 확인

        String oauthId = getUid(token);

        Optional<Member> optionalMember = memberRepository.findBySocialId(oauthId);
        if (!optionalMember.isPresent()) return false; //유저 존재 확인
        else return token.equals(optionalMember.get().getJwtRefreshToken()); //유저가 존재하면 refresh token 비교
    }

    public String getUid(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    public Authentication createAuthentication(String uid) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
        log.info("JwtTokenService::createAuthentication userDetails = {}", userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getAccessToken(HttpServletRequest request) {
        return HeaderUtils.getAccessToken(request);
    }

    public String getRefreshToken(HttpServletRequest request) {
        return CookieUtils.getCookie(request, REFRESH_TOKEN)
                .map(cookie -> cookie.getValue())
                .orElse(null);
    }

    public void responseAccessToken(HttpServletResponse response, String accessToken) {
        HeaderUtils.setAccessToken(response, accessToken);
    }

    public void responseRefreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, refreshToken, COOKIE_PERIOD);
    }
}
