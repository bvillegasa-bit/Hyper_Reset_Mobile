package com.hyperreset.app.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hyperreset.app.R;
import com.hyperreset.app.data.api.RetrofitClient;
import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.ui.auth.LoginActivity;
import com.hyperreset.app.ui.home.HomeActivity;
import com.hyperreset.app.ui.splash.onboarding.OnboardingAdapter;
import com.hyperreset.app.ui.splash.onboarding.OnboardingPage;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabDots;
    private Button btnSkip;
    private Button btnNext;
    private Button btnStart;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Skip onboarding if already logged in — but validate the token first
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            validateTokenAndNavigate();
            return;
        }

        setContentView(R.layout.activity_splash);

        initViews();
        setupOnboardingPages();
        setupListeners();
    }

    /**
     * Validates that the stored token is still valid before navigating to Home.
     * If the token expired (401), clears session and shows onboarding/login.
     */
    private void validateTokenAndNavigate() {
        String token = new SessionManager(this).getToken();
        if (token == null) {
            navigateToLogin();
            return;
        }

        // Make a lightweight call to validate the token
        Call<com.hyperreset.app.data.model.ApiResponse<AuthResponse>> call =
                RetrofitClient.getInstance().getApiService().getProfile();

        call.enqueue(new Callback<com.hyperreset.app.data.model.ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<com.hyperreset.app.data.model.ApiResponse<AuthResponse>> call,
                                   Response<com.hyperreset.app.data.model.ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful()) {
                    navigateToHome();
                } else if (response.code() == 401) {
                    // Token expired — clear session and show login
                    new SessionManager(SplashActivity.this).clearSession();
                    navigateToLogin();
                } else {
                    // Other error — still try Home (will redirect on 401 via the listener)
                    navigateToHome();
                }
            }

            @Override
            public void onFailure(Call<com.hyperreset.app.data.model.ApiResponse<AuthResponse>> call, Throwable t) {
                // Network error — still try Home (offline mode maybe)
                navigateToHome();
            }
        });
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabDots = findViewById(R.id.tabDots);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);
        btnStart = findViewById(R.id.btnStart);
    }

    private void setupOnboardingPages() {
        List<OnboardingPage> pages = new ArrayList<>();

        pages.add(new OnboardingPage(
                R.drawable.ic_launcher_foreground,
                R.string.splash_title_1,
                R.string.splash_desc_1
        ));
        pages.add(new OnboardingPage(
                R.drawable.ic_launcher_foreground,
                R.string.splash_title_2,
                R.string.splash_desc_2
        ));
        pages.add(new OnboardingPage(
                R.drawable.ic_launcher_foreground,
                R.string.splash_title_3,
                R.string.splash_desc_3
        ));

        adapter = new OnboardingAdapter(pages);
        viewPager.setAdapter(adapter);
        tabDots.setupWithViewPager(viewPager, true);
    }

    private void setupListeners() {
        btnSkip.setOnClickListener(v -> navigateToLogin());

        btnNext.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });

        btnStart.setOnClickListener(v -> navigateToLogin());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Not needed
            }

            @Override
            public void onPageSelected(int position) {
                updateButtonVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Not needed
            }
        });

        // Initialize button visibility for first page
        updateButtonVisibility(0);
    }

    private void updateButtonVisibility(int position) {
        boolean isLastPage = position == adapter.getCount() - 1;

        btnNext.setVisibility(isLastPage ? android.view.View.GONE : android.view.View.VISIBLE);
        btnStart.setVisibility(isLastPage ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
