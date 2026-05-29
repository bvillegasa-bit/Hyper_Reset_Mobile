package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches the backend's ResultadoResponse DTO.
 */
public class ResultadoResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("testId")
    private long testId;

    @SerializedName("parametro")
    private String parametro;

    @SerializedName("valor")
    private double valor;

    @SerializedName("unidad")
    private String unidad;

    @SerializedName("observaciones")
    private String observaciones;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
