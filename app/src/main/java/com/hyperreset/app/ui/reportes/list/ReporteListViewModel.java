package com.hyperreset.app.ui.reportes.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.data.repository.ReporteRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class ReporteListViewModel extends ViewModel {

    private final ReporteRepository repository;

    private final MutableLiveData<Resource<List<ReporteResponse>>> reportes = new MutableLiveData<>();

    public ReporteListViewModel() {
        this.repository = new ReporteRepository();
    }

    public ReporteListViewModel(ReporteRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<ReporteResponse>>> getReportes() {
        return reportes;
    }

    public void loadReportes(long deportistaId) {
        reportes.setValue(Resource.loading());
        repository.getReportesByDeportista(deportistaId, result -> reportes.setValue(result));
    }
}
