package com.hyperreset.app.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.ui.auth.LoginActivity;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

/**
 * Fragment that displays the logged-in user's profile with a redesigned layout.
 * Shows avatar gradient circle with role emoji, contact info card,
 * settings menu with 4 items, and a destructive logout button.
 */
public class PerfilFragment extends Fragment {

    private PerfilViewModel viewModel;
    private SessionManager sessionManager;

    private View profileContent;
    private View progressLoading;
    private View layoutError;
    private View btnRetry;

    private FrameLayout avatarCircle;
    private TextView tvEmoji;
    private TextView tvInitialsFallback;
    private TextView tvNombre;
    private TextView tvRol;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvMemberSince;
    private MaterialButton btnLogout;
    private LinearLayout menuItemsContainer;

    // Menu item data
    private static final int[][] MENU_ITEMS = {
        { R.drawable.ic_user, R.string.perfil_menu_edit_profile },
        { R.drawable.ic_bell, R.string.perfil_menu_notifications },
        { R.drawable.ic_shield, R.string.perfil_menu_privacy },
        { R.drawable.ic_settings, R.string.perfil_menu_settings }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new PerfilViewModel();
        sessionManager = new SessionManager(requireContext());

        initViews(view);
        setupObservers();
        setupListeners();

        viewModel.loadProfile();
    }

    private void initViews(View view) {
        profileContent = view.findViewById(R.id.profileContent);
        progressLoading = view.findViewById(R.id.progressLoading);
        layoutError = view.findViewById(R.id.layoutError);
        btnRetry = view.findViewById(R.id.btnRetry);

        avatarCircle = view.findViewById(R.id.avatarCircle);
        tvEmoji = view.findViewById(R.id.tvEmoji);
        tvInitialsFallback = view.findViewById(R.id.tvInitialsFallback);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvRol = view.findViewById(R.id.tvRol);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
        btnLogout = view.findViewById(R.id.btnLogout);
        menuItemsContainer = view.findViewById(R.id.menuItemsContainer);

        // Inflate menu items
        inflateMenuItems();
    }

    private void inflateMenuItems() {
        menuItemsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < MENU_ITEMS.length; i++) {
            View itemView = inflater.inflate(R.layout.item_menu_ajuste, menuItemsContainer, false);

            ImageView icon = itemView.findViewById(R.id.menuIcon);
            TextView label = itemView.findViewById(R.id.menuLabel);

            icon.setImageResource(MENU_ITEMS[i][0]);
            label.setText(MENU_ITEMS[i][1]);

            final int index = i;
            itemView.setOnClickListener(v -> onMenuItemClick(index));

            menuItemsContainer.addView(itemView);
        }
    }

    private void onMenuItemClick(int index) {
        switch (index) {
            case 0: // Editar Perfil
                Toast.makeText(requireContext(), "Editar Perfil - Próximamente", Toast.LENGTH_SHORT).show();
                break;
            case 1: // Notificaciones
                Toast.makeText(requireContext(), "Notificaciones - Próximamente", Toast.LENGTH_SHORT).show();
                break;
            case 2: // Privacidad y Seguridad
                Toast.makeText(requireContext(), "Privacidad y Seguridad - Próximamente", Toast.LENGTH_SHORT).show();
                break;
            case 3: // Configuración
                Toast.makeText(requireContext(), "Configuración - Próximamente", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setupListeners() {
        btnRetry.setOnClickListener(v -> viewModel.loadProfile());

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.perfil_logout_confirm)
                .setMessage(R.string.perfil_logout_message)
                .setPositiveButton(R.string.perfil_logout, (dialog, which) -> logout())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void logout() {
        viewModel.logout();
        sessionManager.clearSession();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Apply a fade-in animation (300ms) to the given content view.
     */
    private void fadeInContent(View contentView) {
        if (contentView == null) return;
        contentView.setAlpha(0f);
        contentView.setVisibility(View.VISIBLE);
        contentView.animate()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void setupObservers() {
        viewModel.getProfile().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            profileContent.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        fadeInContent(profileContent);
                        bindProfile(resource.data);
                    } else {
                        layoutError.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    layoutError.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void bindProfile(AuthResponse profile) {
        String nombre = profile.getNombre() != null ? profile.getNombre() : "";
        String email = profile.getEmail() != null ? profile.getEmail() : "";
        String rol = profile.getRol() != null ? profile.getRol() : "";
        String telefono = profile.getTelefono();
        String fechaRegistro = profile.getFechaRegistro();

        // Set avatar emoji based on role (or fallback to initials)
        setAvatar(rol, nombre);

        // Set name
        tvNombre.setText(nombre);

        // Set role label
        tvRol.setText(getRoleLabel(rol));

        // Set email
        tvEmail.setText(email);

        // Set phone
        if (telefono != null && !telefono.trim().isEmpty()) {
            tvPhone.setText(telefono);
        } else {
            tvPhone.setText(getString(R.string.perfil_phone_unavailable));
        }

        // Set member-since date
        if (fechaRegistro != null && !fechaRegistro.isEmpty()) {
            String formattedDate = formatDate(fechaRegistro);
            tvMemberSince.setText(formattedDate);
        } else {
            tvMemberSince.setText("—");
        }
    }

    /**
     * Sets the avatar: shows emoji based on role, or falls back to initials.
     */
    private void setAvatar(String rol, String nombre) {
        String emoji = getRoleEmoji(rol);
        if (emoji != null) {
            // Show emoji
            tvEmoji.setVisibility(View.VISIBLE);
            tvEmoji.setText(emoji);
            tvInitialsFallback.setVisibility(View.GONE);
        } else {
            // Fallback to initials
            tvEmoji.setVisibility(View.GONE);
            tvInitialsFallback.setVisibility(View.VISIBLE);
            tvInitialsFallback.setText(getInitials(nombre));
        }
    }

    /**
     * Returns an emoji for the given role, or null if not available.
     * COACH → 👨‍⚕️, DEPORTISTA → 🏃, ADMIN → 🛡️
     */
    private String getRoleEmoji(String rol) {
        if (rol == null) return null;
        switch (rol.toUpperCase()) {
            case "COACH":
                return "\uD83D\uDC68\u200D\u2695\uFE0F"; // 👨‍⚕️
            case "DEPORTISTA":
                return "\uD83C\uDFC3"; // 🏃
            case "ADMIN":
                return "\uD83D\uDEE1\uFE0F"; // 🛡️
            default:
                return null;
        }
    }

    /**
     * Extracts initials from a full name (up to 2 characters).
     * Example: "Juan Pérez" -> "JP", "Admin" -> "A"
     */
    private String getInitials(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "?";
        }
        String[] parts = nombre.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(Character.toUpperCase(part.charAt(0)));
                if (initials.length() >= 2) break;
            }
        }
        return initials.toString();
    }

    /**
     * Returns a human-readable label for the role.
     * COACH → "Fisioterapeuta", DEPORTISTA → "Deportista", ADMIN → "Administrador"
     */
    private String getRoleLabel(String rol) {
        if (rol == null) return "";
        switch (rol.toUpperCase()) {
            case "COACH":
                return getString(R.string.perfil_role_fisioterapeuta);
            case "ADMIN":
                return getString(R.string.perfil_role_admin);
            case "DEPORTISTA":
                return getString(R.string.perfil_role_deportista);
            default:
                return rol;
        }
    }

    /**
     * Formats an ISO date string (yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)
     * into a human-readable format like "Enero 2026".
     */
    private String formatDate(String fechaRegistro) {
        try {
            String datePart = fechaRegistro;
            if (datePart.contains("T")) {
                datePart = datePart.substring(0, datePart.indexOf("T"));
            }
            String[] parts = datePart.split("-");
            if (parts.length == 3) {
                String monthName = getMonthName(Integer.parseInt(parts[1]));
                return monthName + " " + parts[0];
            }
        } catch (Exception e) {
            // Fall through — return original string
        }
        return fechaRegistro;
    }

    /**
     * Returns the Spanish month name for the given month number (1-12).
     */
    private String getMonthName(int month) {
        switch (month) {
            case 1:  return getString(R.string.month_enero);
            case 2:  return getString(R.string.month_febrero);
            case 3:  return getString(R.string.month_marzo);
            case 4:  return getString(R.string.month_abril);
            case 5:  return getString(R.string.month_mayo);
            case 6:  return getString(R.string.month_junio);
            case 7:  return getString(R.string.month_julio);
            case 8:  return getString(R.string.month_agosto);
            case 9:  return getString(R.string.month_septiembre);
            case 10: return getString(R.string.month_octubre);
            case 11: return getString(R.string.month_noviembre);
            case 12: return getString(R.string.month_diciembre);
            default: return "";
        }
    }
}
