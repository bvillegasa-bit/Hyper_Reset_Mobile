package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Android model matching the backend's DashboardDeportistaResponse.
 * Contains data for the deportista dashboard: next appointment, weekly progress,
 * monthly goal, and achievements.
 */
public class DashboardDeportistaResponse {

    @SerializedName("proximaCita")
    private ProximaCita proximaCita;

    @SerializedName("progresoSemanal")
    private List<ProgresoSemanalItem> progresoSemanal;

    @SerializedName("metaDelMes")
    private MetaDelMes metaDelMes;

    @SerializedName("logros")
    private Logros logros;

    // Getters and Setters

    public ProximaCita getProximaCita() { return proximaCita; }
    public void setProximaCita(ProximaCita proximaCita) { this.proximaCita = proximaCita; }

    public List<ProgresoSemanalItem> getProgresoSemanal() { return progresoSemanal; }
    public void setProgresoSemanal(List<ProgresoSemanalItem> progresoSemanal) { this.progresoSemanal = progresoSemanal; }

    public MetaDelMes getMetaDelMes() { return metaDelMes; }
    public void setMetaDelMes(MetaDelMes metaDelMes) { this.metaDelMes = metaDelMes; }

    public Logros getLogros() { return logros; }
    public void setLogros(Logros logros) { this.logros = logros; }

    // ==================================================================
    // Inner classes
    // ==================================================================

    public static class ProximaCita {
        @SerializedName("fecha")
        private String fecha;

        @SerializedName("hora")
        private String hora;

        @SerializedName("coachNombre")
        private String coachNombre;

        @SerializedName("motivo")
        private String motivo;

        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }
        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }
        public String getCoachNombre() { return coachNombre; }
        public void setCoachNombre(String coachNombre) { this.coachNombre = coachNombre; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    public static class ProgresoSemanalItem {
        @SerializedName("dia")
        private String dia;

        @SerializedName("valor")
        private int valor;

        public String getDia() { return dia; }
        public void setDia(String dia) { this.dia = dia; }
        public int getValor() { return valor; }
        public void setValor(int valor) { this.valor = valor; }
    }

    public static class MetaDelMes {
        @SerializedName("descripcion")
        private String descripcion;

        @SerializedName("actual")
        private int actual;

        @SerializedName("objetivo")
        private int objetivo;

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public int getActual() { return actual; }
        public void setActual(int actual) { this.actual = actual; }
        public int getObjetivo() { return objetivo; }
        public void setObjetivo(int objetivo) { this.objetivo = objetivo; }
    }

    public static class Logros {
        @SerializedName("testsCompletados")
        private int testsCompletados;

        @SerializedName("totalTests")
        private int totalTests;

        @SerializedName("sesiones")
        private int sesiones;

        @SerializedName("racha")
        private int racha;

        public int getTestsCompletados() { return testsCompletados; }
        public void setTestsCompletados(int testsCompletados) { this.testsCompletados = testsCompletados; }
        public int getTotalTests() { return totalTests; }
        public void setTotalTests(int totalTests) { this.totalTests = totalTests; }
        public int getSesiones() { return sesiones; }
        public void setSesiones(int sesiones) { this.sesiones = sesiones; }
        public int getRacha() { return racha; }
        public void setRacha(int racha) { this.racha = racha; }
    }
}
