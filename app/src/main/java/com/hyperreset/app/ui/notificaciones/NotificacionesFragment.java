package com.hyperreset.app.ui.notificaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.NotificacionItem;
import com.hyperreset.app.ui.mensajes.list.MensajeListFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.List;

/**
 * Fragment that displays a list of notifications for the current user.
 * Data is fetched from DashboardRepository.getActividad() and mapped
 * to NotificacionItem UI models via NotificacionesViewModel.
 */
public class NotificacionesFragment extends Fragment {

    private NotificacionesViewModel viewModel;

    private RecyclerView rvNotificaciones;
    private View progressLoading;
    private View layoutEmpty;
    private View layoutError;
    private View btnRetry;
    private View btnGoToMessages;
    private NotificacionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notificaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new NotificacionesViewModel(requireActivity().getApplication());

        initViews(view);
        setupRecyclerView();
        setupObservers();

        String userRole = new SessionManager(requireContext()).getUserRole();

        if ("COACH".equals(userRole)) {
            viewModel.loadNotificaciones();
            btnRetry.setOnClickListener(v -> viewModel.loadNotificaciones());
        } else {
            progressLoading.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initViews(View view) {
        rvNotificaciones = view.findViewById(R.id.rvNotificaciones);
        progressLoading = view.findViewById(R.id.progressLoading);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        btnRetry = view.findViewById(R.id.btnRetry);
        btnGoToMessages = view.findViewById(R.id.btnGoToMessages);

        btnGoToMessages.setOnClickListener(v -> navigateToMessages());
    }

    private void setupRecyclerView() {
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificacionAdapter();
        rvNotificaciones.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getNotificaciones().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvNotificaciones.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        rvNotificaciones.setVisibility(View.VISIBLE);
                        adapter.updateData(resource.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    if (adapter.getItemCount() == 0) {
                        layoutError.setVisibility(View.VISIBLE);
                    } else {
                        // Keep existing data but show snackbar
                        if (getView() != null) {
                            com.google.android.material.snackbar.Snackbar.make(
                                    getView(),
                                    resource.message != null ? resource.message : "Error al cargar notificaciones",
                                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                            ).show();
                        }
                    }
                    break;
            }
        });
    }

    private void navigateToMessages() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MensajeListFragment())
                .addToBackStack(null)
                .commit();
    }

    // ==================================================================
    // NotificacionAdapter
    // ==================================================================

    private static class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

        private List<NotificacionItem> items;

        void updateData(List<NotificacionItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notificacion, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotificacionItem item = items.get(position);
            holder.ivIcon.setImageResource(item.getIconResId());
            holder.tvTitle.setText(item.getTitle() != null ? item.getTitle() : "");
            holder.tvDescription.setText(item.getDescription() != null ? item.getDescription() : "");
            holder.tvTimestamp.setText(item.getTimestamp() != null ? item.getTimestamp() : "");
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView ivIcon;
            final TextView tvTitle;
            final TextView tvDescription;
            final TextView tvTimestamp;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.ivNotificacionIcon);
                tvTitle = itemView.findViewById(R.id.tvNotificacionTitle);
                tvDescription = itemView.findViewById(R.id.tvNotificacionDescription);
                tvTimestamp = itemView.findViewById(R.id.tvNotificacionTimestamp);
            }
        }
    }
}
