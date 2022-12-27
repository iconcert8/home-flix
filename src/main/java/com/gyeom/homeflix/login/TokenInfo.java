package com.gyeom.homeflix.login;


public class TokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public TokenInfo(String grantType, String accessToken, String refreshToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "grantType='" + grantType + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
