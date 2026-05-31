package com.hyperreset.app.ui.citas.detail;

import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
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
import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.data.repository.CitaRepository;
import com.hyperreset.app.ui.citas.form.CitaFormFragment;
import com.hyperreset.app.utils.Resource;

/**
 * Fragment showing detailed view of an appointment (cita).
 * Displays all fields, color-coded estado badge, and action buttons
 * for estado transitions (Confirmar/Completar/Cancelar), Edit, and Delete.
 */
public class CitaDetailFragment extends Fragment {

    private CitaDetailViewModel viewModel;
    private long citaId;

    private TextView tvCoachNombre;
    private TextView tvDeportistaNombre;
    private TextView tvFechaCita;
    private TextView tvHoraCita;
    private TextView badgeEstado;
    private TextView tvMotivoValue;
    private View cardNotas;
    private TextView tvNotasValue;
    private View progressLoading;
    private View layoutActions;
    private MaterialButton btnConfirmar;
    private MaterialButton btnCompletar;
    private MaterialButton btnCancelarEstado;
    private MaterialButton btnEditar;
    private MaterialButton btnEliminar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cita_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            citaId = args.getLong("citaId", -1);
        }

        viewModel = new CitaDetailViewModel();

        initViews(view);
        setupObservers();
        setupActionButtons();

        if (citaId > 0) {
            viewModel.loadCita(citaId);
        }
    }

    private void initViews(View view) {
        tvCoachNombre = view.findViewById(R.id.tvCoachNombre);
        tvDeportistaNombre = view.findViewById(R.id.tvDeportistaNombre);
        tvFechaCita = view.findViewById(R.id.tvFechaCita);
        tvHoraCita = view.findViewById(R.id.tvHoraCita);
        badgeEstado = view.findViewById(R.id.badgeEstado);
        tvMotivoValue = view.findViewById(R.id.tvMotivoValue);
        cardNotas = view.findViewById(R.id.cardNotas);
        tvNotasValue = view.findViewById(R.id.tvNotasValue);
        progressLoading = view.findViewById(R.id.progressLoading);
        layoutActions = view.findViewById(R.id.layoutActions);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);
        btnCompletar = view.findViewById(R.id.btnCompletar);
        btnCancelarEstado = view.findViewById(R.id.btnCancelarEstado);
        btnEditar = view.findViewById(R.id.btnEditar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
    }

    private void setupActionButtons() {
        btnConfirmar.setOnClickListener(v -> {
            if (citaId > 0) {
                viewModel.changeEstado(citaId, "CONFIRMADA");
            }
        });

        btnCompletar.setOnClickListener(v -> {
            if (citaId > 0) {
                viewModel.changeEstado(citaId, "COMPLETADA");
            }
        });

        btnCancelarEstado.setOnClickListener(v -> {
            if (citaId > 0) {
                viewModel.changeEstado(citaId, "CANCELADA");
            }
        });

        btnEditar.setOnClickListener(v -> navigateToEdit());

        btnEliminar.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupObservers() {
        viewModel.getCita().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                progressLoading.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.GONE);
            } else {
                progressLoading.setVisibility(View.GONE);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    bindCitaData(resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al cargar la cita",
                            Snackbar.LENGTH_LONG).show();
                    layoutActions.setVisibility(View.GONE);
                }
            }
        });
    }

    private void bindCitaData(CitaResponse cita) {
        tvCoachNombre.setText(cita.getCoachNombre() != null ? cita.getCoachNombre() : "-");
        tvDeportistaNombre.setText(cita.getDeportistaNombre() != null ? cita.getDeportistaNombre() : "-");
        tvFechaCita.setText(cita.getFechaCita() != null ? cita.getFechaCita() : "-");
        tvHoraCita.setText(cita.getHoraCita() != null ? cita.getHoraCita() : "-");
        tvMotivoValue.setText(cita.getMotivo() != null ? cita.getMotivo() : "-");

        // Notas section: show only if notas is present
        if (cita.getNotas() != null && !cita.getNotas().isEmpty()) {
            cardNotas.setVisibility(View.VISIBLE);
            tvNotasValue.setText(cita.getNotas());
        } else {
            cardNotas.setVisibility(View.GONE);
        }

        // Set estado badge
        setEstadoBadge(cita.getEstado());

        // Update action buttons based on current estado
        updateActionButtons(cita.getEstado());
    }

    private void setEstadoBadge(String estado) {
        if (estado == null) {
            badgeEstado.setText("");
            badgeEstado.setVisibility(View.GONE);
            return;
        }

        badgeEstado.setVisibility(View.VISIBLE);
        badgeEstado.setText(estado);

        int color;
        switch (estado) {
            case "PENDIENTE":
                color = ContextCompat.getColor(requireContext(), R.color.hyper_in_progress);
                break;
            case "CONFIRMADA":
                color = ContextCompat.getColor(requireContext(), R.color.hyper_bueno);
                break;
            case "COMPLETADA":
                color = ContextCompat.getColor(requireContext(), R.color.hyper_excelente);
                break;
            case "CANCELADA":
                color = ContextCompat.getColor(requireContext(), R.color.hyper_deficiente);
                break;
            default:
                color = ContextCompat.getColor(requireContext(), R.color.hyper_in_progress);
                break;
        }

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16f);
        drawable.setColor(color);
        badgeEstado.setBackground(drawable);
    }

    /**
     * Show/hide action buttons based on current estado per the visibility matrix:
     * | Estado      | Confirmar | Completar | Cancelar | Editar | Eliminar |
     * |-------------|-----------|-----------|----------|--------|----------|
     * | PENDIENTE   | ✅        | ❌        | ✅       | ✅     | ✅       |
     * | CONFIRMADA  | ❌        | ✅        | ✅       | ❌     | ❌       |
     * | COMPLETADA  | ❌        | ❌        | ❌       | ❌     | ❌       |
     * | CANCELADA   | ❌        | ❌        | ❌       | ❌     | ❌       |
     */
    private void updateActionButtons(String estado) {
        if (estado == null) {
            layoutActions.setVisibility(View.GONE);
            return;
        }

        layoutActions.setVisibility(View.VISIBLE);

        // Start with all buttons gone
        btnConfirmar.setVisibility(View.GONE);
        btnCompletar.setVisibility(View.GONE);
        btnCancelarEstado.setVisibility(View.GONE);
        btnEditar.setVisibility(View.GONE);
        btnEliminar.setVisibility(View.GONE);

        switch (estado) {
            case "PENDIENTE":
                btnConfirmar.setVisibility(View.VISIBLE);
                btnCancelarEstado.setVisibility(View.VISIBLE);
                btnEditar.setVisibility(View.VISIBLE);
                btnEliminar.setVisibility(View.VISIBLE);
                break;
            case "CONFIRMADA":
                btnCompletar.setVisibility(View.VISIBLE);
                btnCancelarEstado.setVisibility(View.VISIBLE);
                break;
            case "COMPLETADA":
            case "CANCELADA":
                // Terminal states — no action buttons
                break;
        }
    }

    private void navigateToEdit() {
        Bundle args = new Bundle();
        args.putLong("citaId", citaId);
        CitaFormFragment formFragment = new CitaFormFragment();
        formFragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.citas_delete_title)
                .setPositiveButton(R.string.citas_delete_confirm, (dialog, which) -> {
                    deleteCita();
                })
                .setNegativeButton(R.string.citas_delete_cancel, null)
                .show();
    }

    private void deleteCita() {
        viewModel.deleteCita(citaId, new CitaRepository.ResourceCallback<Void>() {
            @Override
            public void onResult(Resource<Void> resource) {
                if (resource.status == Resource.Status.SUCCESS) {
                    // Navigate back to list
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al eliminar la cita",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
