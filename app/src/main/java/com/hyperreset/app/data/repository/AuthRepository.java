package com.hyperreset.app.data.repository;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.model.LoginRequest;
import com.hyperreset.app.data.model.LoginResponse;
import com.hyperreset.app.utils.Resource;

/**
 * Repository for authentication operations.
 * Currently a placeholder — will make actual API calls in future changes.
 */
public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Attempts to log in with the given credentials.
     * Currently returns a "not available" error as a placeholder.
     *
     * @param email    The user's email
     * @param password The user's password
     * @param callback Callback to receive the result
     */
    public void login(String email, String password, ResourceCallback<LoginResponse> callback) {
        // Placeholder — will make the API call when the backend is ready
        callback.onResult(Resource.error("Servicio de autenticación no disponible"));
    }

    public interface ResourceCallback<T> {
        void onResult(Resource<T> resource);
    }
}
