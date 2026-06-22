package com.hyperreset.app.ui.tests.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.TipoTestEstadoResponse;
import com.hyperreset.app.ui.tests.create.TestCreateFragment;
import com.hyperreset.app.ui.tests.detail.TestDetailFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Redesigned test list fragment showing 8 fixed physical test cards (batería).
 * - DEPORTISTA: loads tipos-con-estado from endpoint, no filter, no FAB
 * - COACH: existing behavior + spinner filter + FAB
 */
public class TestListFragment extends Fragment {

    private TestListViewModel viewModel;
    private TestBateriaAdapter adapter;
    private RecyclerView rvTests;
    private Spinner spinnerFilter;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private FloatingActionButton fabCreateTest;
    private TextView tvProgressCount;
    private View progressFill;
    private View progressTrack;

    // COACH-specific state
    private List<DeportistaResponse> deportistaList = new ArrayList<>();
    private boolean spinnerInitializing = true;

    // Currently selected deportista for navigation
    private long currentDeportistaId = -1;
    private String currentDeportistaNombre = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new TestListViewModel();

        initViews(view);
        setupRecyclerView();
        setupFAB();
        setupObservers();

        SessionManager sessionManager = new SessionManager(requireContext());

        // Check if a specific deportistaId was passed in arguments
        Bundle args = getArguments();
        long argDepId = args != null ? args.getLong("deportistaId", -1) : -1;

        if (argDepId > 0) {
            // Called from another module (e.g. Reportes) with a pre-selected deportista
            spinnerFilter.setVisibility(View.GONE);
            fabCreateTest.setVisibility(View.GONE);
            currentDeportistaId = argDepId;
            currentDeportistaNombre = args.getString("deportistaNombre", "");
            viewModel.loadTiposTestConEstado(currentDeportistaId);
        } else if (sessionManager.isDeportista()) {
            // DEPORTISTA: hide COACH-only views, load tipos-con-estado
            spinnerFilter.setVisibility(View.GONE);
            fabCreateTest.setVisibility(View.GONE);
            currentDeportistaId = sessionManager.getDeportistaId();
            viewModel.loadTiposTestConEstado(currentDeportistaId);
        } else {
            // COACH: setup spinner filter, show FAB
            setupSpinner();
            long coachId = sessionManager.getUserId();
            viewModel.loadDeportistas(coachId);
        }
    }

    private void initViews(View view) {
        rvTests = view.findViewById(R.id.rvTests);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);
        fabCreateTest = view.findViewById(R.id.fabCreateTest);
        tvProgressCount = view.findViewById(R.id.tvProgressCount);
        progressFill = view.findViewById(R.id.progressFill);
        progressTrack = view.findViewById(R.id.progressTrack);

        // Wire retry button to reload based on role
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> {
            SessionManager sm = new SessionManager(requireContext());
            if (sm.isDeportista()) {
                viewModel.loadTiposTestConEstado(sm.getDeportistaId());
            } else {
                // COACH: reload from current spinner selection
                int pos = spinnerFilter.getSelectedItemPosition();
                if (pos > 0 && pos - 1 < deportistaList.size()) {
                    long depId = deportistaList.get(pos - 1).getId();
                    viewModel.loadTiposTestConEstado(depId);
                } else {
                    viewModel.loadDeportistas(sm.getUserId());
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new TestBateriaAdapter(new ArrayList<>(), test -> {
            // Both DEPORTISTA and COACH navigate to detail
            navigateToDetail(test);
        });
        rvTests.setAdapter(adapter);
    }

    private void setupSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Todos los deportistas");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, items);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);
        spinnerFilter.setVisibility(View.VISIBLE);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Skip the first automatic trigger from setAdapter() during init
                if (spinnerInitializing) {
                    spinnerInitializing = false;
                    return;
                }
                if (position == 0) {
                    // "Todos" — load all test sessions (original behavior)
                    viewModel.loadTests();
                } else if (position - 1 < deportistaList.size()) {
                    // Selected a specific deportista — load their tipos-con-estado
                    DeportistaResponse selected = deportistaList.get(position - 1);
                    currentDeportistaId = selected.getId();
                    currentDeportistaNombre = selected.getNombreCompleto();
                    viewModel.loadTiposTestConEstado(currentDeportistaId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (!spinnerInitializing) {
                    viewModel.loadTests();
                }
            }
        });
    }

    private void setupFAB() {
        fabCreateTest.setOnClickListener(v -> navigateToCreate());
    }

    // ==================================================================
    // Navigation
    // ==================================================================

    private void navigateToDetail(TipoTestEstadoResponse test) {
        Bundle args = new Bundle();
        args.putString("tipoTest", test.getTipoTest());
        args.putString("testName", test.getNombre());
        args.putBoolean("completado", test.isCompletado());
        if (test.getUltimoValor() != null) {
            args.putDouble("ultimoValor", test.getUltimoValor());
        }
        if (test.getUnidad() != null) {
            args.putString("unidad", test.getUnidad());
        }
        if (test.getFechaUltimo() != null) {
            args.putString("fechaUltimo", test.getFechaUltimo());
        }
        if (test.getCalificacion() != null && !test.getCalificacion().isEmpty()) {
            args.putString("calificacion", test.getCalificacion());
        }
        // Pass deportista context for executing the test
        args.putLong("deportistaId", currentDeportistaId);
        args.putString("deportistaNombre", currentDeportistaNombre);
        // Pasar idTestFisico como testId (solo si completado y no nulo)
        args.putLong("testId", test.isCompletado() && test.getIdTestFisico() != null
                ? test.getIdTestFisico() : -1L);
        TestDetailFragment detailFragment = new TestDetailFragment();
        detailFragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToCreate() {
        TestCreateFragment createFragment = new TestCreateFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, createFragment)
                .addToBackStack(null)
                .commit();
    }

    // ==================================================================
    // Observers
    // ==================================================================

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
        // Observe tipos test con estado (used by both roles)
        viewModel.getTiposTestConEstado().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvTests.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        fadeInContent(rvTests);
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

        // Observe progress (completados/total)
        viewModel.getCompletadosCount().observe(getViewLifecycleOwner(), completados -> {
            Integer total = viewModel.getTotalCount().getValue();
            if (total == null) total = 8;
            if (completados == null) completados = 0;
            tvProgressCount.setText(completados + "/" + total);
            animateProgressBar(completados, total);
        });

        // Observe deportistas for COACH spinner
        viewModel.getDeportistas().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS
                    && resource.data != null) {
                deportistaList = resource.data;
                List<String> items = new ArrayList<>();
                items.add("Todos los deportistas");
                for (DeportistaResponse d : resource.data) {
                    items.add(d.getNombreCompleto());
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        requireContext(), R.layout.spinner_item, items);
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerInitializing = true;
                spinnerFilter.setAdapter(spinnerAdapter);

                // Auto-load the first deportista's battery tests after spinner is ready
                if (!deportistaList.isEmpty()) {
                    // Use a small delay to let the spinner settle after setAdapter
                    spinnerFilter.post(() -> {
                        if (isAdded()) {
                            spinnerFilter.setSelection(1);
                            viewModel.loadTiposTestConEstado(deportistaList.get(0).getId());
                        }
                    });
                }
            }
        });
    }

    // ==================================================================
    // Progress bar animation
    // ==================================================================

    private void animateProgressBar(int completed, int total) {
        if (progressFill == null || progressTrack == null) return;
        final int targetPercent = total > 0 ? (completed * 100) / total : 0;
        progressFill.post(() -> {
            int parentWidth = progressTrack.getWidth();
            if (parentWidth <= 0) return;
            int targetWidth = (parentWidth * targetPercent) / 100;
            ViewGroup.LayoutParams params = progressFill.getLayoutParams();
            params.width = targetWidth;
            progressFill.setLayoutParams(params);
        });
    }
}
