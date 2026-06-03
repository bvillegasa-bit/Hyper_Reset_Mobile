package com.hyperreset.app.ui.mensajes.list;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.Conversacion;
import com.hyperreset.app.ui.mensajes.conversacion.ConversacionFragment;
import com.hyperreset.app.ui.mensajes.form.MensajeFormFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.ArrayList;

/**
 * Redesigned Fragment for the conversation list.
 * Shows grouped conversations with search bar, replacing the old tab-based
 * Recibidos/Enviados view.
 */
public class MensajeListFragment extends Fragment {

    private MensajeListViewModel viewModel;
    private MensajeListAdapter adapter;
    private RecyclerView rvConversaciones;
    private EditText etSearch;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private FloatingActionButton fabCompose;
    private SessionManager sessionManager;
    private TextView tvEmptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mensaje_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new MensajeListViewModel();
        sessionManager = new SessionManager(requireContext());
        viewModel.setSessionManager(sessionManager);

        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupFAB();
        setupObservers();

        viewModel.loadConversaciones();
        viewModel.loadNoLeidos();
    }

    private void initViews(View view) {
        rvConversaciones = view.findViewById(R.id.rvConversaciones);
        etSearch = view.findViewById(R.id.etSearch);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);
        fabCompose = view.findViewById(R.id.fabCompose);
        tvEmptyText = view.findViewById(R.id.tvEmptyText);

        view.findViewById(R.id.btnRetry).setOnClickListener(v -> {
            viewModel.loadConversaciones();
        });
    }

    private void setupRecyclerView() {
        adapter = new MensajeListAdapter(new ArrayList<>(), conversacion -> {
            // Navigate to conversation
            Bundle args = new Bundle();
            args.putLong("otherUserId", conversacion.getOtherUserId());
            args.putString("otherUserName", conversacion.getContactName());
            args.putString("otherUserEmoji", conversacion.getAvatarEmoji());

            ConversacionFragment fragment = new ConversacionFragment();
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvConversaciones.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFAB() {
        fabCompose.setOnClickListener(v -> {
            MensajeFormFragment formFragment = new MensajeFormFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, formFragment)
                    .addToBackStack(null)
                    .commit();
        });
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
        viewModel.getConversaciones().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvConversaciones.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        fadeInContent(rvConversaciones);
                        adapter.updateData(resource.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        String searchText = etSearch.getText() != null
                                ? etSearch.getText().toString() : "";
                        if (!searchText.isEmpty()) {
                            tvEmptyText.setText(R.string.mensajes_search_no_results);
                        } else {
                            tvEmptyText.setText(R.string.mensajes_list_empty_conversations);
                        }
                    }
                    break;
                case ERROR:
                    layoutError.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadConversaciones();
        viewModel.loadNoLeidos();
    }
}
