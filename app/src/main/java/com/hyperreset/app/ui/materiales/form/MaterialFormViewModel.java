package com.hyperreset.app.ui.materiales.form;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.data.repository.MaterialRepository;
import com.hyperreset.app.utils.Resource;

public class MaterialFormViewModel extends ViewModel {

    private final MaterialRepository repository;

    private final MutableLiveData<Resource<MaterialResponse>> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<MaterialResponse>> materialToEdit = new MutableLiveData<>();

    public MaterialFormViewModel() {
        this.repository = new MaterialRepository();
    }

    public MaterialFormViewModel(MaterialRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<MaterialResponse>> getSaveResult() {
        return saveResult;
    }

    public LiveData<Resource<MaterialResponse>> getMaterialToEdit() {
        return materialToEdit;
    }

    public void loadMaterial(long materialId) {
        materialToEdit.setValue(Resource.loading());
        repository.getMaterial(materialId, result -> materialToEdit.setValue(result));
    }

    public void createMaterial(Object request) {
        saveResult.setValue(Resource.loading());
        repository.createMaterial(request, result -> saveResult.setValue(result));
    }

    public void updateMaterial(long materialId, Object request) {
        saveResult.setValue(Resource.loading());
        repository.updateMaterial(materialId, request, result -> saveResult.setValue(result));
    }
}
