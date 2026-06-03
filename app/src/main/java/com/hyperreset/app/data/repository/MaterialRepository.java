package com.hyperreset.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Material (educational material) API operations.
 * Follows the exact pattern as CitaRepository:
 * - Uses ApiService for HTTP calls
 * - Uses ExecutorService for background execution
 * - Posts results to main thread via Handler
 * - Returns results via Resource<T> wrapper
 */
public class MaterialRepository {

    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public MaterialRepository() {
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public MaterialRepository(ApiService apiService) {
        this.apiService = apiService;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ==================================================================
    // Queries
    // ==================================================================

    public void getMateriales(ResourceCallback<List<MaterialResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<MaterialResponse>>> call = apiService.getMateriales();
            call.enqueue(new Callback<ApiResponse<List<MaterialResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<MaterialResponse>>> call,
                                       Response<ApiResponse<List<MaterialResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<MaterialResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getMaterial(long id, ResourceCallback<MaterialResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<MaterialResponse>> call = apiService.getMaterial(id);
            call.enqueue(new Callback<ApiResponse<MaterialResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<MaterialResponse>> call,
                                       Response<ApiResponse<MaterialResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<MaterialResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    // ==================================================================
    // Mutations
    // ==================================================================

    public void createMaterial(Object request, ResourceCallback<MaterialResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<MaterialResponse>> call = apiService.createMaterial(request);
            call.enqueue(new Callback<ApiResponse<MaterialResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<MaterialResponse>> call,
                                       Response<ApiResponse<MaterialResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<MaterialResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void updateMaterial(long id, Object request, ResourceCallback<MaterialResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<MaterialResponse>> call = apiService.updateMaterial(id, request);
            call.enqueue(new Callback<ApiResponse<MaterialResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<MaterialResponse>> call,
                                       Response<ApiResponse<MaterialResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<MaterialResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void deleteMaterial(long id, ResourceCallback<Void> callback) {
        executor.execute(() -> {
            Call<ApiResponse<Void>> call = apiService.deleteMaterial(id);
            call.enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call,
                                       Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        postResult(callback, Resource.success(null));
                    } else {
                        String message = getHttpErrorMessage(response.code());
                        postResult(callback, Resource.error(message));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
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
