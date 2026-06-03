package com.hyperreset.app.ui.mensajes.form;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.CoachResponse;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.data.repository.DeportistaRepository;
import com.hyperreset.app.data.repository.MensajeRepository;
import com.hyperreset.app.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MensajeFormViewModel extends ViewModel {

    private final MensajeRepository mensajeRepository;
    private final DeportistaRepository deportistaRepository;

    private final MutableLiveData<Resource<List<DeportistaResponse>>> deportistas = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<CoachResponse>>> coaches = new MutableLiveData<>();
    private final MutableLiveData<Resource<MensajeResponse>> sendResult = new MutableLiveData<>();

    public MensajeFormViewModel() {
        this.mensajeRepository = new MensajeRepository();
        this.deportistaRepository = new DeportistaRepository();
    }

    public MensajeFormViewModel(MensajeRepository mensajeRepository,
                                DeportistaRepository deportistaRepository) {
        this.mensajeRepository = mensajeRepository;
        this.deportistaRepository = deportistaRepository;
    }

    public LiveData<Resource<List<DeportistaResponse>>> getDeportistas() {
        return deportistas;
    }

    public LiveData<Resource<List<CoachResponse>>> getCoaches() {
        return coaches;
    }

    public LiveData<Resource<MensajeResponse>> getSendResult() {
        return sendResult;
    }

    public void loadDeportistas(long coachId) {
        deportistas.setValue(Resource.loading());
        deportistaRepository.getDeportistasByCoach(coachId, result -> deportistas.setValue(result));
    }

    public void loadCoaches() {
        coaches.setValue(Resource.loading());
        deportistaRepository.getCoaches(result -> coaches.setValue(result));
    }

    public void sendMensaje(long destinatarioId, String contenido) {
        sendResult.setValue(Resource.loading());
        mensajeRepository.sendMensaje(destinatarioId, contenido, result -> sendResult.setValue(result));
    }
}
