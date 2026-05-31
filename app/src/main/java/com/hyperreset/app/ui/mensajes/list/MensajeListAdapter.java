package com.hyperreset.app.ui.mensajes.list;

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
 * RecyclerView adapter for the mensajes (messages) list.
 * Displays sender/recipient name, content preview, date, and read/unread indicator.
 */
public class MensajeListAdapter extends RecyclerView.Adapter<MensajeListAdapter.ViewHolder> {

    private List<MensajeResponse> mensajeList;
    private OnItemClickListener listener;
    private boolean showRecibidos; // true = show remitenteNombre, false = show destinatario logic

    public interface OnItemClickListener {
        void onItemClick(MensajeResponse mensaje);
    }

    public MensajeListAdapter(List<MensajeResponse> mensajeList, boolean showRecibidos,
                              OnItemClickListener listener) {
        this.mensajeList = mensajeList != null ? mensajeList : new ArrayList<>();
        this.showRecibidos = showRecibidos;
        this.listener = listener;
    }

    public void updateData(List<MensajeResponse> newList) {
        this.mensajeList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setShowRecibidos(boolean showRecibidos) {
        this.showRecibidos = showRecibidos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MensajeResponse mensaje = mensajeList.get(position);
        holder.bind(mensaje, showRecibidos, listener);
    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvContactName;
        private final TextView tvContentPreview;
        private final TextView tvDate;
        private final View unreadIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContentPreview = itemView.findViewById(R.id.tvContentPreview);
            tvDate = itemView.findViewById(R.id.tvDate);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }

        void bind(final MensajeResponse mensaje, final boolean showRecibidos,
                  final OnItemClickListener listener) {
            // Show the other person's name
            if (showRecibidos) {
                tvContactName.setText(mensaje.getRemitenteNombre() != null
                        ? mensaje.getRemitenteNombre() : "-");
            } else {
                // For sent messages, show "Para: {name}" or just the name
                tvContactName.setText(mensaje.getRemitenteNombre() != null
                        ? mensaje.getRemitenteNombre() : "-");
            }

            // Content preview (first 80 chars)
            String preview = mensaje.getContenido();
            if (preview != null && preview.length() > 80) {
                preview = preview.substring(0, 80) + "...";
            }
            tvContentPreview.setText(preview != null ? preview : "");

            // Date
            tvDate.setText(mensaje.getFechaEnvio() != null ? mensaje.getFechaEnvio() : "");

            // Unread indicator
            boolean isUnread = showRecibidos && !mensaje.isLeido();
            unreadIndicator.setVisibility(isUnread ? View.VISIBLE : View.GONE);
            tvContactName.getPaint().setFakeBoldText(isUnread);
            tvContentPreview.getPaint().setFakeBoldText(isUnread);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(mensaje);
                }
            });
        }
    }
}
