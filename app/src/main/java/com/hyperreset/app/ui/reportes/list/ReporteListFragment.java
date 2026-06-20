package com.hyperreset.app.ui.reportes.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.ui.reportes.detail.ReporteDetailFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.List;

/**
 * Fragment that lists all reports for a given deportista.
 * Supports two modes:
 * 1. Direct mode (deportistaId passed in args): Load and show reports directly
 * 2. Selector mode (no deportistaId): First show a deportista list to select from
 */
public class ReporteListFragment extends Fragment {

    private ReporteListViewModel viewModel;
    private SessionManager sessionManager;
    private long deportistaId = -1;
    private boolean modoSeleccion = true;

    // Deportista selector views
    private View layoutDeportistaSelector;
    private RecyclerView rvDeportistas;
    private View layoutDeportistaEmpty;
    private DeportistaAdapter deportistaAdapter;

    // Reportes views
    private RecyclerView rvReportes;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private ReporteAdapter reporteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reporte_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            deportistaId = args.getLong("deportistaId", -1);
        }

        sessionManager = new SessionManager(requireContext());
        viewModel = new ReporteListViewModel();

        initViews(view);
        setupRecyclerViews();
        setupObservers();
        setupBackHandler();

        if (deportistaId > 0) {
            modoSeleccion = false;
            showReportesMode();
            viewModel.loadReportes(deportistaId);
        } else {
            modoSeleccion = true;
            showDeportistaSelector();
            long coachId = sessionManager.getUserId();
            if (coachId > 0) {
                viewModel.loadDeportistasByCoach(coachId);
            }
        }
    }

    private void initViews(View view) {
        // Deportista selector
        layoutDeportistaSelector = view.findViewById(R.id.layoutDeportistaSelector);
        rvDeportistas = view.findViewById(R.id.rvDeportistas);
        layoutDeportistaEmpty = view.findViewById(R.id.layoutDeportistaEmpty);

        // Reportes
        rvReportes = view.findViewById(R.id.rvReportes);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutError = view.findViewById(R.id.layoutError);
        progressLoading = view.findViewById(R.id.progressLoading);

        view.findViewById(R.id.btnRetry).setOnClickListener(v -> {
            if (deportistaId > 0) {
                viewModel.loadReportes(deportistaId);
            }
        });
    }

    private void setupRecyclerViews() {
        // Deportista adapter
        rvDeportistas.setLayoutManager(new LinearLayoutManager(requireContext()));
        deportistaAdapter = new DeportistaAdapter(deportista -> {
            // User selected a deportista → load reports
            deportistaId = deportista.getId();
            modoSeleccion = false;
            showReportesMode();
            viewModel.loadReportes(deportistaId);
        });
        rvDeportistas.setAdapter(deportistaAdapter);

        // Reporte adapter
        rvReportes.setLayoutManager(new LinearLayoutManager(requireContext()));
        reporteAdapter = new ReporteAdapter(reporte -> {
            Bundle args = new Bundle();
            args.putLong("reporteId", reporte.getId());
            ReporteDetailFragment detailFragment = new ReporteDetailFragment();
            detailFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvReportes.setAdapter(reporteAdapter);
    }

    private void setupObservers() {
        // Observe deportistas list
        viewModel.getDeportistas().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            progressLoading.setVisibility(View.GONE);
            layoutDeportistaEmpty.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    if (modoSeleccion) {
                        progressLoading.setVisibility(View.VISIBLE);
                    }
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        deportistaAdapter.updateData(resource.data);
                    } else {
                        layoutDeportistaEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    if (deportistaAdapter.getItemCount() == 0) {
                        layoutError.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar.make(requireView(),
                                resource.message != null ? resource.message : "Error al cargar deportistas",
                                Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        });

        // Observe reportes list
        viewModel.getReportes().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            rvReportes.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            layoutError.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);

            switch (resource.status) {
                case LOADING:
                    progressLoading.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        rvReportes.setVisibility(View.VISIBLE);
                        reporteAdapter.updateData(resource.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    if (reporteAdapter.getItemCount() == 0) {
                        layoutError.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar.make(requireView(),
                                resource.message != null ? resource.message : "Error al cargar reportes",
                                Snackbar.LENGTH_LONG).show();
                    }
                    break;
            }
        });
    }

    private void setupBackHandler() {
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (!modoSeleccion) {
                            // In reports mode → go back to deportista selector
                            showDeportistaSelector();
                        } else {
                            // In selector mode → pop fragment
                            setEnabled(false);
                            requireActivity().onBackPressed();
                        }
                    }
                });
    }

    private void showDeportistaSelector() {
        modoSeleccion = true;
        deportistaId = -1;
        layoutDeportistaSelector.setVisibility(View.VISIBLE);
        rvReportes.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        progressLoading.setVisibility(View.GONE);
    }

    private void showReportesMode() {
        modoSeleccion = false;
        layoutDeportistaSelector.setVisibility(View.GONE);
    }

    // ==================================================================
    // DeportistaAdapter
    // ==================================================================

    /**
     * RecyclerView adapter for displaying deportista list in selector mode.
     */
    private static class DeportistaAdapter extends RecyclerView.Adapter<DeportistaAdapter.ViewHolder> {

        private List<DeportistaResponse> deportistas;
        private final OnDeportistaClickListener listener;

        interface OnDeportistaClickListener {
            void onDeportistaClick(DeportistaResponse deportista);
        }

        DeportistaAdapter(OnDeportistaClickListener listener) {
            this.listener = listener;
        }

        void updateData(List<DeportistaResponse> deportistas) {
            this.deportistas = deportistas;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_deportista, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DeportistaResponse d = deportistas.get(position);
            holder.tvNombre.setText(d.getNombreCompleto() != null ? d.getNombreCompleto() : "-");
            holder.tvEmail.setText(d.getEmail() != null ? d.getEmail() : "-");
            holder.itemView.setOnClickListener(v -> listener.onDeportistaClick(d));
        }

        @Override
        public int getItemCount() {
            return deportistas != null ? deportistas.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvNombre;
            final TextView tvEmail;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombreCompleto);
                tvEmail = itemView.findViewById(R.id.tvEmail);
            }
        }
    }

    // ==================================================================
    // ReporteAdapter (unchanged)
    // ==================================================================

    /**
     * RecyclerView adapter for displaying report cards.
     */
    private static class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ViewHolder> {

        private List<ReporteResponse> reportes;
        private final OnReporteClickListener listener;

        interface OnReporteClickListener {
            void onReporteClick(ReporteResponse reporte);
        }

        ReporteAdapter(OnReporteClickListener listener) {
            this.listener = listener;
        }

        void updateData(List<ReporteResponse> reportes) {
            this.reportes = reportes;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reporte, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReporteResponse r = reportes.get(position);
            holder.tvTipoTest.setText(r.getTipoTest() != null ? r.getTipoTest() : "-");
            holder.tvFecha.setText(r.getFechaGeneracion() != null ? r.getFechaGeneracion() : "-");
            holder.tvDeportista.setText(r.getDeportistaNombre() != null ? r.getDeportistaNombre() : "-");

            String cal = r.getCalificacion();
            if (cal != null && !cal.isEmpty()) {
                holder.tvCalificacion.setVisibility(View.VISIBLE);
                holder.tvCalificacion.setText(cal);
            } else {
                holder.tvCalificacion.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> listener.onReporteClick(r));
        }

        @Override
        public int getItemCount() {
            return reportes != null ? reportes.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvTipoTest;
            final TextView tvFecha;
            final TextView tvDeportista;
            final TextView tvCalificacion;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTipoTest = itemView.findViewById(R.id.tvItemReporteTipoTest);
                tvFecha = itemView.findViewById(R.id.tvItemReporteFecha);
                tvDeportista = itemView.findViewById(R.id.tvItemReporteDeportista);
                tvCalificacion = itemView.findViewById(R.id.tvItemReporteCalificacion);
            }
        }
    }
}
