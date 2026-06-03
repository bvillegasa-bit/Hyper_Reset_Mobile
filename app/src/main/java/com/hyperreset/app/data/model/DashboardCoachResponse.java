package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Android model matching the backend's DashboardCoachResponse.
 * Contains data for the coach dashboard: statistics, recent activity,
 * and upcoming appointments.
 */
public class DashboardCoachResponse {

    @SerializedName("estadisticas")
    private Estadisticas estadisticas;

    @SerializedName("actividadReciente")
    private List<ActividadRecienteItem> actividadReciente;

    @SerializedName("proximasCitas")
    private List<ProximaCitaItem> proximasCitas;

    // Getters and Setters

    public Estadisticas getEstadisticas() { return estadisticas; }
    public void setEstadisticas(Estadisticas estadisticas) { this.estadisticas = estadisticas; }

    public List<ActividadRecienteItem> getActividadReciente() { return actividadReciente; }
    public void setActividadReciente(List<ActividadRecienteItem> actividadReciente) { this.actividadReciente = actividadReciente; }

    public List<ProximaCitaItem> getProximasCitas() { return proximasCitas; }
    public void setProximasCitas(List<ProximaCitaItem> proximasCitas) { this.proximasCitas = proximasCitas; }

    // ==================================================================
    // Inner classes
    // ==================================================================

    public static class Estadisticas {
        @SerializedName("pacientesHoy")
        private int pacientesHoy;

        @SerializedName("pruebasPendientes")
        private int pruebasPendientes;

        @SerializedName("reportes")
        private int reportes;

        public int getPacientesHoy() { return pacientesHoy; }
        public void setPacientesHoy(int pacientesHoy) { this.pacientesHoy = pacientesHoy; }
        public int getPruebasPendientes() { return pruebasPendientes; }
        public void setPruebasPendientes(int pruebasPendientes) { this.pruebasPendientes = pruebasPendientes; }
        public int getReportes() { return reportes; }
        public void setReportes(int reportes) { this.reportes = reportes; }
    }

    public static class ActividadRecienteItem {
        @SerializedName("paciente")
        private String paciente;

        @SerializedName("accion")
        private String accion;

        @SerializedName("timestamp")
        private String timestamp;

        @SerializedName("tipo")
        private String tipo;

        public String getPaciente() { return paciente; }
        public void setPaciente(String paciente) { this.paciente = paciente; }
        public String getAccion() { return accion; }
        public void setAccion(String accion) { this.accion = accion; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
    }

    public static class ProximaCitaItem {
        @SerializedName("deportistaNombre")
        private String deportistaNombre;

        @SerializedName("fecha")
        private String fecha;

        @SerializedName("hora")
        private String hora;

        @SerializedName("motivo")
        private String motivo;

        public String getDeportistaNombre() { return deportistaNombre; }
        public void setDeportistaNombre(String deportistaNombre) { this.deportistaNombre = deportistaNombre; }
        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }
        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }
}
