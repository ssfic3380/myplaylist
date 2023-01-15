package com.mypli.myplaylist.config;

import com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository;
import com.mypli.myplaylist.oauth2.jwt.JwtAuthFilter;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenService;
import com.mypli.myplaylist.oauth2.service.CustomOAuth2AuthService;
import com.mypli.myplaylist.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.mypli.myplaylist.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.mypli.myplaylist.oauth2.service.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2AuthService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtTokenService tokenService;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                    .formLogin().disable()
                    .csrf().disable()
                    .cors()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    //.antMatchers("/login", "/social").permitAll()
                    //.anyRequest().authenticated()
                    .anyRequest().permitAll()
                .and()
                    .oauth2Login()//.loginPage("/token/expired")
                        .authorizationEndpoint()
                        .baseUri("/oauth2/authorization")
                        .authorizationRequestRepository(authorizationRequestRepository)
                .and()
                        .redirectionEndpoint()
                        .baseUri("/*/oauth2/code/*")
                .and()
                        .userInfoEndpoint()
                        .oidcUserService(customOidcUserService)
                        .userService(customOAuth2UserService)
                .and()
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler);

        http.addFilterBefore(new JwtAuthFilter(tokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**", "/h2-console/**");
    }
}
