package com.hyperreset.app.ui.tests.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.data.model.TipoTestEstadoResponse;
import com.hyperreset.app.data.repository.TestRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class TestListViewModel extends ViewModel {

    private final TestRepository repository;

    // Existing LiveData for COACH (test sessions)
    private final MutableLiveData<Resource<List<TestFisicoResponse>>> tests = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<DeportistaResponse>>> deportistas = new MutableLiveData<>();

    // New LiveData for Phase 3 (8 fixed test types with status)
    private final MutableLiveData<Resource<List<TipoTestEstadoResponse>>> tiposTestConEstado = new MutableLiveData<>();

    // Progress tracking
    private final MutableLiveData<Integer> completadosCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalCount = new MutableLiveData<>(8);

    // Flag to prevent UI updates after ViewModel is cleared
    private volatile boolean cleared = false;

    public TestListViewModel() {
        this.repository = new TestRepository();
    }

    public TestListViewModel(TestRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleared = true;
    }

    // ==================================================================
    // Existing getters (test sessions for COACH)
    // ==================================================================

    public LiveData<Resource<List<TestFisicoResponse>>> getTests() {
        return tests;
    }

    public LiveData<Resource<List<DeportistaResponse>>> getDeportistas() {
        return deportistas;
    }

    // ==================================================================
    // New getters (test tipos with estado for the 8 fixed tests)
    // ==================================================================

    public LiveData<Resource<List<TipoTestEstadoResponse>>> getTiposTestConEstado() {
        return tiposTestConEstado;
    }

    public LiveData<Integer> getCompletadosCount() {
        return completadosCount;
    }

    public LiveData<Integer> getTotalCount() {
        return totalCount;
    }

    // ==================================================================
    // Existing load methods (test sessions for COACH)
    // ==================================================================

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

    // ==================================================================
    // New: load tipos test con estado (for both COACH and DEPORTISTA)
    // ==================================================================

    /**
     * Loads the 8 fixed test types with completion status for a deportista.
     * Updates the tiposTestConEstado LiveData and calculates progress.
     */
    public void loadTiposTestConEstado(long deportistaId) {
        tiposTestConEstado.setValue(Resource.loading());
        repository.getTiposTestConEstado(deportistaId, result -> {
            tiposTestConEstado.setValue(result);
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                int completed = 0;
                for (TipoTestEstadoResponse t : result.data) {
                    if (t.isCompletado()) {
                        completed++;
                    }
                }
                completadosCount.setValue(completed);
                totalCount.setValue(result.data.size());
            }
        });
    }
}
