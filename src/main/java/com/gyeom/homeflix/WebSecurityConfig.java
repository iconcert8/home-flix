package com.gyeom.homeflix;

import com.gyeom.homeflix.login.AuthFailureHandler;
import com.gyeom.homeflix.login.AuthSuccessHandler;
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

    public static String[] ALLOW_URLS = {"/", "/screen/login/**", "/login/**", "/favicon.ico"};
    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    private final JwtTokenProvider tokenProvider;

    public WebSecurityConfig(JwtTokenProvider tokenProvider, AuthSuccessHandler authSuccessHandler, AuthFailureHandler authFailureHandler){
        this.tokenProvider = tokenProvider;
        this.authSuccessHandler = authSuccessHandler;
        this.authFailureHandler = authFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(ALLOW_URLS).permitAll()
                .anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();

//        http
//                .authorizeHttpRequests((requests) -> requests
//                        .mvcMatchers(ALLOW_URLS).permitAll() // allow this urls.
//                        .anyRequest().authenticated() // not allow other urls.
//                )
//                .formLogin((form) -> form
//                        .loginPage("/screen/login")
//                        .loginProcessingUrl("/login/process") //Front-end request url for login.
//                        .successHandler(authSuccessHandler)
//                        .failureHandler(authFailureHandler)
//                )
//                //TODO: logout handle.
//                .logout(LogoutConfigurer::permitAll);
//
//        return http.build();
    }

}