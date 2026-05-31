package com.hyperreset.app.ui.tests.list;

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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.ui.tests.create.TestCreateFragment;
import com.hyperreset.app.ui.tests.detail.TestDetailFragment;
import com.hyperreset.app.utils.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays a list of physical test sessions.
 * Supports filtering by deportista, pull-to-refresh, and navigation to create/detail screens.
 */
public class TestListFragment extends Fragment {

    private TestListViewModel viewModel;
    private TestListAdapter adapter;
    private RecyclerView rvTests;
    private Spinner spinnerFilter;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private FloatingActionButton fabCreateTest;
    private List<DeportistaResponse> deportistaList = new ArrayList<>();

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
        setupSpinner();
        setupFAB();
        setupObservers();

        // Load data
        viewModel.loadTests();
        // For now load with coachId=0 — real coachId comes from shared preferences
        viewModel.loadDeportistas(0);
    }

    private void initViews(View view) {
        rvTests = view.findViewById(R.id.rvTests);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);
        fabCreateTest = view.findViewById(R.id.fabCreateTest);

        // Wire retry button
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> viewModel.loadTests());
        view.findViewById(R.id.btnEmptyCreate).setOnClickListener(v -> navigateToCreate());
    }

    private void setupRecyclerView() {
        adapter = new TestListAdapter(new ArrayList<>(), test -> {
            // Navigate to detail
            Bundle args = new Bundle();
            args.putLong("testId", test.getId());
            args.putString("tipoTest", test.getTipoTest());
            TestDetailFragment detailFragment = new TestDetailFragment();
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvTests.setAdapter(adapter);
    }

    private void setupSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Todos los deportistas");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    viewModel.loadTests();
                } else if (position - 1 < deportistaList.size()) {
                    viewModel.loadTestsByDeportista(deportistaList.get(position - 1).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.loadTests();
            }
        });
    }

    private void setupFAB() {
        fabCreateTest.setOnClickListener(v -> navigateToCreate());
    }

    private void navigateToCreate() {
        TestCreateFragment createFragment = new TestCreateFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, createFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupObservers() {
        viewModel.getTests().observe(getViewLifecycleOwner(), resource -> {
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
                        rvTests.setVisibility(View.VISIBLE);
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
                        requireContext(), android.R.layout.simple_spinner_item, items);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFilter.setAdapter(spinnerAdapter);
            }
        });
    }
}
