package com.hyperreset.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DashboardCoachResponse;
import com.hyperreset.app.data.model.DashboardDeportistaResponse;
import com.hyperreset.app.data.repository.DashboardRepository;
import com.hyperreset.app.utils.Resource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * ViewModel for the Home/Dashboard screen.
 * Loads and exposes dashboard data for both DEPORTISTA and COACH roles.
 */
public class HomeViewModel extends ViewModel {

    private final DashboardRepository dashboardRepository;

    private final MutableLiveData<Resource<DashboardDeportistaResponse>> dashboardDeportista = new MutableLiveData<>();
    private final MutableLiveData<Resource<DashboardCoachResponse>> dashboardCoach = new MutableLiveData<>();
    private final MutableLiveData<Integer> noLeidos = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    // Track active API calls for cancellation on ViewModel clear
    private final List<Call<?>> activeCalls = new ArrayList<>();
    private volatile boolean cleared = false;

    public HomeViewModel() {
        this.dashboardRepository = new DashboardRepository();
    }

    public HomeViewModel(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleared = true;
        // Cancel all active API calls to prevent memory leaks
        for (Call<?> call : activeCalls) {
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
        }
        activeCalls.clear();
    }

    // ==================================================================
    // Public LiveData accessors
    // ==================================================================

    public LiveData<Resource<DashboardDeportistaResponse>> getDashboardDeportista() {
        return dashboardDeportista;
    }

    public LiveData<Resource<DashboardCoachResponse>> getDashboardCoach() {
        return dashboardCoach;
    }

    public LiveData<Integer> getNoLeidos() {
        return noLeidos;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    // ==================================================================
    // Data loading methods
    // ==================================================================

    /**
     * Load dashboard data for DEPORTISTA role.
     *
     * @param deportistaId The deportista's ID
     */
    public void loadDashboardDeportista(long deportistaId) {
        loading.setValue(true);
        dashboardDeportista.setValue(Resource.loading());

        dashboardRepository.getDashboardDeportista(deportistaId, resource -> {
            loading.setValue(false);
            dashboardDeportista.setValue(resource);
        });
    }

    /**
     * Load dashboard data for COACH role.
     *
     * @param coachId The coach's user ID
     */
    public void loadDashboardCoach(long coachId) {
        loading.setValue(true);
        dashboardCoach.setValue(Resource.loading());

        dashboardRepository.getDashboardCoach(coachId, resource -> {
            loading.setValue(false);
            dashboardCoach.setValue(resource);
        });
    }

    /**
     * Update the unread messages count.
     *
     * @param count Number of unread messages
     */
    public void setNoLeidos(int count) {
        noLeidos.setValue(count);
    }
}
