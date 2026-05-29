package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches the backend's MensajeResponse DTO.
 */
public class MensajeResponse {

    @SerializedName("id")
    private long id;

    @SerializedName("remitenteId")
    private long remitenteId;

    @SerializedName("remitenteNombre")
    private String remitenteNombre;

    @SerializedName("destinatarioId")
    private long destinatarioId;

    @SerializedName("contenido")
    private String contenido;

    @SerializedName("fechaEnvio")
    private String fechaEnvio;

    @SerializedName("leido")
    private boolean leido;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(long remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }

    public long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}
