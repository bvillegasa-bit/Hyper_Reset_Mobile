package com.hyperreset.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.DashboardActivityResponse;
import com.hyperreset.app.data.model.DashboardCoachResponse;
import com.hyperreset.app.data.model.DashboardDeportistaResponse;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for dashboard-related API operations.
 * Follows the same pattern as TestRepository:
 * - Uses ApiService for HTTP calls
 * - Uses ExecutorService for background execution
 * - Posts results to main thread via Handler
 * - Returns results via Resource&lt;T&gt; wrapper
 */
public class DashboardRepository {

    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public DashboardRepository() {
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public DashboardRepository(ApiService apiService) {
        this.apiService = apiService;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ==================================================================
    // Dashboard Deportista
    // ==================================================================

    public void getDashboardDeportista(long id,
                                        ResourceCallback<DashboardDeportistaResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<DashboardDeportistaResponse>> call = apiService.getDashboardDeportista(id);
            call.enqueue(new Callback<ApiResponse<DashboardDeportistaResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<DashboardDeportistaResponse>> call,
                                       Response<ApiResponse<DashboardDeportistaResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<DashboardDeportistaResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    // ==================================================================
    // Dashboard Coach
    // ==================================================================

    public void getDashboardCoach(long id,
                                   ResourceCallback<DashboardCoachResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<DashboardCoachResponse>> call = apiService.getDashboardCoach(id);
            call.enqueue(new Callback<ApiResponse<DashboardCoachResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<DashboardCoachResponse>> call,
                                       Response<ApiResponse<DashboardCoachResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<DashboardCoachResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    // ==================================================================
    // Actividad (paginated)
    // ==================================================================

    public void getActividad(int page, int size,
                              ResourceCallback<DashboardActivityResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<DashboardActivityResponse>> call = apiService.getActividad(page, size);
            call.enqueue(new Callback<ApiResponse<DashboardActivityResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<DashboardActivityResponse>> call,
                                       Response<ApiResponse<DashboardActivityResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        postResult(callback, Resource.success(response.body().getData()));
                    } else if (response.code() == 401) {
                        SessionManager.notifySessionExpired();
                        postResult(callback, Resource.error("Sesi\u00f3n expirada"));
                    } else {
                        postResult(callback, Resource.error("Error al cargar actividad"));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<DashboardActivityResponse>> call, Throwable t) {
                    postResult(callback, Resource.error("Error de red: " + t.getMessage()));
                }
            });
        });
    }

    // ==================================================================
    // Generic response handler
    // ==================================================================

    private <T> void handleApiResponse(Response<ApiResponse<T>> response,
                                        ResourceCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            T data = response.body().getData();
            postResult(callback, Resource.success(data));
        } else {
            String message = getHttpErrorMessage(response.code());
            postResult(callback, Resource.error(message));
        }
    }

    // ==================================================================
    // Error message mapping
    // ==================================================================

    private String getHttpErrorMessage(int code) {
        switch (code) {
            case 400: return "Datos inv\u00e1lidos. Verifica los campos.";
            case 401:
                SessionManager.notifySessionExpired();
                return "Sesi\u00f3n expirada. Inicia sesi\u00f3n de nuevo.";
            case 403: return "No tienes permisos para esta acci\u00f3n.";
            case 404: return "El recurso solicitado no existe.";
            default:
                if (code >= 500) {
                    return "Error del servidor. Intenta m\u00e1s tarde.";
                }
                return "Error del servidor (" + code + ")";
        }
    }

    private String getErrorMessage(Throwable t) {
        if (t instanceof java.net.ConnectException) {
            return "Servidor no disponible. Intenta m\u00e1s tarde.";
        } else if (t instanceof java.net.SocketTimeoutException) {
            return "La conexi\u00f3n ha expirado. Intenta de nuevo.";
        } else if (t instanceof java.net.UnknownHostException) {
            return "Servidor no disponible. Intenta m\u00e1s tarde.";
        }
        return "No se puede conectar al servidor. Verifica tu conexi\u00f3n.";
    }

    private void postResult(ResourceCallback<?> callback, Resource<?> resource) {
        mainHandler.post(() -> {
            @SuppressWarnings("unchecked")
            ResourceCallback<Object> rawCallback = (ResourceCallback<Object>) callback;
            rawCallback.onResult((Resource<Object>) resource);
        });
    }

    /**
     * Callback interface for receiving repository operation results on the main thread.
     */
    public interface ResourceCallback<T> {
        void onResult(Resource<T> resource);
    }
}
