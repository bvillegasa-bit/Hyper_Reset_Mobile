package com.hyperreset.app.ui.dashboard;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.ActividadRecienteItem;
import com.hyperreset.app.data.model.DashboardActivityResponse;
import com.hyperreset.app.utils.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that displays a paginated list of recent activity
 * for the coach dashboard. Shows loading, empty, and error states.
 */
public class ActividadListFragment extends Fragment {

    private ActividadListViewModel viewModel;

    private View mainContent;
    private ProgressBar progressLoading;
    private LinearLayout layoutError;
    private LinearLayout layoutEmpty;
    private TextView tvError;
    private MaterialButton btnRetry;
    private MaterialButton btnRefresh;
    private RecyclerView recyclerView;

    private ActividadAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actividad_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ActividadListViewModel();

        initViews(view);
        setupObservers();
        setupListeners();

        viewModel.loadActividad(0);
    }

    private void initViews(View view) {
        mainContent = view.findViewById(R.id.mainContent);
        progressLoading = view.findViewById(R.id.progressLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvError = view.findViewById(R.id.tvError);
        btnRetry = view.findViewById(R.id.btnRetry);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActividadAdapter();
        recyclerView.setAdapter(adapter);

        // Infinite scroll listener — loads next page when reaching the bottom
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = llm.getChildCount();
                int totalItemCount = llm.getItemCount();
                int pastVisibleItems = llm.findFirstVisibleItemPosition();

                if (pastVisibleItems + visibleItemCount >= totalItemCount
                        && !viewModel.isLoading()
                        && viewModel.hasMorePages()) {
                    viewModel.loadNextPage();
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.getActividadResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            mainContent.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    if (adapter.getItemCount() == 0) {
                        progressLoading.setVisibility(View.VISIBLE);
                    }
                    break;

                case SUCCESS:
                    if (resource.data != null && resource.data.getItems() != null
                            && !resource.data.getItems().isEmpty()) {
                        mainContent.setVisibility(View.VISIBLE);
                        adapter.setItems(resource.data.getItems());
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    if (adapter.getItemCount() == 0) {
                        layoutError.setVisibility(View.VISIBLE);
                        if (tvError != null) {
                            tvError.setText(resource.message != null
                                    ? resource.message
                                    : getString(R.string.actividad_list_error));
                        }
                    }
                    break;
            }
        });
    }

    private void setupListeners() {
        btnRetry.setOnClickListener(v -> viewModel.refresh());
        btnRefresh.setOnClickListener(v -> viewModel.refresh());
    }

    /**
     * RecyclerView adapter for activity items.
     */
    private class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

        private List<ActividadRecienteItem> items = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_actividad, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ActividadRecienteItem item = items.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setItems(List<ActividadRecienteItem> newItems) {
            this.items = newItems != null ? newItems : new ArrayList<>();
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View dotStatus;
            private final TextView tvPacienteNombre;
            private final TextView tvAccion;
            private final TextView tvTimestamp;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                dotStatus = itemView.findViewById(R.id.dotStatus);
                tvPacienteNombre = itemView.findViewById(R.id.tvPacienteNombre);
                tvAccion = itemView.findViewById(R.id.tvAccion);
                tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            }

            void bind(ActividadRecienteItem item) {
                tvPacienteNombre.setText(item.getPacienteNombre() != null
                        ? item.getPacienteNombre() : "");
                tvAccion.setText(item.getAccion() != null ? item.getAccion() : "");
                tvTimestamp.setText(formatRelativeTime(item.getTimestamp()));

                // Set dot color based on tipo
                int dotColor;
                String tipo = item.getTipo();
                if ("success".equals(tipo)) {
                    dotColor = requireContext().getColor(R.color.hyper_success);
                } else if ("warning".equals(tipo)) {
                    dotColor = requireContext().getColor(R.color.hyper_warning);
                } else if ("info".equals(tipo)) {
                    dotColor = requireContext().getColor(R.color.hyper_info);
                } else {
                    dotColor = requireContext().getColor(R.color.hyper_accent);
                }

                GradientDrawable dotShape = new GradientDrawable();
                dotShape.setShape(GradientDrawable.OVAL);
                dotShape.setColor(dotColor);
                dotShape.setSize(
                        (int) (10 * getResources().getDisplayMetrics().density),
                        (int) (10 * getResources().getDisplayMetrics().density));
                dotStatus.setBackground(dotShape);
            }
        }
    }

    /**
     * Formats an ISO timestamp into a relative time string.
     */
    private String formatRelativeTime(String timestamp) {
        if (timestamp == null) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);
            if (date != null) {
                long diff = new Date().getTime() - date.getTime();
                long minutes = diff / (1000 * 60);
                long hours = minutes / 60;
                long days = hours / 24;

                if (minutes < 1) return "Ahora";
                if (minutes < 60) return minutes + "m";
                if (hours < 24) return hours + "h";
                if (days < 7) return days + "d";
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
                return dateFormat.format(date);
            }
        } catch (Exception e) {
            // Fallback
        }
        return timestamp;
    }
}
