package com.mypli.myplaylist.config;

import com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository;
import com.mypli.myplaylist.oauth2.exception.JwtAccessDeniedHandler;
import com.mypli.myplaylist.oauth2.exception.JwtAuthenticationEntryPoint;
import com.mypli.myplaylist.oauth2.jwt.JwtAuthFilter;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
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

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final CustomOidcUserService customOidcUserService;
    private final CustomOAuth2AuthService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    private final JwtTokenProvider tokenProvider;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**", "/favicon.ico", "/h2-console/**");
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
                    //.antMatchers("/auth/**").permitAll()
                    //.anyRequest().authenticated()
                    .anyRequest().permitAll()
//                .and()
//                    .exceptionHandling()
//                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                    .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                    .oauth2Login()//.loginPage("")
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

        http.addFilterBefore(new JwtAuthFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
