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

    public PerfilViewModel() {
        this.authRepository = new AuthRepository();
    }

    public PerfilViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Resource<AuthResponse>> getProfile() {
        return profile;
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
