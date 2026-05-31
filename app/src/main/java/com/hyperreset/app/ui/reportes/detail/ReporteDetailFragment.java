package com.hyperreset.app.ui.reportes.detail;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.utils.Resource;

/**
 * Fragment showing the full detail of a report.
 * Accessed from ReporteListFragment or directly after generating a report.
 * Pass reporteId as argument.
 */
public class ReporteDetailFragment extends Fragment {

    private ReporteDetailViewModel viewModel;
    private long reporteId;

    private TextView tvDeportista;
    private TextView tvTipoTest;
    private TextView tvFecha;
    private TextView tvTipoReporte;
    private TextView tvBadgeCalificacion;
    private TextView tvObservaciones;
    private TextView tvRecomendaciones;
    private TextView tvContenido;
    private View cardObservaciones;
    private View cardRecomendaciones;
    private View cardContenido;
    private View progressLoading;
    private MaterialButton btnGenerarPdf;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reporte_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            reporteId = args.getLong("reporteId", -1);
        }

        viewModel = new ReporteDetailViewModel();

        initViews(view);
        setupObservers();

        if (reporteId > 0) {
            viewModel.loadReporte(reporteId);
        }
    }

    private void initViews(View view) {
        tvDeportista = view.findViewById(R.id.tvReporteDeportista);
        tvTipoTest = view.findViewById(R.id.tvReporteTipoTest);
        tvFecha = view.findViewById(R.id.tvReporteFecha);
        tvTipoReporte = view.findViewById(R.id.tvReporteTipoReporte);
        tvBadgeCalificacion = view.findViewById(R.id.badgeCalificacion);
        tvObservaciones = view.findViewById(R.id.tvReporteObservaciones);
        tvRecomendaciones = view.findViewById(R.id.tvReporteRecomendaciones);
        tvContenido = view.findViewById(R.id.tvReporteContenido);
        cardObservaciones = view.findViewById(R.id.cardObservaciones);
        cardRecomendaciones = view.findViewById(R.id.cardRecomendaciones);
        cardContenido = view.findViewById(R.id.cardContenido);
        progressLoading = view.findViewById(R.id.progressLoading);
        btnGenerarPdf = view.findViewById(R.id.btnGenerarPdf);

        btnGenerarPdf.setOnClickListener(v -> {
            Snackbar.make(requireView(),
                    R.string.reporte_pdf_placeholder, Snackbar.LENGTH_SHORT).show();
        });
    }

    private void setupObservers() {
        viewModel.getReporte().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                progressLoading.setVisibility(View.GONE);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    bindReporteData(resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al cargar reporte",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindReporteData(ReporteResponse reporte) {
        tvDeportista.setText(reporte.getDeportistaNombre() != null
                ? reporte.getDeportistaNombre() : "-");
        tvTipoTest.setText(reporte.getTipoTest() != null
                ? reporte.getTipoTest() : "-");
        tvFecha.setText(reporte.getFechaGeneracion() != null
                ? reporte.getFechaGeneracion() : "-");
        tvTipoReporte.setText(reporte.getTipoReporte() != null
                ? reporte.getTipoReporte() : "-");

        // Calificacion badge
        String cal = reporte.getCalificacion();
        if (cal != null && !cal.isEmpty()) {
            tvBadgeCalificacion.setVisibility(View.VISIBLE);
            tvBadgeCalificacion.setText(getCalificacionLabel(cal));
            setBadgeColor(getColorForCalificacion(cal));
        } else {
            tvBadgeCalificacion.setVisibility(View.GONE);
        }

        // Observaciones
        String obs = reporte.getObservaciones();
        if (obs != null && !obs.isEmpty()) {
            cardObservaciones.setVisibility(View.VISIBLE);
            tvObservaciones.setText(obs);
        } else {
            cardObservaciones.setVisibility(View.GONE);
        }

        // Recomendaciones
        String rec = reporte.getRecomendaciones();
        if (rec != null && !rec.isEmpty()) {
            cardRecomendaciones.setVisibility(View.VISIBLE);
            tvRecomendaciones.setText(rec);
        } else {
            cardRecomendaciones.setVisibility(View.GONE);
        }

        // Contenido
        String contenido = reporte.getContenido();
        if (contenido != null && !contenido.isEmpty()) {
            cardContenido.setVisibility(View.VISIBLE);
            tvContenido.setText(contenido);
        } else {
            cardContenido.setVisibility(View.GONE);
        }

        // PDF button visibility
        String rutaPdf = reporte.getRutaPdf();
        if (rutaPdf != null && !rutaPdf.isEmpty()) {
            btnGenerarPdf.setText(R.string.reporte_action_ver_pdf);
        } else {
            btnGenerarPdf.setText(R.string.reporte_action_generar_pdf);
        }
    }

    private void setBadgeColor(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16f);
        drawable.setColor(color);
        tvBadgeCalificacion.setBackground(drawable);
    }

    private int getColorForCalificacion(String calificacion) {
        if (calificacion == null) return ContextCompat.getColor(requireContext(), R.color.hyper_on_surface);
        switch (calificacion) {
            case "EXCELENTE": return ContextCompat.getColor(requireContext(), R.color.hyper_excelente);
            case "BUENO":     return ContextCompat.getColor(requireContext(), R.color.hyper_bueno);
            case "REGULAR":   return ContextCompat.getColor(requireContext(), R.color.hyper_regular);
            case "DEFICIENTE": return ContextCompat.getColor(requireContext(), R.color.hyper_deficiente);
            default:          return ContextCompat.getColor(requireContext(), R.color.hyper_on_surface);
        }
    }

    private String getCalificacionLabel(String calificacion) {
        if (calificacion == null) return "";
        switch (calificacion) {
            case "EXCELENTE": return getString(R.string.calificacion_excelente);
            case "BUENO":     return getString(R.string.calificacion_bueno);
            case "REGULAR":   return getString(R.string.calificacion_regular);
            case "DEFICIENTE": return getString(R.string.calificacion_deficiente);
            default:          return calificacion;
        }
    }
}
