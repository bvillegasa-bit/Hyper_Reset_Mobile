package com.hyperreset.app.ui.materiales.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.ui.materiales.detail.MaterialDetailFragment;
import com.hyperreset.app.ui.materiales.form.MaterialFormFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.ArrayList;

/**
 * Fragment that displays a grid of educational materials.
 * Shows a FAB to create new materials for COACH role only.
 */
public class MaterialListFragment extends Fragment {

    private MaterialListViewModel viewModel;
    private MaterialListAdapter adapter;
    private RecyclerView rvMateriales;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private FloatingActionButton fabCreateMaterial;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new MaterialListViewModel();

        initViews(view);
        setupRecyclerView();
        setupFAB();
        setupObservers();

        // Load data
        viewModel.loadMateriales();
    }

    private void initViews(View view) {
        rvMateriales = view.findViewById(R.id.rvMateriales);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);
        fabCreateMaterial = view.findViewById(R.id.fabCreateMaterial);

        // Wire retry button
        view.findViewById(R.id.btnRetry).setOnClickListener(v -> viewModel.loadMateriales());
        view.findViewById(R.id.btnEmptyCreate).setOnClickListener(v -> navigateToCreate());
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rvMateriales.setLayoutManager(gridLayoutManager);

        adapter = new MaterialListAdapter(new ArrayList<>(), material -> {
            // Navigate to detail
            Bundle args = new Bundle();
            args.putLong("materialId", material.getId());
            MaterialDetailFragment detailFragment = new MaterialDetailFragment();
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvMateriales.setAdapter(adapter);
    }

    private void setupFAB() {
        // Only show FAB if user is COACH
        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.getAuthResponse() != null
                && "COACH".equals(sessionManager.getAuthResponse().getRol())) {
            fabCreateMaterial.setVisibility(View.VISIBLE);
            fabCreateMaterial.setOnClickListener(v -> navigateToCreate());
        } else {
            fabCreateMaterial.setVisibility(View.GONE);
        }
    }

    private void navigateToCreate() {
        MaterialFormFragment formFragment = new MaterialFormFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupObservers() {
        viewModel.getMateriales().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvMateriales.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        rvMateriales.setVisibility(View.VISIBLE);
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
