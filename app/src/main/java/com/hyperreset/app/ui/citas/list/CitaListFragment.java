package com.hyperreset.app.ui.citas.list;

import com.google.android.material.color.MaterialColors;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.ui.citas.detail.CitaDetailFragment;
import com.hyperreset.app.ui.citas.form.CitaFormFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Redesigned weekly calendar fragment for appointments.
 * Features:
 * - Week navigation (< >)
 * - 7-day horizontal selector (L M M J V S D)
 * - Filtered appointment list by selected day (local filtering)
 * - Role-based: COACH="Mi Agenda", DEPORTISTA="Citas"
 * - DEPORTISTA: "Agendar Nueva Cita" card
 * - FAB "+" only for COACH
 */
public class CitaListFragment extends Fragment {

    private CitaListViewModel viewModel;
    private CitaListAdapter adapter;
    private SessionManager sessionManager;

    // Views
    private TextView tvTitle;
    private TextView tvMonthSubtitle;
    private TextView tvDayHeader;
    private TextView tvAppointmentCount;
    private RecyclerView rvCitas;
    private ProgressBar progressLoading;
    private LinearLayout layoutError;
    private LinearLayout layoutEmpty;
    private MaterialCardView cardScheduleAppointment;
    private FloatingActionButton fabCreateCita;
    private MaterialButton btnCreateCita;
    private ImageButton btnPrevWeek;
    private ImageButton btnNextWeek;
    private LinearLayout layoutDays;
    private View layoutDayHeader;

    // Day buttons
    private final List<View> dayButtons = new ArrayList<>();
    private final String[] dayAbbr = {"L", "M", "M", "J", "V", "S", "D"};

    // Date formatting
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private final SimpleDateFormat dayHeaderFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

    // State
    private boolean isCoach = true;
    private long deportistaId;
    private Calendar todayCal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cita_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new CitaListViewModel();
        sessionManager = new SessionManager(requireContext());

        isCoach = !sessionManager.isDeportista();
        deportistaId = sessionManager.getDeportistaId();
        todayCal = Calendar.getInstance();

        initViews(view);
        setupWeekNavigation();
        setupDayButtons();
        setupRoleBasedUI();
        setupObservers();

        // Load data based on role
        loadWeekData();
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        tvMonthSubtitle = view.findViewById(R.id.tvMonthSubtitle);
        tvDayHeader = view.findViewById(R.id.tvDayHeader);
        tvAppointmentCount = view.findViewById(R.id.tvAppointmentCount);
        rvCitas = view.findViewById(R.id.rvCitas);
        progressLoading = view.findViewById(R.id.progressLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        cardScheduleAppointment = view.findViewById(R.id.cardScheduleAppointment);
        fabCreateCita = view.findViewById(R.id.fabCreateCita);
        btnCreateCita = view.findViewById(R.id.btnCreateCita);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);
        layoutDays = view.findViewById(R.id.layoutDays);
        layoutDayHeader = view.findViewById(R.id.layoutDayHeader);

        // Setup RecyclerView
        adapter = new CitaListAdapter(new ArrayList<>(), cita -> {
            // Navigate to detail
            Bundle args = new Bundle();
            args.putLong("citaId", cita.getId());
            CitaDetailFragment detailFragment = new CitaDetailFragment();
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        adapter.setIsCoach(isCoach);
        rvCitas.setAdapter(adapter);

        // Retry button
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> loadWeekData());
    }

    private void setupWeekNavigation() {
        btnPrevWeek.setOnClickListener(v -> {
            viewModel.navigateWeek(-1);
            updateDayButtonsFromViewModel();
            // Reload data for the new week
            loadWeekData();
        });

        btnNextWeek.setOnClickListener(v -> {
            viewModel.navigateWeek(1);
            updateDayButtonsFromViewModel();
            loadWeekData();
        });
    }

    private void setupDayButtons() {
        layoutDays.removeAllViews();
        dayButtons.clear();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(2, 0, 2, 0);

        for (int i = 0; i < 7; i++) {
            View dayView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_day_button, layoutDays, false);
            layoutDays.addView(dayView, params);
            dayButtons.add(dayView);

            final int index = i;
            dayView.setOnClickListener(v -> {
                String dateStr = getDateForDayIndex(index);
                viewModel.selectDay(dateStr);
                updateDaySelection(index);
            });
        }

        // Initialize with current week
        updateDayButtonsFromViewModel();
    }

    /**
     * Resolve a theme attribute color to a concrete color value.
     * Uses MaterialColors to properly handle Material theme attributes (like colorOnSurfaceVariant)
     * across all API levels.
     */
    private int getThemeColor(int attrRes) {
        return MaterialColors.getColor(requireContext(), attrRes,
            ContextCompat.getColor(requireContext(), R.color.hyper_on_surface));
    }

    /**
     * Update the 7 day buttons with dates from the current week.
     */
    private void updateDayButtonsFromViewModel() {
        Calendar weekStart = viewModel.getCurrentWeekStart();
        String todayStr = dateFormat.format(todayCal.getTime());

        for (int i = 0; i < 7 && i < dayButtons.size(); i++) {
            Calendar dayCal = (Calendar) weekStart.clone();
            dayCal.add(Calendar.DAY_OF_YEAR, i);
            String dateStr = dateFormat.format(dayCal.getTime());
            int dayNum = dayCal.get(Calendar.DAY_OF_MONTH);

            View dayView = dayButtons.get(i);
            TextView tvAbbr = dayView.findViewById(R.id.tvDayAbbr);
            TextView tvDate = dayView.findViewById(R.id.tvDayDate);

            tvAbbr.setText(dayAbbr[i]);
            tvDate.setText(String.valueOf(dayNum));

            // Tag the view with the date for click handling
            dayView.setTag(dateStr);

            // Style: selected = gradient, today (not selected) = accent border
            boolean isToday = dateStr.equals(todayStr);
            String selectedDay = viewModel.getSelectedDay().getValue();

            if (dateStr.equals(selectedDay)) {
                // Selected day: gradient background, white text
                dayView.setBackgroundResource(R.drawable.bg_card_gradient_primary_accent);
                tvAbbr.setTextColor(ContextCompat.getColor(requireContext(), R.color.hyper_on_primary));
                tvDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.hyper_on_primary));
            } else {
                // Non-selected: surface background, muted text
                dayView.setBackgroundResource(R.drawable.bg_day_button_default);
                tvAbbr.setTextColor(getThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant));
                tvDate.setTextColor(getThemeColor(com.google.android.material.R.attr.colorOnSurface));
            }
        }

        // Update month subtitle
        String monthStr = monthFormat.format(weekStart.getTime());
        // Capitalize first letter
        if (monthStr.length() > 0) {
            monthStr = monthStr.substring(0, 1).toUpperCase() + monthStr.substring(1);
        }
        tvMonthSubtitle.setText(monthStr);
    }

    /**
     * Highlight the selected day button.
     */
    private void updateDaySelection(int selectedIndex) {
        String todayStr = dateFormat.format(todayCal.getTime());

        for (int i = 0; i < dayButtons.size(); i++) {
            View dayView = dayButtons.get(i);
            String dateStr = (String) dayView.getTag();
            TextView tvAbbr = dayView.findViewById(R.id.tvDayAbbr);
            TextView tvDate = dayView.findViewById(R.id.tvDayDate);

            if (i == selectedIndex || (dateStr != null && dateStr.equals(viewModel.getSelectedDay().getValue()))) {
                dayView.setBackgroundResource(R.drawable.bg_card_gradient_primary_accent);
                tvAbbr.setTextColor(ContextCompat.getColor(requireContext(), R.color.hyper_on_primary));
                tvDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.hyper_on_primary));
            } else {
                dayView.setBackgroundResource(R.drawable.bg_day_button_default);
                tvAbbr.setTextColor(getThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant));
                tvDate.setTextColor(getThemeColor(com.google.android.material.R.attr.colorOnSurface));
            }
        }
    }

    /**
     * Get the yyyy-MM-dd date string for the day at position index in the current week.
     */
    private String getDateForDayIndex(int index) {
        Calendar weekStart = viewModel.getCurrentWeekStart();
        Calendar dayCal = (Calendar) weekStart.clone();
        dayCal.add(Calendar.DAY_OF_YEAR, index);
        return dateFormat.format(dayCal.getTime());
    }

    private void setupRoleBasedUI() {
        if (isCoach) {
            // COACH: title "Mi Agenda", show FAB + "Nueva" button
            tvTitle.setText(R.string.citas_calendar_title_coach);
            fabCreateCita.setVisibility(View.VISIBLE);
            btnCreateCita.setVisibility(View.VISIBLE);
            cardScheduleAppointment.setVisibility(View.GONE);

            fabCreateCita.setOnClickListener(v -> navigateToCreate());
            btnCreateCita.setOnClickListener(v -> navigateToCreate());
        } else {
            // DEPORTISTA: title "Citas", hide FAB + "Nueva", show schedule card
            tvTitle.setText(R.string.citas_calendar_title_deportista);
            fabCreateCita.setVisibility(View.GONE);
            btnCreateCita.setVisibility(View.GONE);
            cardScheduleAppointment.setVisibility(View.VISIBLE);

            // Schedule appointment button (placeholder)
            MaterialButton btnSchedule = cardScheduleAppointment.findViewById(R.id.btnScheduleAppointment);
            btnSchedule.setOnClickListener(v -> {
                // Navigate to cita form or show message
                navigateToCreate();
            });
        }
    }

    private void loadWeekData() {
        if (isCoach) {
            viewModel.loadCitasPorSemanaCoach();
        } else {
            viewModel.loadCitasPorSemanaDeportista(deportistaId);
        }
    }

    /**
     * Apply a fade-in animation (300ms) to the given content view.
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

    private void setupObservers() {
        // Observe filtered day appointments
        viewModel.getCitas().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvCitas.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        fadeInContent(rvCitas);
                        adapter.updateData(resource.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    updateDayHeader();
                    break;
                case ERROR:
                    layoutError.setVisibility(View.VISIBLE);
                    break;
            }
        });

        // Observe loading state
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                progressLoading.setVisibility(View.VISIBLE);
            }
        });

        // Observe selected day changes
        viewModel.getSelectedDay().observe(getViewLifecycleOwner(), day -> {
            if (day != null) {
                updateDayButtonsFromViewModel();
                updateDayHeader();
            }
        });
    }

    /**
     * Format and update the day header (e.g. "Miércoles, 13 de Mayo" + "4 citas").
     */
    private void updateDayHeader() {
        String dayStr = viewModel.getSelectedDay().getValue();
        if (dayStr == null) return;

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(dayStr));

            String dayName = dayHeaderFormat.format(cal.getTime());
            int dayNum = cal.get(Calendar.DAY_OF_MONTH);
            String monthName = getMonthName(cal.get(Calendar.MONTH));

            // Capitalize first letter of day name
            if (dayName.length() > 0) {
                dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
            }

            tvDayHeader.setText(String.format("%s, %d de %s", dayName, dayNum, monthName));

            // Count citas for this day
            Resource<List<CitaResponse>> resource = viewModel.getCitas().getValue();
            int count = 0;
            if (resource != null && resource.status == Resource.Status.SUCCESS
                    && resource.data != null) {
                count = resource.data.size();
            }
            tvAppointmentCount.setText(requireContext().getString(
                    R.string.citas_calendar_appointment_count, count));

        } catch (Exception e) {
            tvDayHeader.setText(dayStr);
            tvAppointmentCount.setText("");
        }
    }

    private String getMonthName(int monthIndex) {
        String[] months = {
                requireContext().getString(R.string.month_enero),
                requireContext().getString(R.string.month_febrero),
                requireContext().getString(R.string.month_marzo),
                requireContext().getString(R.string.month_abril),
                requireContext().getString(R.string.month_mayo),
                requireContext().getString(R.string.month_junio),
                requireContext().getString(R.string.month_julio),
                requireContext().getString(R.string.month_agosto),
                requireContext().getString(R.string.month_septiembre),
                requireContext().getString(R.string.month_octubre),
                requireContext().getString(R.string.month_noviembre),
                requireContext().getString(R.string.month_diciembre)
        };
        if (monthIndex >= 0 && monthIndex < months.length) {
            return months[monthIndex];
        }
        return "";
    }

    private void navigateToCreate() {
        CitaFormFragment formFragment = new CitaFormFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }
}
