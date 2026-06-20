package com.hyperreset.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyperreset.app.R;
import com.hyperreset.app.data.repository.MensajeRepository;
import com.hyperreset.app.ui.auth.LoginActivity;
import com.hyperreset.app.ui.citas.list.CitaListFragment;
import com.hyperreset.app.ui.deportistas.list.DeportistaListFragment;
import com.hyperreset.app.ui.mensajes.list.MensajeListFragment;
import com.hyperreset.app.ui.perfil.PerfilFragment;
import com.hyperreset.app.ui.reportes.list.ReporteListFragment;
import com.hyperreset.app.ui.tests.list.TestListFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

/**
 * Main activity with role-based bottom navigation.
 * Shows different navigation menus for DEPORTISTA vs COACH users.
 */
public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private SessionManager sessionManager;
    private BottomNavigationView bottomNav;
    private MensajeRepository mensajeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        // Global session expiry handler — clears session and redirects to login on 401
        SessionManager.setSessionExpiredListener(() -> {
            runOnUiThread(() -> {
                sessionManager.clearSession();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        fragmentManager = getSupportFragmentManager();
        mensajeRepository = new MensajeRepository();

        bottomNav = findViewById(R.id.bottomNavigation);

        // Clear default menu and inflate role-specific one
        bottomNav.getMenu().clear();
        String userRole = sessionManager.getUserRole();
        if ("COACH".equals(userRole)) {
            bottomNav.inflateMenu(R.menu.bottom_nav_coach);
        } else {
            bottomNav.inflateMenu(R.menu.bottom_nav_deportista);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            String role = sessionManager.getUserRole();

            if (itemId == R.id.navigation_home) {
                fragment = new HomeDashboardFragment();
            } else if (itemId == R.id.navigation_tests) {
                fragment = new TestListFragment();
            } else if (itemId == R.id.navigation_appointments) {
                fragment = new CitaListFragment();
            } else if (itemId == R.id.navigation_messages) {
                fragment = new MensajeListFragment();
            } else if (itemId == R.id.navigation_profile) {
                fragment = new PerfilFragment();
            } else if (itemId == R.id.navigation_patients) {
                fragment = new DeportistaListFragment();
            } else if (itemId == R.id.navigation_reports) {
                if ("COACH".equals(role)) {
                    fragment = new ReporteListFragment();
                } else {
                    // Placeholder for future implementation
                    fragment = PlaceholderFragment.newInstance(getString(R.string.nav_reports));
                }
            } else if (itemId == R.id.navigation_schedule) {
                fragment = new CitaListFragment();
            }

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
            return true;
        });

        // Set default selection to Home
        bottomNav.setSelectedItemId(R.id.navigation_home);

        // Load unread messages badge
        loadUnreadBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh unread badge when returning to the activity
        loadUnreadBadge();
    }

    /**
     * Load unread messages count and show/hide badge on the Messages tab.
     */
    private void loadUnreadBadge() {
        mensajeRepository.getNoLeidos(result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                int count = result.data;
                updateUnreadBadge(count);
            }
        });
    }

    /**
     * Show or hide the unread badge on the messages navigation icon.
     *
     * @param count Number of unread messages
     */
    private void updateUnreadBadge(int count) {
        if (bottomNav == null) return;
        BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.navigation_messages);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(this, R.color.hyper_error));
            badge.setBadgeTextColor(
                    androidx.core.content.ContextCompat.getColor(this, R.color.hyper_on_primary));
        } else {
            badge.setVisible(false);
        }
    }

    /**
     * Programmatically select a bottom navigation item.
     * Used by fragments to navigate to a different tab.
     */
    public void selectNavigationItem(int itemId) {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(itemId);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Placeholder fragment showing a message for unimplemented nav items.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_MESSAGE = "message";

        public static PlaceholderFragment newInstance(String message) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            TextView textView = new TextView(requireContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(android.view.Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.hyper_on_primary));
            textView.setTextSize(18);
            String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : "";
            textView.setText(message);
            return textView;
        }
    }
}
