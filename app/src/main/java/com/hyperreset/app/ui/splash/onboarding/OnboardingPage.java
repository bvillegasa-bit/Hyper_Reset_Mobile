package com.hyperreset.app.ui.splash.onboarding;

/**
 * Represents a single onboarding page with title, description, and image resources.
 */
public class OnboardingPage {

    private final int imageResId;
    private final int titleResId;
    private final int descriptionResId;

    public OnboardingPage(int imageResId, int titleResId, int descriptionResId) {
        this.imageResId = imageResId;
        this.titleResId = titleResId;
        this.descriptionResId = descriptionResId;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }
}
