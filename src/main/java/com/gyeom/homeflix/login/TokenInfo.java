package com.gyeom.homeflix.login;


import java.util.Date;

public class TokenInfo {
    private String grantType = JwtProperties.TOKEN_HEADER_PREFIX;
    private String accessToken;
    private String refreshToken;
    private Date expireDate;

    private String username;

//    public TokenInfo(String grantType, String accessToken, String refreshToken) {
//        this.grantType = grantType;
//        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
//        this.expireDate = new Date(new Date().getTime()+JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME);
//    }

    public TokenInfo(String accessToken, String refreshToken, Date expireDate, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireDate = expireDate;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public Date getExpireDate() {
        return expireDate;
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
