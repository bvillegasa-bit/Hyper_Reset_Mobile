package com.hyperreset.app.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.hyperreset.app.R;

/**
 * A CardView with a configurable gradient background.
 * Uses startColor and endColor attributes to create a linear gradient.
 * Defaults to primary→accent gradient if no colors are specified.
 */
public class GradientCardView extends CardView {

    public GradientCardView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public GradientCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GradientCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        int startColor = getContext().getColor(R.color.hyper_primary);
        int endColor = getContext().getColor(R.color.hyper_accent);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GradientCardView);
            startColor = a.getColor(R.styleable.GradientCardView_gradientStartColor, startColor);
            endColor = a.getColor(R.styleable.GradientCardView_gradientEndColor, endColor);
            a.recycle();
        }

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor}
        );
        gradient.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        setBackground(gradient);
        setCardElevation(0f);
        setRadius(0f);
        setPreventCornerOverlap(false);
        setUseCompatPadding(false);
    }

    /**
     * Update the gradient colors at runtime.
     *
     * @param startColor Starting color resource
     * @param endColor   Ending color resource
     */
    public void setGradientColors(int startColor, int endColor) {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor}
        );
        gradient.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        setBackground(gradient);
    }
}
