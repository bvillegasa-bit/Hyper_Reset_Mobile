package com.hyperreset.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.data.model.ChangePasswordRequest;
import com.hyperreset.app.data.repository.AuthRepository;
import com.hyperreset.app.utils.Resource;

/**
 * ViewModel for the Change Password screen.
 * Validates input fields and handles password change via AuthRepository.
 */
public class ChangePasswordViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<Object>> changeResult = new MutableLiveData<>();
    private final MutableLiveData<String> validationError = new MutableLiveData<>();

    // Flag to prevent UI updates after ViewModel is cleared
    private volatile boolean cleared = false;

    public ChangePasswordViewModel() {
        this.authRepository = new AuthRepository();
    }

    public ChangePasswordViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleared = true;
    }

    public LiveData<Resource<Object>> getChangeResult() {
        return changeResult;
    }

    public LiveData<String> getValidationError() {
        return validationError;
    }

    /**
     * Validates the input fields and calls the repository to change the password.
     * <p>
     * Validation rules (checked in order):
     * <ol>
     *     <li>newPassword must match confirmPassword</li>
     *     <li>newPassword must be at least 8 characters long</li>
     *     <li>newPassword must be different from currentPassword</li>
     * </ol>
     * If validation fails, {@link #validationError} is set with a string code.
     * If validation passes, the repository is called and {@link #changeResult} is updated.
     *
     * @param currentPassword The user's current password
     * @param newPassword     The desired new password
     * @param confirmPassword Confirmation of the new password
     */
    public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        // Clear previous validation error
        validationError.setValue(null);

        // Validate: new password must match confirmation
        if (!newPassword.equals(confirmPassword)) {
            validationError.setValue("password_mismatch");
            return;
        }

        // Validate: minimum length
        if (newPassword.length() < 8) {
            validationError.setValue("password_too_short");
            return;
        }

        // Validate: new password must be different from current
        if (currentPassword.equals(newPassword)) {
            validationError.setValue("same_password");
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword, confirmPassword);

        changeResult.setValue(Resource.loading());
        authRepository.changePassword(request, result -> {
            if (!cleared) {
                changeResult.postValue(result);
            }
        });
    }
}
