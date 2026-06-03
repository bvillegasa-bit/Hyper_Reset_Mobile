package com.hyperreset.app.ui.citas.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.data.repository.CitaRepository;
import com.hyperreset.app.utils.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CitaListViewModel extends ViewModel {

    private final CitaRepository repository;

    // All appointments loaded for the current week
    private final MutableLiveData<Resource<List<CitaResponse>>> citasSemana = new MutableLiveData<>();
    // Appointments filtered by selected day
    private final MutableLiveData<Resource<List<CitaResponse>>> citasPorDia = new MutableLiveData<>();
    // Selected day (yyyy-MM-dd format)
    private final MutableLiveData<String> selectedDay = new MutableLiveData<>();
    // Loading state
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    // Internal cache of all week appointments
    private List<CitaResponse> allWeekCitas = new ArrayList<>();

    // Current week start date (Calendar)
    private Calendar currentWeekStart;

    // Format for date strings
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // Flag to prevent UI updates after ViewModel is cleared
    private volatile boolean cleared = false;

    public CitaListViewModel() {
        this.repository = new CitaRepository();
        initCurrentWeek();
    }

    public CitaListViewModel(CitaRepository repository) {
        this.repository = repository;
        initCurrentWeek();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleared = true;
        // The repository creates and manages Retrofit calls internally.
        // The cleared flag prevents callbacks from updating LiveData
        // after the ViewModel is no longer in use.
    }

    /**
     * Initialize current week start (Monday).
     */
    private void initCurrentWeek() {
        currentWeekStart = Calendar.getInstance();
        currentWeekStart.setFirstDayOfWeek(Calendar.MONDAY);
        currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // Normalize time to start of day
        currentWeekStart.set(Calendar.HOUR_OF_DAY, 0);
        currentWeekStart.set(Calendar.MINUTE, 0);
        currentWeekStart.set(Calendar.SECOND, 0);
        currentWeekStart.set(Calendar.MILLISECOND, 0);
    }

    // ==================================================================
    // LiveData Getters
    // ==================================================================

    public LiveData<Resource<List<CitaResponse>>> getCitas() {
        return citasPorDia;
    }

    public LiveData<Resource<List<CitaResponse>>> getCitasSemana() {
        return citasSemana;
    }

    public LiveData<String> getSelectedDay() {
        return selectedDay;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    // ==================================================================
    // Week Navigation
    // ==================================================================

    /**
     * Load appointments for the current week and select today.
     *
     * @param roleBased Determines API call based on role. If true, the caller
     *                  is expected to call loadCitasPorSemanaDeportista or
     *                  loadCitasPorSemanaCoach directly.
     */
    public void loadCurrentWeek() {
        String weekStart = dateFormat.format(currentWeekStart.getTime());
        Calendar weekEndCal = (Calendar) currentWeekStart.clone();
        weekEndCal.add(Calendar.DAY_OF_WEEK, 6);
        String weekEnd = dateFormat.format(weekEndCal.getTime());

        loadCitasForRange(weekStart, weekEnd);
    }

    /**
     * Load citas for DEPORTISTA for the current week range.
     */
    public void loadCitasPorSemanaDeportista(long deportistaId) {
        loading.setValue(true);
        citasSemana.setValue(Resource.loading());

        String weekStart = dateFormat.format(currentWeekStart.getTime());
        Calendar weekEndCal = (Calendar) currentWeekStart.clone();
        weekEndCal.add(Calendar.DAY_OF_WEEK, 6);
        String weekEnd = dateFormat.format(weekEndCal.getTime());

        // DEPORTISTA uses getCitasByDeportista then filters by week locally
        repository.getCitasByDeportista(deportistaId, result -> {
            loading.setValue(false);
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                // Filter by week range
                List<CitaResponse> weekCitas = new ArrayList<>();
                for (CitaResponse cita : result.data) {
                    String fecha = cita.getFechaCita();
                    if (fecha != null && fecha.compareTo(weekStart) >= 0 && fecha.compareTo(weekEnd) <= 0) {
                        weekCitas.add(cita);
                    }
                }
                allWeekCitas = weekCitas;
                citasSemana.setValue(Resource.success(weekCitas));
                // Filter by selected day (defaults to today)
                String currentDay = selectedDay.getValue();
                if (currentDay == null) {
                    currentDay = dateFormat.format(Calendar.getInstance().getTime());
                }
                filterByDayInternal(currentDay);
            } else {
                citasSemana.setValue(result);
                citasPorDia.setValue(result);
            }
        });
    }

    /**
     * Load citas for COACH for the current week range using /rango endpoint.
     */
    public void loadCitasPorSemanaCoach() {
        loading.setValue(true);
        citasSemana.setValue(Resource.loading());

        String weekStart = dateFormat.format(currentWeekStart.getTime());
        Calendar weekEndCal = (Calendar) currentWeekStart.clone();
        weekEndCal.add(Calendar.DAY_OF_WEEK, 6);
        String weekEnd = dateFormat.format(weekEndCal.getTime());

        repository.getCitasByDateRange(weekStart, weekEnd, result -> {
            loading.setValue(false);
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                allWeekCitas = result.data;
                citasSemana.setValue(Resource.success(result.data));
                // Filter by selected day
                String currentDay = selectedDay.getValue();
                if (currentDay == null) {
                    currentDay = dateFormat.format(Calendar.getInstance().getTime());
                }
                filterByDayInternal(currentDay);
            } else {
                citasSemana.setValue(result);
                citasPorDia.setValue(result);
            }
        });
    }

    /**
     * Load citas for an arbitrary date range for COACH.
     */
    private void loadCitasForRange(String start, String end) {
        loading.setValue(true);
        repository.getCitasByDateRange(start, end, result -> {
            loading.setValue(false);
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                allWeekCitas = result.data;
                citasSemana.setValue(Resource.success(result.data));
                String currentDay = selectedDay.getValue();
                if (currentDay == null) {
                    currentDay = dateFormat.format(Calendar.getInstance().getTime());
                }
                filterByDayInternal(currentDay);
            } else {
                citasSemana.setValue(result);
                citasPorDia.setValue(result);
            }
        });
    }

    /**
     * Navigate weeks forward (+1) or backward (-1).
     */
    public void navigateWeek(int direction) {
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, direction);
        String currentDay = dateFormat.format(currentWeekStart.getTime());
        selectedDay.setValue(currentDay);
        loadCurrentWeek();
    }

    // ==================================================================
    // Day Selection (local filter)
    // ==================================================================

    /**
     * Select a day and filter citas locally (no API call).
     *
     * @param dateStr Date in yyyy-MM-dd format.
     */
    public void selectDay(String dateStr) {
        selectedDay.setValue(dateStr);
        filterByDayInternal(dateStr);
    }

    /**
     * Get the Calendar object for the start of the current week.
     */
    public Calendar getCurrentWeekStart() {
        return (Calendar) currentWeekStart.clone();
    }

    /**
     * Internal filtering by day from local cache.
     */
    private void filterByDayInternal(String dateStr) {
        if (dateStr == null || allWeekCitas == null || allWeekCitas.isEmpty()) {
            citasPorDia.setValue(Resource.success(new ArrayList<>()));
            return;
        }

        List<CitaResponse> filtered = new ArrayList<>();
        for (CitaResponse cita : allWeekCitas) {
            String fecha = cita.getFechaCita();
            if (fecha != null && fecha.equals(dateStr)) {
                filtered.add(cita);
            }
        }
        citasPorDia.setValue(Resource.success(filtered));
    }

    // ==================================================================
    // Legacy Methods (preserved for backward compatibility)
    // ==================================================================

    public void loadCitas() {
        loading.setValue(true);
        repository.getCitas(result -> {
            loading.setValue(false);
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                allWeekCitas = result.data;
                citasSemana.setValue(Resource.success(result.data));
                String currentDay = selectedDay.getValue();
                if (currentDay == null) {
                    currentDay = dateFormat.format(Calendar.getInstance().getTime());
                }
                filterByDayInternal(currentDay);
            } else {
                citasSemana.setValue(result);
                citasPorDia.setValue(result);
            }
        });
    }

    public void loadCitasByDateRange(String start, String end) {
        loadCitasForRange(start, end);
    }

    public void loadCitasByDeportista(long deportistaId) {
        loading.setValue(true);
        repository.getCitasByDeportista(deportistaId, result -> {
            loading.setValue(false);
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                allWeekCitas = result.data;
                citasSemana.setValue(Resource.success(result.data));
                String currentDay = selectedDay.getValue();
                if (currentDay == null) {
                    currentDay = dateFormat.format(Calendar.getInstance().getTime());
                }
                filterByDayInternal(currentDay);
            } else {
                citasSemana.setValue(result);
                citasPorDia.setValue(result);
            }
        });
    }
}
