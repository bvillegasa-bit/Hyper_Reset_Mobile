package com.hyperreset.app.ui.mensajes.conversacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.data.repository.MensajeRepository;
import com.hyperreset.app.utils.Resource;

import java.util.List;

/**
 * ViewModel for the redesigned ConversacionFragment.
 * Loads conversation messages and handles sending new messages.
 */
public class ConversacionViewModel extends ViewModel {

    private final MensajeRepository repository;

    private final MutableLiveData<Resource<List<MensajeResponse>>> mensajes = new MutableLiveData<>();
    private final MutableLiveData<Resource<MensajeResponse>> sendResult = new MutableLiveData<>();
    private final MutableLiveData<String> otherUserName = new MutableLiveData<>("");
    private final MutableLiveData<String> otherUserEmoji = new MutableLiveData<>("\uD83D\uDC64"); // default 👤

    private long otherUserId;
    private long currentUserId;

    // Emoji constants
    private static final String EMOJI_COACH = "\uD83D\uDC68\u200D\u2695\uFE0F";  // 👨‍⚕️
    private static final String EMOJI_DEPORTISTA = "\uD83C\uDFC3";                // 🏃

    // Flag to prevent UI updates after ViewModel is cleared
    private volatile boolean cleared = false;

    public ConversacionViewModel() {
        this.repository = new MensajeRepository();
    }

    public ConversacionViewModel(MensajeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleared = true;
    }

    public void init(long otherUserId, long currentUserId, String otherUserName) {
        this.otherUserId = otherUserId;
        this.currentUserId = currentUserId;
        this.otherUserName.setValue(otherUserName);
    }

    public void init(long otherUserId, long currentUserId, String otherUserName, String emoji) {
        this.otherUserId = otherUserId;
        this.currentUserId = currentUserId;
        this.otherUserName.setValue(otherUserName);
        if (emoji != null && !emoji.isEmpty()) {
            this.otherUserEmoji.setValue(emoji);
        }
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

    public LiveData<String> getOtherUserEmoji() {
        return otherUserEmoji;
    }

    public long getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Determine the emoji based on the other user's inferred role.
     * If we don't have an explicit emoji, use a heuristic:
     * if current user is DEPORTISTA, the other is likely a COACH.
     */
    public static String getEmojiForRole(boolean isCurrentDeportista) {
        return isCurrentDeportista ? EMOJI_COACH : EMOJI_DEPORTISTA;
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
        if (contenido == null || contenido.trim().isEmpty()) return;

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
