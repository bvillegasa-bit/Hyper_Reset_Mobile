package com.hyperreset.app.ui.materiales.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.data.repository.MaterialRepository;
import com.hyperreset.app.utils.Resource;

public class MaterialDetailViewModel extends ViewModel {

    private final MaterialRepository repository;

    private final MutableLiveData<Resource<MaterialResponse>> material = new MutableLiveData<>();

    public MaterialDetailViewModel() {
        this.repository = new MaterialRepository();
    }

    public MaterialDetailViewModel(MaterialRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<MaterialResponse>> getMaterial() {
        return material;
    }

    public void loadMaterial(long id) {
        material.setValue(Resource.loading());
        repository.getMaterial(id, result -> material.setValue(result));
    }

    public void deleteMaterial(long id, MaterialRepository.ResourceCallback<Void> callback) {
        repository.deleteMaterial(id, callback);
    }
}
