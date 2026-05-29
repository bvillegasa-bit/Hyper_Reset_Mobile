package com.hyperreset.app.utils;

public class Constants {
    // Development: Android emulator → host machine
    // For physical device testing, replace with your machine's local IP
    public static final String BASE_URL = "http://10.0.2.2:8080/api/";
    public static final String PREFS_NAME = "hyper_reset_prefs";
    public static final String PREF_FIRST_LAUNCH = "is_first_launch";
    public static final String DATABASE_NAME = "hyper_reset_db";
    public static final String PREF_AUTH_TOKEN = "auth_token";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_ROL = "user_rol";
    public static final String PREF_USER_NOMBRE = "user_nombre";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
