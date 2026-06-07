package com.hyperreset.app.ui.perfil;

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
import com.hyperreset.app.utils.Resource;

/**
 * Fragment that allows the user to change their password.
 * Provides fields for current password, new password, and confirmation,
 * with client-side validation and a loading overlay during submission.
 */
public class ChangePasswordFragment extends Fragment {

    private ChangePasswordViewModel viewModel;

    private View layoutSaving;

    private TextInputLayout tilCurrentPassword;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;

    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;

    private MaterialButton btnSave;
    private MaterialButton btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ChangePasswordViewModel();

        initViews(view);
        setupObservers();
        setupListeners();
    }

    private void initViews(View view) {
        layoutSaving = view.findViewById(R.id.layoutSaving);

        tilCurrentPassword = view.findViewById(R.id.tilCurrentPassword);
        tilNewPassword = view.findViewById(R.id.tilNewPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);

        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void setupObservers() {
        viewModel.getChangeResult().observe(getViewLifecycleOwner(), resource -> {
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
                            R.string.password_changed, Toast.LENGTH_SHORT).show();
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

        viewModel.getValidationError().observe(getViewLifecycleOwner(), error -> {
            if (error == null) return;

            clearErrors();

            // Map validation error codes to field-level errors
            switch (error) {
                case "password_mismatch":
                    tilConfirmPassword.setError(getString(R.string.password_mismatch));
                    break;
                case "password_too_short":
                    tilNewPassword.setError(getString(R.string.password_too_short));
                    break;
                case "same_password":
                    tilNewPassword.setError(getString(R.string.same_password));
                    break;
            }
        });
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        btnSave.setOnClickListener(v -> attemptSave());
    }

    private void attemptSave() {
        clearErrors();

        // Get values
        String currentPassword = etCurrentPassword.getText() != null
                ? etCurrentPassword.getText().toString() : "";
        String newPassword = etNewPassword.getText() != null
                ? etNewPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null
                ? etConfirmPassword.getText().toString() : "";

        // Client-side validation for required fields
        boolean valid = true;

        if (currentPassword.isEmpty()) {
            tilCurrentPassword.setError("La contraseña actual es obligatoria");
            valid = false;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("La nueva contraseña es obligatoria");
            valid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("La confirmación de contraseña es obligatoria");
            valid = false;
        }

        if (!valid) {
            return;
        }

        // Delegate business-rule validation and API call to ViewModel
        viewModel.changePassword(currentPassword, newPassword, confirmPassword);
    }

    /**
     * Clears all field-level error states.
     */
    private void clearErrors() {
        tilCurrentPassword.setError(null);
        tilCurrentPassword.setErrorEnabled(false);
        tilNewPassword.setError(null);
        tilNewPassword.setErrorEnabled(false);
        tilConfirmPassword.setError(null);
        tilConfirmPassword.setErrorEnabled(false);
    }
}
