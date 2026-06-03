package com.hyperreset.app.ui.mensajes.conversacion;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hyperreset.app.R;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

/**
 * Redesigned Fragment for the individual conversation/chat view.
 * Features a header with avatar + contact name, chat bubbles with
 * gradient styling, and an input bar with send button.
 */
public class ConversacionFragment extends Fragment {

    private ConversacionViewModel viewModel;
    private ConversacionAdapter adapter;
    private RecyclerView rvConversacion;
    private TextView tvContactName;
    private TextView tvAvatar;
    private TextView tvError;
    private View progressLoading;
    private TextInputEditText etReply;
    private MaterialButton btnSend;
    private View layoutInput;
    private View btnBack;
    private SessionManager sessionManager;

    private long otherUserId;
    private String otherUserName;
    private String otherUserEmoji;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        Bundle args = getArguments();
        if (args != null) {
            otherUserId = args.getLong("otherUserId", -1);
            otherUserName = args.getString("otherUserName", "");
            otherUserEmoji = args.getString("otherUserEmoji", "");

            // Fallback: support legacy navigation from old messages
            if (otherUserId <= 0) {
                otherUserId = args.getLong("remitenteId", -1);
            }
            if (otherUserId <= 0) {
                otherUserId = args.getLong("destinatarioId", -1);
            }
            if (otherUserName == null || otherUserName.isEmpty()) {
                otherUserName = args.getString("remitenteNombre", "");
            }
        }

        long currentUserId = sessionManager.getUserId();
        viewModel = new ConversacionViewModel();
        viewModel.init(otherUserId, currentUserId, otherUserName, otherUserEmoji);

        initViews(view);
        setupRecyclerView();
        setupObservers();

        if (otherUserId > 0) {
            viewModel.loadConversacion();
        }
    }

    private void initViews(View view) {
        tvContactName = view.findViewById(R.id.tvContactName);
        tvAvatar = view.findViewById(R.id.tvAvatar);
        rvConversacion = view.findViewById(R.id.rvConversacion);
        progressLoading = view.findViewById(R.id.progressLoading);
        tvError = view.findViewById(R.id.tvError);
        etReply = view.findViewById(R.id.etReply);
        btnSend = view.findViewById(R.id.btnSend);
        layoutInput = view.findViewById(R.id.layoutInput);
        btnBack = view.findViewById(R.id.btnBack);

        // Set contact info
        viewModel.getOtherUserName().observe(getViewLifecycleOwner(), name -> {
            tvContactName.setText(name != null && !name.isEmpty()
                    ? name
                    : getString(R.string.mensajes_conversacion_title));
        });

        // Set avatar emoji
        viewModel.getOtherUserEmoji().observe(getViewLifecycleOwner(), emoji -> {
            if (emoji != null && !emoji.isEmpty()) {
                tvAvatar.setText(emoji);
            } else {
                // Determine based on current user role
                boolean isDeportista = sessionManager.isDeportista();
                tvAvatar.setText(ConversacionViewModel.getEmojiForRole(isDeportista));
            }
        });

        // Back button
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Send button
        btnSend.setOnClickListener(v -> sendReply());

        // Enable/disable send button based on input
        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s != null && s.toString().trim().length() > 0;
                btnSend.setEnabled(hasText);
                btnSend.setAlpha(hasText ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // Initial state: disabled
        btnSend.setEnabled(false);
        btnSend.setAlpha(0.5f);
    }

    private void setupRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);
        rvConversacion.setLayoutManager(llm);

        adapter = new ConversacionAdapter(viewModel.getCurrentUserId());
        rvConversacion.setAdapter(adapter);
    }

    private void sendReply() {
        String contenido = etReply.getText() != null ? etReply.getText().toString().trim() : "";
        if (contenido.isEmpty()) {
            Snackbar.make(requireView(),
                    getString(R.string.mensajes_validation_contenido),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        btnSend.setEnabled(false);
        btnSend.setAlpha(0.5f);
        viewModel.sendReply(contenido);
        etReply.setText("");
    }

    private void setupObservers() {
        viewModel.getMensajes().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvConversacion.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    rvConversacion.setVisibility(View.VISIBLE);
                    if (resource.data != null) {
                        adapter.updateData(resource.data);
                        // Scroll to bottom
                        if (resource.data.size() > 0) {
                            rvConversacion.smoothScrollToPosition(resource.data.size() - 1);
                        }
                    }
                    break;
                case ERROR:
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(resource.message != null
                            ? resource.message
                            : getString(R.string.mensajes_conversacion_error));
                    break;
            }
        });

        viewModel.getSendResult().observe(getViewLifecycleOwner(), resource -> {
            btnSend.setEnabled(true);
            btnSend.setAlpha(1.0f);
            if (resource != null && resource.status == Resource.Status.ERROR) {
                Snackbar.make(requireView(),
                        resource.message != null ? resource.message : getString(R.string.mensajes_send_error),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
