package com.hyperreset.app.ui.mensajes.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.ui.mensajes.conversacion.ConversacionFragment;
import com.hyperreset.app.ui.mensajes.form.MensajeFormFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.ArrayList;

/**
 * Fragment that displays the list of messages (inbox/sent) with tab toggle.
 * Accessible from the bottom navigation "Mensajes" tab.
 */
public class MensajeListFragment extends Fragment {

    private MensajeListViewModel viewModel;
    private MensajeListAdapter adapter;
    private RecyclerView rvMensajes;
    private MaterialButton btnTabRecibidos;
    private MaterialButton btnTabEnviados;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private FloatingActionButton fabCompose;
    private SessionManager sessionManager;

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

        initViews(view);
        setupRecyclerView();
        setupTabs();
        setupFAB();
        setupObservers();

        // Load inbox by default
        viewModel.setTabActual(true);
        viewModel.loadNoLeidos();
    }

    private void initViews(View view) {
        rvMensajes = view.findViewById(R.id.rvMensajes);
        btnTabRecibidos = view.findViewById(R.id.btnTabRecibidos);
        btnTabEnviados = view.findViewById(R.id.btnTabEnviados);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);
        fabCompose = view.findViewById(R.id.fabCompose);

        view.findViewById(R.id.btnRetry).setOnClickListener(v -> {
            Boolean isRecibidos = viewModel.getTabActual().getValue();
            if (isRecibidos != null && isRecibidos) {
                viewModel.loadRecibidos();
            } else {
                viewModel.loadEnviados();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new MensajeListAdapter(new ArrayList<>(), true, mensaje -> {
            // Navigate to conversation thread
            Bundle args = new Bundle();
            args.putLong("remitenteId", mensaje.getRemitenteId());
            args.putLong("destinatarioId", mensaje.getDestinatarioId());
            args.putString("remitenteNombre", mensaje.getRemitenteNombre());

            ConversacionFragment fragment = new ConversacionFragment();
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvMensajes.setAdapter(adapter);
    }

    private void setupTabs() {
        btnTabRecibidos.setOnClickListener(v -> {
            if (Boolean.FALSE.equals(viewModel.getTabActual().getValue())) {
                viewModel.setTabActual(true);
                updateTabStyles(true);
            }
        });

        btnTabEnviados.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getTabActual().getValue())) {
                viewModel.setTabActual(false);
                updateTabStyles(false);
            }
        });

        updateTabStyles(true);
    }

    private void updateTabStyles(boolean isRecibidos) {
        if (isRecibidos) {
            btnTabRecibidos.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            getResources().getColor(R.color.hyper_accent, requireContext().getTheme())));
            btnTabRecibidos.setTextColor(
                    getResources().getColor(R.color.hyper_on_accent, requireContext().getTheme()));
            btnTabEnviados.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            getResources().getColor(R.color.hyper_surface, requireContext().getTheme())));
            btnTabEnviados.setTextColor(
                    getResources().getColor(R.color.hyper_on_primary, requireContext().getTheme()));
        } else {
            btnTabRecibidos.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            getResources().getColor(R.color.hyper_surface, requireContext().getTheme())));
            btnTabRecibidos.setTextColor(
                    getResources().getColor(R.color.hyper_on_primary, requireContext().getTheme()));
            btnTabEnviados.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            getResources().getColor(R.color.hyper_accent, requireContext().getTheme())));
            btnTabEnviados.setTextColor(
                    getResources().getColor(R.color.hyper_on_accent, requireContext().getTheme()));
        }
        adapter.setShowRecibidos(isRecibidos);
    }

    private void setupFAB() {
        fabCompose.setOnClickListener(v -> navigateToCompose(null));
    }

    private void navigateToCompose(Long preSelectedDeportistaId) {
        MensajeFormFragment formFragment = new MensajeFormFragment();
        if (preSelectedDeportistaId != null) {
            Bundle args = new Bundle();
            args.putLong("deportistaId", preSelectedDeportistaId);
            formFragment.setArguments(args);
        }
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupObservers() {
        viewModel.getMensajes().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvMensajes.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        rvMensajes.setVisibility(View.VISIBLE);
                        adapter.updateData(resource.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    layoutError.setVisibility(View.VISIBLE);
                    break;
            }
        });

        viewModel.getTabActual().observe(getViewLifecycleOwner(), isRecibidos -> {
            if (isRecibidos != null) {
                updateTabStyles(isRecibidos);
            }
        });
    }

    /**
     * Refresh data when returning from back stack.
     */
    @Override
    public void onResume() {
        super.onResume();
        Boolean isRecibidos = viewModel.getTabActual().getValue();
        if (isRecibidos != null && isRecibidos) {
            viewModel.loadRecibidos();
        } else {
            viewModel.loadEnviados();
        }
        viewModel.loadNoLeidos();
    }
}
