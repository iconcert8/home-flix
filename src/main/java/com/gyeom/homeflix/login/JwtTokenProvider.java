package com.gyeom.homeflix.login;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private Key key = null;

    private Key getKey(){
        if(key == null){
            byte[] keyBytes = Decoders.BASE64.decode(JwtProperties.SECRET);
            key = Keys.hmacShaKeyFor(keyBytes);
        }

        return key;
    }

    // generate Access Token, RefreshToken
    public TokenInfo generateToken(Authentication authentication){
        Date nowDate = new Date();
        long now = nowDate.getTime();
        Date expireDate = new Date(now + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME);

        //Access Token
        String accessToken = generateAccessToken(authentication);

        //Refresh Token
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName()) // TODO: remove setSubject(). after save refreshToken in DB.
//                .setExpiration(new Date(now + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME)) // TODO: enable setExpiration().
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return new TokenInfo(accessToken, refreshToken, expireDate, authentication.getName());
    }

    public String generateAccessToken(Authentication authentication){
        return generateAccessToken(authentication.getName(), authentication.getAuthorities());
    }

    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities){
        Date nowDate = new Date();
        long now = nowDate.getTime();
        Date expireDate = new Date(now + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME);

        String authority = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //Access Token
        String accessToken = Jwts.builder()
                .setSubject(username)
                .claim(JwtProperties.TOKEN_AUTH, authority)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    // Decode JWT token and extract info in token.
    public Authentication getAuthentication(String accessToken) {
        // decode token
        Claims claims = parseClaims(accessToken);

        if(claims.getSubject() == null){
            throw new RuntimeException("Not exists subject in token");
        }

        if (claims.get(JwtProperties.TOKEN_AUTH) == null) {
            throw new RuntimeException("Not exists authority in token");
        }

        // get authority in claim info.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(JwtProperties.TOKEN_AUTH).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // create UserDetails object and return authentication
        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // verify token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String parseSubject(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody().getSubject();
        }catch (ExpiredJwtException e){
            log.warn("Can't not parse subject in token");
            throw e;
        }
    }

    public static Cookie createAccessTokenCookie(String accessToken){
        Cookie accessTokenCookie = new Cookie(JwtProperties.ACCESS_TOKEN_HEADER, accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int)(JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME/1000) + 5);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setHttpOnly(true);

        return accessTokenCookie;
    }

    public static Cookie createAccessTokenExpireTimeCookie(Date date){
        Cookie accessExpireTimeCookie = new Cookie(JwtProperties.ACCESS_EXPIRTE_DATE, String.valueOf(date.getTime()));
        accessExpireTimeCookie.setPath("/");
        accessExpireTimeCookie.setMaxAge((int)(JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME/1000) + 5);
        accessExpireTimeCookie.setSecure(true);
        accessExpireTimeCookie.setHttpOnly(true);

        return accessExpireTimeCookie;
    }

}
