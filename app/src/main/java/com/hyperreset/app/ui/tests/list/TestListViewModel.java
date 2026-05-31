package com.hyperreset.app.ui.tests.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.data.repository.TestRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class TestListViewModel extends ViewModel {

    private final TestRepository repository;

    private final MutableLiveData<Resource<List<TestFisicoResponse>>> tests = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<DeportistaResponse>>> deportistas = new MutableLiveData<>();

    public TestListViewModel() {
        this.repository = new TestRepository();
    }

    public TestListViewModel(TestRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<TestFisicoResponse>>> getTests() {
        return tests;
    }

    public LiveData<Resource<List<DeportistaResponse>>> getDeportistas() {
        return deportistas;
    }

    public void loadTests() {
        tests.setValue(Resource.loading());
        repository.getTestFisicos(result -> tests.setValue(result));
    }

    public void loadTestsByDeportista(long deportistaId) {
        tests.setValue(Resource.loading());
        repository.getTestFisicosByDeportista(deportistaId, result -> tests.setValue(result));
    }

    public void loadDeportistas(long coachId) {
        deportistas.setValue(Resource.loading());
        repository.getDeportistasByCoach(coachId, result -> deportistas.setValue(result));
    }
}
