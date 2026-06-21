package com.hyperreset.app.ui.materiales.form;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hyperreset.app.R;
import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.utils.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment for creating or editing an educational material.
 * Dual mode: create (POST) when no materialId argument, edit (PUT) when materialId is present.
 */
public class MaterialFormFragment extends Fragment {

    private MaterialFormViewModel viewModel;
    private TextInputEditText etTitulo;
    private TextInputEditText etDescripcion;
    private Spinner spinnerTipo;
    private TextInputEditText etUrl;
    private MaterialButton btnGuardar;

    private Long editMaterialId = null;
    private String selectedTipo = "VIDEO"; // Default type

    // Tipo options
    private static final String[] TIPO_OPTIONS = {"VIDEO", "PDF", "ENLACE"};
    private static final String[] TIPO_LABELS = {"Video", "PDF", "Enlace"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new MaterialFormViewModel();

        // Check if we're in edit mode
        Bundle args = getArguments();
        if (args != null && args.containsKey("materialId")) {
            editMaterialId = args.getLong("materialId");
        }

        initViews(view);
        setupTipoSpinner();
        setupObservers();

        if (editMaterialId != null) {
            // Edit mode: load existing data
            viewModel.loadMaterial(editMaterialId);
        }
    }

    private void initViews(View view) {
        etTitulo = view.findViewById(R.id.etTitulo);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        spinnerTipo = view.findViewById(R.id.spinnerTipo);
        etUrl = view.findViewById(R.id.etUrl);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // Update title for edit mode
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        if (editMaterialId != null) {
            tvTitle.setText(R.string.materiales_form_title_edit);
        }

        btnGuardar.setOnClickListener(v -> onSaveClick());
    }

    private void setupTipoSpinner() {
        List<String> items = new ArrayList<>();
        for (String label : TIPO_LABELS) {
            items.add(label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTipo = TIPO_OPTIONS[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTipo = "VIDEO";
            }
        });
    }

    private void onSaveClick() {
        // Validate titulo
        String titulo = etTitulo.getText() != null ? etTitulo.getText().toString().trim() : "";
        if (titulo.isEmpty()) {
            Snackbar.make(requireView(), R.string.materiales_validation_titulo,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Validate url
        String url = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
        if (url.isEmpty()) {
            Snackbar.make(requireView(), R.string.materiales_validation_url,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Build request body
        String descripcion = etDescripcion.getText() != null ? etDescripcion.getText().toString().trim() : "";

        Map<String, Object> request = new HashMap<>();
        request.put("titulo", titulo);
        request.put("descripcion", descripcion);
        request.put("tipoMaterial", selectedTipo);
        request.put("urlRecurso", url);

        if (editMaterialId != null) {
            viewModel.updateMaterial(editMaterialId, request);
        } else {
            viewModel.createMaterial(request);
        }
    }

    private void setupObservers() {
        // Observe edit mode data (pre-populate)
        viewModel.getMaterialToEdit().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                prePopulateForm(resource.data);
            }
        });

        // Observe save result
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.LOADING) {
                btnGuardar.setEnabled(false);
                btnGuardar.setText(R.string.materiales_form_guardando);
            } else {
                btnGuardar.setEnabled(true);
                btnGuardar.setText(R.string.materiales_form_guardar);

                if (resource.status == Resource.Status.SUCCESS) {
                    int messageRes = editMaterialId != null
                            ? R.string.materiales_success_updated
                            : R.string.materiales_success_created;
                    Snackbar.make(requireView(), messageRes, Snackbar.LENGTH_SHORT).show();
                    // Navigate back to list
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : getString(R.string.material_error_save),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void prePopulateForm(MaterialResponse material) {
        etTitulo.setText(material.getTitulo());
        etDescripcion.setText(material.getDescripcion());
        etUrl.setText(material.getUrlRecurso());

        // Set tipo spinner
        if (material.getTipoMaterial() != null) {
            for (int i = 0; i < TIPO_OPTIONS.length; i++) {
                if (TIPO_OPTIONS[i].equals(material.getTipoMaterial())) {
                    spinnerTipo.setSelection(i);
                    break;
                }
            }
        }
    }
}
