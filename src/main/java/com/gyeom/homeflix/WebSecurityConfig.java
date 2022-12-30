package com.gyeom.homeflix;

import com.gyeom.homeflix.login.JwtAuthenticationFilter;
import com.gyeom.homeflix.login.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

    public static String[] ALLOW_URLS = {
            "/", "/screen/**", "/login/**", "/favicon.ico", "/img/**", "/css/**"
    };
    private final JwtTokenProvider tokenProvider;

    public WebSecurityConfig(JwtTokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // csrf disable: read 요청밖에 없기 때문에 CSRF(Cross Site Request Forgery)걱정은 안해도 된다.
        // 난수를 생성해 다음 요청때 난수를 같이 포함하여 요청하도록 한다.
        http.csrf().disable()
                .authorizeRequests().antMatchers(ALLOW_URLS).permitAll()
                .anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}