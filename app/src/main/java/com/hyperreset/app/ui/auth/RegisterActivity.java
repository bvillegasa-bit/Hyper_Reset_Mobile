package com.hyperreset.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.data.repository.AuthRepository;
import com.hyperreset.app.ui.home.HomeActivity;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private SessionManager sessionManager;

    private TextInputLayout nombresLayout;
    private TextInputLayout apellidosLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText nombresEditText;
    private TextInputEditText apellidosEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private MaterialCardView deportistaCard;
    private MaterialCardView coachCard;
    private View deportistaIndicator;
    private View coachIndicator;

    private String selectedRole = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = ((com.hyperreset.app.HyperResetApplication) getApplication())
                .getAppContainer().getAuthRepository();
        sessionManager = new SessionManager(this);

        initViews();
        setupRoleSelector();
        setupListeners();
    }

    private void initViews() {
        View backButton = findViewById(R.id.backButton);
        nombresLayout = findViewById(R.id.nombresLayout);
        apellidosLayout = findViewById(R.id.apellidosLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        nombresEditText = findViewById(R.id.nombresEditText);
        apellidosEditText = findViewById(R.id.apellidosEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        deportistaCard = findViewById(R.id.deportistaCard);
        coachCard = findViewById(R.id.coachCard);
        deportistaIndicator = findViewById(R.id.deportistaIndicator);
        coachIndicator = findViewById(R.id.coachIndicator);

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupRoleSelector() {
        deportistaCard.setOnClickListener(v -> selectRole("DEPORTISTA"));
        coachCard.setOnClickListener(v -> selectRole("COACH"));
    }

    private void selectRole(String role) {
        if (role.equals(selectedRole)) return;
        selectedRole = role;
        updateRoleUI();
    }

    private void updateRoleUI() {
        int accentColor = ContextCompat.getColor(this, R.color.hyper_accent);
        int borderColor = ContextCompat.getColor(this, R.color.hyper_border);
        int surfaceColor = ContextCompat.getColor(this, R.color.hyper_surface);
        int surfaceVariantColor = ContextCompat.getColor(this, R.color.hyper_surface_variant);
        int strokePx = dpToPx(2);
        int defaultStrokePx = dpToPx(1);

        if ("DEPORTISTA".equals(selectedRole)) {
            deportistaCard.setCardBackgroundColor(surfaceVariantColor);
            deportistaCard.setStrokeColor(accentColor);
            deportistaCard.setStrokeWidth(strokePx);
            deportistaIndicator.setVisibility(View.VISIBLE);

            coachCard.setCardBackgroundColor(surfaceColor);
            coachCard.setStrokeColor(borderColor);
            coachCard.setStrokeWidth(defaultStrokePx);
            coachIndicator.setVisibility(View.INVISIBLE);
        } else if ("COACH".equals(selectedRole)) {
            coachCard.setCardBackgroundColor(surfaceVariantColor);
            coachCard.setStrokeColor(accentColor);
            coachCard.setStrokeWidth(strokePx);
            coachIndicator.setVisibility(View.VISIBLE);

            deportistaCard.setCardBackgroundColor(surfaceColor);
            deportistaCard.setStrokeColor(borderColor);
            deportistaCard.setStrokeWidth(defaultStrokePx);
            deportistaIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private void setupListeners() {
        findViewById(R.id.registerButton).setOnClickListener(v -> attemptRegister());

        findViewById(R.id.loginLink).setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegister() {
        String nombres = nombresEditText.getText() != null
                ? nombresEditText.getText().toString().trim() : "";
        String apellidos = apellidosEditText.getText() != null
                ? apellidosEditText.getText().toString().trim() : "";
        String email = emailEditText.getText() != null
                ? emailEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null
                ? passwordEditText.getText().toString() : "";
        String confirmPassword = confirmPasswordEditText.getText() != null
                ? confirmPasswordEditText.getText().toString() : "";

        boolean valid = true;

        if (TextUtils.isEmpty(nombres)) {
            nombresLayout.setError(getString(R.string.register_name_invalid));
            valid = false;
        } else {
            nombresLayout.setError(null);
        }

        if (TextUtils.isEmpty(apellidos)) {
            apellidosLayout.setError(getString(R.string.register_name_invalid));
            valid = false;
        } else {
            apellidosLayout.setError(null);
        }

        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            emailLayout.setError(getString(R.string.login_error_invalid_email));
            valid = false;
        } else {
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordLayout.setError(getString(R.string.register_password_invalid));
            valid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError(getString(R.string.register_password_mismatch));
            valid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        if (selectedRole == null) {
            Snackbar.make(findViewById(R.id.register_root),
                    R.string.register_role_required, Snackbar.LENGTH_SHORT).show();
            valid = false;
        }

        if (!valid) return;

        View registerButton = findViewById(R.id.registerButton);
        registerButton.setEnabled(false);
        ((android.widget.Button) registerButton).setText("Creando cuenta...");

        authRepository.register(nombres, apellidos, email, password, selectedRole,
                new AuthRepository.ResourceCallback<AuthResponse>() {
                    @Override
                    public void onResult(Resource<AuthResponse> resource) {
                        registerButton.setEnabled(true);
                        ((android.widget.Button) registerButton).setText(R.string.register_button);

                        switch (resource.status) {
                            case SUCCESS:
                                if (resource.data != null) {
                                    sessionManager.saveAuthResponse(resource.data);
                                    Snackbar.make(findViewById(R.id.register_root),
                                            R.string.register_success, Snackbar.LENGTH_SHORT).show();
                                    navigateToHome();
                                } else {
                                    Snackbar.make(findViewById(R.id.register_root),
                                            "Error al obtener datos del usuario",
                                            Snackbar.LENGTH_LONG).show();
                                }
                                break;
                            case ERROR:
                                String msg = resource.message != null
                                        ? resource.message : "Error de registro";
                                Snackbar.make(findViewById(R.id.register_root),
                                        msg, Snackbar.LENGTH_LONG).show();
                                break;
                            case LOADING:
                                break;
                        }
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
