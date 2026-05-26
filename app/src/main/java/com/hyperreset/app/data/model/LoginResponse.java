package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private Usuario user;

    public String getToken() {
        return token;
    }

    public Usuario getUser() {
        return user;
    }
}
