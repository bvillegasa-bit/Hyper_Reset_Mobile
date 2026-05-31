package com.hyperreset.app.ui.citas.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.ui.citas.detail.CitaDetailFragment;
import com.hyperreset.app.ui.citas.form.CitaFormFragment;
import com.hyperreset.app.utils.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that displays a list of appointments (citas).
 * Supports filtering by date range, and navigation to create/detail screens.
 */
public class CitaListFragment extends Fragment {

    private CitaListViewModel viewModel;
    private CitaListAdapter adapter;
    private RecyclerView rvCitas;
    private Spinner spinnerDateFilter;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private FloatingActionButton fabCreateCita;

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

        initViews(view);
        setupRecyclerView();
        setupSpinner();
        setupFAB();
        setupObservers();

        // Load data
        viewModel.loadCitas();
    }

    private void initViews(View view) {
        rvCitas = view.findViewById(R.id.rvCitas);
        spinnerDateFilter = view.findViewById(R.id.spinnerDateFilter);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);
        fabCreateCita = view.findViewById(R.id.fabCreateCita);

        // Wire retry button
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> viewModel.loadCitas());
        view.findViewById(R.id.btnEmptyCreate).setOnClickListener(v -> navigateToCreate());
    }

    private void setupRecyclerView() {
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
        rvCitas.setAdapter(adapter);
    }

    private void setupSpinner() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.citas_filter_todas));
        items.add(getString(R.string.citas_filter_hoy));
        items.add(getString(R.string.citas_filter_semana));
        items.add(getString(R.string.citas_filter_mes));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDateFilter.setAdapter(spinnerAdapter);

        spinnerDateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal = Calendar.getInstance();

                switch (position) {
                    case 0: // Todas
                        viewModel.loadCitas();
                        break;
                    case 1: // Hoy
                        String today = sdf.format(cal.getTime());
                        viewModel.loadCitasByDateRange(today, today);
                        break;
                    case 2: // Esta semana
                        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                        String weekStart = sdf.format(cal.getTime());
                        cal.add(Calendar.DAY_OF_WEEK, 6);
                        String weekEnd = sdf.format(cal.getTime());
                        viewModel.loadCitasByDateRange(weekStart, weekEnd);
                        break;
                    case 3: // Este mes
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        String monthStart = sdf.format(cal.getTime());
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        String monthEnd = sdf.format(cal.getTime());
                        viewModel.loadCitasByDateRange(monthStart, monthEnd);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.loadCitas();
            }
        });
    }

    private void setupFAB() {
        fabCreateCita.setOnClickListener(v -> navigateToCreate());
    }

    private void navigateToCreate() {
        CitaFormFragment formFragment = new CitaFormFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupObservers() {
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
                        rvCitas.setVisibility(View.VISIBLE);
                        adapter.updateData(resource.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    layoutError.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }
}
