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

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        //Access Token
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(JwtProperties.TOKEN_AUTH, authorities)
                .setIssuedAt(nowDate)
                .setExpiration(new Date(now + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        //Refresh Token
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return new TokenInfo(JwtProperties.TOKEN_HEADER_PREFIX, accessToken, refreshToken);
    }

    // Decode JWT token and extract info in token.
    public Authentication getAuthentication(String accessToken) {
        // decode token
        Claims claims = parseClaims(accessToken);

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
}
