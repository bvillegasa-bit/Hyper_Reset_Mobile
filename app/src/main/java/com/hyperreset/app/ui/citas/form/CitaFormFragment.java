package com.hyperreset.app.ui.citas.form;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.data.model.CoachResponse;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.ui.citas.detail.CitaDetailFragment;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for creating or editing an appointment (cita).
 * Dual mode: create (POST) when no citaId argument, edit (PUT) when citaId is present.
 */
public class CitaFormFragment extends Fragment {

    private CitaFormViewModel viewModel;
    private SessionManager sessionManager;
    private Spinner spinnerDeportista;
    private TextView tvDeportistaLabel;
    private TextInputEditText etFecha;
    private TextInputEditText etHora;
    private TextInputEditText etMotivo;
    private TextInputEditText etNotas;
    private MaterialButton btnGuardar;

    private List<DeportistaResponse> deportistaList = new ArrayList<>();
    private List<CoachResponse> coachList = new ArrayList<>();
    private int selectedDeportistaPosition = -1;
    private int selectedCoachPosition = -1;
    private boolean isDeportista;

    private Long editCitaId = null;
    private String selectedFecha = "";
    private String selectedHora = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cita_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new CitaFormViewModel();
        sessionManager = new SessionManager(requireContext());
        isDeportista = sessionManager.isDeportista();

        // Check if we're in edit mode
        Bundle args = getArguments();
        if (args != null && args.containsKey("citaId")) {
            editCitaId = args.getLong("citaId");
        }

        initViews(view);
        setupDeportistaSpinner();
        setupFechaPicker();
        setupHoraPicker();
        setupObservers();

        if (editCitaId != null) {
            // Edit mode: load existing data
            viewModel.loadCita(editCitaId);
        }

        if (isDeportista) {
            // DEPORTISTA: load coaches for selection, use own deportistaId when saving
            tvDeportistaLabel.setText(R.string.citas_form_coach);
            viewModel.loadCoaches();
        } else {
            viewModel.loadDeportistas(sessionManager.getUserId());
        }
    }

    private void initViews(View view) {
        spinnerDeportista = view.findViewById(R.id.spinnerDeportista);
        tvDeportistaLabel = view.findViewById(R.id.tvDeportistaLabel);
        etFecha = view.findViewById(R.id.etFecha);
        etHora = view.findViewById(R.id.etHora);
        etMotivo = view.findViewById(R.id.etMotivo);
        etNotas = view.findViewById(R.id.etNotas);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // Update title for edit mode
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        if (editCitaId != null) {
            tvTitle.setText(R.string.citas_form_title_edit);
        }

        btnGuardar.setOnClickListener(v -> onSaveClick());
    }

    private void setupDeportistaSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Seleccionar...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDeportista.setAdapter(adapter);

        // Listener is set by each observer (COACH or DEPORTISTA) with the appropriate variable
    }

    private void setupFechaPicker() {
        etFecha.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectedFecha = sdf.format(selected.getTime());
                        // Also show a user-friendly format
                        SimpleDateFormat displaySdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etFecha.setText(displaySdf.format(selected.getTime()));
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    private void setupHoraPicker() {
        etHora.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute) -> {
                        selectedHora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        etHora.setText(selectedHora);
                    },
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    true);
            dialog.show();
        });
    }

    private void onSaveClick() {
        // Validate fecha and hora
        if (selectedFecha.isEmpty() || selectedHora.isEmpty()) {
            Snackbar.make(requireView(), R.string.citas_validation_fecha_hora,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Validate motivo
        String motivo = etMotivo.getText() != null ? etMotivo.getText().toString().trim() : "";
        if (motivo.isEmpty()) {
            Snackbar.make(requireView(), R.string.citas_validation_motivo,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Build request body
        String notas = etNotas.getText() != null ? etNotas.getText().toString().trim() : "";
        Map<String, Object> request = new HashMap<>();

        if (isDeportista) {
            // DEPORTISTA: selectedCoachPosition is the coach index, deportistaId comes from session
            if (selectedCoachPosition < 0 || selectedCoachPosition >= coachList.size()) {
                Snackbar.make(requireView(), R.string.citas_validation_deportista,
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            request.put("deportistaId", sessionManager.getDeportistaId());
            request.put("coachId", coachList.get(selectedCoachPosition).getIdCoach());
        } else {
            // COACH: selectedDeportistaPosition is the deportista index
            if (selectedDeportistaPosition < 0 || selectedDeportistaPosition >= deportistaList.size()) {
                Snackbar.make(requireView(), R.string.citas_validation_deportista,
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            long deportistaId = deportistaList.get(selectedDeportistaPosition).getId();
            request.put("deportistaId", deportistaId);
            request.put("coachId", sessionManager.getUserId());
        }

        request.put("fechaCita", selectedFecha);
        request.put("horaCita", selectedHora);
        request.put("motivo", motivo);
        request.put("notas", notas);

        if (editCitaId != null) {
            viewModel.updateCita(editCitaId, request);
        } else {
            viewModel.createCita(request);
        }
    }

    private void setupObservers() {
        // Observe deportista list for spinner (COACH path)
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
                // Set deportista-selection listener before setting adapter to avoid stale listener firing
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
                spinnerDeportista.setAdapter(adapter);
            }
        });

        // Observe coach list for spinner (DEPORTISTA path)
        viewModel.getCoaches().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                coachList = resource.data;
                List<String> items = new ArrayList<>();
                items.add("Seleccionar...");
                for (CoachResponse c : resource.data) {
                    items.add(c.getNombreCompleto());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(), R.layout.spinner_item, items);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                // Set coach-selection listener before setting adapter to avoid stale listener firing
                spinnerDeportista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCoachPosition = position - 1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedCoachPosition = -1;
                    }
                });
                spinnerDeportista.setAdapter(adapter);
            }
        });

        // Observe edit mode data (pre-populate)
        viewModel.getCitaToEdit().observe(getViewLifecycleOwner(), resource -> {
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
                btnGuardar.setText(R.string.citas_form_guardando);
            } else {
                btnGuardar.setEnabled(true);
                btnGuardar.setText(R.string.citas_form_guardar);

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    navigateToDetail(resource.data);
                } else if (resource.status == Resource.Status.ERROR) {
                    Snackbar.make(requireView(),
                            resource.message != null ? resource.message : "Error al guardar la cita",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void prePopulateForm(CitaResponse cita) {
        // Find and select the deportista in the spinner
        for (int i = 0; i < deportistaList.size(); i++) {
            if (deportistaList.get(i).getNombreCompleto().equals(cita.getDeportistaNombre())) {
                spinnerDeportista.setSelection(i + 1); // +1 for placeholder
                selectedDeportistaPosition = i;
                break;
            }
        }

        // Set fecha (stored as yyyy-MM-dd in the API)
        selectedFecha = cita.getFechaCita() != null ? cita.getFechaCita() : "";
        if (!selectedFecha.isEmpty()) {
            try {
                SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat displaySdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etFecha.setText(displaySdf.format(apiSdf.parse(selectedFecha)));
            } catch (Exception e) {
                etFecha.setText(selectedFecha);
            }
        }

        // Set hora
        selectedHora = cita.getHoraCita() != null ? cita.getHoraCita() : "";
        etHora.setText(selectedHora);

        // Set motivo
        etMotivo.setText(cita.getMotivo());

        // Set notas
        etNotas.setText(cita.getNotas());
    }

    private void navigateToDetail(CitaResponse cita) {
        Bundle args = new Bundle();
        args.putLong("citaId", cita.getId());
        CitaDetailFragment detailFragment = new CitaDetailFragment();
        detailFragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
