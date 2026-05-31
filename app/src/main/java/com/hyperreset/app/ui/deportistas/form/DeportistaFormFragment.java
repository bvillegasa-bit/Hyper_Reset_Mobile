package com.hyperreset.app.ui.deportistas.form;

import android.app.DatePickerDialog;
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
import com.google.android.material.textfield.TextInputEditText;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.ui.deportistas.detail.DeportistaDetailFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for creating or editing an athlete (deportista).
 * Dual mode: create (POST) when no deportistaId argument, edit (PUT) when deportistaId is present.
 * CoachId is obtained from SharedPreferences via SessionManager.
 */
public class DeportistaFormFragment extends Fragment {

    private DeportistaFormViewModel viewModel;
    private TextInputEditText etNombres;
    private TextInputEditText etApellidos;
    private TextInputEditText etEmail;
    private TextInputEditText etTelefono;
    private TextInputEditText etFechaNacimiento;
    private TextInputEditText etDireccion;
    private MaterialButton btnGuardar;
    private SessionManager sessionManager;

    private Long editDeportistaId = null;
    private String selectedFechaNacimiento = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deportista_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new DeportistaFormViewModel();
        sessionManager = new SessionManager(requireContext());

        // Check if we're in edit mode
        Bundle args = getArguments();
        if (args != null && args.containsKey("deportistaId")) {
            editDeportistaId = args.getLong("deportistaId");
        }

        initViews(view);
        setupFechaNacimientoPicker();
        setupObservers();

        if (editDeportistaId != null) {
            // Edit mode: load existing data
            viewModel.loadDeportista(editDeportistaId);
        }
    }

    private void initViews(View view) {
        etNombres = view.findViewById(R.id.etNombres);
        etApellidos = view.findViewById(R.id.etApellidos);
        etEmail = view.findViewById(R.id.etEmail);
        etTelefono = view.findViewById(R.id.etTelefono);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        etDireccion = view.findViewById(R.id.etDireccion);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // Update title for edit mode
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        if (editDeportistaId != null) {
            tvTitle.setText(R.string.deportistas_edit_title);
        }

        btnGuardar.setOnClickListener(v -> onSaveClick());
    }

    private void setupFechaNacimientoPicker() {
        etFechaNacimiento.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectedFechaNacimiento = sdf.format(selected.getTime());
                        // Display a user-friendly format
                        SimpleDateFormat displaySdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etFechaNacimiento.setText(displaySdf.format(selected.getTime()));
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            // Allow selecting any date (not just past)
            dialog.show();
        });
    }

    private void onSaveClick() {
        // Validate required fields
        String nombres = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
        String apellidos = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (nombres.isEmpty()) {
            Snackbar.make(requireView(), R.string.deportistas_validation_nombres,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        if (apellidos.isEmpty()) {
            Snackbar.make(requireView(), R.string.deportistas_validation_apellidos,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty()) {
            Snackbar.make(requireView(), R.string.deportistas_validation_email,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Build request body
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String direccion = etDireccion.getText() != null ? etDireccion.getText().toString().trim() : "";

        Map<String, Object> request = new HashMap<>();
        request.put("nombres", nombres);
        request.put("apellidos", apellidos);
        request.put("email", email);
        request.put("telefono", telefono);
        if (!selectedFechaNacimiento.isEmpty()) {
            request.put("fechaNacimiento", selectedFechaNacimiento);
        }
        request.put("direccion", direccion);

        // Get coach ID from shared preferences
        long coachId = sessionManager.getUserId();
        request.put("coachId", coachId);

        if (editDeportistaId != null) {
            viewModel.updateDeportista(editDeportistaId, request);
        } else {
            viewModel.createDeportista(request);
        }
    }

    private void setupObservers() {
        // Observe edit mode data (pre-populate)
        viewModel.getDeportistaToEdit().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                prePopulateForm(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                Snackbar.make(requireView(),
                        resource.message != null ? resource.message : "Error al cargar deportista",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        // Observe save result
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                btnGuardar.setEnabled(false);
                btnGuardar.setText(R.string.deportistas_form_guardando);
            } else {
                btnGuardar.setEnabled(true);
                btnGuardar.setText(R.string.deportistas_form_guardar);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    navigateToDetail(resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al guardar deportista",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void prePopulateForm(DeportistaResponse deportista) {
        // The DeportistaResponse from the API has nombreCompleto (combined),
        // but the form has separate nombres and apellidos.
        // We'll split the nombreCompleto by first space as best-effort,
        // and also pre-fill from the email, telefono, fechaNacimiento fields.
        String nombreCompleto = deportista.getNombreCompleto();
        if (nombreCompleto != null) {
            int spaceIndex = nombreCompleto.indexOf(' ');
            if (spaceIndex > 0) {
                etNombres.setText(nombreCompleto.substring(0, spaceIndex));
                etApellidos.setText(nombreCompleto.substring(spaceIndex + 1));
            } else {
                etNombres.setText(nombreCompleto);
            }
        }

        // Email
        etEmail.setText(deportista.getEmail() != null ? deportista.getEmail() : "");

        // Telefono
        etTelefono.setText(deportista.getTelefono() != null ? deportista.getTelefono() : "");

        // Fecha de Nacimiento (stored as yyyy-MM-dd)
        String fechaNac = deportista.getFechaNacimiento() != null ? deportista.getFechaNacimiento() : "";
        if (!fechaNac.isEmpty()) {
            selectedFechaNacimiento = fechaNac;
            try {
                SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat displaySdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etFechaNacimiento.setText(displaySdf.format(apiSdf.parse(fechaNac)));
            } catch (Exception e) {
                etFechaNacimiento.setText(fechaNac);
            }
        }

        // Nota: The backend response doesn't include "direccion" directly,
        // but the backend entity does have it. We leave it empty if not available.
    }

    private void navigateToDetail(DeportistaResponse deportista) {
        Bundle args = new Bundle();
        args.putLong("deportistaId", deportista.getId());
        DeportistaDetailFragment detailFragment = new DeportistaDetailFragment();
        detailFragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
