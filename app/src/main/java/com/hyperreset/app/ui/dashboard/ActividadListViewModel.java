package com.hyperreset.app.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.ActividadRecienteItem;
import com.hyperreset.app.data.model.DashboardActivityResponse;
import com.hyperreset.app.data.repository.DashboardRepository;
import com.hyperreset.app.utils.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the paginated activity list.
 * Loads activity from the backend via DashboardRepository and exposes
 * a LiveData with loading/success/error states. Supports infinite scroll
 * by tracking pagination state and accumulating items across pages.
 */
public class ActividadListViewModel extends ViewModel {

    private final DashboardRepository dashboardRepository;
    private final MutableLiveData<Resource<DashboardActivityResponse>> actividadResult = new MutableLiveData<>();
    private int currentPage = 0;
    private int totalPages = 1;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    // Accumulated items across all loaded pages
    private final List<ActividadRecienteItem> allItems = new ArrayList<>();

    public ActividadListViewModel() {
        this.dashboardRepository = new DashboardRepository();
    }

    public LiveData<Resource<DashboardActivityResponse>> getActividadResult() {
        return actividadResult;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void loadActividad(int page) {
        if (isLoading) return;
        isLoading = true;
        currentPage = page;
        actividadResult.setValue(Resource.loading());
        dashboardRepository.getActividad(page, PAGE_SIZE, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                // Track pagination info from the response
                totalPages = resource.data.getTotalPages();
                hasMorePages = (currentPage + 1) < totalPages;

                // Accumulate items across pages
                if (currentPage == 0) {
                    allItems.clear();
                }
                if (resource.data.getItems() != null) {
                    allItems.addAll(resource.data.getItems());
                }

                // Build a merged response with all accumulated items
                DashboardActivityResponse merged = new DashboardActivityResponse();
                merged.setItems(new ArrayList<>(allItems));
                merged.setCurrentPage(currentPage);
                merged.setTotalPages(totalPages);
                merged.setTotalItems(resource.data.getTotalItems());

                isLoading = false;
                actividadResult.postValue(Resource.success(merged));
            } else if (resource.status == Resource.Status.ERROR) {
                hasMorePages = false;
                isLoading = false;
                actividadResult.postValue(resource);
            } else {
                isLoading = false;
                actividadResult.postValue(resource);
            }
        });
    }

    public void loadNextPage() {
        if (!hasMorePages || isLoading) return;
        loadActividad(currentPage + 1);
    }

    public void refresh() {
        hasMorePages = true;
        totalPages = 1;
        allItems.clear();
        loadActividad(0);
    }
}
