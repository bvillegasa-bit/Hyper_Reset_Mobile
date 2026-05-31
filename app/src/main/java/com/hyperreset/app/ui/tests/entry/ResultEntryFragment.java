package com.hyperreset.app.ui.tests.entry;

import android.os.Bundle;
import android.text.InputType;
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
import com.google.android.material.textfield.TextInputEditText;
import com.hyperreset.app.R;
import com.hyperreset.app.ui.tests.detail.TestDetailFragment;
import com.hyperreset.app.utils.Resource;

/**
 * Fragment for entering a single test result.
 * Configures the input field based on test type (time, reps, distance, etc.)
 * and shows the calculated calificacion after saving.
 */
public class ResultEntryFragment extends Fragment {

    private ResultEntryViewModel viewModel;
    private long testId;
    private String tipoTest;

    private TextView tvTestType;
    private TextInputEditText etValor;
    private TextView tvUnit;
    private TextInputEditText etObservaciones;
    private MaterialButton btnSaveResult;
    private MaterialButton btnCompleteTest;
    private View cardCalificacion;
    private TextView tvCalificacion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            testId = args.getLong("testId", -1);
            tipoTest = args.getString("tipoTest", "");
        }

        viewModel = new ResultEntryViewModel();

        initViews(view);
        configureInputForTipoTest(tipoTest);
        setupObservers();
    }

    private void initViews(View view) {
        tvTestType = view.findViewById(R.id.tvTestType);
        etValor = view.findViewById(R.id.etValor);
        tvUnit = view.findViewById(R.id.tvUnit);
        etObservaciones = view.findViewById(R.id.etObservaciones);
        btnSaveResult = view.findViewById(R.id.btnSaveResult);
        btnCompleteTest = view.findViewById(R.id.btnCompleteTest);
        cardCalificacion = view.findViewById(R.id.cardCalificacion);
        tvCalificacion = view.findViewById(R.id.tvCalificacion);

        btnSaveResult.setOnClickListener(v -> onSaveClick());
        btnCompleteTest.setOnClickListener(v -> onCompleteClick());
    }

    private void configureInputForTipoTest(String tipoTest) {
        if (tipoTest == null) return;

        switch (tipoTest) {
            case "ILLINOIS":
            case "VELOCIDAD_20M":
            case "VELOCIDAD_REACCION":
                tvTestType.setText(getTestTypeLabel(tipoTest));
                etValor.setHint("Tiempo (" + getUnit(tipoTest) + ")");
                etValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                tvUnit.setText(getUnit(tipoTest));
                break;
            case "FLEXION_CODOS":
                tvTestType.setText(getTestTypeLabel(tipoTest));
                etValor.setHint("Repeticiones");
                etValor.setInputType(InputType.TYPE_CLASS_NUMBER);
                tvUnit.setText(R.string.result_entry_unit_repeticiones);
                break;
            case "SALTO_HORIZONTAL":
            case "FLEXION_TRONCO":
                tvTestType.setText(getTestTypeLabel(tipoTest));
                etValor.setHint("Distancia (" + getUnit(tipoTest) + ")");
                etValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                tvUnit.setText(getUnit(tipoTest));
                break;
            case "ANDERSEN":
                tvTestType.setText(getTestTypeLabel(tipoTest));
                etValor.setHint("Distancia (metros)");
                etValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                tvUnit.setText(R.string.result_entry_unit_metros);
                break;
            case "DINAMOMETRIA":
                tvTestType.setText(getTestTypeLabel(tipoTest));
                etValor.setHint("Fuerza (kg)");
                etValor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                tvUnit.setText(R.string.result_entry_unit_kg);
                break;
        }
    }

    private void onSaveClick() {
        String valorStr = etValor.getText() != null ? etValor.getText().toString().trim() : "";
        if (valorStr.isEmpty()) {
            Snackbar.make(requireView(), "Ingresa un valor", Snackbar.LENGTH_LONG).show();
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            Snackbar.make(requireView(), R.string.result_entry_validation_positive, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (valor <= 0) {
            Snackbar.make(requireView(), R.string.result_entry_validation_positive, Snackbar.LENGTH_LONG).show();
            return;
        }

        String observaciones = etObservaciones.getText() != null
                ? etObservaciones.getText().toString().trim() : "";
        String unidad = getUnit(tipoTest);

        viewModel.saveResult(testId, tipoTest, valor, unidad, observaciones);
    }

    private void onCompleteClick() {
        viewModel.completeTest(testId);
    }

    private void setupObservers() {
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                btnSaveResult.setEnabled(false);
                btnSaveResult.setText("Guardando...");
            } else {
                btnSaveResult.setEnabled(true);
                btnSaveResult.setText(R.string.result_entry_save);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    // Show calificacion card
                    String calificacion = resource.data.getCalificacion();
                    if (calificacion != null) {
                        showCalificacion(calificacion, resource.data.getValor(), resource.data.getUnidad());
                    }
                    btnCompleteTest.setVisibility(View.VISIBLE);
                    etValor.setText("");
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al guardar",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getCompleteTestResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                btnCompleteTest.setEnabled(false);
                btnCompleteTest.setText("Finalizando...");
            } else {
                btnCompleteTest.setEnabled(true);
                btnCompleteTest.setText(R.string.result_entry_complete);

                if (resource.status == Resource.Status.SUCCESS) {
                    navigateToDetail();
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al finalizar test",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showCalificacion(String calificacion, double valor, String unidad) {
        cardCalificacion.setVisibility(View.VISIBLE);
        tvCalificacion.setText(getCalificacionLabel(calificacion));

        int color = colorForCalificacion(calificacion);
        tvCalificacion.setTextColor(color);
    }

    private void navigateToDetail() {
        Bundle args = new Bundle();
        args.putLong("testId", testId);
        args.putString("tipoTest", tipoTest);
        TestDetailFragment fragment = new TestDetailFragment();
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private String getUnit(String tipoTest) {
        if (tipoTest == null) return "";
        switch (tipoTest) {
            case "ILLINOIS":
            case "VELOCIDAD_20M":
            case "VELOCIDAD_REACCION":
                return "segundos";
            case "SALTO_HORIZONTAL":
            case "FLEXION_TRONCO":
                return "cm";
            case "ANDERSEN":
                return "metros";
            case "DINAMOMETRIA":
                return "kg";
            case "FLEXION_CODOS":
                return "repeticiones";
            default:
                return "";
        }
    }

    private int colorForCalificacion(String calificacion) {
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
}
