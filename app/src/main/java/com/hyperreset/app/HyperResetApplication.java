package com.hyperreset.app;

import android.app.Application;

import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.di.AppContainer;
import com.hyperreset.app.utils.SessionManager;
import com.hyperreset.app.utils.SettingsManager;

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

        // Apply persisted settings on startup
        SettingsManager settingsManager = new SettingsManager(this);

        // Restore saved theme mode
        String theme = settingsManager.getThemeMode();
        if ("light".equals(theme)) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        } else if ("dark".equals(theme)) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        // Restore saved language
        String language = settingsManager.getLanguage();
        android.content.res.Configuration config = new android.content.res.Configuration();
        if ("en".equals(language)) {
            config.setLocale(java.util.Locale.ENGLISH);
        } else {
            config.setLocale(new java.util.Locale("es", "ES"));
        }
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
