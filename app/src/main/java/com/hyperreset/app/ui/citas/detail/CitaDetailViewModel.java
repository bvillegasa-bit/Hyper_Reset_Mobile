package com.hyperreset.app.ui.citas.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.data.repository.CitaRepository;
import com.hyperreset.app.utils.Resource;

public class CitaDetailViewModel extends ViewModel {

    private final CitaRepository repository;

    private final MutableLiveData<Resource<CitaResponse>> cita = new MutableLiveData<>();

    public CitaDetailViewModel() {
        this.repository = new CitaRepository();
    }

    public CitaDetailViewModel(CitaRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<CitaResponse>> getCita() {
        return cita;
    }

    public void loadCita(long id) {
        cita.setValue(Resource.loading());
        repository.getCita(id, result -> cita.setValue(result));
    }

    public void changeEstado(long id, String nuevoEstado) {
        cita.setValue(Resource.loading());
        repository.updateCitaEstado(id, nuevoEstado, result -> {
            if (result.status == Resource.Status.SUCCESS) {
                // Reload the full detail to get updated data
                loadCita(id);
            } else {
                cita.setValue(result);
            }
        });
    }

    public void deleteCita(long id, CitaRepository.ResourceCallback<Void> callback) {
        repository.deleteCita(id, callback);
    }
}
