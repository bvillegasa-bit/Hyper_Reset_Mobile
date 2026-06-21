package com.hyperreset.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.Usuario;
import com.hyperreset.app.ui.home.HomeActivity;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Manual ViewModel creation (no Hilt)
        loginViewModel = new LoginViewModel(
            getApplication(),
            ((com.hyperreset.app.HyperResetApplication) getApplication()).getAppContainer().getAuthRepository()
        );
        loginViewModel.setSessionManager(new SessionManager(this));

        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
    }

    private void setupObservers() {
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) return;

            loginButton.setEnabled(loginFormState.isDataValid());

            if (loginFormState.getEmailError() != null) {
                emailLayout.setError(getString(loginFormState.getEmailError()));
            } else {
                emailLayout.setError(null);
            }

            if (loginFormState.getPasswordError() != null) {
                passwordLayout.setError(getString(loginFormState.getPasswordError()));
            } else {
                passwordLayout.setError(null);
            }
        });

        loginViewModel.getLoginResult().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    loginButton.setEnabled(false);
                    loginButton.setText("Cargando...");
                    break;
                case SUCCESS:
                    loginButton.setEnabled(true);
                    loginButton.setText(R.string.login_button);
                    navigateToHome();
                    break;
                case ERROR:
                    loginButton.setEnabled(true);
                    loginButton.setText(R.string.login_button);
                    Snackbar.make(findViewById(R.id.login_root),
                            resource.message != null ? resource.message : getString(R.string.login_error_auth),
                            Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
            loginViewModel.login(email, password);
        });

        registerLink.setOnClickListener(v ->
                Toast.makeText(this, R.string.login_register_soon, Toast.LENGTH_SHORT).show()
        );

        // Text change listeners for form validation
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateForm();
            }
        });

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateForm();
            }
        });

        // Also validate on text change via a simple TextWatcher approach
        android.text.TextWatcher textWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };

        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
    }

    private void validateForm() {
        String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
        loginViewModel.loginDataChanged(email, password);
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
