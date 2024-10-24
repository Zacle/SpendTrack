package com.zacle.spendtrack.feature.profile.edit_profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.common.util.ImageStorageManager
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.domain.user.UpdateUserUseCase
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.ext.isNameLengthValid
import com.zacle.spendtrack.core.ui.ext.isValidName
import com.zacle.spendtrack.core.ui.types.NameError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val imageStorageManager: ImageStorageManager,
): BaseViewModel<Unit, UiState<Unit>, ProfileUiAction, ProfileUiEvent>() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeUserAuthState()
        }
    }

    override fun initState(): UiState<Unit> = UiState.Loading

    override fun handleAction(action: ProfileUiAction) {
        when (action) {
            is ProfileUiAction.OnProfileSelected ->
                _uiState.value = uiState.value.copy(profileImage = action.profileImage)
            is ProfileUiAction.OnFirstNameChanged ->
                _uiState.value = uiState.value.copy(firstName = action.firstName)
            is ProfileUiAction.OnLastNameChanged ->
                _uiState.value = uiState.value.copy(lastName = action.lastName)
            is ProfileUiAction.OnSaveClicked -> onSaveClicked()
        }
    }

    private suspend fun observeUserAuthState() {
        getUserUseCase.execute(GetUserUseCase.Request)
            .collectLatest { result ->
                if (result is Result.Success) {
                    val user = result.data.user
                    if (user != null) {
                        _uiState.value = uiState.value.copy(
                            user = user,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            email = user.email,
                            profileImage =
                            when {
                                user.profilePictureUrl != null -> ImageData.UriImage(Uri.parse(user.profilePictureUrl))
                                user.localProfilePictureUrl != null -> ImageData.LocalPathImage(
                                    user.localProfilePictureUrl!!
                                )
                                else -> null
                            }
                        )
                    } else {
                        submitSingleEvent(ProfileUiEvent.NavigateToLogin)
                    }
                } else {
                    submitSingleEvent(ProfileUiEvent.NavigateToLogin)
                }
            }
    }

    private fun onSaveClicked() {
        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName

        formValidation(firstName, lastName)

        val errors = listOf(
            _uiState.value.firstNameError,
            _uiState.value.lastNameError
        )
        if (errors.any { it != null }) return

        _uiState.value = _uiState.value.copy(isSaving = true)

        val user = _uiState.value.user
        var didProfilePictureChange = false
        if (_uiState.value.profileImage != null) {
            val imageData = _uiState.value.profileImage
            didProfilePictureChange = when (imageData) {
                is ImageData.UriImage -> imageData.uri.toString() != user?.profilePictureUrl
                is ImageData.LocalPathImage -> imageData.path != user?.localProfilePictureUrl
                else -> true
            }
        } else {
            didProfilePictureChange = true
        }

        viewModelScope.launch {
            var localProfilePictureUrl: String? = user?.localProfilePictureUrl
            if (didProfilePictureChange) {
                // Delete the old profile picture
                user?.localProfilePictureUrl?.let {
                    imageStorageManager.deleteImageLocally(it)
                }

                // Save the new profile picture
                localProfilePictureUrl = _uiState.value.profileImage?.let {
                    imageStorageManager.saveImageLocally(it, "profile_${System.currentTimeMillis()}")
                }
            }

            val updateUser = user?.copy(
                firstName = firstName,
                lastName = lastName,
                profilePictureUrl = if (didProfilePictureChange) null else user.profilePictureUrl,
                localProfilePictureUrl = localProfilePictureUrl
            )
            if (updateUser != null) {
                updateUserUseCase.execute(UpdateUserUseCase.Request(updateUser))
                _uiState.value = _uiState.value.copy(isSaving = false)

            }
        }
    }

    private fun formValidation(firstName: String, lastName: String) {
        verifyFirstName(firstName)
        verifyLastName(lastName)
    }

    private fun verifyFirstName(name: String) {
        if (name.isBlank()) {
            _uiState.value = uiState.value.copy(firstNameError = NameError.BLANK)
            submitSingleEvent(ProfileUiEvent.BlankNameError(NameError.BLANK.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(firstNameError = null)
        }

        if (!name.isNameLengthValid()) {
            _uiState.value = uiState.value.copy(firstNameError = NameError.SHORT)
            submitSingleEvent(ProfileUiEvent.ShortNameError(NameError.SHORT.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(firstNameError = null)
        }

        if (!name.isValidName()) {
            _uiState.value = uiState.value.copy(firstNameError = NameError.INVALID)
            submitSingleEvent(ProfileUiEvent.InvalidNameError(NameError.INVALID.errorMessageResId))
        }
        else
            _uiState.value = uiState.value.copy(firstNameError = null)
    }

    private fun verifyLastName(name: String) {
        if (name.isBlank()) {
            _uiState.value = uiState.value.copy(lastNameError = NameError.BLANK)
            submitSingleEvent(ProfileUiEvent.BlankNameError(NameError.BLANK.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(lastNameError = null)
        }

        if (!name.isNameLengthValid()) {
            _uiState.value = uiState.value.copy(lastNameError = NameError.SHORT)
            submitSingleEvent(ProfileUiEvent.ShortNameError(NameError.SHORT.errorMessageResId))
        } else {
            _uiState.value = uiState.value.copy(lastNameError = null)
        }

        if (!name.isValidName()) {
            _uiState.value = uiState.value.copy(lastNameError = NameError.INVALID)
            submitSingleEvent(ProfileUiEvent.InvalidNameError(NameError.INVALID.errorMessageResId))
        }
        else
            _uiState.value = uiState.value.copy(lastNameError = null)

    }
}