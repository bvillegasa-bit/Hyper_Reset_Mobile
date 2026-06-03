package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Matches the backend's CoachResponse DTO.
 * Returned by endpoints like GET /api/deportistas/coaches and GET /api/deportistas/mi-coach.
 */
public class CoachResponse {

    @SerializedName("idCoach")
    private long idCoach;

    @SerializedName("nombreCompleto")
    private String nombreCompleto;

    @SerializedName("usuarioId")
    private long usuarioId;

    public long getIdCoach() {
        return idCoach;
    }

    public void setIdCoach(long idCoach) {
        this.idCoach = idCoach;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
