package com.hyperreset.app.ui.tests.create;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.data.repository.TestRepository;
import com.hyperreset.app.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCreateViewModel extends ViewModel {

    private final TestRepository repository;

    private final MutableLiveData<Resource<List<DeportistaResponse>>> deportistas = new MutableLiveData<>();
    private final MutableLiveData<Resource<TestFisicoResponse>> createTestResult = new MutableLiveData<>();

    public TestCreateViewModel() {
        this.repository = new TestRepository();
    }

    public TestCreateViewModel(TestRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<DeportistaResponse>>> getDeportistas() {
        return deportistas;
    }

    public LiveData<Resource<TestFisicoResponse>> getCreateTestResult() {
        return createTestResult;
    }

    public void loadDeportistas(long coachId) {
        deportistas.setValue(Resource.loading());
        repository.getDeportistasByCoach(coachId, result -> deportistas.setValue(result));
    }

    public void createTest(Long deportistaId, String tipoTestName, String notas) {
        createTestResult.setValue(Resource.loading());

        Map<String, Object> request = new HashMap<>();
        request.put("deportistaId", deportistaId);
        request.put("tipoTest", tipoTestName);
        request.put("notas", notas != null ? notas : "");

        repository.createTestFisico(request, result -> createTestResult.setValue(result));
    }
}
