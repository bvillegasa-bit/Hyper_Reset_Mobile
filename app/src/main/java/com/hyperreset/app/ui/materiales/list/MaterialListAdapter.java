package com.hyperreset.app.ui.materiales.list;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.MaterialResponse;

import java.util.List;

/**
 * RecyclerView adapter for the educational materials grid.
 * Displays each material as a card with title, description, type badge and date.
 */
public class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.ViewHolder> {

    private List<MaterialResponse> materialList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MaterialResponse material);
    }

    public MaterialListAdapter(List<MaterialResponse> materialList, OnItemClickListener listener) {
        this.materialList = materialList;
        this.listener = listener;
    }

    public void updateData(List<MaterialResponse> newList) {
        this.materialList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialResponse material = materialList.get(position);
        holder.bind(material, listener);
    }

    @Override
    public int getItemCount() {
        return materialList != null ? materialList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView badgeTipo;
        private final TextView tvTitulo;
        private final TextView tvDescripcion;
        private final TextView tvFecha;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeTipo = itemView.findViewById(R.id.badgeTipo);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }

        void bind(final MaterialResponse material, final OnItemClickListener listener) {
            tvTitulo.setText(material.getTitulo() != null ? material.getTitulo() : "");
            tvDescripcion.setText(material.getDescripcion() != null ? material.getDescripcion() : "");
            tvFecha.setText(material.getFechaPublicacion() != null ? material.getFechaPublicacion() : "");
            setTipoBadge(material.getTipoMaterial());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(material);
                }
            });
        }

        private void setTipoBadge(String tipoMaterial) {
            if (tipoMaterial == null) {
                badgeTipo.setText("");
                badgeTipo.setVisibility(View.GONE);
                return;
            }

            badgeTipo.setVisibility(View.VISIBLE);

            int color;
            int labelResId;

            switch (tipoMaterial.toUpperCase()) {
                case "VIDEO":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_excelente);
                    labelResId = R.string.materiales_type_video;
                    break;
                case "PDF":
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_bueno);
                    labelResId = R.string.materiales_type_pdf;
                    break;
                case "ENLACE":
                default:
                    color = ContextCompat.getColor(itemView.getContext(), R.color.hyper_accent);
                    labelResId = R.string.materiales_type_enlace;
                    break;
            }

            badgeTipo.setText(itemView.getContext().getString(labelResId));

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(16f);
            drawable.setColor(color);
            badgeTipo.setBackground(drawable);
        }
    }
}
