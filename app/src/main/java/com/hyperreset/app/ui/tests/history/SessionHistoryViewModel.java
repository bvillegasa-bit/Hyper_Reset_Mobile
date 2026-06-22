package com.hyperreset.app.ui.tests.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.data.repository.TestRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class SessionHistoryViewModel extends ViewModel {

    private final TestRepository testRepository;
    private final MutableLiveData<Resource<List<TestFisicoResponse>>> sessions = new MutableLiveData<>();

    public SessionHistoryViewModel() {
        this.testRepository = new TestRepository();
    }

    public LiveData<Resource<List<TestFisicoResponse>>> getSessions() {
        return sessions;
    }

    public void loadHistory(long deportistaId, String tipoTest) {
        sessions.setValue(Resource.loading());
        testRepository.getTestFisicosByDeportistaAndTipo(deportistaId, tipoTest, result -> {
            sessions.setValue(result);
        });
    }
}
