package com.hyperreset.app.data.model;

/**
 * Model for a grouped conversation (not a direct API response).
 * Composed from received + sent messages by MensajeListViewModel.
 * Represents a conversation thread with another user.
 */
public class Conversacion {

    private final long otherUserId;
    private final String contactName;
    private final String avatarEmoji;
    private final String lastMessage;
    private final String lastMessageTimestamp;
    private final String relativeTime;
    private final int unreadCount;

    public Conversacion(long otherUserId, String contactName, String avatarEmoji,
                        String lastMessage, String lastMessageTimestamp,
                        String relativeTime, int unreadCount) {
        this.otherUserId = otherUserId;
        this.contactName = contactName;
        this.avatarEmoji = avatarEmoji;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.relativeTime = relativeTime;
        this.unreadCount = unreadCount;
    }

    public long getOtherUserId() {
        return otherUserId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public String getRelativeTime() {
        return relativeTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
