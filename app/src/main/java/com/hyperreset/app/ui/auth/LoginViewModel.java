package com.hyperreset.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.Usuario;
import com.hyperreset.app.data.repository.AuthRepository;
import com.hyperreset.app.utils.Resource;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<Resource<Usuario>> loginResult = new MutableLiveData<>();

    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<Resource<Usuario>> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        loginResult.setValue(Resource.loading());

        authRepository.login(email, password, new AuthRepository.ResourceCallback<com.hyperreset.app.data.model.LoginResponse>() {
            @Override
            public void onResult(Resource<com.hyperreset.app.data.model.LoginResponse> resource) {
                switch (resource.status) {
                    case SUCCESS:
                        if (resource.data != null && resource.data.getUser() != null) {
                            Usuario user = resource.data.getUser();
                            loginResult.setValue(Resource.success(user));
                        } else {
                            loginResult.setValue(Resource.error("Error al obtener datos del usuario"));
                        }
                        break;
                    case ERROR:
                        loginResult.setValue(Resource.error(resource.message != null
                                ? resource.message : "Error de autenticación"));
                        break;
                    case LOADING:
                        // Already set loading state
                        break;
                }
            }
        });
    }

    public void loginDataChanged(String email, String password) {
        Integer emailError = null;
        Integer passwordError = null;

        if (email == null || email.trim().isEmpty()) {
            emailError = com.hyperreset.app.R.string.login_error_invalid_email;
        } else if (!email.contains("@")) {
            emailError = com.hyperreset.app.R.string.login_error_invalid_email;
        }

        if (password == null || password.trim().isEmpty()) {
            passwordError = com.hyperreset.app.R.string.login_error_invalid_password;
        } else if (password.trim().length() < 6) {
            passwordError = com.hyperreset.app.R.string.login_error_invalid_password;
        }

        loginFormState.setValue(new LoginFormState(emailError, passwordError));
    }
}
