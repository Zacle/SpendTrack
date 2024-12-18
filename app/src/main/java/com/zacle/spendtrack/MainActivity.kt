package com.zacle.spendtrack

import android.app.LocaleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.common.util.TimeZoneMonitor
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.getCurrencies
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.composition_local.LocalCurrency
import com.zacle.spendtrack.core.ui.composition_local.LocalTimeZone
import com.zacle.spendtrack.data.UserStateModel
import com.zacle.spendtrack.ui.STApp
import com.zacle.spendtrack.ui.rememberSTAppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: UiState<UserStateModel> by mutableStateOf(UiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow
                    .onEach { uiState = it }
                    .collect()
            }
        }

        /**
         * Keep the splash screen on-screen until the UI state is loaded. This condition is
         * evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
         * the UI.
         */
        splashScreen.setKeepOnScreenCondition {
            uiState == UiState.Loading
        }

        enableEdgeToEdge()

        setContent {
            val darkTheme =
                when (uiState) {
                    UiState.Loading -> isSystemInDarkTheme()
                    is UiState.Error -> isSystemInDarkTheme()
                    is UiState.Success -> {
                        val themeAppearance = (uiState as UiState.Success<UserStateModel>).data.userData.themeAppearance
                        val isDarkTheme =
                            when (themeAppearance) {
                                ThemeAppearance.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                                ThemeAppearance.LIGHT -> false
                                ThemeAppearance.DARK -> true
                            }
                        isDarkTheme
                    }
                }

            // Update the edge to edge configuration to match the theme
            // This is the same parameters as the default enableEdgeToEdge call, but we manually
            // resolve whether or not to show dark theme using uiState, since it can be different
            // than the configuration's dark theme value based on the user preference.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }

            val appState = rememberSTAppState(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()
            
            CompositionLocalProvider(
                LocalTimeZone provides currentTimeZone
            ) {
                if (uiState is UiState.Success) {
                    val currencyCode = (uiState as UiState.Success<UserStateModel>).data.userData.currencyCode
                    val currencies = getCurrencies(this)
                    val currency = currencies.find { it.code == currencyCode }?.symbol ?: "$"

                    val languageCode = (uiState as UiState.Success<UserStateModel>).data.userData.languageCode

                    val context = LocalContext.current
                    LaunchedEffect(languageCode) {
                        setLanguage(context, languageCode)
                    }

                    CompositionLocalProvider(
                        LocalCurrency provides currency
                    ) {
                        SpendTrackTheme(darkTheme = darkTheme) {
                            STApp(
                                appState = appState,
                                userStateModel = (uiState as UiState.Success<UserStateModel>).data,
                                onRestartApp = {
                                    val packageManager: PackageManager = context.packageManager
                                    val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                                    val componentName: ComponentName = intent.component!!
                                    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                                    context.startActivity(restartIntent)
                                    Runtime.getRuntime().exit(0)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun setLanguage(context: Context, languageCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(languageCode)
    } else {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)