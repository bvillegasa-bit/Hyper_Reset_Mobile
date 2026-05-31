package com.hyperreset.app.ui.deportistas.list;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.ui.deportistas.detail.DeportistaDetailFragment;
import com.hyperreset.app.ui.deportistas.form.DeportistaFormFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.ArrayList;

/**
 * Fragment that displays a list of athletes (deportistas) for the current coach.
 * Supports search/filter by name, pull-to-refresh, and navigation to create/detail screens.
 */
public class DeportistaListFragment extends Fragment {

    private DeportistaListViewModel viewModel;
    private DeportistaListAdapter adapter;
    private RecyclerView rvDeportistas;
    private TextInputEditText etSearch;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private FloatingActionButton fabCreateDeportista;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deportista_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new DeportistaListViewModel();
        sessionManager = new SessionManager(requireContext());

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupFAB();
        setupObservers();

        // Load data with coach ID from SharedPreferences
        long coachId = sessionManager.getUserId();
        viewModel.loadDeportistas(coachId);
    }

    private void initViews(View view) {
        rvDeportistas = view.findViewById(R.id.rvDeportistas);
        etSearch = view.findViewById(R.id.etSearch);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);
        fabCreateDeportista = view.findViewById(R.id.fabCreateDeportista);

        // Wire retry button
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> {
            long coachId = sessionManager.getUserId();
            viewModel.loadDeportistas(coachId);
        });
        view.findViewById(R.id.btnEmptyCreate).setOnClickListener(v -> navigateToCreate());
    }

    private void setupRecyclerView() {
        adapter = new DeportistaListAdapter(new ArrayList<>(), deportista -> {
            // Navigate to detail
            Bundle args = new Bundle();
            args.putLong("deportistaId", deportista.getId());
            DeportistaDetailFragment detailFragment = new DeportistaDetailFragment();
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvDeportistas.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFAB() {
        fabCreateDeportista.setOnClickListener(v -> navigateToCreate());
    }

    private void navigateToCreate() {
        DeportistaFormFragment formFragment = new DeportistaFormFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupObservers() {
        viewModel.getDeportistas().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvDeportistas.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        rvDeportistas.setVisibility(View.VISIBLE);
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
