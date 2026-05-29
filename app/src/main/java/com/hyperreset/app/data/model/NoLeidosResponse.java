package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response for unread message count.
 * Backend returns ApiResponse<Integer> for GET /api/mensajes/no-leidos
 */
public class NoLeidosResponse {

    @SerializedName("cantidad")
    private int cantidad;

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
