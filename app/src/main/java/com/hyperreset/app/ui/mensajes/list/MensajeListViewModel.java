package com.hyperreset.app.ui.mensajes.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.Conversacion;
import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.data.repository.MensajeRepository;
import com.hyperreset.app.utils.Resource;
import com.hyperreset.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ViewModel for the redesigned MensajeListFragment.
 * Loads received + sent messages, groups them by contact into conversations,
 * and supports local search filtering.
 */
public class MensajeListViewModel extends ViewModel {

    private final MensajeRepository repository;

    private final MutableLiveData<Resource<List<Conversacion>>> conversaciones = new MutableLiveData<>();
    private final MutableLiveData<Integer> noLeidos = new MutableLiveData<>(0);

    // Full list (unfiltered) for search
    private List<Conversacion> allConversations = new ArrayList<>();
    private String currentSearchQuery = "";

    // Session manager for user info
    private SessionManager sessionManager;

    // Flag to prevent UI updates after ViewModel is cleared
    private volatile boolean cleared = false;

    // Emoji for the other user based on current user's role
    private static final String EMOJI_COACH = "\uD83D\uDC68\u200D\u2695\uFE0F";  // 👨‍⚕️
    private static final String EMOJI_DEPORTISTA = "\uD83C\uDFC3";                // 🏃
    private static final String EMOJI_DEFAULT = "\uD83D\uDC64";                   // 👤

    public MensajeListViewModel() {
        this.repository = new MensajeRepository();
    }

    public MensajeListViewModel(MensajeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleared = true;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public LiveData<Resource<List<Conversacion>>> getConversaciones() {
        return conversaciones;
    }

    public LiveData<Integer> getNoLeidos() {
        return noLeidos;
    }

    /**
     * Load both received and sent messages, then group by contact into conversations.
     */
    public void loadConversaciones() {
        conversaciones.setValue(Resource.loading());

        final Object lock = new Object();
        AtomicInteger pendingCalls = new AtomicInteger(2);
        List<MensajeResponse> allRecibidos = new ArrayList<>();
        List<MensajeResponse> allEnviados = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();

        MensajeRepository.ResourceCallback<List<MensajeResponse>> recibidosCallback = result -> {
            synchronized (lock) {
                if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    allRecibidos.addAll(result.data);
                } else if (result.status == Resource.Status.ERROR) {
                    errorMsg.append("Error al cargar recibidos: ")
                            .append(result.message != null ? result.message : "desconocido")
                            .append(". ");
                }
                if (pendingCalls.decrementAndGet() == 0) {
                    onBothLoaded(allRecibidos, allEnviados, errorMsg.toString());
                }
            }
        };

        MensajeRepository.ResourceCallback<List<MensajeResponse>> enviadosCallback = result -> {
            synchronized (lock) {
                if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    allEnviados.addAll(result.data);
                } else if (result.status == Resource.Status.ERROR) {
                    errorMsg.append("Error al cargar enviados: ")
                            .append(result.message != null ? result.message : "desconocido")
                            .append(". ");
                }
                if (pendingCalls.decrementAndGet() == 0) {
                    onBothLoaded(allRecibidos, allEnviados, errorMsg.toString());
                }
            }
        };

        repository.getMensajesRecibidos(recibidosCallback);
        repository.getMensajesEnviados(enviadosCallback);
    }

    /**
     * Called when both API calls complete. Groups messages into conversations.
     */
    private void onBothLoaded(List<MensajeResponse> recibidos,
                              List<MensajeResponse> enviados,
                              String errorMsg) {
        // Determine avatar emoji based on current user's role
        boolean isCurrentUserDeportista = sessionManager != null && sessionManager.isDeportista();
        String otherUserEmoji = isCurrentUserDeportista ? EMOJI_COACH : EMOJI_DEPORTISTA;

        // Build map: otherUserId -> data for the conversation
        // Also build a name map: userId -> userName from recibidos (where we know the name)
        Map<Long, String> nameMap = new HashMap<>();
        for (MensajeResponse msg : recibidos) {
            if (!nameMap.containsKey(msg.getRemitenteId())) {
                nameMap.put(msg.getRemitenteId(), msg.getRemitenteNombre());
            }
        }
        // Add names from enviados if we have destinatarioNombre
        for (MensajeResponse msg : enviados) {
            // For sent messages, we use the recipient's name if available
            // The name should come from recibidos (if we've received from this person)
            // or could be stored elsewhere
        }

        // Group received messages
        Map<Long, List<MensajeResponse>> groups = new HashMap<>();
        for (MensajeResponse msg : recibidos) {
            long otherId = msg.getRemitenteId();
            if (!groups.containsKey(otherId)) {
                groups.put(otherId, new ArrayList<>());
            }
            groups.get(otherId).add(msg);
        }

        // Group sent messages by destinatarioId
        for (MensajeResponse msg : enviados) {
            long otherId = msg.getDestinatarioId();
            if (!groups.containsKey(otherId)) {
                groups.put(otherId, new ArrayList<>());
            }
            groups.get(otherId).add(msg);
        }

        // For each group, compute the conversation data
        List<Conversacion> result = new ArrayList<>();
        for (Map.Entry<Long, List<MensajeResponse>> entry : groups.entrySet()) {
            long otherUserId = entry.getKey();
            if (otherUserId <= 0) continue;

            List<MensajeResponse> msgs = entry.getValue();

            // Find contact name: try from received messages first
            String contactName = nameMap.get(otherUserId);
            if (contactName == null || contactName.isEmpty()) {
                // Fallback: look in sent messages for destinatarioNombre
                for (MensajeResponse msg : enviados) {
                    if (msg.getDestinatarioId() == otherUserId
                            && msg.getDestinatarioNombre() != null
                            && !msg.getDestinatarioNombre().isEmpty()) {
                        contactName = msg.getDestinatarioNombre();
                        break;
                    }
                }
            }
            if (contactName == null || contactName.isEmpty()) {
                contactName = "Usuario #" + otherUserId;
            }

            // Find the latest message
            MensajeResponse latest = msgs.get(0);
            for (MensajeResponse msg : msgs) {
                if (compareDates(msg.getFechaEnvio(), latest.getFechaEnvio()) > 0) {
                    latest = msg;
                }
            }

            // Count unread: received messages where leido=false
            int unreadCount = 0;
            for (MensajeResponse msg : recibidos) {
                if (msg.getRemitenteId() == otherUserId && !msg.isLeido()) {
                    unreadCount++;
                }
            }

            String lastMessage = latest.getContenido();
            String timestamp = latest.getFechaEnvio();
            String relativeTime = formatRelativeTime(timestamp);

            Conversacion conv = new Conversacion(
                    otherUserId,
                    contactName,
                    otherUserEmoji,
                    lastMessage,
                    timestamp,
                    relativeTime,
                    unreadCount
            );
            result.add(conv);
        }

        // Sort: newest first by timestamp
        result.sort((a, b) -> compareDates(b.getLastMessageTimestamp(), a.getLastMessageTimestamp()));

        allConversations = result;

        // Apply any existing search filter
        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            filterConversations(currentSearchQuery);
        } else {
            if (result.isEmpty() && errorMsg.isEmpty()) {
                conversaciones.setValue(Resource.success(result));
            } else if (result.isEmpty() && !errorMsg.isEmpty()) {
                conversaciones.setValue(Resource.error(errorMsg));
            } else {
                conversaciones.setValue(Resource.success(result));
            }
        }
    }

    /**
     * Filter conversations by search query (filters locally by contact name).
     */
    public void filterConversations(String query) {
        currentSearchQuery = query != null ? query : "";
        if (currentSearchQuery.isEmpty()) {
            conversaciones.setValue(Resource.success(new ArrayList<>(allConversations)));
            return;
        }
        List<Conversacion> filtered = new ArrayList<>();
        String lowerQuery = currentSearchQuery.toLowerCase(Locale.ROOT);
        for (Conversacion c : allConversations) {
            if (c.getContactName() != null
                    && c.getContactName().toLowerCase(Locale.ROOT).contains(lowerQuery)) {
                filtered.add(c);
            }
        }
        conversaciones.setValue(Resource.success(filtered));
    }

    /**
     * Public setter for search query (called from fragment).
     */
    public void setSearchQuery(String query) {
        filterConversations(query);
    }

    public void loadNoLeidos() {
        repository.getNoLeidos(result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                noLeidos.setValue(result.data);
            }
        });
    }

    // ==================================================================
    // Utility methods
    // ==================================================================

    /**
     * Compare two date strings. Returns negative if date1 < date2, etc.
     */
    private int compareDates(String date1, String date2) {
        if (date1 == null && date2 == null) return 0;
        if (date1 == null) return -1;
        if (date2 == null) return 1;
        return date1.compareTo(date2);
    }

    /**
     * Format a timestamp string into a relative human-readable form.
     * "2026-05-14T10:30:00" → "2 min", "1 h", "3 h", "15 May", etc.
     */
    public static String formatRelativeTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) return "";

        try {
            // Parse ISO-like date
            String cleanTs = timestamp.replace("T", " ").replace("Z", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(cleanTs);
            if (date == null) return "";

            Date now = new Date();
            long diffMs = now.getTime() - date.getTime();
            if (diffMs < 0) diffMs = 0;

            long diffMinutes = diffMs / (60 * 1000);
            long diffHours = diffMinutes / 60;
            long diffDays = diffHours / 24;

            if (diffMinutes < 1) return "ahora";
            if (diffMinutes < 60) return diffMinutes + " min";
            if (diffHours < 24) return diffHours + " h";
            if (diffDays < 7) return diffDays + " d";

            // Older: show date
            SimpleDateFormat dateFmt = new SimpleDateFormat("d MMM", Locale.getDefault());
            return dateFmt.format(date);

        } catch (Exception e) {
            // If parsing fails, return original timestamp truncated
            if (timestamp.length() > 10) {
                return timestamp.substring(0, 10);
            }
            return timestamp;
        }
    }

}
