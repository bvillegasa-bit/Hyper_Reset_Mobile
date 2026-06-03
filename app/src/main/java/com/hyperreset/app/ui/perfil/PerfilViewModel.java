package com.hyperreset.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.data.repository.AuthRepository;
import com.hyperreset.app.utils.Resource;

/**
 * ViewModel for the Profile screen.
 * Loads the authenticated user's profile data via AuthRepository.
 */
public class PerfilViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<AuthResponse>> profile = new MutableLiveData<>();

    // Flag to prevent UI updates after ViewModel is cleared
    private volatile boolean cleared = false;

    public PerfilViewModel() {
        this.authRepository = new AuthRepository();
    }

    public PerfilViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleared = true;
    }

    public LiveData<Resource<AuthResponse>> getProfile() {
        return profile;
    }

    /**
     * Returns the phone number from the current profile data, or null if not loaded.
     */
    public String getTelefono() {
        if (profile.getValue() != null && profile.getValue().data != null) {
            return profile.getValue().data.getTelefono();
        }
        return null;
    }

    /**
     * Loads the authenticated user's profile from the backend.
     * Sets loading state immediately, then updates on success/error.
     */
    public void loadProfile() {
        profile.setValue(Resource.loading());
        authRepository.getProfile(result -> profile.setValue(result));
    }

    /**
     * Logs out the current user by clearing the stored auth token.
     * Navigation to LoginActivity is handled by the Fragment.
     */
    public void logout() {
        authRepository.logout();
    }
}
