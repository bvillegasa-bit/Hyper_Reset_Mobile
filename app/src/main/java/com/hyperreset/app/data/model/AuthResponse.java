package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Authentication response containing JWT token and user info.
 * Matches the backend's AuthResponse DTO.
 */
public class AuthResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("type")
    private String type;

    @SerializedName("userId")
    private long userId;

    @SerializedName("email")
    private String email;

    @SerializedName("rol")
    private String rol;

    @SerializedName("nombre")
    private String nombre;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
