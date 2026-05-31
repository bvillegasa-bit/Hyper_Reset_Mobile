package com.hyperreset.app.ui.mensajes.conversacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hyperreset.app.R;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

/**
 * Fragment that displays a conversation thread with another user.
 * Chat-like view with sent messages right-aligned, received left-aligned.
 */
public class ConversacionFragment extends Fragment {

    private ConversacionViewModel viewModel;
    private ConversacionAdapter adapter;
    private RecyclerView rvConversacion;
    private TextView tvTitle;
    private TextView tvError;
    private View progressLoading;
    private TextInputEditText etReply;
    private MaterialButton btnSend;
    private View layoutInput;
    private SessionManager sessionManager;

    private long otherUserId;

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
            otherUserId = args.getLong("remitenteId", -1);
            // If we navigated from a sent message, destinatarioId is the other user
            if (otherUserId <= 0) {
                otherUserId = args.getLong("destinatarioId", -1);
            }
        }

        String otherUserName = args != null ? args.getString("remitenteNombre", "") : "";

        long currentUserId = sessionManager.getUserId();
        viewModel = new ConversacionViewModel();
        viewModel.init(otherUserId, currentUserId, otherUserName);

        initViews(view);
        setupRecyclerView();
        setupObservers();

        if (otherUserId > 0) {
            viewModel.loadConversacion();
        }
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tvTitle);
        rvConversacion = view.findViewById(R.id.rvConversacion);
        progressLoading = view.findViewById(R.id.progressLoading);
        tvError = view.findViewById(R.id.tvError);
        etReply = view.findViewById(R.id.etReply);
        btnSend = view.findViewById(R.id.btnSend);
        layoutInput = view.findViewById(R.id.layoutInput);

        // Set title
        viewModel.getOtherUserName().observe(getViewLifecycleOwner(), name -> {
            tvTitle.setText(name != null && !name.isEmpty()
                    ? name
                    : getString(R.string.mensajes_conversacion_title));
        });

        btnSend.setOnClickListener(v -> sendReply());
    }

    private void setupRecyclerView() {
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
            if (resource != null && resource.status == Resource.Status.ERROR) {
                Snackbar.make(requireView(),
                        resource.message != null ? resource.message : getString(R.string.mensajes_send_error),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
