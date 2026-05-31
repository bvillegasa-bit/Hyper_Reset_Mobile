package com.hyperreset.app.ui.deportistas.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.DeportistaResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for the deportistas (athletes) list.
 * Displays athlete name, email, and registration date.
 * Supports search/filter by name.
 */
public class DeportistaListAdapter extends RecyclerView.Adapter<DeportistaListAdapter.ViewHolder>
        implements Filterable {

    private List<DeportistaResponse> deportistaList;
    private List<DeportistaResponse> deportistaListFull; // Unfiltered copy for search
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DeportistaResponse deportista);
    }

    public DeportistaListAdapter(List<DeportistaResponse> deportistaList, OnItemClickListener listener) {
        this.deportistaList = deportistaList;
        this.deportistaListFull = new ArrayList<>(deportistaList);
        this.listener = listener;
    }

    public void updateData(List<DeportistaResponse> newList) {
        this.deportistaList = newList;
        this.deportistaListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deportista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeportistaResponse deportista = deportistaList.get(position);
        holder.bind(deportista, listener);
    }

    @Override
    public int getItemCount() {
        return deportistaList != null ? deportistaList.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<DeportistaResponse> filteredList = new ArrayList<>();

                if (constraint == null || constraint.toString().trim().isEmpty()) {
                    filteredList.addAll(deportistaListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();
                    for (DeportistaResponse item : deportistaListFull) {
                        String nombre = item.getNombreCompleto() != null
                                ? item.getNombreCompleto().toLowerCase(Locale.ROOT) : "";
                        String email = item.getEmail() != null
                                ? item.getEmail().toLowerCase(Locale.ROOT) : "";
                        if (nombre.contains(filterPattern) || email.contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                deportistaList.clear();
                deportistaList.addAll((List<DeportistaResponse>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNombreCompleto;
        private final TextView tvEmail;
        private final TextView tvFechaRegistro;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCompleto = itemView.findViewById(R.id.tvNombreCompleto);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvFechaRegistro = itemView.findViewById(R.id.tvFechaRegistro);
        }

        void bind(final DeportistaResponse deportista, final OnItemClickListener listener) {
            tvNombreCompleto.setText(deportista.getNombreCompleto());
            tvEmail.setText(deportista.getEmail() != null ? deportista.getEmail() : "-");

            // Format date: yyyy-MM-dd
            String fechaReg = deportista.getFechaRegistro() != null
                    ? deportista.getFechaRegistro() : "";
            tvFechaRegistro.setText(fechaReg);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(deportista);
                }
            });
        }
    }
}
