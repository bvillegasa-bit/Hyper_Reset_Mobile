package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches the backend's ReporteResponse DTO.
 */
public class ReporteResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("deportistaNombre")
    private String deportistaNombre;

    @SerializedName("tipoTest")
    private String tipoTest;

    @SerializedName("fechaGeneracion")
    private String fechaGeneracion;

    @SerializedName("tipoReporte")
    private String tipoReporte;

    @SerializedName("contenido")
    private String contenido;

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

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
