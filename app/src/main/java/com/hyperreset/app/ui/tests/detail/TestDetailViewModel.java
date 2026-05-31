package com.hyperreset.app.ui.tests.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.data.model.ResultadoResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.data.repository.ReporteRepository;
import com.hyperreset.app.data.repository.TestRepository;
import com.hyperreset.app.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDetailViewModel extends ViewModel {

    private final TestRepository repository;
    private final ReporteRepository reporteRepository;

    private long deportistaId;

    private final MutableLiveData<Resource<TestFisicoResponse>> test = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<ResultadoResponse>>> resultados = new MutableLiveData<>();
    private final MutableLiveData<Resource<ReporteResponse>> generateResult = new MutableLiveData<>();

    public TestDetailViewModel() {
        this.repository = new TestRepository();
        this.reporteRepository = new ReporteRepository();
    }

    public TestDetailViewModel(TestRepository repository, ReporteRepository reporteRepository) {
        this.repository = repository;
        this.reporteRepository = reporteRepository;
    }

    public LiveData<Resource<TestFisicoResponse>> getTest() {
        return test;
    }

    public LiveData<Resource<List<ResultadoResponse>>> getResultados() {
        return resultados;
    }

    public LiveData<Resource<ReporteResponse>> getGenerateResult() {
        return generateResult;
    }

    public long getDeportistaId() {
        return deportistaId;
    }

    public void loadTest(long testId) {
        test.setValue(Resource.loading());
        repository.getTestFisico(testId, result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                deportistaId = result.data.getDeportistaId();
            }
            test.setValue(result);
        });
    }

    public void loadResultados(long testId) {
        resultados.setValue(Resource.loading());
        repository.getResultadosByTest(testId, result -> resultados.setValue(result));
    }

    public void completarTest(long testId) {
        repository.completarTest(testId, result -> {
            if (result.status == Resource.Status.SUCCESS) {
                loadTest(testId);
            }
        });
    }

    public void generarReporte(long testFisicoId, long deportistaId) {
        generateResult.setValue(Resource.loading());
        Map<String, Object> request = new HashMap<>();
        request.put("testFisicoId", testFisicoId);
        request.put("deportistaId", deportistaId);
        request.put("tipoReporte", "COMPLETO");
        reporteRepository.generarReporte(request, result -> generateResult.setValue(result));
    }
}
