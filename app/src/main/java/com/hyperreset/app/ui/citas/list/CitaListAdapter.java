package com.hyperreset.app.ui.citas.list;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.CitaResponse;

import java.util.List;

/**
 * RecyclerView adapter for the appointment (citas) list.
 * Displays appointments with deportista name, fecha, hora, and color-coded estado badge.
 */
public class CitaListAdapter extends RecyclerView.Adapter<CitaListAdapter.ViewHolder> {

    private List<CitaResponse> citaList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CitaResponse cita);
    }

    public CitaListAdapter(List<CitaResponse> citaList, OnItemClickListener listener) {
        this.citaList = citaList;
        this.listener = listener;
    }

    public void updateData(List<CitaResponse> newList) {
        this.citaList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CitaResponse cita = citaList.get(position);
        holder.bind(cita, listener);
    }

    @Override
    public int getItemCount() {
        return citaList != null ? citaList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDeportistaNombre;
        private final TextView tvFechaCita;
        private final TextView tvHoraCita;
        private final TextView badgeEstado;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeportistaNombre = itemView.findViewById(R.id.tvDeportistaNombre);
            tvFechaCita = itemView.findViewById(R.id.tvFechaCita);
            tvHoraCita = itemView.findViewById(R.id.tvHoraCita);
            badgeEstado = itemView.findViewById(R.id.badgeEstado);
        }

        void bind(final CitaResponse cita, final OnItemClickListener listener) {
            tvDeportistaNombre.setText(cita.getDeportistaNombre());
            tvFechaCita.setText(cita.getFechaCita());
            tvHoraCita.setText(cita.getHoraCita());
            setEstadoBadge(cita.getEstado());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(cita);
                }
            });
        }

        private void setEstadoBadge(String estado) {
            if (estado == null) {
                badgeEstado.setText("");
                return;
            }

            badgeEstado.setText(estado);

            int color;
            switch (estado) {
                case "PENDIENTE":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_in_progress);
                    break;
                case "CONFIRMADA":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_bueno);
                    break;
                case "COMPLETADA":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_excelente);
                    break;
                case "CANCELADA":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_deficiente);
                    break;
                default:
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_in_progress);
                    break;
            }

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(16f);
            drawable.setColor(color);
            badgeEstado.setBackground(drawable);
        }
    }
}
