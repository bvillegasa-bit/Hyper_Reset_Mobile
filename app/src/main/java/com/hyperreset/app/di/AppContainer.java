package com.hyperreset.app.di;

import com.hyperreset.app.data.api.ApiService;
import com.hyperreset.app.data.repository.AuthRepository;

/**
 * Manual dependency injection container.
 * Provides app-wide dependencies without a DI framework.
 * This pattern helps understand DI concepts before using Hilt/Dagger.
 */
public class AppContainer {

    private final ApiService apiService;
    private final AuthRepository authRepository;

    public AppContainer() {
        // Configure OkHttp
        okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(new okhttp3.logging.HttpLoggingInterceptor()
                        .setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY))
                .build();

        // Configure Retrofit
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(com.hyperreset.app.utils.Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        authRepository = new AuthRepository(apiService);
    }

    public ApiService getApiService() {
        return apiService;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }
}
