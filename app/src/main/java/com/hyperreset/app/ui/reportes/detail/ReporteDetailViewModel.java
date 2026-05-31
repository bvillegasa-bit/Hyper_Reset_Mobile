package com.hyperreset.app.ui.reportes.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.data.repository.ReporteRepository;
import com.hyperreset.app.utils.Resource;

public class ReporteDetailViewModel extends ViewModel {

    private final ReporteRepository repository;

    private final MutableLiveData<Resource<ReporteResponse>> reporte = new MutableLiveData<>();

    public ReporteDetailViewModel() {
        this.repository = new ReporteRepository();
    }

    public ReporteDetailViewModel(ReporteRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<ReporteResponse>> getReporte() {
        return reporte;
    }

    public void loadReporte(long reporteId) {
        reporte.setValue(Resource.loading());
        repository.getReporte(reporteId, result -> reporte.setValue(result));
    }
}
