package com.hyperreset.app.ui.splash.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hyperreset.app.R;

import java.util.List;

/**
 * PagerAdapter for the onboarding ViewPager.
 * Creates page views from a list of OnboardingPage models.
 */
public class OnboardingAdapter extends PagerAdapter {

    private final List<OnboardingPage> pages;

    public OnboardingAdapter(List<OnboardingPage> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.onboarding_page, container, false);

        OnboardingPage page = pages.get(position);

        ImageView imageView = view.findViewById(R.id.onboarding_image);
        TextView titleView = view.findViewById(R.id.onboarding_title);
        TextView descriptionView = view.findViewById(R.id.onboarding_description);

        imageView.setImageResource(page.getImageResId());
        titleView.setText(page.getTitleResId());
        descriptionView.setText(page.getDescriptionResId());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
