package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Android model for a single actividad reciente item from
 * the paginated dashboard activity endpoint.
 */
public class ActividadRecienteItem {

    @SerializedName("pacienteNombre")
    private String pacienteNombre;

    @SerializedName("accion")
    private String accion;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("tipo")
    private String tipo;

    // Getters and Setters

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
