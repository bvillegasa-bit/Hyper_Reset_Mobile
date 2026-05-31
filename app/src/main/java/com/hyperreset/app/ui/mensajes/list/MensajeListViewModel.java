package com.hyperreset.app.ui.mensajes.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.data.repository.MensajeRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class MensajeListViewModel extends ViewModel {

    private final MensajeRepository repository;

    private final MutableLiveData<Resource<List<MensajeResponse>>> mensajes = new MutableLiveData<>();
    private final MutableLiveData<Integer> noLeidos = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> tabActual = new MutableLiveData<>(true); // true = Recibidos, false = Enviados

    public MensajeListViewModel() {
        this.repository = new MensajeRepository();
    }

    public MensajeListViewModel(MensajeRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<MensajeResponse>>> getMensajes() {
        return mensajes;
    }

    public LiveData<Integer> getNoLeidos() {
        return noLeidos;
    }

    public LiveData<Boolean> getTabActual() {
        return tabActual;
    }

    public void setTabActual(boolean isRecibidos) {
        tabActual.setValue(isRecibidos);
        if (isRecibidos) {
            loadRecibidos();
        } else {
            loadEnviados();
        }
    }

    public void loadRecibidos() {
        mensajes.setValue(Resource.loading());
        repository.getMensajesRecibidos(result -> mensajes.setValue(result));
    }

    public void loadEnviados() {
        mensajes.setValue(Resource.loading());
        repository.getMensajesEnviados(result -> mensajes.setValue(result));
    }

    public void loadNoLeidos() {
        repository.getNoLeidos(result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                noLeidos.setValue(result.data);
            }
        });
    }
}
