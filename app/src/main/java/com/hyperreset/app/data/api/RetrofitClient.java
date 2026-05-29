package com.hyperreset.app.data.api;

import com.hyperreset.app.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client with JWT token injection interceptor.
 * <p>
 * Usage:
 * <pre>
 * ApiService api = RetrofitClient.getInstance().getApiService();
 * </pre>
 */
public class RetrofitClient {

    private static RetrofitClient instance;
    private final Retrofit retrofit;
    private String authToken;

    private RetrofitClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }

    /**
     * Sets the JWT auth token to be injected into subsequent requests.
     *
     * @param token The JWT token string (without "Bearer " prefix)
     */
    public void setAuthToken(String token) {
        this.authToken = token;
    }

    /**
     * Clears the stored auth token (e.g., on logout).
     */
    public void clearAuthToken() {
        this.authToken = null;
    }

    /**
     * Returns whether a token is currently stored.
     */
    public boolean hasAuthToken() {
        return authToken != null && !authToken.isEmpty();
    }

    /**
     * OkHttp Interceptor that adds the JWT Bearer token to all requests
     * except login and register (public endpoints).
     */
    private class AuthInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            String path = original.url().encodedPath();

            // Skip token for public endpoints
            if (path.contains("/auth/login") || path.contains("/auth/register")) {
                return chain.proceed(original);
            }

            // Inject token if available
            if (authToken != null && !authToken.isEmpty()) {
                Request.Builder builder = original.newBuilder()
                        .header("Authorization", "Bearer " + authToken);
                return chain.proceed(builder.build());
            }

            return chain.proceed(original);
        }
    }
}
