package com.hyperreset.app.ui.perfil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import com.hyperreset.app.R;
import com.hyperreset.app.utils.SettingsManager;

/**
 * Fragment that displays app settings: theme, language, and notifications.
 * All changes are saved immediately via SettingsManager (SharedPreferences).
 */
public class SettingsFragment extends Fragment {

    private SettingsManager settingsManager;

    private RadioGroup radioGroupTheme;
    private RadioGroup radioGroupLanguage;
    private SwitchCompat switchNotifications;

    private RadioButton radioThemeLight;
    private RadioButton radioThemeDark;
    private RadioButton radioThemeSystem;
    private RadioButton radioLangEs;
    private RadioButton radioLangEn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingsManager = new SettingsManager(requireContext());

        initViews(view);
        loadCurrentSettings();
        setupListeners();
    }

    private void initViews(View view) {
        radioGroupTheme = view.findViewById(R.id.radioGroupTheme);
        radioGroupLanguage = view.findViewById(R.id.radioGroupLanguage);
        switchNotifications = view.findViewById(R.id.switchNotifications);

        radioThemeLight = view.findViewById(R.id.radioThemeLight);
        radioThemeDark = view.findViewById(R.id.radioThemeDark);
        radioThemeSystem = view.findViewById(R.id.radioThemeSystem);
        radioLangEs = view.findViewById(R.id.radioLangEs);
        radioLangEn = view.findViewById(R.id.radioLangEn);
    }

    private void loadCurrentSettings() {
        // Load theme
        String theme = settingsManager.getThemeMode();
        if ("light".equals(theme)) {
            radioThemeLight.setChecked(true);
        } else if ("dark".equals(theme)) {
            radioThemeDark.setChecked(true);
        } else {
            radioThemeSystem.setChecked(true);
        }

        // Load language
        String lang = settingsManager.getLanguage();
        if ("en".equals(lang)) {
            radioLangEn.setChecked(true);
        } else {
            radioLangEs.setChecked(true);
        }

        // Load notifications
        switchNotifications.setChecked(settingsManager.isNotificationsEnabled());
    }

    private void setupListeners() {
        // Theme radio group — save and apply immediately
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            if (checkedId == R.id.radioThemeLight) {
                settingsManager.setThemeMode("light");
                mode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioThemeDark) {
                settingsManager.setThemeMode("dark");
                mode = AppCompatDelegate.MODE_NIGHT_YES;
            } else if (checkedId == R.id.radioThemeSystem) {
                settingsManager.setThemeMode("system");
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
            AppCompatDelegate.setDefaultNightMode(mode);
            requireActivity().recreate();
        });

        // Language radio group — save and apply immediately
        radioGroupLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            String langCode = (checkedId == R.id.radioLangEn) ? "en" : "es";
            settingsManager.setLanguage(langCode);

            Locale locale = langCode.equals("en") ? Locale.ENGLISH : new Locale("es", "ES");
            Locale.setDefault(locale);
            Configuration config = getResources().getConfiguration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            requireActivity().recreate();
        });

        // Notifications toggle — save on change
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsManager.setNotificationsEnabled(isChecked));
    }
}
