package com.hyperreset.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.data.model.LoginRequest;
import com.hyperreset.app.data.model.RegisterRequest;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for authentication operations.
 * Makes real API calls to the backend via Retrofit.
 */
public class AuthRepository {

    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public AuthRepository() {
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Constructor used for dependency injection with a provided ApiService instance.
     */
    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Attempts to log in with the given credentials.
     *
     * @param email    The user's email (correo)
     * @param password The user's password (contrasena)
     * @param callback Callback to receive the result on the main thread
     */
    public void login(String email, String password, ResourceCallback<AuthResponse> callback) {
        executor.execute(() -> {
            LoginRequest request = new LoginRequest(email, password);
            Call<ApiResponse<AuthResponse>> call = apiService.login(request);
            call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                       Response<ApiResponse<AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        AuthResponse authData = response.body().getData();
                        if (authData != null && authData.getToken() != null) {
                            // Store token in Retrofit client for subsequent requests
                            RetrofitClient.getInstance().setAuthToken(authData.getToken());
                        }
                        postResult(callback, Resource.success(authData));
                    } else if (response.code() == 401) {
                        postResult(callback, Resource.error("Credenciales inválidas"));
                    } else if (response.code() == 400) {
                        String msg = response.body() != null
                                ? response.body().getMessage()
                                : "Datos inválidos";
                        postResult(callback, Resource.error(msg));
                    } else {
                        postResult(callback, Resource.error("Error del servidor (" + response.code() + ")"));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                    String msg = "No se puede conectar al servidor. Verifica tu conexión.";
                    if (t instanceof java.net.ConnectException) {
                        msg = "Servidor no disponible. Intenta más tarde.";
                    } else if (t instanceof java.net.SocketTimeoutException) {
                        msg = "La conexión ha expirado. Intenta de nuevo.";
                    }
                    postResult(callback, Resource.error(msg));
                }
            });
        });
    }

    /**
     * Registers a new user (COACH or DEPORTISTA).
     *
     * @param nombres    User's first name(s)
     * @param apellidos  User's last name(s)
     * @param email      User's email (correo)
     * @param password   User's password (contrasena)
     * @param rol        User's role: "COACH" or "DEPORTISTA"
     * @param callback   Callback to receive the result on the main thread
     */
    public void register(String nombres, String apellidos, String email,
                         String password, String rol,
                         ResourceCallback<AuthResponse> callback) {
        executor.execute(() -> {
            RegisterRequest request = new RegisterRequest(nombres, apellidos, email, password, rol);
            Call<ApiResponse<AuthResponse>> call = apiService.register(request);
            call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                       Response<ApiResponse<AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        AuthResponse authData = response.body().getData();
                        if (authData != null && authData.getToken() != null) {
                            RetrofitClient.getInstance().setAuthToken(authData.getToken());
                        }
                        postResult(callback, Resource.success(authData));
                    } else if (response.code() == 400) {
                        String msg = response.body() != null
                                ? response.body().getMessage()
                                : "Datos inválidos. Verifica los campos.";
                        postResult(callback, Resource.error(msg));
                    } else if (response.code() == 409) {
                        postResult(callback, Resource.error("El correo ya está registrado"));
                    } else {
                        postResult(callback, Resource.error("Error del servidor (" + response.code() + ")"));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                    String msg = "No se puede conectar al servidor. Verifica tu conexión.";
                    if (t instanceof java.net.ConnectException) {
                        msg = "Servidor no disponible. Intenta más tarde.";
                    } else if (t instanceof java.net.SocketTimeoutException) {
                        msg = "La conexión ha expirado. Intenta de nuevo.";
                    }
                    postResult(callback, Resource.error(msg));
                }
            });
        });
    }

    /**
     * Gets the authenticated user's profile.
     *
     * @param callback Callback to receive the result on the main thread
     */
    public void getProfile(ResourceCallback<AuthResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<AuthResponse>> call = apiService.getProfile();
            call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                       Response<ApiResponse<AuthResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        postResult(callback, Resource.success(response.body().getData()));
                    } else if (response.code() == 401) {
                        SessionManager.notifySessionExpired();
                        RetrofitClient.getInstance().clearAuthToken();
                        postResult(callback, Resource.error("Sesión expirada. Inicia sesión de nuevo."));
                    } else {
                        postResult(callback, Resource.error("Error del servidor (" + response.code() + ")"));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                    postResult(callback, Resource.error("No se puede conectar al servidor"));
                }
            });
        });
    }

    /**
     * Logs out the current user by clearing the stored token.
     */
    public void logout() {
        RetrofitClient.getInstance().clearAuthToken();
    }

    /**
     * Returns whether the user is currently authenticated.
     */
    public boolean isAuthenticated() {
        return RetrofitClient.getInstance().hasAuthToken();
    }

    private void postResult(ResourceCallback<AuthResponse> callback, Resource<AuthResponse> resource) {
        mainHandler.post(() -> callback.onResult(resource));
    }

    /**
     * Callback interface for receiving auth operation results on the main thread.
     */
    public interface ResourceCallback<T> {
        void onResult(Resource<T> resource);
    }
}
