package com.hyperreset.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DashboardCoachResponse;
import com.hyperreset.app.data.model.DashboardDeportistaResponse;
import com.hyperreset.app.ui.citas.list.CitaListFragment;
import com.hyperreset.app.ui.custom.GradientCardView;
import com.hyperreset.app.ui.custom.WeeklyBarChartView;
import com.hyperreset.app.ui.deportistas.form.DeportistaFormFragment;
import com.hyperreset.app.ui.deportistas.list.DeportistaListFragment;
import com.hyperreset.app.ui.tests.create.TestCreateFragment;
import com.hyperreset.app.ui.tests.list.TestListFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Role-based home dashboard fragment with real data from ViewModel.
 * Shows different content for DEPORTISTA vs COACH users with gradients,
 * custom bar chart, and dynamic data loading.
 */
public class HomeDashboardFragment extends Fragment {

    private HomeViewModel viewModel;
    private SessionManager sessionManager;
    private String userRole;

    // Header
    private TextView tvGreeting;
    private TextView tvSubtitle;
    private View badgeDot;
    private FrameLayout notificationBell;

    // Loading / Error
    private ProgressBar loadingIndicator;
    private LinearLayout errorState;
    private TextView tvError;
    private MaterialButton btnRetryDashboard;

    // Deportista views
    private LinearLayout deportistaContent;
    private GradientCardView cardNextAppointment;
    private TextView tvAppointmentDate;
    private TextView tvAppointmentCoach;
    private TextView tvNoAppointment;
    private WeeklyBarChartView weeklyBarChart;
    private TextView tvWeeklyPercent;
    private TextView tvLogrosTests;
    private TextView tvLogrosSesiones;
    private TextView tvLogrosRacha;
    private CardView btnRutinas;
    private CardView btnProximaCita;
    private CardView btnLogros;
    private GradientCardView btnMetaMes;
    private TextView tvMetaMesDesc;
    private View progressMetaFill;
    private TextView tvMetaMesCounter;

    // Coach views
    private LinearLayout coachContent;
    private TextView tvMetricPatientsCount;
    private TextView tvMetricPendingTests;
    private TextView tvMetricReportsCount;
    private CardView btnNewPatient;
    private CardView btnRegisterTest;
    private CardView btnViewReports;
    private CardView btnMySchedule;
    private LinearLayout activityContainer;
    private TextView tvViewAllActivity;
    private TextView tvRendimientoMes;
    private TextView tvRendimientoMesDesc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        userRole = sessionManager.getUserRole();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        initViews(view);
        setupObservers();
        setupGreeting();
        setupRoleContent();
        setupClickListeners();

        // Load data based on role
        loadDashboardData();
    }

    private void initViews(View view) {
        // Header
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);
        notificationBell = view.findViewById(R.id.notificationBell);
        badgeDot = view.findViewById(R.id.badgeDot);

        // Loading / Error
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        errorState = view.findViewById(R.id.errorState);
        tvError = view.findViewById(R.id.tvError);
        btnRetryDashboard = view.findViewById(R.id.btnRetryDashboard);

        // Deportista
        deportistaContent = view.findViewById(R.id.deportistaContent);
        cardNextAppointment = view.findViewById(R.id.cardNextAppointment);
        tvAppointmentDate = view.findViewById(R.id.tvAppointmentDate);
        tvAppointmentCoach = view.findViewById(R.id.tvAppointmentCoach);
        tvNoAppointment = view.findViewById(R.id.tvNoAppointment);
        weeklyBarChart = view.findViewById(R.id.weeklyBarChart);
        tvWeeklyPercent = view.findViewById(R.id.tvWeeklyPercent);
        tvLogrosTests = view.findViewById(R.id.tvLogrosTests);
        tvLogrosSesiones = view.findViewById(R.id.tvLogrosSesiones);
        tvLogrosRacha = view.findViewById(R.id.tvLogrosRacha);
        btnRutinas = view.findViewById(R.id.btnRutinas);
        btnProximaCita = view.findViewById(R.id.btnProximaCita);
        btnLogros = view.findViewById(R.id.btnLogros);
        btnMetaMes = view.findViewById(R.id.btnMetaMes);
        tvMetaMesDesc = view.findViewById(R.id.tvMetaMesDesc);
        progressMetaFill = view.findViewById(R.id.progressMetaFill);
        tvMetaMesCounter = view.findViewById(R.id.tvMetaMesCounter);

        // Coach
        coachContent = view.findViewById(R.id.coachContent);
        tvMetricPatientsCount = view.findViewById(R.id.tvMetricPatientsCount);
        tvMetricPendingTests = view.findViewById(R.id.tvMetricPendingTests);
        tvMetricReportsCount = view.findViewById(R.id.tvMetricReportsCount);
        btnNewPatient = view.findViewById(R.id.btnNewPatient);
        btnRegisterTest = view.findViewById(R.id.btnRegisterTest);
        btnViewReports = view.findViewById(R.id.btnViewReports);
        btnMySchedule = view.findViewById(R.id.btnMySchedule);
        activityContainer = view.findViewById(R.id.activityContainer);
        tvViewAllActivity = view.findViewById(R.id.tvViewAllActivity);
        tvRendimientoMes = view.findViewById(R.id.tvRendimientoMes);
        tvRendimientoMesDesc = view.findViewById(R.id.tvRendimientoMesDesc);
    }

    // ==================================================================
    // Data Loading
    // ==================================================================

    private void loadDashboardData() {
        if ("COACH".equals(userRole)) {
            long coachId = sessionManager.getUserId();
            viewModel.loadDashboardCoach(coachId);
        } else {
            long deportistaId = sessionManager.getDeportistaId();
            if (deportistaId > 0) {
                viewModel.loadDashboardDeportista(deportistaId);
            } else {
                showError("No se pudo identificar al deportista");
            }
        }
    }

    // ==================================================================
    // Observers
    // ==================================================================

    private void setupObservers() {
        if ("COACH".equals(userRole)) {
            viewModel.getDashboardCoach().observe(getViewLifecycleOwner(), this::handleCoachResponse);
        } else {
            viewModel.getDashboardDeportista().observe(getViewLifecycleOwner(), this::handleDeportistaResponse);
        }

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (Boolean.TRUE.equals(isLoading)) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }

    // ==================================================================
    // DEPORTISTA Response Handler
    // ==================================================================

    private void handleDeportistaResponse(Resource<DashboardDeportistaResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                showLoading();
                break;

            case SUCCESS:
                hideLoading();
                hideError();
                if (resource.data != null) {
                    populateDeportistaDashboard(resource.data);
                } else {
                    // Empty data — show empty message
                    showEmptyState(getString(R.string.home_data_empty));
                }
                break;

            case ERROR:
                hideLoading();
                showError(resource.message != null ? resource.message : getString(R.string.home_data_error));
                break;
        }
    }

    private void populateDeportistaDashboard(DashboardDeportistaResponse data) {
        // Fade-in the deportista content container on successful load
        fadeInContent(deportistaContent);

        // Next Appointment
        DashboardDeportistaResponse.ProximaCita cita = data.getProximaCita();
        if (cita != null && cita.getFecha() != null) {
            cardNextAppointment.setVisibility(View.VISIBLE);
            tvNoAppointment.setVisibility(View.GONE);
            String fechaFormateada = formatCitaDate(cita.getFecha(), cita.getHora());
            tvAppointmentDate.setText(fechaFormateada);
            String coachInfo = getString(R.string.home_appointment_with,
                    cita.getCoachNombre() != null ? cita.getCoachNombre() : "",
                    cita.getMotivo() != null ? cita.getMotivo() : "");
            tvAppointmentCoach.setText(coachInfo);
        } else {
            cardNextAppointment.setVisibility(View.GONE);
            tvNoAppointment.setVisibility(View.VISIBLE);
        }

        // Weekly Progress Chart
        List<DashboardDeportistaResponse.ProgresoSemanalItem> progreso = data.getProgresoSemanal();
        if (progreso != null && !progreso.isEmpty()) {
            List<Integer> values = new ArrayList<>();
            int sum = 0;
            for (DashboardDeportistaResponse.ProgresoSemanalItem item : progreso) {
                values.add(item.getValor());
                sum += item.getValor();
            }
            weeklyBarChart.setData(values);
            // Calculate and show average percentage
            int avg = sum / values.size();
            tvWeeklyPercent.setVisibility(View.VISIBLE);
            tvWeeklyPercent.setText(getString(R.string.home_weekly_percent, avg));
        } else {
            // Show empty chart (all zeros already set by default)
            List<Integer> zeros = new ArrayList<>();
            for (int i = 0; i < 7; i++) zeros.add(0);
            weeklyBarChart.setData(zeros);
            tvWeeklyPercent.setVisibility(View.GONE);
        }

        // Achievements Grid
        DashboardDeportistaResponse.Logros logros = data.getLogros();
        if (logros != null) {
            tvLogrosTests.setText(logros.getTestsCompletados() + "/" + logros.getTotalTests());
            tvLogrosSesiones.setText(String.valueOf(logros.getSesiones()));
            tvLogrosRacha.setText(logros.getRacha() + " d\u00edas");
        }

        // Monthly Goal
        DashboardDeportistaResponse.MetaDelMes meta = data.getMetaDelMes();
        if (meta != null && meta.getDescripcion() != null) {
            tvMetaMesDesc.setText(meta.getDescripcion());
            tvMetaMesCounter.setText(getString(R.string.home_goal_counter, meta.getActual(), meta.getObjetivo()));
            // Animate progress bar width
            int objetivo = meta.getObjetivo() > 0 ? meta.getObjetivo() : 1;
            int progressPercent = (meta.getActual() * 100) / objetivo;
            animateProgressBar(progressMetaFill, progressPercent);
        } else {
            tvMetaMesDesc.setText(R.string.home_goal_default);
            tvMetaMesCounter.setText("");
            animateProgressBar(progressMetaFill, 0);
        }
    }

    // ==================================================================
    // COACH Response Handler
    // ==================================================================

    private void handleCoachResponse(Resource<DashboardCoachResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                showLoading();
                break;

            case SUCCESS:
                hideLoading();
                hideError();
                if (resource.data != null) {
                    populateCoachDashboard(resource.data);
                }
                break;

            case ERROR:
                hideLoading();
                showError(resource.message != null ? resource.message : getString(R.string.home_data_error));
                break;
        }
    }

    private void populateCoachDashboard(DashboardCoachResponse data) {
        // Fade-in the coach content container on successful load
        fadeInContent(coachContent);

        // Statistics
        DashboardCoachResponse.Estadisticas stats = data.getEstadisticas();
        if (stats != null) {
            tvMetricPatientsCount.setText(String.valueOf(stats.getPacientesHoy()));
            tvMetricPendingTests.setText(String.valueOf(stats.getPruebasPendientes()));
            tvMetricReportsCount.setText(String.valueOf(stats.getReportes()));
        }

        // Recent Activity
        List<DashboardCoachResponse.ActividadRecienteItem> actividad = data.getActividadReciente();
        activityContainer.removeAllViews();
        if (actividad != null && !actividad.isEmpty()) {
            int maxItems = Math.min(actividad.size(), 5);
            for (int i = 0; i < maxItems; i++) {
                View activityCard = createActivityCard(actividad.get(i));
                activityContainer.addView(activityCard);
            }
            tvViewAllActivity.setVisibility(View.VISIBLE);
        } else {
            TextView emptyActivity = new TextView(requireContext());
            emptyActivity.setText(R.string.home_coach_no_activity);
            emptyActivity.setTextColor(getResources().getColor(R.color.hyper_on_surface_variant));
            emptyActivity.setTextSize(13f);
            emptyActivity.setPadding(0, 8, 0, 8);
            activityContainer.addView(emptyActivity);
            tvViewAllActivity.setVisibility(View.GONE);
        }

        // Monthly Performance
        List<DashboardCoachResponse.ProximaCitaItem> citas = data.getProximasCitas();
        if (citas != null) {
            tvRendimientoMesDesc.setText(getString(R.string.home_coach_performance_desc, citas.size()));
        } else {
            tvRendimientoMesDesc.setText(getString(R.string.home_coach_performance_desc, 0));
        }
    }

    private View createActivityCard(DashboardCoachResponse.ActividadRecienteItem item) {
        float density = getResources().getDisplayMetrics().density;

        CardView card = new CardView(requireContext());
        card.setRadius(12 * density);
        card.setCardElevation(2 * density);
        card.setCardBackgroundColor(getResources().getColor(R.color.hyper_surface));
        card.setUseCompatPadding(true);
        card.setContentPadding((int) (12 * density), (int) (12 * density),
                (int) (12 * density), (int) (12 * density));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = (int) (8 * density);
        card.setLayoutParams(cardParams);

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Status dot
        View dot = new View(requireContext());
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(
                (int) (10 * density), (int) (10 * density));
        dotParams.setMargins(0, 0, (int) (12 * density), 0);
        dot.setLayoutParams(dotParams);

        int dotColor;
        if ("success".equals(item.getTipo())) {
            dotColor = getResources().getColor(R.color.hyper_success);
        } else if ("warning".equals(item.getTipo())) {
            dotColor = getResources().getColor(R.color.hyper_warning);
        } else if ("info".equals(item.getTipo())) {
            dotColor = getResources().getColor(R.color.hyper_info);
        } else {
            dotColor = getResources().getColor(R.color.hyper_accent);
        }

        android.graphics.drawable.GradientDrawable dotShape = new android.graphics.drawable.GradientDrawable();
        dotShape.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        dotShape.setColor(dotColor);
        dot.setBackground(dotShape);

        row.addView(dot);

        // Text content
        LinearLayout textLayout = new LinearLayout(requireContext());
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvPaciente = new TextView(requireContext());
        tvPaciente.setText(item.getPaciente() != null ? item.getPaciente() : "");
        tvPaciente.setTextColor(getResources().getColor(R.color.hyper_on_primary));
        tvPaciente.setTextSize(14f);
        tvPaciente.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvPaciente);

        TextView tvAccion = new TextView(requireContext());
        tvAccion.setText(item.getAccion() != null ? item.getAccion() : "");
        tvAccion.setTextColor(getResources().getColor(R.color.hyper_on_surface_variant));
        tvAccion.setTextSize(12f);
        textLayout.addView(tvAccion);

        row.addView(textLayout);

        // Timestamp
        TextView tvTimestamp = new TextView(requireContext());
        tvTimestamp.setText(formatRelativeTime(item.getTimestamp()));
        tvTimestamp.setTextColor(getResources().getColor(R.color.hyper_on_surface_variant));
        tvTimestamp.setTextSize(11f);
        row.addView(tvTimestamp);

        card.addView(row);
        return card;
    }

    // ==================================================================
    // UI State Helpers
    // ==================================================================

    private void showLoading() {
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);
        if (errorState != null) errorState.setVisibility(View.GONE);
        if (deportistaContent != null) deportistaContent.setVisibility(View.GONE);
        if (coachContent != null) coachContent.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
    }

    private void showError(String message) {
        if (deportistaContent != null) deportistaContent.setVisibility(View.GONE);
        if (coachContent != null) coachContent.setVisibility(View.GONE);
        if (errorState != null) {
            errorState.setVisibility(View.VISIBLE);
            if (tvError != null) tvError.setText(message);
        }
    }

    private void hideError() {
        if (errorState != null) errorState.setVisibility(View.GONE);
    }

    /**
     * Show empty state when data is null/empty (no dashboard data available).
     */
    private void showEmptyState(String message) {
        if (deportistaContent != null) deportistaContent.setVisibility(View.GONE);
        if (coachContent != null) coachContent.setVisibility(View.GONE);
        // Reuse the error state layout but with a neutral message and no retry
        if (errorState != null) {
            errorState.setVisibility(View.VISIBLE);
            if (tvError != null) tvError.setText(message != null ? message : "No hay datos disponibles");
            if (btnRetryDashboard != null) btnRetryDashboard.setVisibility(View.GONE);
        }
    }

    /**
     * Apply a fade-in animation (300ms) to the given content view.
     * Used for smooth transitions when data loads successfully.
     */
    private void fadeInContent(View contentView) {
        if (contentView == null) return;
        contentView.setAlpha(0f);
        contentView.setVisibility(View.VISIBLE);
        contentView.animate()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void setupGreeting() {
        String userName = sessionManager.getUserName();
        if ("COACH".equals(userRole)) {
            if (userName != null && !userName.isEmpty()) {
                tvGreeting.setText(getString(R.string.home_coach_greeting, userName));
            } else {
                tvGreeting.setText(getString(R.string.home_welcome));
            }
        } else {
            if (userName != null && !userName.isEmpty()) {
                tvGreeting.setText(getString(R.string.home_greeting, userName));
            } else {
                tvGreeting.setText(getString(R.string.home_welcome));
            }
        }

        if ("COACH".equals(userRole)) {
            tvSubtitle.setText(R.string.home_coach_subtitle);
        } else {
            tvSubtitle.setText(R.string.home_deportista_subtitle);
        }
    }

    private void setupRoleContent() {
        if ("COACH".equals(userRole)) {
            deportistaContent.setVisibility(View.GONE);
            coachContent.setVisibility(View.VISIBLE);
        } else {
            deportistaContent.setVisibility(View.VISIBLE);
            coachContent.setVisibility(View.GONE);
        }
    }

    // ==================================================================
    // Click Listeners
    // ==================================================================

    private void setupClickListeners() {
        btnRetryDashboard.setOnClickListener(v -> {
            hideError();
            loadDashboardData();
        });

        // Notification bell click (placeholder for future)
        notificationBell.setOnClickListener(v -> {
            // TODO: Navigate to notifications screen
        });

        if ("COACH".equals(userRole)) {
            btnNewPatient.setOnClickListener(v -> navigateToFragment(new DeportistaFormFragment()));
            btnRegisterTest.setOnClickListener(v -> navigateToFragment(new TestCreateFragment()));
            btnViewReports.setOnClickListener(v -> navigateToFragment(new DeportistaListFragment()));
            btnMySchedule.setOnClickListener(v -> navigateToFragment(new CitaListFragment()));

            tvViewAllActivity.setOnClickListener(v -> {
                // TODO: Navigate to full activity list
            });
        } else {
            btnRutinas.setOnClickListener(v -> navigateToFragment(new TestListFragment()));
            btnProximaCita.setOnClickListener(v -> navigateToFragment(new CitaListFragment()));
            btnLogros.setOnClickListener(v -> navigateToFragment(new TestListFragment()));
            btnMetaMes.setOnClickListener(v -> navigateToFragment(new com.hyperreset.app.ui.perfil.PerfilFragment()));
        }
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null);
        transaction.commit();
    }

    // ==================================================================
    // Utility Methods
    // ==================================================================

    private String formatCitaDate(String fecha, String hora) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(fecha);
            if (date != null) {
                SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String todayStr = todayFormat.format(new Date());

                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
                String tomorrowStr = todayFormat.format(cal.getTime());

                String dayPrefix;
                if (fecha.equals(todayStr)) {
                    dayPrefix = "Hoy";
                } else if (fecha.equals(tomorrowStr)) {
                    dayPrefix = "Ma\u00f1ana";
                } else {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                    dayPrefix = capitalize(dayFormat.format(date));
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM", Locale.getDefault());
                String dateFormatted = dateFormat.format(date);

                String timeFormatted = hora;
                if (hora != null && hora.length() > 5) {
                    timeFormatted = hora.substring(0, 5);
                }

                return dayPrefix + ", " + dateFormatted + " - " + timeFormatted;
            }
        } catch (Exception e) {
            // Fallback
        }
        return (fecha != null ? fecha : "") + " - " + (hora != null ? hora : "");
    }

    private String formatRelativeTime(String timestamp) {
        if (timestamp == null) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);
            if (date != null) {
                long diff = new Date().getTime() - date.getTime();
                long minutes = diff / (1000 * 60);
                long hours = minutes / 60;
                long days = hours / 24;

                if (minutes < 1) return "Ahora";
                if (minutes < 60) return minutes + "m";
                if (hours < 24) return hours + "h";
                if (days < 7) return days + "d";
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
                return dateFormat.format(date);
            }
        } catch (Exception e) {
            // Fallback
        }
        return timestamp;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void animateProgressBar(final View progressFill, int percent) {
        if (progressFill == null) return;
        final int targetWidthPercent = Math.min(percent, 100);
        progressFill.post(() -> {
            View parent = (View) progressFill.getParent();
            if (parent != null) {
                int parentWidth = parent.getWidth();
                int targetWidth = (parentWidth * targetWidthPercent) / 100;
                ViewGroup.LayoutParams params = progressFill.getLayoutParams();
                params.width = targetWidth;
                progressFill.setLayoutParams(params);
            }
        });
    }
}
