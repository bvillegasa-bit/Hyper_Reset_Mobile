package com.hyperreset.app.ui.reportes.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.data.repository.DeportistaRepository;
import com.hyperreset.app.data.repository.ReporteRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class ReporteListViewModel extends ViewModel {

    private final ReporteRepository reporteRepository;
    private final DeportistaRepository deportistaRepository;

    private final MutableLiveData<Resource<List<ReporteResponse>>> reportes = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<DeportistaResponse>>> deportistas = new MutableLiveData<>();

    public ReporteListViewModel() {
        this.reporteRepository = new ReporteRepository();
        this.deportistaRepository = new DeportistaRepository();
    }

    public ReporteListViewModel(ReporteRepository reporteRepository, DeportistaRepository deportistaRepository) {
        this.reporteRepository = reporteRepository;
        this.deportistaRepository = deportistaRepository;
    }

    public LiveData<Resource<List<ReporteResponse>>> getReportes() {
        return reportes;
    }

    public LiveData<Resource<List<DeportistaResponse>>> getDeportistas() {
        return deportistas;
    }

    public void loadReportes(long deportistaId) {
        reportes.setValue(Resource.loading());
        reporteRepository.getReportesByDeportista(deportistaId, result -> reportes.setValue(result));
    }

    public void loadDeportistasByCoach(long coachId) {
        deportistas.setValue(Resource.loading());
        deportistaRepository.getDeportistasByCoach(coachId, result -> deportistas.setValue(result));
    }
}
