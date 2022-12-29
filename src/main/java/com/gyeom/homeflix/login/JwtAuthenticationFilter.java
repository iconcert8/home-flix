package com.gyeom.homeflix.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Request Header or Cookie 에서 JWT 토큰 추출
//        String token = resolveToken(request);
        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            Optional<Cookie> accessTokenCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(JwtProperties.ACCESS_TOKEN_HEADER)).findFirst();
            if(accessTokenCookie.isPresent()){
               token = accessTokenCookie.get().getValue();
            }
        }

        // 2. validateToken 으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            saveSecurityContext(token);
        }
        // if expired accessToken. refresh.
        else{
            if(cookies != null){
                Optional<Cookie> refreshTokenCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(JwtProperties.REFRESH_TOKEN_HEADER)).findFirst();
                if(refreshTokenCookie.isPresent()) {
                    try{
                        String refreshToken = refreshTokenCookie.get().getValue();
                        String accessToken = jwtTokenProvider.generateAccessToken(jwtTokenProvider.parseSubject(refreshToken), User.defaultAuthorities());
                        response.addCookie(JwtTokenProvider.createAccessTokenCookie(accessToken));
                        response.addCookie(JwtTokenProvider.createAccessTokenExpireTimeCookie(new Date(new Date().getTime()+JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME)));
                        saveSecurityContext(accessToken);
                    }catch (Exception e){
                        filterChain.doFilter(request, response);
                        return;
                    }
                }
                else{
                    // refresh-token is empty. should go to the login page.
                    request.setAttribute(JwtProperties.REQUIERED_LOGIN, true);
                }
            }else{
                // refresh-token is empty. should go to the login page.
                request.setAttribute(JwtProperties.REQUIERED_LOGIN, true);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void saveSecurityContext(String accessToken){
        // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
