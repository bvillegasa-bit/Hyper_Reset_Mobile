package com.hyperreset.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.utils.Resource;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for appointment (Cita) API operations.
 * Follows the exact pattern as TestRepository:
 * - Uses ApiService for HTTP calls
 * - Uses ExecutorService for background execution
 * - Posts results to main thread via Handler
 * - Returns results via Resource<T> wrapper
 */
public class CitaRepository {

    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public CitaRepository() {
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public CitaRepository(ApiService apiService) {
        this.apiService = apiService;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ==================================================================
    // Queries
    // ==================================================================

    public void getCitas(ResourceCallback<List<CitaResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<CitaResponse>>> call = apiService.getCitas();
            call.enqueue(new Callback<ApiResponse<List<CitaResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<CitaResponse>>> call,
                                       Response<ApiResponse<List<CitaResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<CitaResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getCita(long id, ResourceCallback<CitaResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<CitaResponse>> call = apiService.getCita(id);
            call.enqueue(new Callback<ApiResponse<CitaResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CitaResponse>> call,
                                       Response<ApiResponse<CitaResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<CitaResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getCitasByCoach(long coachId, ResourceCallback<List<CitaResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<CitaResponse>>> call = apiService.getCitasByCoach(coachId);
            call.enqueue(new Callback<ApiResponse<List<CitaResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<CitaResponse>>> call,
                                       Response<ApiResponse<List<CitaResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<CitaResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getCitasByDeportista(long deportistaId, ResourceCallback<List<CitaResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<CitaResponse>>> call = apiService.getCitasByDeportista(deportistaId);
            call.enqueue(new Callback<ApiResponse<List<CitaResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<CitaResponse>>> call,
                                       Response<ApiResponse<List<CitaResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<CitaResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getCitasByDateRange(String start, String end,
                                    ResourceCallback<List<CitaResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<CitaResponse>>> call = apiService.getCitasByDateRange(start, end);
            call.enqueue(new Callback<ApiResponse<List<CitaResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<CitaResponse>>> call,
                                       Response<ApiResponse<List<CitaResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<CitaResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    // ==================================================================
    // Mutations
    // ==================================================================

    public void createCita(Object request, ResourceCallback<CitaResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<CitaResponse>> call = apiService.createCita(request);
            call.enqueue(new Callback<ApiResponse<CitaResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CitaResponse>> call,
                                       Response<ApiResponse<CitaResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<CitaResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void updateCita(long id, Object request, ResourceCallback<CitaResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<CitaResponse>> call = apiService.updateCita(id, request);
            call.enqueue(new Callback<ApiResponse<CitaResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CitaResponse>> call,
                                       Response<ApiResponse<CitaResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<CitaResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void deleteCita(long id, ResourceCallback<Void> callback) {
        executor.execute(() -> {
            Call<ApiResponse<Void>> call = apiService.deleteCita(id);
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

    public void updateCitaEstado(long id, String estado, ResourceCallback<CitaResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<CitaResponse>> call = apiService.updateCitaEstado(id, estado);
            call.enqueue(new Callback<ApiResponse<CitaResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<CitaResponse>> call,
                                       Response<ApiResponse<CitaResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<CitaResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    // ==================================================================
    // Deportistas helper (for form spinner)
    // ==================================================================

    public void getDeportistasByCoach(long coachId,
                                      ResourceCallback<List<com.hyperreset.app.data.model.DeportistaResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<com.hyperreset.app.data.model.DeportistaResponse>>> call =
                    apiService.getDeportistasByCoach(coachId);
            call.enqueue(new Callback<ApiResponse<List<com.hyperreset.app.data.model.DeportistaResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<com.hyperreset.app.data.model.DeportistaResponse>>> call,
                                       Response<ApiResponse<List<com.hyperreset.app.data.model.DeportistaResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<com.hyperreset.app.data.model.DeportistaResponse>>> call, Throwable t) {
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
