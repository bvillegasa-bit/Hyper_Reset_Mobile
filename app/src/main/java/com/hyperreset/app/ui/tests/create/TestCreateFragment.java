package com.hyperreset.app.ui.tests.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.ui.tests.entry.ResultEntryFragment;
import com.hyperreset.app.utils.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for creating a new physical test session.
 * Shows deportista selection, test type selection, notes, and optional biometrics.
 */
public class TestCreateFragment extends Fragment {

    private TestCreateViewModel viewModel;
    private Spinner spinnerDeportista;
    private Spinner spinnerTipoTest;
    private TextInputEditText etNotas;
    private MaterialButton btnCreateTest;
    private List<DeportistaResponse> deportistaList = new ArrayList<>();
    private int selectedDeportistaPosition = -1;
    private int selectedTipoTestPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new TestCreateViewModel();

        initViews(view);
        setupDeportistaSpinner();
        setupTipoTestSpinner();
        setupObservers();

        viewModel.loadDeportistas(0);
    }

    private void initViews(View view) {
        spinnerDeportista = view.findViewById(R.id.spinnerDeportista);
        spinnerTipoTest = view.findViewById(R.id.spinnerTipoTest);
        etNotas = view.findViewById(R.id.etNotas);
        btnCreateTest = view.findViewById(R.id.btnCreateTest);

        btnCreateTest.setOnClickListener(v -> onCreateTestClick());
    }

    private void setupDeportistaSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Seleccionar...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDeportista.setAdapter(adapter);

        spinnerDeportista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeportistaPosition = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDeportistaPosition = -1;
            }
        });
    }

    private void setupTipoTestSpinner() {
        String[] testTypes = {
                getString(R.string.test_type_illinois),
                getString(R.string.test_type_flexion_codos),
                getString(R.string.test_type_velocidad_20m),
                getString(R.string.test_type_velocidad_reaccion),
                getString(R.string.test_type_salto_horizontal),
                getString(R.string.test_type_flexion_tronco),
                getString(R.string.test_type_dinamometria),
                getString(R.string.test_type_andersen)
        };

        List<String> items = new ArrayList<>();
        items.add("Seleccionar...");
        for (String type : testTypes) {
            items.add(type);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTipoTest.setAdapter(adapter);

        spinnerTipoTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTipoTestPosition = position - 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTipoTestPosition = -1;
            }
        });
    }

    private void onCreateTestClick() {
        // Validate deportista
        if (selectedDeportistaPosition < 0 || selectedDeportistaPosition >= deportistaList.size()) {
            Snackbar.make(requireView(), R.string.test_create_validation_deportista,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Validate test type
        if (selectedTipoTestPosition < 0) {
            Snackbar.make(requireView(), R.string.test_create_validation_tipo,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String tipoTestName = getTipoTestName(selectedTipoTestPosition);
        String notas = etNotas.getText() != null ? etNotas.getText().toString().trim() : "";
        long deportistaId = deportistaList.get(selectedDeportistaPosition).getId();

        viewModel.createTest(deportistaId, tipoTestName, notas);
    }

    private void setupObservers() {
        viewModel.getDeportistas().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                deportistaList = resource.data;
                List<String> items = new ArrayList<>();
                items.add("Seleccionar...");
                for (DeportistaResponse d : resource.data) {
                    items.add(d.getNombreCompleto());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(), R.layout.spinner_item, items);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerDeportista.setAdapter(adapter);
            }
        });

        viewModel.getCreateTestResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                btnCreateTest.setEnabled(false);
                btnCreateTest.setText("Creando...");
            } else {
                btnCreateTest.setEnabled(true);
                btnCreateTest.setText(R.string.test_create_button);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    navigateToResultEntry(resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al crear test",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void navigateToResultEntry(TestFisicoResponse test) {
        Bundle args = new Bundle();
        args.putLong("testId", test.getId());
        args.putString("tipoTest", test.getTipoTest());
        ResultEntryFragment fragment = new ResultEntryFragment();
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Maps spinner position to TipoTest enum name.
     */
    private String getTipoTestName(int position) {
        String[] names = {
                "ILLINOIS", "FLEXION_CODOS", "VELOCIDAD_20M",
                "VELOCIDAD_REACCION", "SALTO_HORIZONTAL",
                "FLEXION_TRONCO", "DINAMOMETRIA", "ANDERSEN"
        };
        return position >= 0 && position < names.length ? names[position] : null;
    }
}
