package com.hyperreset.app.ui.citas.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.data.repository.CitaRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class CitaListViewModel extends ViewModel {

    private final CitaRepository repository;

    private final MutableLiveData<Resource<List<CitaResponse>>> citas = new MutableLiveData<>();

    public CitaListViewModel() {
        this.repository = new CitaRepository();
    }

    public CitaListViewModel(CitaRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<CitaResponse>>> getCitas() {
        return citas;
    }

    public void loadCitas() {
        citas.setValue(Resource.loading());
        repository.getCitas(result -> citas.setValue(result));
    }

    public void loadCitasByDateRange(String start, String end) {
        citas.setValue(Resource.loading());
        repository.getCitasByDateRange(start, end, result -> citas.setValue(result));
    }
}
