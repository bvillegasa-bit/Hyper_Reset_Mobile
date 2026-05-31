package com.hyperreset.app.ui.tests.entry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.ResultadoResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.data.repository.TestRepository;
import com.hyperreset.app.utils.Resource;

import java.util.HashMap;
import java.util.Map;

public class ResultEntryViewModel extends ViewModel {

    private final TestRepository repository;

    private final MutableLiveData<Resource<ResultadoResponse>> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<TestFisicoResponse>> completeTestResult = new MutableLiveData<>();

    public ResultEntryViewModel() {
        this.repository = new TestRepository();
    }

    public ResultEntryViewModel(TestRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<ResultadoResponse>> getSaveResult() {
        return saveResult;
    }

    public LiveData<Resource<TestFisicoResponse>> getCompleteTestResult() {
        return completeTestResult;
    }

    public void saveResult(long testId, String tipoTestName, double valor,
                           String unidad, String observaciones) {
        saveResult.setValue(Resource.loading());

        Map<String, Object> request = new HashMap<>();
        request.put("testId", testId);
        request.put("tipoTest", tipoTestName);
        request.put("valor", valor);
        request.put("unidad", unidad);
        request.put("observaciones", observaciones != null ? observaciones : "");

        repository.createResultado(request, result -> saveResult.setValue(result));
    }

    public void completeTest(long testId) {
        completeTestResult.setValue(Resource.loading());
        repository.completarTest(testId, result -> completeTestResult.setValue(result));
    }
}
