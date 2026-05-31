package com.hyperreset.app.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.ui.auth.LoginActivity;
import com.hyperreset.app.utils.Resource;

/**
 * Fragment that displays the logged-in user's profile information.
 * Shows name, email, role badge, member-since date, and a logout button.
 * Uses the same Resource/LiveData pattern as Citas and Tests modules.
 */
public class PerfilFragment extends Fragment {

    private PerfilViewModel viewModel;

    private View profileContent;
    private View progressLoading;
    private View layoutError;
    private View btnRetry;

    private View avatarCircle;
    private TextView tvInitials;
    private TextView tvNombre;
    private TextView tvEmail;
    private TextView tvRol;
    private TextView tvMemberSince;
    private MaterialButton btnLogout;

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
        tvInitials = view.findViewById(R.id.tvInitials);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRol = view.findViewById(R.id.tvRol);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        btnRetry.setOnClickListener(v -> viewModel.loadProfile());

        btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
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
                        profileContent.setVisibility(View.VISIBLE);
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

        // Set initials in avatar circle
        tvInitials.setText(getInitials(nombre));

        // Set name and email
        tvNombre.setText(nombre);
        tvEmail.setText(email);

        // Set role label
        tvRol.setText(getRoleLabel(rol));

        // Set member-since date
        String fechaRegistro = profile.getFechaRegistro();
        if (fechaRegistro != null && !fechaRegistro.isEmpty()) {
            String formattedDate = formatDate(fechaRegistro);
            tvMemberSince.setText(getString(R.string.perfil_member_since, formattedDate));
            tvMemberSince.setVisibility(View.VISIBLE);
        } else {
            tvMemberSince.setVisibility(View.GONE);
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
     */
    private String getRoleLabel(String rol) {
        if (rol == null) return "";
        switch (rol.toUpperCase()) {
            case "COACH":
                return getString(R.string.perfil_role_coach);
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
     * into a human-readable format like "Enero 15, 2024".
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
                return monthName + " " + parts[2] + ", " + parts[0];
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
