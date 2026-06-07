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

    @SerializedName("id")
    private long id;

    @SerializedName("userId")
    private long userId;

    @SerializedName("email")
    private String email;

    @SerializedName("rol")
    private String rol;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("fechaRegistro")
    private String fechaRegistro;

    @SerializedName("activo")
    private boolean activo;

    @SerializedName("deportistaId")
    private long deportistaId;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

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

    public long getId() {
        return id;
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

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public long getDeportistaId() {
        return deportistaId;
    }

    public void setDeportistaId(long deportistaId) {
        this.deportistaId = deportistaId;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
