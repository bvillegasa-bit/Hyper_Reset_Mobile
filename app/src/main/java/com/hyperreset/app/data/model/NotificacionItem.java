package com.hyperreset.app.data.model;

/**
 * Model for a single notification display item.
 * Mapped from ActividadRecienteItem or other data sources.
 */
public class NotificacionItem {

    private int iconResId;
    private String title;
    private String description;
    private String timestamp;
    private String tipo;

    public NotificacionItem(int iconResId, String title, String description,
                            String timestamp, String tipo) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.tipo = tipo;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
