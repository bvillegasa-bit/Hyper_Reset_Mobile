package com.hyperreset.app.ui.deportistas.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.ui.deportistas.form.DeportistaFormFragment;
import com.hyperreset.app.ui.mensajes.form.MensajeFormFragment;
import com.hyperreset.app.ui.reportes.list.ReporteListFragment;
import com.hyperreset.app.utils.Resource;

/**
 * Fragment showing detailed view of an athlete (deportista).
 * Displays all fields and action buttons for Edit and View Test History.
 */
public class DeportistaDetailFragment extends Fragment {

    private DeportistaDetailViewModel viewModel;
    private long deportistaId;

    private TextView tvNombreCompleto;
    private TextView tvEmail;
    private TextView tvTelefono;
    private TextView tvFechaNacimiento;
    private TextView tvDireccion;
    private TextView tvFechaRegistro;
    private TextView tvCoachNombre;
    private View progressLoading;
    private View cardDetail;
    private View cardStats;
    private TextView tvTotalTests;
    private TextView tvLastTestDate;
    private MaterialButton btnEditar;
    private MaterialButton btnVerTests;
    private MaterialButton btnVerReportes;
    private MaterialButton btnEnviarMensaje;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deportista_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            deportistaId = args.getLong("deportistaId", -1);
        }

        viewModel = new DeportistaDetailViewModel();

        initViews(view);
        setupObservers();
        setupActionButtons();

        if (deportistaId > 0) {
            viewModel.loadDeportista(deportistaId);
        }
    }

    private void initViews(View view) {
        tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTelefono = view.findViewById(R.id.tvTelefono);
        tvFechaNacimiento = view.findViewById(R.id.tvFechaNacimiento);
        tvDireccion = view.findViewById(R.id.tvDireccion);
        tvFechaRegistro = view.findViewById(R.id.tvFechaRegistro);
        tvCoachNombre = view.findViewById(R.id.tvCoachNombre);
        progressLoading = view.findViewById(R.id.progressLoading);
        cardDetail = view.findViewById(R.id.cardDetail);
        cardStats = view.findViewById(R.id.cardStats);
        tvTotalTests = view.findViewById(R.id.tvTotalTests);
        tvLastTestDate = view.findViewById(R.id.tvLastTestDate);
        btnEditar = view.findViewById(R.id.btnEditar);
        btnVerTests = view.findViewById(R.id.btnVerTests);
        btnVerReportes = view.findViewById(R.id.btnVerReportes);
        btnEnviarMensaje = view.findViewById(R.id.btnEnviarMensaje);
    }

    private void setupActionButtons() {
        btnEditar.setOnClickListener(v -> navigateToEdit());
        btnVerTests.setOnClickListener(v -> navigateToTests());
        btnVerReportes.setOnClickListener(v -> navigateToReportes());
        btnEnviarMensaje.setOnClickListener(v -> navigateToSendMessage());
    }

    private void navigateToEdit() {
        Bundle args = new Bundle();
        args.putLong("deportistaId", deportistaId);
        DeportistaFormFragment formFragment = new DeportistaFormFragment();
        formFragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToTests() {
        // Navigate to tests list filtered by this deportista
        // For now, switch to the Tests tab in bottom nav
        requireActivity().getSupportFragmentManager().popBackStack();
        // The bottom nav will show the Tests tab
    }

    private void navigateToReportes() {
        Bundle args = new Bundle();
        args.putLong("deportistaId", deportistaId);
        ReporteListFragment fragment = new ReporteListFragment();
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToSendMessage() {
        Bundle args = new Bundle();
        args.putLong("deportistaId", deportistaId);
        MensajeFormFragment formFragment = new MensajeFormFragment();
        formFragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupObservers() {
        viewModel.getDeportista().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                progressLoading.setVisibility(View.VISIBLE);
                cardDetail.setVisibility(View.GONE);
                cardStats.setVisibility(View.GONE);
            } else {
                progressLoading.setVisibility(View.GONE);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    bindDeportistaData(resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al cargar deportista",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindDeportistaData(DeportistaResponse deportista) {
        tvNombreCompleto.setText(deportista.getNombreCompleto() != null
                ? deportista.getNombreCompleto() : "-");
        tvEmail.setText(deportista.getEmail() != null ? deportista.getEmail() : "-");
        tvTelefono.setText(deportista.getTelefono() != null ? deportista.getTelefono() : "-");
        tvFechaNacimiento.setText(deportista.getFechaNacimiento() != null
                ? deportista.getFechaNacimiento() : "-");
        tvFechaRegistro.setText(deportista.getFechaRegistro() != null
                ? deportista.getFechaRegistro() : "-");
        tvCoachNombre.setText(deportista.getCoachNombre() != null
                ? deportista.getCoachNombre() : "-");

        // Direccion is not in the current DeportistaResponse model,
        // but the backend entity supports it. Show placeholder.
        tvDireccion.setText("-");

        // Show stats card with placeholder data (actual stats would need another API call)
        cardStats.setVisibility(View.VISIBLE);
        tvTotalTests.setText("0");
        tvLastTestDate.setText("-");

        // Show the detail card
        cardDetail.setVisibility(View.VISIBLE);
    }
}
