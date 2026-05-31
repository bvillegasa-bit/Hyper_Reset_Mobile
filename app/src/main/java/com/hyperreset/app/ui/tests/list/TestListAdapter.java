package com.hyperreset.app.ui.tests.list;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.TestFisicoResponse;

import java.util.List;

/**
 * RecyclerView adapter for the test list.
 * Displays test sessions with deportista name, test type, date, and status/calificacion badge.
 */
public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.ViewHolder> {

    private List<TestFisicoResponse> testList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TestFisicoResponse test);
    }

    public TestListAdapter(List<TestFisicoResponse> testList, OnItemClickListener listener) {
        this.testList = testList;
        this.listener = listener;
    }

    public void updateData(List<TestFisicoResponse> newList) {
        this.testList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestFisicoResponse test = testList.get(position);
        holder.bind(test, listener);
    }

    @Override
    public int getItemCount() {
        return testList != null ? testList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDeportistaNombre;
        private final TextView tvTestType;
        private final TextView tvDate;
        private final TextView badgeStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeportistaNombre = itemView.findViewById(R.id.tvDeportistaNombre);
            tvTestType = itemView.findViewById(R.id.tvTestType);
            tvDate = itemView.findViewById(R.id.tvDate);
            badgeStatus = itemView.findViewById(R.id.badgeStatus);
        }

        void bind(final TestFisicoResponse test, final OnItemClickListener listener) {
            tvDeportistaNombre.setText(test.getDeportistaNombre());
            tvTestType.setText(getTestTypeLabel(test.getTipoTest()));
            tvDate.setText(test.getFechaTest());

            if (test.isCompletado()) {
                badgeStatus.setText(R.string.test_detail_completado);
                setBadgeColor(ContextCompat.getColor(itemView.getContext(), R.color.hyper_excelente));
            } else {
                badgeStatus.setText(R.string.test_detail_en_progreso);
                setBadgeColor(ContextCompat.getColor(itemView.getContext(), R.color.hyper_in_progress));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(test);
                }
            });
        }

        private void setBadgeColor(int color) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(16f);
            drawable.setColor(color);
            badgeStatus.setBackground(drawable);
        }

        private String getTestTypeLabel(String tipoTestName) {
            if (tipoTestName == null) return "";
            switch (tipoTestName) {
                case "ILLINOIS": return itemView.getContext().getString(R.string.test_type_illinois);
                case "FLEXION_CODOS": return itemView.getContext().getString(R.string.test_type_flexion_codos);
                case "VELOCIDAD_20M": return itemView.getContext().getString(R.string.test_type_velocidad_20m);
                case "VELOCIDAD_REACCION": return itemView.getContext().getString(R.string.test_type_velocidad_reaccion);
                case "SALTO_HORIZONTAL": return itemView.getContext().getString(R.string.test_type_salto_horizontal);
                case "FLEXION_TRONCO": return itemView.getContext().getString(R.string.test_type_flexion_tronco);
                case "DINAMOMETRIA": return itemView.getContext().getString(R.string.test_type_dinamometria);
                case "ANDERSEN": return itemView.getContext().getString(R.string.test_type_andersen);
                default: return tipoTestName;
            }
        }
    }
}
