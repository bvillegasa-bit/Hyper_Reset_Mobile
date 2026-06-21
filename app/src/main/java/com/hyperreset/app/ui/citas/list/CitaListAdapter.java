package com.hyperreset.app.ui.citas.list;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.CitaResponse;

import java.util.List;

/**
 * RecyclerView adapter for the redesigned weekly calendar appointments.
 * Displays each appointment as a card with:
 * - Left column: clock gradient background + time
 * - Center: name (patient or coach), type/motivo, duration + estado badges
 * - Right: chevron
 */
public class CitaListAdapter extends RecyclerView.Adapter<CitaListAdapter.ViewHolder> {

    private List<CitaResponse> citaList;
    private OnItemClickListener listener;
    private boolean isCoach;

    public interface OnItemClickListener {
        void onItemClick(CitaResponse cita);
    }

    public CitaListAdapter(List<CitaResponse> citaList, OnItemClickListener listener) {
        this.citaList = citaList;
        this.listener = listener;
        this.isCoach = true;
    }

    public void updateData(List<CitaResponse> newList) {
        this.citaList = newList;
        notifyDataSetChanged();
    }

    /**
     * Set the role for name display.
     * COACH: shows paciente name (deportistaNombre)
     * DEPORTISTA: shows coach name (coachNombre)
     */
    public void setIsCoach(boolean isCoach) {
        this.isCoach = isCoach;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita_semanal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CitaResponse cita = citaList.get(position);
        holder.bind(cita, listener, isCoach);
    }

    @Override
    public int getItemCount() {
        return citaList != null ? citaList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvHora;
        private final TextView tvNombre;
        private final TextView tvMotivo;
        private final TextView badgeDuracion;
        private final TextView badgeEstado;
        private final LinearLayout layoutBadges;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvMotivo = itemView.findViewById(R.id.tvMotivo);
            badgeDuracion = itemView.findViewById(R.id.badgeDuracion);
            badgeEstado = itemView.findViewById(R.id.badgeEstado);
            layoutBadges = itemView.findViewById(R.id.layoutBadges);
        }

        void bind(final CitaResponse cita, final OnItemClickListener listener, boolean isCoach) {
            // Time display (show HH:mm from horaCita)
            String hora = cita.getHoraCita();
            if (hora != null && hora.length() >= 5) {
                // Extract HH:mm if longer format
                tvHora.setText(hora.length() > 5 ? hora.substring(0, 5) : hora);
            } else {
                tvHora.setText(hora != null ? hora : "--:--");
            }

            // Name: COACH sees deportistaNombre, DEPORTISTA sees coachNombre
            if (isCoach) {
                String nombre = cita.getDeportistaNombre();
                tvNombre.setText(nombre != null ? nombre : itemView.getContext().getString(R.string.citas_list_sin_nombre));
            } else {
                String nombre = cita.getCoachNombre();
                tvNombre.setText(nombre != null ? nombre : "Dr. Asignado");
            }

            // Motivo / Type
            String motivo = cita.getMotivo();
            tvMotivo.setText(motivo != null ? motivo : itemView.getContext().getString(R.string.citas_list_sin_motivo));

            // Duration badge (hidden by default — backend may not send duration)
            badgeDuracion.setVisibility(View.GONE);

            // Estado badge
            setEstadoBadge(cita.getEstado());

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(cita);
                }
            });
        }

        private void setEstadoBadge(String estado) {
            if (estado == null) {
                badgeEstado.setVisibility(View.GONE);
                return;
            }

            badgeEstado.setVisibility(View.VISIBLE);

            int bgColor;
            String displayText;

            switch (estado.toUpperCase()) {
                case "CONFIRMADA":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.hyper_success);
                    displayText = itemView.getContext().getString(R.string.citas_calendar_confirmada);
                    break;
                case "PENDIENTE":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.hyper_in_progress);
                    displayText = itemView.getContext().getString(R.string.citas_calendar_pendiente);
                    break;
                case "COMPLETADA":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.hyper_excelente);
                    displayText = itemView.getContext().getString(R.string.citas_calendar_completada);
                    break;
                case "CANCELADA":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.hyper_deficiente);
                    displayText = itemView.getContext().getString(R.string.citas_calendar_cancelada);
                    break;
                default:
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.hyper_in_progress);
                    displayText = estado;
                    break;
            }

            badgeEstado.setText(displayText);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(16f);
            drawable.setColor(bgColor);
            // Make semi-transparent (20% opacity)
            drawable.setAlpha(51); // ~20%
            badgeEstado.setBackground(drawable);

            // Set text color to the full color
            badgeEstado.setTextColor(bgColor);
        }
    }
}
