package com.gyeom.homeflix;

import com.gyeom.homeflix.login.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    private Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO requestDTO, HttpServletResponse response) {

        try{
            TokenInfo tokenInfo = loginService.login(requestDTO.getUsername(), requestDTO.getPassword());

            Cookie refreshTokenCookie = new Cookie(JwtProperties.REFRESH_TOKEN_HEADER, tokenInfo.getRefreshToken());
            refreshTokenCookie.setMaxAge((int)(JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME/1000));
            refreshTokenCookie.setPath("/");
//            refreshTokenCookie.setSecure(true); // https에서 사용하는 옵션
            refreshTokenCookie.setHttpOnly(true);

            response.addCookie(refreshTokenCookie);
            response.addCookie(JwtTokenProvider.createAccessTokenCookie(tokenInfo.getAccessToken()));
            response.addCookie(JwtTokenProvider.createAccessTokenExpireTimeCookie(tokenInfo.getExpireDate()));

            return ResponseEntity.ok().build();
        }catch (AuthenticationException e){
            log.error("Auth exception, " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    //    @GetMapping("/refresh_token")
    //    public ResponseEntity<Object> refreshAccessToken(@CookieValue(name = JwtProperties.REFRESH_TOKEN_HEADER) String refreshToken){
    //        Map<String, Object> res = new HashMap<>(){{
    //            put(JwtProperties.ACCESS_TOKEN_HEADER, loginService.refreshAccessToken(refreshToken));
    //            put(JwtProperties.ACCESS_EXPIRTE_DATE, new Date(new Date().getTime() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME));
    //        }};
    //        return ResponseEntity.ok().body(res);
    //    }
}
