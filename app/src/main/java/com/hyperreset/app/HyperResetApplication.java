package com.hyperreset.app;

import android.app.Application;

import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.di.AppContainer;
import com.hyperreset.app.utils.SessionManager;

public class HyperResetApplication extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer();

        // Restore saved JWT token on app startup so that RetrofitClient
        // has the Bearer token immediately for all API calls.
        SessionManager sessionManager = new SessionManager(this);
        String savedToken = sessionManager.getToken();
        if (savedToken != null && !savedToken.isEmpty()) {
            RetrofitClient.getInstance().setAuthToken(savedToken);
        }
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
