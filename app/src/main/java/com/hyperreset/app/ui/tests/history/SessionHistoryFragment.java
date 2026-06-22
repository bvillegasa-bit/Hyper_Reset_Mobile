package com.hyperreset.app.ui.tests.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.ui.tests.detail.TestDetailFragment;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class SessionHistoryFragment extends Fragment {

    private static final String ARG_DEPORTISTA_ID = "deportistaId";
    private static final String ARG_TIPO_TEST = "tipoTest";
    private static final String ARG_TEST_NAME = "testName";

    private long deportistaId;
    private String tipoTest;
    private String testName;

    private SessionHistoryViewModel viewModel;
    private RecyclerView rvHistory;
    private ProgressBar progressBar;
    private View emptyState;
    private TextView tvHistoryTitle;
    private SessionHistoryAdapter adapter;

    public static SessionHistoryFragment newInstance(long deportistaId, String tipoTest, String testName) {
        SessionHistoryFragment fragment = new SessionHistoryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DEPORTISTA_ID, deportistaId);
        args.putString(ARG_TIPO_TEST, tipoTest);
        args.putString(ARG_TEST_NAME, testName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            deportistaId = args.getLong(ARG_DEPORTISTA_ID, -1);
            tipoTest = args.getString(ARG_TIPO_TEST, "");
            testName = args.getString(ARG_TEST_NAME, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SessionHistoryViewModel.class);

        tvHistoryTitle = view.findViewById(R.id.tvHistoryTitle);
        rvHistory = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);

        tvHistoryTitle.setText(testName != null && !testName.isEmpty()
                ? getString(R.string.test_history_title_format, testName)
                : getString(R.string.test_history_title));

        adapter = new SessionHistoryAdapter(session -> {
            // Navigate to TestDetailFragment in session mode
            TestDetailFragment fragment = new TestDetailFragment();
            Bundle args = new Bundle();
            args.putLong("testId", session.getId());
            args.putString("tipoTest", session.getTipoTest());
            args.putLong("deportistaId", deportistaId);
            args.putString("deportistaNombre", session.getDeportistaNombre());
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvHistory.setAdapter(adapter);

        // Observe sessions
        viewModel.getSessions().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
                emptyState.setVisibility(View.GONE);
            } else if (resource.status == Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                List<TestFisicoResponse> data = resource.data;
                if (data != null && !data.isEmpty()) {
                    rvHistory.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                    adapter.setData(data);
                } else {
                    rvHistory.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                Snackbar.make(view, resource.message != null ? resource.message : "Error al cargar historial",
                        Snackbar.LENGTH_LONG).show();
            }
        });

        // Load data
        if (deportistaId > 0 && tipoTest != null && !tipoTest.isEmpty()) {
            viewModel.loadHistory(deportistaId, tipoTest);
        }
    }
}
