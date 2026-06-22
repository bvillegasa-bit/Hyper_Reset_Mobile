package com.hyperreset.app.ui.tests.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.TestFisicoResponse;

import java.util.ArrayList;
import java.util.List;

public class SessionHistoryAdapter extends RecyclerView.Adapter<SessionHistoryAdapter.ViewHolder> {

    private List<TestFisicoResponse> data = new ArrayList<>();
    private final OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onClick(TestFisicoResponse session);
    }

    public SessionHistoryAdapter(OnSessionClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<TestFisicoResponse> newData) {
        this.data = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestFisicoResponse session = data.get(position);
        holder.bind(session);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(session);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus, tvCalificacion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvSessionDate);
            tvStatus = itemView.findViewById(R.id.tvSessionStatus);
            tvCalificacion = itemView.findViewById(R.id.tvSessionCalificacion);
        }

        void bind(TestFisicoResponse session) {
            // Date
            String date = session.getFechaTest() != null ? session.getFechaTest() : "\u2014";
            tvDate.setText(date);

            // Status
            if (session.isCompletado()) {
                tvStatus.setText(R.string.test_detail_completado);
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.hyper_excelente));
            } else {
                tvStatus.setText(R.string.test_detail_en_progreso);
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.hyper_in_progress));
            }

            // Calificaci\u00f3n
            String calif = session.getCalificacion();
            if (calif != null && !calif.trim().isEmpty()) {
                tvCalificacion.setVisibility(View.VISIBLE);
                tvCalificacion.setText(calif);
            } else {
                tvCalificacion.setVisibility(View.GONE);
            }
        }
    }
}
