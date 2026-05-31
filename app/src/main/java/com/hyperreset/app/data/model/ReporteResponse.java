package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches the backend's ReporteResponse DTO with additional fields for UI display.
 */
public class ReporteResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("testFisicoId")
    private long testFisicoId;

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

    @SerializedName("calificacion")
    private String calificacion;

    @SerializedName("observaciones")
    private String observaciones;

    @SerializedName("recomendaciones")
    private String recomendaciones;

    @SerializedName("coachNombre")
    private String coachNombre;

    @SerializedName("rutaPdf")
    private String rutaPdf;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestFisicoId() {
        return testFisicoId;
    }

    public void setTestFisicoId(long testFisicoId) {
        this.testFisicoId = testFisicoId;
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

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public String getCoachNombre() {
        return coachNombre;
    }

    public void setCoachNombre(String coachNombre) {
        this.coachNombre = coachNombre;
    }

    public String getRutaPdf() {
        return rutaPdf;
    }

    public void setRutaPdf(String rutaPdf) {
        this.rutaPdf = rutaPdf;
    }
}
