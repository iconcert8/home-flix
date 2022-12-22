package com.gyeom.homeflix.login;

import org.springframework.lang.Nullable;

public interface LoginRepository {

    @Nullable
    User findByUsername(String username);
}
