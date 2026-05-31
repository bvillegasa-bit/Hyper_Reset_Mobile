package com.hyperreset.app.ui.deportistas.form;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.repository.DeportistaRepository;
import com.hyperreset.app.utils.Resource;

import java.util.HashMap;
import java.util.Map;

public class DeportistaFormViewModel extends ViewModel {

    private final DeportistaRepository repository;

    private final MutableLiveData<Resource<DeportistaResponse>> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<DeportistaResponse>> deportistaToEdit = new MutableLiveData<>();

    public DeportistaFormViewModel() {
        this.repository = new DeportistaRepository();
    }

    public DeportistaFormViewModel(DeportistaRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<DeportistaResponse>> getSaveResult() {
        return saveResult;
    }

    public LiveData<Resource<DeportistaResponse>> getDeportistaToEdit() {
        return deportistaToEdit;
    }

    public void loadDeportista(long deportistaId) {
        deportistaToEdit.setValue(Resource.loading());
        repository.getDeportista(deportistaId, result -> deportistaToEdit.setValue(result));
    }

    public void createDeportista(Map<String, Object> request) {
        saveResult.setValue(Resource.loading());
        repository.createDeportista(request, result -> saveResult.setValue(result));
    }

    public void updateDeportista(long deportistaId, Map<String, Object> request) {
        saveResult.setValue(Resource.loading());
        repository.updateDeportista(deportistaId, request, result -> saveResult.setValue(result));
    }
}
