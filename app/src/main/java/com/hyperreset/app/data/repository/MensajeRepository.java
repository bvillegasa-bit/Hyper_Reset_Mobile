package com.hyperreset.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for Mensaje (message) API operations.
 * Follows the exact pattern as CitaRepository and DeportistaRepository.
 */
public class MensajeRepository {

    private final ApiService apiService;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public MensajeRepository() {
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public MensajeRepository(ApiService apiService) {
        this.apiService = apiService;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // ==================================================================
    // Queries
    // ==================================================================

    /**
     * Send a new message.
     */
    public void sendMensaje(long destinatarioId, String contenido,
                            ResourceCallback<MensajeResponse> callback) {
        executor.execute(() -> {
            Map<String, Object> request = new HashMap<>();
            request.put("destinatarioId", destinatarioId);
            request.put("contenido", contenido);

            Call<ApiResponse<MensajeResponse>> call = apiService.sendMensaje(request);
            call.enqueue(new Callback<ApiResponse<MensajeResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<MensajeResponse>> call,
                                       Response<ApiResponse<MensajeResponse>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<MensajeResponse>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    /**
     * Get conversation thread with another user.
     */
    public void getConversacion(long otherUserId,
                                ResourceCallback<List<MensajeResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<MensajeResponse>>> call = apiService.getConversacion(otherUserId);
            call.enqueue(new Callback<ApiResponse<List<MensajeResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<MensajeResponse>>> call,
                                       Response<ApiResponse<List<MensajeResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<MensajeResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    /**
     * Get received messages (inbox).
     */
    public void getMensajesRecibidos(ResourceCallback<List<MensajeResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<MensajeResponse>>> call = apiService.getMensajesRecibidos();
            call.enqueue(new Callback<ApiResponse<List<MensajeResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<MensajeResponse>>> call,
                                       Response<ApiResponse<List<MensajeResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<MensajeResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    /**
     * Get sent messages.
     */
    public void getMensajesEnviados(ResourceCallback<List<MensajeResponse>> callback) {
        executor.execute(() -> {
            Call<ApiResponse<List<MensajeResponse>>> call = apiService.getMensajesEnviados();
            call.enqueue(new Callback<ApiResponse<List<MensajeResponse>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<MensajeResponse>>> call,
                                       Response<ApiResponse<List<MensajeResponse>>> response) {
                    handleApiResponse(response, callback);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<MensajeResponse>>> call, Throwable t) {
                    postResult(callback, Resource.error(getErrorMessage(t)));
                }
            });
        });
    }

    /**
     * Mark a message as read.
     */
    public void marcarMensajeLeido(long id, ResourceCallback<Void> callback) {
        executor.execute(() -> {
            Call<ApiResponse<Void>> call = apiService.marcarMensajeLeido(id);
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

    /**
     * Get unread messages count.
     */
    public void getNoLeidos(ResourceCallback<Integer> callback) {
        executor.execute(() -> {
            Call<ApiResponse<Integer>> call = apiService.getNoLeidos();
            call.enqueue(new Callback<ApiResponse<Integer>>() {
                @Override
                public void onResponse(Call<ApiResponse<Integer>> call,
                                       Response<ApiResponse<Integer>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Integer data = response.body().getData();
                        postResult(callback, Resource.success(data));
                    } else {
                        String message = getHttpErrorMessage(response.code());
                        postResult(callback, Resource.error(message));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
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
