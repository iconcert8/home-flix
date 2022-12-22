package com.gyeom.homeflix;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .mvcMatchers("/screen/login", "/login/**").permitAll() // allow this urls.
                        .anyRequest().authenticated() // not allow other urls.
                )
                .formLogin((form) -> form
                        .loginPage("/screen/login")
                        .loginProcessingUrl("/login")
                        .successHandler(null)
                        .failureHandler(null)
                )
                //TODO: logout handle.
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }
}