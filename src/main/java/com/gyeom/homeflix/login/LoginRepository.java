package com.gyeom.homeflix.login;

import org.springframework.lang.Nullable;

public interface LoginRepository {

    @Nullable
    User findByUsername(String username);

    void upsertRefreshToken(String refreshToken, TokenInfo tokenInfo);

//    TokenInfo findByRefreshToken()
}
