package com.hyperreset.app.ui.mensajes.form;

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
import com.hyperreset.app.data.model.CoachResponse;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for composing a new message.
 * Includes a dropdown to select the recipient (deportista) and a text area for the message content.
 */
public class MensajeFormFragment extends Fragment {

    private MensajeFormViewModel viewModel;
    private Spinner spinnerDestinatario;
    private TextInputEditText etContenido;
    private MaterialButton btnSend;
    private View progressLoading;
    private SessionManager sessionManager;

    private List<DeportistaResponse> deportistaList = new ArrayList<>();
    private List<CoachResponse> coachList = new ArrayList<>();
    private long preSelectedDeportistaId = -1;
    private boolean isDeportista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mensaje_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new MensajeFormViewModel();
        sessionManager = new SessionManager(requireContext());
        isDeportista = sessionManager.isDeportista();

        Bundle args = getArguments();
        if (args != null) {
            preSelectedDeportistaId = args.getLong("deportistaId", -1);
        }

        initViews(view);
        setupSpinner();
        setupObservers();

        if (isDeportista) {
            // DEPORTISTA: load coaches for the dropdown
            viewModel.loadCoaches();
        } else {
            // COACH: load deportistas for the dropdown
            long coachId = sessionManager.getUserId();
            viewModel.loadDeportistas(coachId);
        }
    }

    private void initViews(View view) {
        spinnerDestinatario = view.findViewById(R.id.spinnerDestinatario);
        etContenido = view.findViewById(R.id.etContenido);
        btnSend = view.findViewById(R.id.btnSend);
        progressLoading = view.findViewById(R.id.progressLoading);

        btnSend.setOnClickListener(v -> sendMensaje());
    }

    private void setupSpinner() {
        List<String> placeholder = new ArrayList<>();
        placeholder.add(getString(R.string.mensajes_form_loading));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, placeholder);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDestinatario.setAdapter(adapter);
        spinnerDestinatario.setEnabled(false);
    }

    private void populateSpinner(List<DeportistaResponse> deportistas) {
        List<String> names = new ArrayList<>();
        List<Long> ids = new ArrayList<>(); // These will be usuarioIds for message routing

        for (DeportistaResponse d : deportistas) {
            names.add(d.getNombreCompleto() != null ? d.getNombreCompleto() : "ID: " + d.getId());
            ids.add(d.getUsuarioId()); // Use usuarioId (the user account ID) for messaging
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDestinatario.setAdapter(adapter);
        spinnerDestinatario.setEnabled(true);

        // Tag the IDs for later retrieval (these are usuarioIds)
        spinnerDestinatario.setTag(ids);

        // Auto-select if pre-selected (match by deportista ID in the list)
        if (preSelectedDeportistaId > 0) {
            for (int i = 0; i < deportistas.size(); i++) {
                if (deportistas.get(i).getId() == preSelectedDeportistaId) {
                    spinnerDestinatario.setSelection(i);
                    break;
                }
            }
        }
    }

    private void populateCoachSpinner(List<CoachResponse> coaches) {
        List<String> names = new ArrayList<>();
        List<Long> ids = new ArrayList<>(); // These will be usuarioIds for message routing

        for (CoachResponse c : coaches) {
            names.add(c.getNombreCompleto() != null ? c.getNombreCompleto() : "Coach");
            ids.add(c.getUsuarioId()); // Use usuarioId for messaging
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_item, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDestinatario.setAdapter(adapter);
        spinnerDestinatario.setEnabled(true);

        // Tag the IDs for later retrieval (these are usuarioIds)
        spinnerDestinatario.setTag(ids);
    }

    private long getSelectedDestinatarioId() {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) spinnerDestinatario.getTag();
        if (ids != null && spinnerDestinatario.getSelectedItemPosition() >= 0
                && spinnerDestinatario.getSelectedItemPosition() < ids.size()) {
            return ids.get(spinnerDestinatario.getSelectedItemPosition());
        }
        return -1;
    }

    private void sendMensaje() {
        long destinatarioId = getSelectedDestinatarioId();
        String contenido = etContenido.getText() != null
                ? etContenido.getText().toString().trim() : "";

        if (destinatarioId <= 0) {
            Snackbar.make(requireView(),
                    getString(R.string.mensajes_validation_destinatario),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (contenido.isEmpty()) {
            Snackbar.make(requireView(),
                    getString(R.string.mensajes_validation_contenido),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        btnSend.setEnabled(false);
        progressLoading.setVisibility(View.VISIBLE);
        viewModel.sendMensaje(destinatarioId, contenido);
    }

    private void setupObservers() {
        // Observe deportista list for spinner (COACH path)
        viewModel.getDeportistas().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                deportistaList = resource.data;
                populateSpinner(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                Snackbar.make(requireView(),
                        resource.message != null ? resource.message
                                : getString(R.string.mensajes_list_error),
                        Snackbar.LENGTH_LONG).show();
            }
        });

        // Observe coach list for spinner (DEPORTISTA path)
        viewModel.getCoaches().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                coachList = resource.data;
                populateCoachSpinner(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                Snackbar.make(requireView(),
                        resource.message != null ? resource.message
                                : getString(R.string.mensajes_list_error),
                        Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getSendResult().observe(getViewLifecycleOwner(), resource -> {
            btnSend.setEnabled(true);
            progressLoading.setVisibility(View.GONE);

            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                Snackbar.make(requireView(),
                        getString(R.string.mensajes_send_success),
                        Snackbar.LENGTH_SHORT).show();
                // Go back to the message list
                requireActivity().getSupportFragmentManager().popBackStack();
            } else if (resource.status == Resource.Status.ERROR) {
                Snackbar.make(requireView(),
                        resource.message != null ? resource.message
                                : getString(R.string.mensajes_send_error),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
