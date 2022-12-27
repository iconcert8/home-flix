package com.gyeom.homeflix.login;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class User implements UserDetails {

    public static final String STR_USERNAME = "username";
    public static final String STR_PASSWORD = "password";

    private String username;
    private String password;

//    private Collection<? extends GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList();
    public User(){}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Collection<? extends GrantedAuthority> defaultAuthorities() {
        return new ArrayList<>(){{add(new SimpleGrantedAuthority("user"));}};
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return defaultAuthorities();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
