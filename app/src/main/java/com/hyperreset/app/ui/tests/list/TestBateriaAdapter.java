package com.hyperreset.app.ui.tests.list;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.TipoTestEstadoResponse;

import java.util.List;

/**
 * RecyclerView adapter for the 8 fixed physical tests (batería).
 * Displays each test with emoji, name, duration, and completion badge.
 */
public class TestBateriaAdapter extends RecyclerView.Adapter<TestBateriaAdapter.ViewHolder> {

    private List<TipoTestEstadoResponse> testList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TipoTestEstadoResponse test);
    }

    public TestBateriaAdapter(List<TipoTestEstadoResponse> testList, OnItemClickListener listener) {
        this.testList = testList;
        this.listener = listener;
    }

    public void updateData(List<TipoTestEstadoResponse> newList) {
        this.testList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_bateria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TipoTestEstadoResponse test = testList.get(position);
        holder.bind(test, listener);
    }

    @Override
    public int getItemCount() {
        return testList != null ? testList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvEmoji;
        private final TextView tvTestName;
        private final TextView tvClockIcon;
        private final TextView tvDuration;
        private final TextView badgeCompleted;
        private final TextView ivChevron;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvTestName = itemView.findViewById(R.id.tvTestName);
            tvClockIcon = itemView.findViewById(R.id.tvClockIcon);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            badgeCompleted = itemView.findViewById(R.id.badgeCompleted);
            ivChevron = itemView.findViewById(R.id.ivChevron);
        }

        void bind(final TipoTestEstadoResponse test, final OnItemClickListener listener) {
            // Emoji — use backend icono or fallback based on tipoTest
            String emoji = test.getIcono();
            if (TextUtils.isEmpty(emoji)) {
                emoji = getDefaultEmoji(test.getTipoTest());
            }
            tvEmoji.setText(emoji);

            // Name
            tvTestName.setText(test.getNombre() != null ? test.getNombre() : test.getTipoTest());

            // Duration
            tvDuration.setText(test.getDuracion() != null ? test.getDuracion() : "");

            // Badge: completado / pendiente
            if (test.isCompletado()) {
                badgeCompleted.setVisibility(View.VISIBLE);
                badgeCompleted.setText(R.string.test_bateria_completado);
            } else {
                badgeCompleted.setVisibility(View.GONE);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(test);
                }
            });
        }

        private String getDefaultEmoji(String tipoTest) {
            if (tipoTest == null) return "";
            switch (tipoTest.toUpperCase()) {
                case "ILLINOIS": return "\uD83C\uDFC3";
                case "FLEXION_CODOS": return "\uD83D\uDCAA";
                case "VELOCIDAD_20M": return "\u26A1";
                case "VELOCIDAD_REACCION": return "\uD83D\uDC46";
                case "SALTO_HORIZONTAL": return "\uD83E\uDD98";
                case "FLEXION_TRONCO": return "\uD83E\uDDD8";
                case "DINAMOMETRIA": return "\uD83E\uDD1C";
                case "ANDERSEN": return "\uD83C\uDFC3\u200D\u200D";
                default: return "\uD83D\uDCCB";
            }
        }
    }
}
