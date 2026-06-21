package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Android model matching the backend's TipoTestEstadoResponse.
 * Represents one of the 8 fixed test types with its completion status
 * for a specific deportista.
 */
public class TipoTestEstadoResponse {

    @SerializedName("tipoTest")
    private String tipoTest;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("icono")
    private String icono;

    @SerializedName("duracion")
    private String duracion;

    @SerializedName("completado")
    private boolean completado;

    @SerializedName("ultimoValor")
    private Double ultimoValor;

    @SerializedName("unidad")
    private String unidad;

    @SerializedName("fechaUltimo")
    private String fechaUltimo;

    @SerializedName("calificacion")
    private String calificacion;

    @SerializedName("idTestFisico")
    private Long idTestFisico;

    // Getters and Setters

    public String getTipoTest() { return tipoTest; }
    public void setTipoTest(String tipoTest) { this.tipoTest = tipoTest; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }

    public Double getUltimoValor() { return ultimoValor; }
    public void setUltimoValor(Double ultimoValor) { this.ultimoValor = ultimoValor; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getFechaUltimo() { return fechaUltimo; }
    public void setFechaUltimo(String fechaUltimo) { this.fechaUltimo = fechaUltimo; }

    public String getCalificacion() { return calificacion; }
    public void setCalificacion(String calificacion) { this.calificacion = calificacion; }

    public Long getIdTestFisico() { return idTestFisico; }
    public void setIdTestFisico(Long idTestFisico) { this.idTestFisico = idTestFisico; }
}
