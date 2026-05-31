package com.hyperreset.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.utils.Resource;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for report-related API operations.
 * Follows the same pattern as TestRepository:
 * - Uses ApiService for HTTP calls
 * - Uses ExecutorService for background execution
 * - Posts results to main thread via Handler
 * - Returns results via Resource&lt;T&gt; wrapper
 */
public class ReporteRepository {

    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public ReporteRepository() {
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public ReporteRepository(ApiService apiService) {
        this.apiService = apiService;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ==================================================================
    // Reportes
    // ==================================================================

    public void getReporte(long id, ResourceCallback<ReporteResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<ReporteResponse>> call = apiService.getReporte(id);
            call.enqueue(new Callback<ApiResponse<ReporteResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<ReporteResponse>> call,
                                       Response<ApiResponse<ReporteResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<ReporteResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getReportesByDeportista(long deportistaId,
                                        ResourceCallback<List<ReporteResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<ReporteResponse>>> call = apiService.getReportesByDeportista(deportistaId);
            call.enqueue(new Callback<ApiResponse<List<ReporteResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ReporteResponse>>> call,
                                       Response<ApiResponse<List<ReporteResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ReporteResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getReportesByTest(long testId,
                                  ResourceCallback<List<ReporteResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<ReporteResponse>>> call = apiService.getReportesByTest(testId);
            call.enqueue(new Callback<ApiResponse<List<ReporteResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ReporteResponse>>> call,
                                       Response<ApiResponse<List<ReporteResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ReporteResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void generarReporte(Object request, ResourceCallback<ReporteResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<ReporteResponse>> call = apiService.generarReporte(request);
            call.enqueue(new Callback<ApiResponse<ReporteResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<ReporteResponse>> call,
                                       Response<ApiResponse<ReporteResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<ReporteResponse>> call, Throwable t) {
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
            case 401: return "Sesi\u00f3n expirada. Inicia sesi\u00f3n de nuevo.";
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
