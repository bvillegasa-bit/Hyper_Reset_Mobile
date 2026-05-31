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
import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.ui.reportes.detail.ReporteDetailFragment;
import com.hyperreset.app.utils.Resource;

import java.util.List;

/**
 * Fragment that lists all reports for a given deportista.
 * Accessed from DeportistaDetailFragment via "Ver Reportes" button.
 * Pass deportistaId as argument.
 */
public class ReporteListFragment extends Fragment {

    private ReporteListViewModel viewModel;
    private long deportistaId;

    private RecyclerView rvReportes;
    private View layoutEmpty;
    private View layoutError;
    private View progressLoading;
    private ReporteAdapter adapter;

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

        viewModel = new ReporteListViewModel();

        initViews(view);
        setupRecyclerView();
        setupObservers();

        if (deportistaId > 0) {
            viewModel.loadReportes(deportistaId);
        }
    }

    private void initViews(View view) {
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

    private void setupRecyclerView() {
        rvReportes.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ReporteAdapter(reporte -> {
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
        rvReportes.setAdapter(adapter);
    }

    private void setupObservers() {
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
                        adapter.updateData(resource.data);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR:
                    if (adapter.getItemCount() == 0) {
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
