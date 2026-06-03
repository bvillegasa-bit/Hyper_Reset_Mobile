package com.hyperreset.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

    // ==================================================================
    // Session Expired Listener (global 401 handling)
    // ==================================================================

    private static SessionExpiredListener sessionExpiredListener;

    public interface SessionExpiredListener {
        void onSessionExpired();
    }

    public static void setSessionExpiredListener(SessionExpiredListener listener) {
        sessionExpiredListener = listener;
    }

    public static void notifySessionExpired() {
        if (sessionExpiredListener != null) {
            sessionExpiredListener.onSessionExpired();
        }
    }

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
            long uid = auth.getUserId();
            Log.d("SessionManager", "getUserId: userId=" + uid + ", id=" + auth.getId());
            if (uid > 0) return uid;
            return auth.getId();
        }
        Log.e("SessionManager", "getUserId: AuthResponse is NULL");
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

    /**
     * Returns the user's role (e.g., "COACH" or "DEPORTISTA"), or null if not logged in.
     */
    public String getUserRole() {
        AuthResponse auth = getAuthResponse();
        return auth != null ? auth.getRol() : null;
    }

    /**
     * Returns the user's display name, or empty string if not logged in.
     */
    public String getUserName() {
        AuthResponse auth = getAuthResponse();
        return auth != null ? auth.getNombre() : "";
    }

    /**
     * Returns the deportista ID if the user is a DEPORTISTA, or -1 if not logged in or not a deportista.
     */
    public long getDeportistaId() {
        AuthResponse auth = getAuthResponse();
        if (auth != null && auth.getDeportistaId() > 0) {
            return auth.getDeportistaId();
        }
        return -1;
    }

    /**
     * Returns true if the current user is a DEPORTISTA, false otherwise.
     */
    public boolean isDeportista() {
        String role = getUserRole();
        return role != null && role.equals("DEPORTISTA");
    }
}
