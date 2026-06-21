package com.hyperreset.app.ui.perfil;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.utils.Resource;

import java.util.Calendar;

/**
 * Fragment that displays an editable profile form.
 * Loads current profile data, lets the user update fields,
 * and sends the update to the backend.
 */
public class EditProfileFragment extends Fragment {

    private EditProfileViewModel viewModel;

    private View formContent;
    private View progressLoading;
    private View layoutError;
    private View btnRetry;
    private View layoutSaving;

    private TextInputLayout tilNombres;
    private TextInputLayout tilApellidos;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilTelefono;
    private TextInputLayout tilDireccion;
    private TextInputLayout tilFechaNacimiento;

    private TextInputEditText etNombres;
    private TextInputEditText etApellidos;
    private TextInputEditText etCorreo;
    private TextInputEditText etTelefono;
    private TextInputEditText etDireccion;
    private TextInputEditText etFechaNacimiento;

    private MaterialButton btnSave;
    private MaterialButton btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new EditProfileViewModel();

        initViews(view);
        setupObservers();
        setupListeners();

        viewModel.loadProfile();
    }

    private void initViews(View view) {
        formContent = view.findViewById(R.id.formContent);
        progressLoading = view.findViewById(R.id.progressLoading);
        layoutError = view.findViewById(R.id.layoutError);
        btnRetry = view.findViewById(R.id.btnRetry);
        layoutSaving = view.findViewById(R.id.layoutSaving);

        tilNombres = view.findViewById(R.id.tilNombres);
        tilApellidos = view.findViewById(R.id.tilApellidos);
        tilCorreo = view.findViewById(R.id.tilCorreo);
        tilTelefono = view.findViewById(R.id.tilTelefono);
        tilDireccion = view.findViewById(R.id.tilDireccion);
        tilFechaNacimiento = view.findViewById(R.id.tilFechaNacimiento);

        etNombres = view.findViewById(R.id.etNombres);
        etApellidos = view.findViewById(R.id.etApellidos);
        etCorreo = view.findViewById(R.id.etCorreo);
        etTelefono = view.findViewById(R.id.etTelefono);
        etDireccion = view.findViewById(R.id.etDireccion);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);

        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void setupObservers() {
        viewModel.getProfile().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            formContent.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        formContent.setVisibility(View.VISIBLE);
                        bindProfile(resource.data);
                    } else {
                        layoutError.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    layoutError.setVisibility(View.VISIBLE);
                    break;
            }
        });

        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    layoutSaving.setVisibility(View.VISIBLE);
                    btnSave.setEnabled(false);
                    break;

                case SUCCESS:
                    layoutSaving.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(),
                            R.string.edit_profile_success, Toast.LENGTH_SHORT).show();
                    // Navigate back to profile screen
                    requireActivity().getSupportFragmentManager().popBackStack();
                    break;

                case ERROR:
                    layoutSaving.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    String msg = resource.message != null
                            ? resource.message
                            : getString(R.string.global_error_network);
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void bindProfile(AuthResponse profile) {
        etNombres.setText(profile.getNombre() != null ? profile.getNombre() : "");
        etApellidos.setText(profile.getApellidos() != null ? profile.getApellidos() : "");
        etCorreo.setText(profile.getEmail() != null ? profile.getEmail() : "");
        etTelefono.setText(profile.getTelefono() != null ? profile.getTelefono() : "");
        etDireccion.setText(profile.getDireccion() != null ? profile.getDireccion() : "");
        etFechaNacimiento.setText(profile.getFechaNacimiento() != null ? profile.getFechaNacimiento() : "");
    }

    private void setupListeners() {
        btnRetry.setOnClickListener(v -> viewModel.loadProfile());

        btnCancel.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        btnSave.setOnClickListener(v -> attemptSave());

        // Date picker for fecha de nacimiento
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());
        etFechaNacimiento.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker();
            }
        });
        tilFechaNacimiento.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        // If there's an existing date, parse it and use as default
        String currentDate = etFechaNacimiento.getText() != null
                ? etFechaNacimiento.getText().toString().trim() : "";
        if (!currentDate.isEmpty()) {
            try {
                String[] parts = currentDate.split("-");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1; // Calendar months are 0-based
                    int day = Integer.parseInt(parts[2]);
                    cal.set(year, month, day);
                }
            } catch (NumberFormatException ignored) {
                // Use current date as default
            }
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                R.style.HyperResetDatePicker,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format as yyyy-MM-dd
                    String formattedDate = String.format("%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    etFechaNacimiento.setText(formattedDate);
                }, year, month, day);

        datePicker.show();
    }

    private void attemptSave() {
        // Clear previous errors
        clearErrors();

        // Get values
        String nombres = etNombres.getText() != null
                ? etNombres.getText().toString().trim() : "";
        String apellidos = etApellidos.getText() != null
                ? etApellidos.getText().toString().trim() : "";
        String correo = etCorreo.getText() != null
                ? etCorreo.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null
                ? etTelefono.getText().toString().trim() : "";
        String direccion = etDireccion.getText() != null
                ? etDireccion.getText().toString().trim() : "";
        String fechaNacimiento = etFechaNacimiento.getText() != null
                ? etFechaNacimiento.getText().toString().trim() : "";

        boolean valid = true;

        if (nombres.isEmpty()) {
            tilNombres.setError(getString(R.string.edit_profile_error_empty_nombres));
            valid = false;
        }

        if (apellidos.isEmpty()) {
            tilApellidos.setError(getString(R.string.edit_profile_error_empty_apellidos));
            valid = false;
        }

        // Basic email validation
        if (!correo.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.edit_profile_error_invalid_correo));
            valid = false;
        }

        if (!valid) {
            return;
        }

        viewModel.updateProfile(nombres, apellidos, correo, telefono, direccion, fechaNacimiento);
    }

    private void clearErrors() {
        tilNombres.setError(null);
        tilNombres.setErrorEnabled(false);
        tilApellidos.setError(null);
        tilApellidos.setErrorEnabled(false);
        tilCorreo.setError(null);
        tilCorreo.setErrorEnabled(false);
        tilTelefono.setError(null);
        tilTelefono.setErrorEnabled(false);
        tilDireccion.setError(null);
        tilDireccion.setErrorEnabled(false);
        tilFechaNacimiento.setError(null);
        tilFechaNacimiento.setErrorEnabled(false);
    }
}
