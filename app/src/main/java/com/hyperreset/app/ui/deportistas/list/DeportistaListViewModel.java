package com.hyperreset.app.ui.deportistas.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.repository.DeportistaRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class DeportistaListViewModel extends ViewModel {

    private final DeportistaRepository repository;

    private final MutableLiveData<Resource<List<DeportistaResponse>>> deportistas = new MutableLiveData<>();

    public DeportistaListViewModel() {
        this.repository = new DeportistaRepository();
    }

    public DeportistaListViewModel(DeportistaRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<DeportistaResponse>>> getDeportistas() {
        return deportistas;
    }

    public void loadDeportistas(long coachId) {
        deportistas.setValue(Resource.loading());
        repository.getDeportistasByCoach(coachId, result -> deportistas.setValue(result));
    }
}
