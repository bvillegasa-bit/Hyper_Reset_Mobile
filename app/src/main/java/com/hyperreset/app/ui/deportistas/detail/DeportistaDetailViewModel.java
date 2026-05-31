package com.hyperreset.app.ui.deportistas.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.repository.DeportistaRepository;
import com.hyperreset.app.utils.Resource;

import java.util.HashMap;
import java.util.Map;

public class DeportistaDetailViewModel extends ViewModel {

    private final DeportistaRepository repository;

    private final MutableLiveData<Resource<DeportistaResponse>> deportista = new MutableLiveData<>();

    public DeportistaDetailViewModel() {
        this.repository = new DeportistaRepository();
    }

    public DeportistaDetailViewModel(DeportistaRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<DeportistaResponse>> getDeportista() {
        return deportista;
    }

    public void loadDeportista(long id) {
        deportista.setValue(Resource.loading());
        repository.getDeportista(id, result -> deportista.setValue(result));
    }
}
