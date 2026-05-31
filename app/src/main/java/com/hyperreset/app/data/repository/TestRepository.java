package com.hyperreset.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.ResultadoResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.utils.Resource;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for test-related API operations.
 * Follows the same pattern as AuthRepository:
 * - Uses ApiService for HTTP calls
 * - Uses ExecutorService for background execution
 * - Posts results to main thread via Handler
 * - Returns results via Resource&lt;T&gt; wrapper
 */
public class TestRepository {

    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public TestRepository() {
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public TestRepository(ApiService apiService) {
        this.apiService = apiService;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ==================================================================
    // Test Sessions
    // ==================================================================

    public void getTestFisicos(ResourceCallback<List<TestFisicoResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<TestFisicoResponse>>> call = apiService.getTestFisicos();
            call.enqueue(new Callback<ApiResponse<List<TestFisicoResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<TestFisicoResponse>>> call,
                                       Response<ApiResponse<List<TestFisicoResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<TestFisicoResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getTestFisico(long id, ResourceCallback<TestFisicoResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<TestFisicoResponse>> call = apiService.getTestFisico(id);
            call.enqueue(new Callback<ApiResponse<TestFisicoResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<TestFisicoResponse>> call,
                                       Response<ApiResponse<TestFisicoResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<TestFisicoResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void getTestFisicosByDeportista(long deportistaId,
                                           ResourceCallback<List<TestFisicoResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<TestFisicoResponse>>> call = apiService.getTestFisicosByDeportista(deportistaId);
            call.enqueue(new Callback<ApiResponse<List<TestFisicoResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<TestFisicoResponse>>> call,
                                       Response<ApiResponse<List<TestFisicoResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<TestFisicoResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void createTestFisico(Object request, ResourceCallback<TestFisicoResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<TestFisicoResponse>> call = apiService.createTestFisico(request);
            call.enqueue(new Callback<ApiResponse<TestFisicoResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<TestFisicoResponse>> call,
                                       Response<ApiResponse<TestFisicoResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<TestFisicoResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void completarTest(long id, ResourceCallback<TestFisicoResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<TestFisicoResponse>> call = apiService.completarTest(id);
            call.enqueue(new Callback<ApiResponse<TestFisicoResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<TestFisicoResponse>> call,
                                       Response<ApiResponse<TestFisicoResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<TestFisicoResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    // ==================================================================
    // Resultados
    // ==================================================================

    public void getResultadosByTest(long testId, ResourceCallback<List<ResultadoResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<ResultadoResponse>>> call = apiService.getResultadosByTest(testId);
            call.enqueue(new Callback<ApiResponse<List<ResultadoResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ResultadoResponse>>> call,
                                       Response<ApiResponse<List<ResultadoResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ResultadoResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    public void createResultado(Object request, ResourceCallback<ResultadoResponse> callback) {
        executor.execute(() -> {
            Call<ApiResponse<ResultadoResponse>> call = apiService.createResultado(request);
            call.enqueue(new Callback<ApiResponse<ResultadoResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<ResultadoResponse>> call,
                                       Response<ApiResponse<ResultadoResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<ResultadoResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    // ==================================================================
    // Deportistas helper
    // ==================================================================

    public void getDeportistasByCoach(long coachId,
                                      ResourceCallback<List<DeportistaResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<DeportistaResponse>>> call = apiService.getDeportistasByCoach(coachId);
            call.enqueue(new Callback<ApiResponse<List<DeportistaResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<DeportistaResponse>>> call,
                                       Response<ApiResponse<List<DeportistaResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<DeportistaResponse>>> call, Throwable t) {
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
