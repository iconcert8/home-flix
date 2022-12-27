package com.gyeom.homeflix;

import com.gyeom.homeflix.login.LoginRequestDTO;
import com.gyeom.homeflix.login.LoginService;
import com.gyeom.homeflix.login.TokenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO requestDTO) {
        log.info(requestDTO.toString());
        try{
            TokenInfo tokenInfo = loginService.login(requestDTO.getUsername(), requestDTO.getPassword());
            return ResponseEntity.ok().body(tokenInfo);
        }catch (AuthenticationException e){
            log.error("Auth exception, " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed authentication");
    }
}
