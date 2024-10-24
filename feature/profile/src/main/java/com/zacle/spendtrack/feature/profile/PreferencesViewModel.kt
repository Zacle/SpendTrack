package com.zacle.spendtrack.feature.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.zacle.spendtrack.core.domain.auth.SignOutUseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.domain.datastore.SetCurrencyCodeUseCase
import com.zacle.spendtrack.core.domain.datastore.SetLanguageCodeUseCase
import com.zacle.spendtrack.core.domain.datastore.SetThemeAppearanceUseCase
import com.zacle.spendtrack.core.domain.user.GetUserUseCase
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.model.usecase.Result
import com.zacle.spendtrack.core.ui.BaseViewModel
import com.zacle.spendtrack.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val setCurrencyCodeUseCase: SetCurrencyCodeUseCase,
    private val setLanguageCodeUseCase: SetLanguageCodeUseCase,
    private val setThemeAppearanceUseCase: SetThemeAppearanceUseCase
): BaseViewModel<Unit, UiState<Unit>, PreferencesUiAction, PreferencesUiEvent>() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    override fun initState(): UiState<Unit> = UiState.Loading

    override fun handleAction(action: PreferencesUiAction) {
        when (action) {
            PreferencesUiAction.OnLanguagePressed -> {
                _uiState.value = _uiState.value.copy(isLanguageDialogOpen = true)
            }
            PreferencesUiAction.OnLanguageDismissed -> {
                _uiState.value = _uiState.value.copy(isLanguageDialogOpen = false)
            }
            is PreferencesUiAction.OnLanguageConfirmed -> {
                setLanguageCode(action.languageCode)
                _uiState.value = _uiState.value.copy(isLanguageDialogOpen = false)
            }
            PreferencesUiAction.OnCurrencyPressed -> {
                _uiState.value = _uiState.value.copy(isCurrencyDialogOpen = true)
            }
            PreferencesUiAction.OnCurrencyDismissed -> {
                _uiState.value = _uiState.value.copy(isCurrencyDialogOpen = false)
            }
            is PreferencesUiAction.OnCurrencyConfirmed -> {
                setCurrencyCode(action.currencyCode)
                _uiState.value = _uiState.value.copy(isCurrencyDialogOpen = false)
                _uiState.value = _uiState.value.copy(isCurrencyDialogOpen = false)
            }
            PreferencesUiAction.OnThemePressed -> {
                _uiState.value = _uiState.value.copy(isThemeDialogOpen = true)
            }
            PreferencesUiAction.OnThemeDismissed -> {
                _uiState.value = _uiState.value.copy(isThemeDialogOpen = false)
            }
            is PreferencesUiAction.OnThemeAppearanceConfirmed -> {
                setThemeAppearance(action.themeAppearance)
                _uiState.value = _uiState.value.copy(isThemeDialogOpen = false)
            }
            PreferencesUiAction.OnLogoutPressed -> {
                logout()
            }
        }
    }

    private fun load() {
        runViewModelScope {
            combine(
                getUserUseCase.execute(GetUserUseCase.Request),
                getUserDataUseCase.execute(GetUserDataUseCase.Request)
            ) { userResponse, userDataResponse ->
                if (userResponse !is Result.Success || userDataResponse !is Result.Success) {
                    submitSingleEvent(PreferencesUiEvent.NavigateToLogin)
                }
                val user = (userResponse as Result.Success).data.user
                val userData = (userDataResponse as Result.Success).data.userData

                Pair(user, userData)
            }.collectLatest {
                val (user, userData) = it
                if (user == null) {
                    submitSingleEvent(PreferencesUiEvent.NavigateToLogin)
                } else {
                    _uiState.value = _uiState.value.copy(
                        name = user.firstName + " " + user.lastName,
                        photoImage =
                            when {
                                user.profilePictureUrl != null -> ImageData.UriImage(Uri.parse(user.profilePictureUrl))
                                user.localProfilePictureUrl != null -> ImageData.LocalPathImage(
                                    user.localProfilePictureUrl!!
                                )
                                else -> null
                            }
                    )
                }
                _uiState.value = _uiState.value.copy(
                    languageCode = userData.languageCode,
                    currencyCode = userData.currencyCode,
                    themeAppearance = userData.themeAppearance
                )
            }
        }
    }

    private fun setLanguageCode(languageCode: String) {
        runViewModelScope {
            setLanguageCodeUseCase.execute(SetLanguageCodeUseCase.Request(languageCode)).collect()
        }
    }

    private fun setCurrencyCode(currencyCode: String) {
        runViewModelScope {
            setCurrencyCodeUseCase.execute(SetCurrencyCodeUseCase.Request(currencyCode)).collect()
        }
    }

    private fun setThemeAppearance(themeAppearance: ThemeAppearance) {
        runViewModelScope {
            setThemeAppearanceUseCase.execute(SetThemeAppearanceUseCase.Request(themeAppearance)).collect()
        }
    }

    private fun logout() {
        runViewModelScope {
            signOutUseCase.execute(SignOutUseCase.Request).collect()
        }
    }

    private fun runViewModelScope(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }
}