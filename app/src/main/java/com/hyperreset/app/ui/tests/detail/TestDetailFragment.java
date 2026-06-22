package com.hyperreset.app.ui.tests.detail;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.ResultadoResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.ui.reportes.detail.ReporteDetailFragment;
import com.hyperreset.app.ui.tests.entry.ResultEntryFragment;
import com.hyperreset.app.ui.tests.execution.TestExecutionFragment;
import com.hyperreset.app.ui.tests.history.SessionHistoryFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.List;

/**
 * Fragment showing detailed view of a physical test session or test type.
 * - If testId > 0: loads the full test session with results (existing behavior for COACH)
 * - If testId <= 0: shows test type info passed via arguments (from battery test list)
 */
public class TestDetailFragment extends Fragment {

    private TestDetailViewModel viewModel;
    private long testId;
    private String tipoTest;
    private String testName;
    private long currentDepId;
    private boolean isBatteryView; // true if navigated from battery test (no session ID)

    private TextView tvDeportista;
    private TextView tvTestType;
    private TextView tvDate;
    private TextView badgeStatus;
    private View cardBiometrics;
    private TextView tvPeso;
    private TextView tvEstatura;
    private RecyclerView rvResultados;
    private View progressLoading;
    private MaterialButton btnAddResult;
    private MaterialButton btnFinalizar;
    private MaterialButton btnGenerarReporte;
    private MaterialButton btnVerHistorial;

    // Battery test type info views
    private View batteryInfoSection;
    private TextView tvBatteryLastValue;
    private TextView tvBatteryDate;
    private TextView tvBatteryUnidad;
    private TextView tvBatteryCalificacion;
    private MaterialButton btnRealizarPrueba;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            testId = args.getLong("testId", -1);
            tipoTest = args.getString("tipoTest", "");
            testName = args.getString("testName", "");
            isBatteryView = testId <= 0;
        }

        viewModel = new TestDetailViewModel();

        initViews(view);
        setupObservers();

        if (!isBatteryView) {
            // Existing behavior: load full test session
            viewModel.loadTest(testId);
            viewModel.loadResultados(testId);
        } else {
            // Battery test type view: show info from arguments
            showBatteryInfo(view);
        }
    }

    private void initViews(View view) {
        tvDeportista = view.findViewById(R.id.tvDeportista);
        tvTestType = view.findViewById(R.id.tvTestType);
        tvDate = view.findViewById(R.id.tvDate);
        badgeStatus = view.findViewById(R.id.badgeStatus);
        cardBiometrics = view.findViewById(R.id.cardBiometrics);
        tvPeso = view.findViewById(R.id.tvPeso);
        tvEstatura = view.findViewById(R.id.tvEstatura);
        rvResultados = view.findViewById(R.id.rvResultados);
        progressLoading = view.findViewById(R.id.progressLoading);
        btnAddResult = view.findViewById(R.id.btnAddResult);
        btnFinalizar = view.findViewById(R.id.btnFinalizar);
        btnGenerarReporte = view.findViewById(R.id.btnGenerarReporte);

        // Battery info section (hidden by default in XML)
        batteryInfoSection = view.findViewById(R.id.batteryInfoSection);
        tvBatteryLastValue = view.findViewById(R.id.tvBatteryLastValue);
        tvBatteryDate = view.findViewById(R.id.tvBatteryDate);
        tvBatteryUnidad = view.findViewById(R.id.tvBatteryUnidad);
        tvBatteryCalificacion = view.findViewById(R.id.tvBatteryCalificacion);
        btnRealizarPrueba = view.findViewById(R.id.btnRealizarPrueba);

        rvResultados.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnAddResult.setOnClickListener(v -> navigateToResultEntry());
        btnFinalizar.setOnClickListener(v -> viewModel.completarTest(testId));
        btnGenerarReporte.setOnClickListener(v -> navigateToGenerateReporte());
        btnVerHistorial = view.findViewById(R.id.btnVerHistorial);
        btnVerHistorial.setOnClickListener(v -> navigateToHistory());
        btnRealizarPrueba.setOnClickListener(v -> onRealizarPruebaClick());
    }

    private void showBatteryInfo(View view) {
        Bundle args = getArguments();
        if (args == null) return;

        // Hide session-specific views
        cardBiometrics.setVisibility(View.GONE);
        btnAddResult.setVisibility(View.GONE);
        btnFinalizar.setVisibility(View.GONE);

        // Show battery info section
        batteryInfoSection.setVisibility(View.VISIBLE);

        // Set test type name
        tvTestType.setText(!testName.isEmpty() ? testName : getTestTypeLabel(tipoTest));

        // Set deportista label — hide in battery view (no name available)
        tvDeportista.setVisibility(View.GONE);

        // Set last value if available
        boolean completado = args.getBoolean("completado", false);
        if (completado) {
            badgeStatus.setText(R.string.test_detail_completado);
            setBadgeColor(ContextCompat.getColor(requireContext(), R.color.hyper_excelente));

            double ultimoValor = args.getDouble("ultimoValor", 0);
            String unidad = args.getString("unidad", "");
            String fechaUltimo = args.getString("fechaUltimo", "");

            tvBatteryLastValue.setText(String.valueOf(ultimoValor));
            tvBatteryUnidad.setText(unidad);
            tvBatteryDate.setText(fechaUltimo != null ? fechaUltimo : "");

            // Completed test: hide "Realizar prueba" button
            btnRealizarPrueba.setVisibility(View.GONE);

            // Check if user is COACH to show report button
            SessionManager sm = new SessionManager(requireContext());
            String role = sm.getUserRole();
            boolean isCoach = role != null && role.equals("COACH");
            if (completado && testId > 0 && isCoach) {
                btnGenerarReporte.setVisibility(View.VISIBLE);
            } else {
                btnGenerarReporte.setVisibility(View.GONE);
            }

            // Show history button when test type has been completed (any user role)
            btnVerHistorial.setVisibility(View.VISIBLE);
        } else {
            badgeStatus.setText(R.string.test_bateria_coming_soon);
            setBadgeColor(ContextCompat.getColor(requireContext(), R.color.hyper_in_progress));

            tvBatteryLastValue.setText("--");
            tvBatteryUnidad.setText("");
            tvBatteryDate.setText("");

            // Pending test: show "Realizar prueba" button
            btnRealizarPrueba.setVisibility(View.VISIBLE);

            // Hide history button for pending tests
            btnVerHistorial.setVisibility(View.GONE);
        }

        // Show calificación badge if available
        String calificacion = args.getString("calificacion", null);
        if (calificacion != null && !calificacion.trim().isEmpty()) {
            tvBatteryCalificacion.setVisibility(View.VISIBLE);
            tvBatteryCalificacion.setText(calificacion);
            tvBatteryCalificacion.setTextColor(getColorForCalificacion(calificacion));
        } else {
            tvBatteryCalificacion.setVisibility(View.GONE);
        }

        tvDate.setVisibility(View.GONE);
    }

    private void setupObservers() {
        viewModel.getTest().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null || isBatteryView) return;

            if (resource.status == Resource.Status.LOADING) {
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                progressLoading.setVisibility(View.GONE);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    bindTestData(resource.data);
                }
            }
        });

        viewModel.getResultados().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null || isBatteryView) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                bindResultados(resource.data);
            }
        });

        viewModel.getCreateTestResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                btnRealizarPrueba.setEnabled(false);
                btnRealizarPrueba.setText(R.string.test_bateria_creando);
            } else {
                btnRealizarPrueba.setEnabled(true);
                btnRealizarPrueba.setText(R.string.test_bateria_realizar_prueba);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    // Navigate to test execution with the newly created test session
                    navigateToTestExecution(resource.data.getId(), tipoTest, currentDepId);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al crear sesión de prueba",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getGenerateResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                btnGenerarReporte.setEnabled(false);
                btnGenerarReporte.setText(R.string.reportes_generating);
            } else {
                btnGenerarReporte.setEnabled(true);
                btnGenerarReporte.setText(R.string.test_action_generar_reporte);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    navigateToReporteDetail(resource.data.getId());
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al generar reporte",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindTestData(TestFisicoResponse test) {
        tvDeportista.setText(test.getDeportistaNombre());
        tvTestType.setText(getTestTypeLabel(test.getTipoTest()));
        tvDate.setText(test.getFechaTest());

        if (test.isCompletado()) {
            badgeStatus.setText(R.string.test_detail_completado);
            setBadgeColor(ContextCompat.getColor(requireContext(), R.color.hyper_excelente));
            btnAddResult.setVisibility(View.GONE);
            btnFinalizar.setVisibility(View.GONE);

            // Only COACH can generate reports
            SessionManager sm = new SessionManager(requireContext());
            String role = sm.getUserRole();
            boolean isCoach = role != null && role.equals("COACH");
            btnGenerarReporte.setVisibility(isCoach ? View.VISIBLE : View.GONE);
        } else {
            badgeStatus.setText(R.string.test_detail_en_progreso);
            setBadgeColor(ContextCompat.getColor(requireContext(), R.color.hyper_in_progress));
            btnAddResult.setVisibility(View.VISIBLE);
            btnFinalizar.setVisibility(View.VISIBLE);
            btnGenerarReporte.setVisibility(View.GONE);
        }
    }

    private void bindResultados(List<ResultadoResponse> resultados) {
        ResultadoAdapter adapter = new ResultadoAdapter(resultados);
        rvResultados.setAdapter(adapter);
    }

    private void navigateToGenerateReporte() {
        // Use deportistaId from the loaded test or from arguments
        long depId = viewModel.getDeportistaId();
        if (depId <= 0) {
            Bundle args = getArguments();
            if (args != null) {
                depId = args.getLong("deportistaId", 0);
            }
        }
        if (depId > 0 && testId > 0) {
            viewModel.generarReporte(testId, depId);
        } else {
            Snackbar.make(requireView(),
                    getString(R.string.test_error_generar_reporte),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void navigateToReporteDetail(long reporteId) {
        Bundle args = new Bundle();
        args.putLong("reporteId", reporteId);
        ReporteDetailFragment fragment = new ReporteDetailFragment();
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void onRealizarPruebaClick() {
        Bundle args = getArguments();
        if (args == null) return;

        long depId = args.getLong("deportistaId", 0);
        if (depId <= 0) {
            Snackbar.make(requireView(), "No se ha seleccionado un deportista", Snackbar.LENGTH_LONG).show();
            return;
        }
        currentDepId = depId;
        viewModel.createTestSession(depId, tipoTest);
    }

    private void navigateToHistory() {
        Bundle args = getArguments();
        if (args == null) return;
        long depId = args.getLong("deportistaId", -1);
        String tipo = args.getString("tipoTest", "");
        String name = args.getString("testName", "");
        SessionHistoryFragment fragment =
                SessionHistoryFragment.newInstance(depId, tipo, name);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToTestExecution(long newTestId, String tipoTest, long depId) {
        TestExecutionFragment fragment = new TestExecutionFragment();
        Bundle args = new Bundle();
        args.putLong("testId", newTestId);
        args.putString("tipoTest", tipoTest);
        args.putLong("deportistaId", depId);
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToResultEntryForNewTest(long newTestId) {
        Bundle args = new Bundle();
        args.putLong("testId", newTestId);
        args.putString("tipoTest", tipoTest);
        args.putBoolean("isFromBattery", true);
        ResultEntryFragment fragment = new ResultEntryFragment();
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToResultEntry() {
        Bundle args = new Bundle();
        args.putLong("testId", testId);
        args.putString("tipoTest", tipoTest);
        ResultEntryFragment fragment = new ResultEntryFragment();
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setBadgeColor(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16f);
        drawable.setColor(color);
        badgeStatus.setBackground(drawable);
    }

    private String getTestTypeLabel(String tipoTest) {
        if (tipoTest == null) return "";
        switch (tipoTest) {
            case "ILLINOIS": return getString(R.string.test_type_illinois);
            case "FLEXION_CODOS": return getString(R.string.test_type_flexion_codos);
            case "VELOCIDAD_20M": return getString(R.string.test_type_velocidad_20m);
            case "VELOCIDAD_REACCION": return getString(R.string.test_type_velocidad_reaccion);
            case "SALTO_HORIZONTAL": return getString(R.string.test_type_salto_horizontal);
            case "FLEXION_TRONCO": return getString(R.string.test_type_flexion_tronco);
            case "DINAMOMETRIA": return getString(R.string.test_type_dinamometria);
            case "ANDERSEN": return getString(R.string.test_type_andersen);
            default: return tipoTest;
        }
    }

    /**
     * Return a theme-appropriate color for the given calificación text.
     */
    private int getColorForCalificacion(String calificacion) {
        if (calificacion == null) return ContextCompat.getColor(requireContext(), R.color.hyper_on_primary);
        switch (calificacion) {
            case "EXCELENTE": return ContextCompat.getColor(requireContext(), R.color.hyper_excelente);
            case "BUENO":     return ContextCompat.getColor(requireContext(), R.color.hyper_bueno);
            case "REGULAR":   return ContextCompat.getColor(requireContext(), R.color.hyper_regular);
            case "DEFICIENTE": return ContextCompat.getColor(requireContext(), R.color.hyper_deficiente);
            default:          return ContextCompat.getColor(requireContext(), R.color.hyper_on_primary);
        }
    }

    /**
     * RecyclerView adapter for displaying results with calificacion badges.
     */
    private static class ResultadoAdapter extends RecyclerView.Adapter<ResultadoAdapter.ViewHolder> {

        private final List<ResultadoResponse> resultados;

        ResultadoAdapter(List<ResultadoResponse> resultados) {
            this.resultados = resultados;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ResultadoResponse r = resultados.get(position);
            String line1 = "Intento " + (position + 1) + ": "
                    + r.getValor() + " " + r.getUnidad();
            holder.text1.setText(line1);

            String cal = r.getCalificacion();
            if (cal != null) {
                holder.text2.setText(cal);
                holder.text2.setTextColor(getColorForCalificacion(holder.itemView.getContext(), cal));
            } else {
                holder.text2.setText(holder.itemView.getContext().getString(R.string.test_sin_calificacion));
                holder.text2.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.hyper_on_surface));
            }
        }

        @Override
        public int getItemCount() {
            return resultados != null ? resultados.size() : 0;
        }

        private int getColorForCalificacion(android.content.Context context, String calificacion) {
            if (calificacion == null) return ContextCompat.getColor(context, R.color.hyper_on_surface);
            switch (calificacion) {
                case "EXCELENTE": return ContextCompat.getColor(context, R.color.hyper_excelente);
                case "BUENO":     return ContextCompat.getColor(context, R.color.hyper_bueno);
                case "REGULAR":   return ContextCompat.getColor(context, R.color.hyper_regular);
                case "DEFICIENTE": return ContextCompat.getColor(context, R.color.hyper_deficiente);
                default:          return ContextCompat.getColor(context, R.color.hyper_on_surface);
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView text1;
            final TextView text2;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
                text1.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.hyper_on_primary));
            }
        }
    }
}
