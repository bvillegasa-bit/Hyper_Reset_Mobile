package com.hyperreset.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class wrapping SharedPreferences for app settings.
 * Stores theme mode, language preference, and notification toggle.
 */
public class SettingsManager {
    private static final String PREF_NAME = "hyper_reset_settings";
    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getThemeMode() {
        return prefs.getString(KEY_THEME, "system");
    }

    public void setThemeMode(String mode) {
        prefs.edit().putString(KEY_THEME, mode).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "es");
    }

    public void setLanguage(String lang) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }
}
