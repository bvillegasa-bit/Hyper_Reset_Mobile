package com.hyperreset.app.ui.citas.form;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.data.model.CoachResponse;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.repository.CitaRepository;
import com.hyperreset.app.data.repository.DeportistaRepository;
import com.hyperreset.app.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaFormViewModel extends ViewModel {

    private final CitaRepository repository;
    private final DeportistaRepository deportistaRepository;

    private final MutableLiveData<Resource<List<DeportistaResponse>>> deportistas = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<CoachResponse>>> coaches = new MutableLiveData<>();
    private final MutableLiveData<Resource<CitaResponse>> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<CitaResponse>> citaToEdit = new MutableLiveData<>();

    public CitaFormViewModel() {
        this.repository = new CitaRepository();
        this.deportistaRepository = new DeportistaRepository();
    }

    public CitaFormViewModel(CitaRepository repository) {
        this.repository = repository;
        this.deportistaRepository = new DeportistaRepository();
    }

    public LiveData<Resource<List<DeportistaResponse>>> getDeportistas() {
        return deportistas;
    }

    public LiveData<Resource<List<CoachResponse>>> getCoaches() {
        return coaches;
    }

    public LiveData<Resource<CitaResponse>> getSaveResult() {
        return saveResult;
    }

    public LiveData<Resource<CitaResponse>> getCitaToEdit() {
        return citaToEdit;
    }

    public void loadDeportistas(long coachId) {
        deportistas.setValue(Resource.loading());
        repository.getDeportistasByCoach(coachId, result -> deportistas.setValue(result));
    }

    public void loadCoaches() {
        coaches.setValue(Resource.loading());
        deportistaRepository.getCoaches(result -> coaches.setValue(result));
    }

    public void loadCita(long citaId) {
        citaToEdit.setValue(Resource.loading());
        repository.getCita(citaId, result -> citaToEdit.setValue(result));
    }

    public void createCita(Map<String, Object> request) {
        saveResult.setValue(Resource.loading());
        repository.createCita(request, result -> saveResult.setValue(result));
    }

    public void updateCita(long citaId, Map<String, Object> request) {
        saveResult.setValue(Resource.loading());
        repository.updateCita(citaId, request, result -> saveResult.setValue(result));
    }
}
