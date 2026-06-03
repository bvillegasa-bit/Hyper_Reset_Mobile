package com.hyperreset.app.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hyperreset.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom View that draws a 7-day bar chart (L M M J V S D).
 * Each bar is drawn with a gradient from hyper_primary (#FF4500) to hyper_accent (#FF6B35).
 * Includes day labels below each bar and a percentage display.
 */
public class WeeklyBarChartView extends View {

    private static final int NUM_BARS = 7;
    private static final String[] DAY_LABELS = {"L", "M", "M", "J", "V", "S", "D"};

    private final Paint barPaint;
    private final Paint labelPaint;
    private final Paint percentPaint;
    private final Paint bgPaint;
    private final Paint trackPaint;

    private final RectF barRect;
    private final float density;

    private List<Integer> values;
    private int barColorStart;
    private int barColorEnd;
    private float maxValue = 100f;

    public WeeklyBarChartView(Context context) {
        this(context, null);
    }

    public WeeklyBarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeeklyBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;

        barColorStart = ContextCompat.getColor(context, R.color.hyper_primary);
        barColorEnd = ContextCompat.getColor(context, R.color.hyper_accent);

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(ContextCompat.getColor(context, R.color.hyper_on_surface_variant));
        labelPaint.setTextSize(11 * density);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        percentPaint.setColor(Color.WHITE);
        percentPaint.setTextSize(10 * density);
        percentPaint.setTextAlign(Paint.Align.CENTER);
        percentPaint.setFakeBoldText(true);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(ContextCompat.getColor(context, R.color.hyper_surface_variant));
        bgPaint.setStyle(Paint.Style.FILL);

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setColor(ContextCompat.getColor(context, R.color.hyper_surface));
        trackPaint.setStyle(Paint.Style.FILL);

        barRect = new RectF();
        values = new ArrayList<>();
        // Initialize with zeros
        for (int i = 0; i < NUM_BARS; i++) {
            values.add(0);
        }
    }

    /**
     * Set the 7 values for the weekly chart.
     *
     * @param values List of 7 integers (0-100)
     */
    public void setData(List<Integer> values) {
        if (values == null || values.size() != NUM_BARS) {
            return;
        }
        this.values = values;
        // Calculate max value for scaling (at least 1 to avoid division by zero)
        float max = 0;
        for (int v : values) {
            if (v > max) max = v;
        }
        maxValue = Math.max(max, 1f);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) return;

        float paddingLeft = 16 * density;
        float paddingRight = 16 * density;
        float paddingTop = 20 * density;
        float paddingBottom = 28 * density;

        float chartWidth = width - paddingLeft - paddingRight;
        float chartHeight = height - paddingTop - paddingBottom;

        if (chartWidth <= 0 || chartHeight <= 0) return;

        float barSpacing = 6 * density;
        float totalSpacing = barSpacing * (NUM_BARS - 1);
        float barWidth = (chartWidth - totalSpacing) / NUM_BARS;
        float barRadius = 4 * density;

        // Draw background track for each bar
        for (int i = 0; i < NUM_BARS; i++) {
            float barLeft = paddingLeft + i * (barWidth + barSpacing);
            float barTop = paddingTop;
            float barRight = barLeft + barWidth;
            float barBottom = paddingTop + chartHeight;

            // Background track
            barRect.set(barLeft, barTop, barRight, barBottom);
            canvas.drawRoundRect(barRect, barRadius, barRadius, trackPaint);
        }

        // Draw bars
        for (int i = 0; i < NUM_BARS; i++) {
            int value = values.get(i);
            float barHeight = (value / maxValue) * chartHeight;

            float barLeft = paddingLeft + i * (barWidth + barSpacing);
            float barTop = paddingTop + chartHeight - barHeight;
            float barRight = barLeft + barWidth;
            float barBottom = paddingTop + chartHeight;

            // Gradient per bar
            LinearGradient gradient = new LinearGradient(
                    barLeft, barTop, barLeft, barBottom,
                    barColorStart, barColorEnd,
                    Shader.TileMode.CLAMP
            );
            barPaint.setShader(gradient);

            // Rounded rect bar
            barRect.set(barLeft, barTop, barRight, barBottom);
            canvas.drawRoundRect(barRect, barRadius, barRadius, barPaint);

            // Percentage text above bar (only if value > 0)
            if (value > 0) {
                float textY = barTop - 4 * density;
                canvas.drawText(String.valueOf(value) + "%",
                        barLeft + barWidth / 2f,
                        textY,
                        percentPaint);
            }

            // Day label below bar
            float labelY = paddingTop + chartHeight + 16 * density;
            canvas.drawText(DAY_LABELS[i],
                    barLeft + barWidth / 2f,
                    labelY,
                    labelPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) (300 * density);
        int desiredHeight = (int) (160 * density);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }
}
