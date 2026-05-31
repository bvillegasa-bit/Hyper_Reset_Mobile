package com.hyperreset.app.ui.materiales.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.data.repository.MaterialRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class MaterialListViewModel extends ViewModel {

    private final MaterialRepository repository;

    private final MutableLiveData<Resource<List<MaterialResponse>>> materiales = new MutableLiveData<>();

    public MaterialListViewModel() {
        this.repository = new MaterialRepository();
    }

    public MaterialListViewModel(MaterialRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<MaterialResponse>>> getMateriales() {
        return materiales;
    }

    public void loadMateriales() {
        materiales.setValue(Resource.loading());
        repository.getMateriales(result -> materiales.setValue(result));
    }
}
