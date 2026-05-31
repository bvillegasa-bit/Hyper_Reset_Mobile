package com.hyperreset.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hyperreset.app.data.model.AuthResponse;

/**
 * Manages user session data persisted in SharedPreferences.
 * Stores the full AuthResponse object as JSON so that coach ID,
 * token, and other user info can be retrieved across app restarts.
 */
public class SessionManager {

    private static final String PREF_NAME = "hyper_reset_prefs";
    private static final String KEY_AUTH_RESPONSE = "auth_response";

    private final SharedPreferences prefs;
    private final Gson gson;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Saves the full AuthResponse object to SharedPreferences as JSON.
     */
    public void saveAuthResponse(AuthResponse authResponse) {
        String json = gson.toJson(authResponse);
        prefs.edit().putString(KEY_AUTH_RESPONSE, json).apply();
    }

    /**
     * Retrieves the stored AuthResponse object, or null if not logged in.
     */
    public AuthResponse getAuthResponse() {
        String json = prefs.getString(KEY_AUTH_RESPONSE, null);
        if (json == null) return null;
        return gson.fromJson(json, AuthResponse.class);
    }

    /**
     * Returns the coach/user ID from the stored AuthResponse.
     * Falls back to 0 if no session is active.
     */
    public long getUserId() {
        AuthResponse auth = getAuthResponse();
        if (auth != null) {
            // userId is the primary user ID field
            long uid = auth.getUserId();
            if (uid > 0) return uid;
            // Fallback to id field
            return auth.getId();
        }
        return 0;
    }

    /**
     * Returns the stored auth token, or null if not logged in.
     */
    public String getToken() {
        AuthResponse auth = getAuthResponse();
        return auth != null ? auth.getToken() : null;
    }

    /**
     * Clears the stored session (logout).
     */
    public void clearSession() {
        prefs.edit().remove(KEY_AUTH_RESPONSE).apply();
    }

    /**
     * Returns whether a user session is active.
     */
    public boolean isLoggedIn() {
        return getAuthResponse() != null;
    }
}
