package com.hyperreset.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.data.model.ProfileUpdateRequest;
import com.hyperreset.app.data.repository.AuthRepository;
import com.hyperreset.app.utils.Resource;

/**
 * ViewModel for the Edit Profile screen.
 * Loads current profile data and handles profile updates via AuthRepository.
 */
public class EditProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<AuthResponse>> profile = new MutableLiveData<>();
    private final MutableLiveData<Resource<AuthResponse>> updateResult = new MutableLiveData<>();

    // Flag to prevent UI updates after ViewModel is cleared
    private volatile boolean cleared = false;

    public EditProfileViewModel() {
        this.authRepository = new AuthRepository();
    }

    public EditProfileViewModel(AuthRepository authRepository) {
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

    public LiveData<Resource<AuthResponse>> getUpdateResult() {
        return updateResult;
    }

    /**
     * Loads the current user profile data from the backend.
     * Used to pre-fill the form fields.
     */
    public void loadProfile() {
        profile.setValue(Resource.loading());
        authRepository.getProfile(result -> {
            if (!cleared) {
                profile.setValue(result);
            }
        });
    }

    /**
     * Updates the user profile with the given field values.
     *
     * @param nombres          First name(s)
     * @param apellidos        Last name(s)
     * @param correo           Email address
     * @param telefono         Phone number
     * @param direccion        Street address
     * @param fechaNacimiento  Date of birth in "yyyy-MM-dd" format
     */
    public void updateProfile(String nombres, String apellidos,
                              String correo, String telefono,
                              String direccion, String fechaNacimiento) {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setNombres(nombres);
        request.setApellidos(apellidos);
        request.setCorreo(correo);
        request.setTelefono(telefono);
        request.setDireccion(direccion);
        request.setFechaNacimiento(fechaNacimiento);

        updateResult.setValue(Resource.loading());
        authRepository.updateProfile(request, result -> {
            if (!cleared) {
                updateResult.postValue(result);
            }
        });
    }
}
