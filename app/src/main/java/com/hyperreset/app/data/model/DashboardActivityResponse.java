package com.hyperreset.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Android model matching the backend's paginated dashboard activity endpoint.
 * Wraps a list of {@link ActividadRecienteItem} with pagination metadata.
 */
public class DashboardActivityResponse {

    @SerializedName("items")
    private List<ActividadRecienteItem> items;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalItems")
    private long totalItems;

    // Getters and Setters

    public List<ActividadRecienteItem> getItems() { return items; }
    public void setItems(List<ActividadRecienteItem> items) { this.items = items; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
}
