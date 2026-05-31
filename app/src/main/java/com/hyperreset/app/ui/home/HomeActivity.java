package com.hyperreset.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyperreset.app.R;
import com.hyperreset.app.ui.citas.list.CitaListFragment;
import com.hyperreset.app.ui.deportistas.list.DeportistaListFragment;
import com.hyperreset.app.ui.materiales.list.MaterialListFragment;
import com.hyperreset.app.ui.mensajes.list.MensajeListFragment;
import com.hyperreset.app.ui.perfil.PerfilFragment;
import com.hyperreset.app.ui.tests.list.TestListFragment;

/**
 * Main activity with bottom navigation for switching between
 * Home, Tests, Appointments, and Profile screens.
 */
public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                fragment = new HomeDashboardFragment();
            } else if (itemId == R.id.navigation_tests) {
                fragment = new TestListFragment();
            } else if (itemId == R.id.navigation_deportistas) {
                fragment = new DeportistaListFragment();
            } else if (itemId == R.id.navigation_mensajes) {
                fragment = new MensajeListFragment();
            } else if (itemId == R.id.navigation_appointments) {
                fragment = new CitaListFragment();
            } else if (itemId == R.id.navigation_materiales) {
                fragment = new MaterialListFragment();
            } else if (itemId == R.id.navigation_profile) {
                fragment = new PerfilFragment();
            }

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
            return true;
        });

        // Set default selection to Tests
        bottomNav.setSelectedItemId(R.id.navigation_tests);
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
     * Placeholder fragment showing a message for unimplented nav items.
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

    /**
     * Simple dashboard fragment for the Home tab.
     */
    public static class HomeDashboardFragment extends Fragment {

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
            textView.setText(R.string.home_welcome_message);
            return textView;
        }
    }
}
