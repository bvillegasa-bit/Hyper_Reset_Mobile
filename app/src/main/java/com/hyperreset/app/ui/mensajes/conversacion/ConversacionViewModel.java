package com.hyperreset.app.ui.mensajes.conversacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.data.repository.MensajeRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

public class ConversacionViewModel extends ViewModel {

    private final MensajeRepository repository;

    private final MutableLiveData<Resource<List<MensajeResponse>>> mensajes = new MutableLiveData<>();
    private final MutableLiveData<Resource<MensajeResponse>> sendResult = new MutableLiveData<>();
    private final MutableLiveData<String> otherUserName = new MutableLiveData<>("");

    private long otherUserId;
    private long currentUserId;

    public ConversacionViewModel() {
        this.repository = new MensajeRepository();
    }

    public ConversacionViewModel(MensajeRepository repository) {
        this.repository = repository;
    }

    public void init(long otherUserId, long currentUserId, String otherUserName) {
        this.otherUserId = otherUserId;
        this.currentUserId = currentUserId;
        this.otherUserName.setValue(otherUserName);
    }

    public LiveData<Resource<List<MensajeResponse>>> getMensajes() {
        return mensajes;
    }

    public LiveData<Resource<MensajeResponse>> getSendResult() {
        return sendResult;
    }

    public LiveData<String> getOtherUserName() {
        return otherUserName;
    }

    public long getCurrentUserId() {
        return currentUserId;
    }

    public void loadConversacion() {
        mensajes.setValue(Resource.loading());
        repository.getConversacion(otherUserId, result -> {
            mensajes.setValue(result);
            // Auto-mark unread received messages as read
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                for (MensajeResponse msg : result.data) {
                    if (!msg.isLeido() && msg.getRemitenteId() != currentUserId) {
                        repository.marcarMensajeLeido(msg.getId(), r -> {});
                    }
                }
            }
        });
    }

    public void sendReply(String contenido) {
        sendResult.setValue(Resource.loading());
        repository.sendMensaje(otherUserId, contenido, result -> {
            sendResult.setValue(result);
            // Reload conversation after sending
            if (result.status == Resource.Status.SUCCESS) {
                loadConversacion();
            }
        });
    }
}
