package com.gyeom.homeflix;

import com.gyeom.homeflix.login.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeFlixController {

    private Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final LoginService loginService;

    public HomeFlixController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/screen/videos";
    }

//    @GetMapping("/refresh_token")
//    public ResponseEntity<Object> refreshAccessToken(@CookieValue(name = JwtProperties.REFRESH_TOKEN_HEADER) String refreshToken){
//        Map<String, Object> res = new HashMap<>(){{
//            put(JwtProperties.ACCESS_TOKEN_HEADER, loginService.refreshAccessToken(refreshToken));
//            put(JwtProperties.ACCESS_EXPIRTE_DATE, new Date(new Date().getTime() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME));
//        }};
//        return ResponseEntity.ok().body(res);
//    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO requestDTO, HttpServletResponse response) {

        Map<String, Object> body = new HashMap<>();
        try{
            TokenInfo tokenInfo = loginService.login(requestDTO.getUsername(), requestDTO.getPassword());

            Cookie refreshTokenCookie = new Cookie(JwtProperties.REFRESH_TOKEN_HEADER, tokenInfo.getRefreshToken());
//            refreshTokenCookie.setMaxAge((int)(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME/1000));
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(refreshTokenCookie);
            response.addCookie(JwtTokenProvider.createAccessTokenCookie(tokenInfo.getAccessToken()));
            response.addCookie(JwtTokenProvider.createAccessTokenExpireTimeCookie(tokenInfo.getExpireDate()));

            body.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok().body(body);
        }catch (AuthenticationException e){
            log.error("Auth exception, " + e.getMessage());
        }

        body.put("status", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}
