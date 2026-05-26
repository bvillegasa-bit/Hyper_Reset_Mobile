package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id")
    private long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("email")
    private String email;

    @SerializedName("rol")
    private String rol;

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}
