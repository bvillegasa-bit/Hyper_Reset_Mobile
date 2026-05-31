package com.hyperreset.app.ui.mensajes.conversacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.MensajeResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for the conversation thread.
 * Two view types: sent messages (right-aligned) and received messages (left-aligned).
 */
public class ConversacionAdapter extends RecyclerView.Adapter<ConversacionAdapter.ViewHolder> {

    private List<MensajeResponse> mensajes;
    private final long currentUserId;

    private static final int VIEW_TYPE_SENT = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;

    public ConversacionAdapter(long currentUserId) {
        this.mensajes = new ArrayList<>();
        this.currentUserId = currentUserId;
    }

    public void updateData(List<MensajeResponse> newList) {
        this.mensajes = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MensajeResponse msg = mensajes.get(position);
        return msg.getRemitenteId() == currentUserId ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = viewType == VIEW_TYPE_SENT
                ? R.layout.item_mensaje_conversacion_sent
                : R.layout.item_mensaje_conversacion_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MensajeResponse msg = mensajes.get(position);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvContenido;
        private final TextView tvTimestamp;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        void bind(MensajeResponse mensaje) {
            tvContenido.setText(mensaje.getContenido() != null ? mensaje.getContenido() : "");
            tvTimestamp.setText(mensaje.getFechaEnvio() != null ? mensaje.getFechaEnvio() : "");
        }
    }
}
