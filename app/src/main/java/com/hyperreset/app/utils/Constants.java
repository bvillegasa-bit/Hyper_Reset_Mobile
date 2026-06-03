package com.hyperreset.app.utils;

public class Constants {
    // ==========================================
    // API BASE URL Configuration
    // ==========================================
    // Change this based on your deployment:
    // - Emulator: "http://10.0.2.2:8080/api/"
    // - Localhost IP: "http://192.168.X.X:8080/api/"
    // - Ngrok (Physical device): "https://xxxx-yy-zzzz.ngrok.io/api/"
    
    // For real device testing via Ngrok, replace NGROK_URL with your actual ngrok domain
    private static final String NGROK_URL = "https://erythemal-subadditively-nichole.ngrok-free.dev/api/";
    private static final String LOCALHOST_URL = "http://10.0.2.2:8080/api/";
    
    // Switch between emulator (false) and Ngrok (true)
    private static final boolean USE_NGROK = true;
    
    public static final String BASE_URL = USE_NGROK ? NGROK_URL : LOCALHOST_URL;
    
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
