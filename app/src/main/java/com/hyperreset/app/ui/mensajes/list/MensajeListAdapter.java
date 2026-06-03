package com.hyperreset.app.ui.mensajes.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.Conversacion;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for the redesigned conversation list.
 * Displays avatar with gradient + emoji, badge, contact name,
 * last message, relative timestamp, and chevron.
 */
public class MensajeListAdapter extends RecyclerView.Adapter<MensajeListAdapter.ViewHolder> {

    private List<Conversacion> conversaciones;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Conversacion conversacion);
    }

    public MensajeListAdapter(List<Conversacion> conversaciones, OnItemClickListener listener) {
        this.conversaciones = conversaciones != null ? conversaciones : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<Conversacion> newList) {
        this.conversaciones = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversacion conv = conversaciones.get(position);
        holder.bind(conv, listener);
    }

    @Override
    public int getItemCount() {
        return conversaciones.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAvatar;
        private final TextView badgeUnread;
        private final TextView tvContactName;
        private final TextView tvLastMessage;
        private final TextView tvTimestamp;
        private final FrameLayout frameAvatar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            badgeUnread = itemView.findViewById(R.id.badgeUnread);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            frameAvatar = itemView.findViewById(R.id.frameAvatar);
        }

        void bind(final Conversacion conv, final OnItemClickListener listener) {
            // Avatar emoji
            tvAvatar.setText(conv.getAvatarEmoji() != null ? conv.getAvatarEmoji() : "\uD83D\uDC64");

            // Unread badge
            if (conv.getUnreadCount() > 0) {
                badgeUnread.setVisibility(View.VISIBLE);
                badgeUnread.setText(String.valueOf(conv.getUnreadCount()));
            } else {
                badgeUnread.setVisibility(View.GONE);
            }

            // Contact name (bold if unread)
            tvContactName.setText(conv.getContactName() != null ? conv.getContactName() : "-");
            tvContactName.getPaint().setFakeBoldText(conv.getUnreadCount() > 0);

            // Last message (truncated to 1 line)
            String preview = conv.getLastMessage();
            if (preview != null && preview.length() > 80) {
                preview = preview.substring(0, 80) + "...";
            }
            tvLastMessage.setText(preview != null ? preview : "");
            tvLastMessage.getPaint().setFakeBoldText(conv.getUnreadCount() > 0);

            // Relative timestamp
            tvTimestamp.setText(conv.getRelativeTime() != null ? conv.getRelativeTime() : "");

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(conv);
                }
            });
        }
    }
}
