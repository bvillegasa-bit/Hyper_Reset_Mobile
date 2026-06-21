package com.hyperreset.app.ui.materiales.detail;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.data.repository.MaterialRepository;
import com.hyperreset.app.ui.materiales.form.MaterialFormFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

/**
 * Fragment showing detailed view of an educational material.
 * Displays title, description, type, and URL. Shows Open/Edit/Delete buttons
 * based on user role (Edit/Delete for COACH only).
 */
public class MaterialDetailFragment extends Fragment {

    private MaterialDetailViewModel viewModel;
    private long materialId;

    private TextView tvTitulo;
    private TextView badgeTipo;
    private TextView tvDescripcion;
    private TextView tvFecha;
    private TextView tvUrl;
    private View progressLoading;
    private MaterialButton btnOpenUrl;
    private View layoutCoachActions;
    private MaterialButton btnEditar;
    private MaterialButton btnEliminar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            materialId = args.getLong("materialId", -1);
        }

        viewModel = new MaterialDetailViewModel();

        initViews(view);
        setupObservers();
        setupActionButtons();

        if (materialId > 0) {
            viewModel.loadMaterial(materialId);
        }
    }

    private void initViews(View view) {
        tvTitulo = view.findViewById(R.id.tvTitulo);
        badgeTipo = view.findViewById(R.id.badgeTipo);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);
        tvFecha = view.findViewById(R.id.tvFecha);
        tvUrl = view.findViewById(R.id.tvUrl);
        progressLoading = view.findViewById(R.id.progressLoading);
        btnOpenUrl = view.findViewById(R.id.btnOpenUrl);
        layoutCoachActions = view.findViewById(R.id.layoutCoachActions);
        btnEditar = view.findViewById(R.id.btnEditar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
    }

    private void setupActionButtons() {
        btnOpenUrl.setOnClickListener(v -> openUrlInBrowser());

        btnEditar.setOnClickListener(v -> navigateToEdit());

        btnEliminar.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupObservers() {
        viewModel.getMaterial().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                progressLoading.setVisibility(View.VISIBLE);
                btnOpenUrl.setVisibility(View.GONE);
                layoutCoachActions.setVisibility(View.GONE);
            } else {
                progressLoading.setVisibility(View.GONE);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    bindMaterialData(resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al cargar el material",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindMaterialData(MaterialResponse material) {
        tvTitulo.setText(material.getTitulo() != null ? material.getTitulo() : "-");
        tvDescripcion.setText(material.getDescripcion() != null ? material.getDescripcion() : "-");
        tvFecha.setText(material.getFechaPublicacion() != null ? material.getFechaPublicacion() : "-");
        tvUrl.setText(material.getUrlRecurso() != null ? material.getUrlRecurso() : "-");

        setTipoBadge(material.getTipoMaterial());

        // Show open button if URL is present
        if (material.getUrlRecurso() != null && !material.getUrlRecurso().isEmpty()) {
            btnOpenUrl.setVisibility(View.VISIBLE);
        } else {
            btnOpenUrl.setVisibility(View.GONE);
        }

        // Show coach actions for COACH role only
        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.getAuthResponse() != null
                && "COACH".equals(sessionManager.getAuthResponse().getRol())) {
            layoutCoachActions.setVisibility(View.VISIBLE);
        } else {
            layoutCoachActions.setVisibility(View.GONE);
        }
    }

    private void setTipoBadge(String tipoMaterial) {
        if (tipoMaterial == null) {
            badgeTipo.setText("");
            badgeTipo.setVisibility(View.GONE);
            return;
        }

        badgeTipo.setVisibility(View.VISIBLE);

        int color;
        int labelResId;

        switch (tipoMaterial.toUpperCase()) {
            case "VIDEO":
                color = ContextCompat.getColor(requireContext(), R.color.hyper_excelente);
                labelResId = R.string.materiales_type_video;
                break;
            case "PDF":
                color = ContextCompat.getColor(requireContext(), R.color.hyper_bueno);
                labelResId = R.string.materiales_type_pdf;
                break;
            case "ENLACE":
            default:
                color = ContextCompat.getColor(requireContext(), R.color.hyper_accent);
                labelResId = R.string.materiales_type_enlace;
                break;
        }

        badgeTipo.setText(getString(labelResId));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16f);
        drawable.setColor(color);
        badgeTipo.setBackground(drawable);
    }

    private void openUrlInBrowser() {
        String url = tvUrl.getText().toString();
        if (url == null || url.isEmpty() || "-".equals(url)) {
            return;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Snackbar.make(requireView(), getString(R.string.material_error_open_link), Snackbar.LENGTH_LONG).show();
        }
    }

    private void navigateToEdit() {
        Bundle args = new Bundle();
        args.putLong("materialId", materialId);
        MaterialFormFragment formFragment = new MaterialFormFragment();
        formFragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.materiales_delete_title)
                .setPositiveButton(R.string.materiales_delete_confirm, (dialog, which) -> {
                    deleteMaterial();
                })
                .setNegativeButton(R.string.materiales_delete_cancel, null)
                .show();
    }

    private void deleteMaterial() {
        viewModel.deleteMaterial(materialId, new MaterialRepository.ResourceCallback<Void>() {
            @Override
            public void onResult(Resource<Void> resource) {
                if (resource.status == Resource.Status.SUCCESS) {
                    Snackbar.make(requireView(),
                            R.string.materiales_success_deleted,
                            Snackbar.LENGTH_SHORT).show();
                    // Navigate back to list
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al eliminar el material",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
