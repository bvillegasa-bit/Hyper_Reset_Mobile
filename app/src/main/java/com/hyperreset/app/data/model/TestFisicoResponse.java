package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches the backend's TestFisicoResponse DTO.
 */
public class TestFisicoResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("deportistaNombre")
    private String deportistaNombre;

    @SerializedName("tipoTest")
    private String tipoTest;

    @SerializedName("fechaTest")
    private String fechaTest;

    @SerializedName("notas")
    private String notas;

    @SerializedName("completado")
    private boolean completado;

    @SerializedName("calificacion")
    private String calificacion;

    @SerializedName("deportistaId")
    private long deportistaId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeportistaNombre() {
        return deportistaNombre;
    }

    public void setDeportistaNombre(String deportistaNombre) {
        this.deportistaNombre = deportistaNombre;
    }

    public String getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(String tipoTest) {
        this.tipoTest = tipoTest;
    }

    public String getFechaTest() {
        return fechaTest;
    }

    public void setFechaTest(String fechaTest) {
        this.fechaTest = fechaTest;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public long getDeportistaId() {
        return deportistaId;
    }

    public void setDeportistaId(long deportistaId) {
        this.deportistaId = deportistaId;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
