package com.gyeom.homeflix.login;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

    private final LoginRepository loginRepository;

    public LoginService(@Qualifier("loginFileRepository") LoginRepository loginRepository){
        this.loginRepository = loginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = loginRepository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException("Not Found User '"+username+"'");
        return user;
    }
}
