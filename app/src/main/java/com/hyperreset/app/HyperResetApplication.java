package com.hyperreset.app;

import android.app.Application;

import com.hyperreset.app.di.AppContainer;

public class HyperResetApplication extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer();
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
