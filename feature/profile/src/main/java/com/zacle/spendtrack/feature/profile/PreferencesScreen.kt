package com.zacle.spendtrack.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.component.noRippleEffect
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.getCurrencies
import com.zacle.spendtrack.core.designsystem.util.getLanguages
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.model.ThemeAppearance
import com.zacle.spendtrack.core.shared_resources.R
import java.io.File

@Composable
fun PreferencesRoute(
    onBackClick: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToEditProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                PreferencesUiEvent.NavigateToLogin -> navigateToLogin()
            }
        }
    }

    PreferencesScreen(
        stateHolder = stateHolder,
        onBackClick = onBackClick,
        onEditProfileClick = navigateToEditProfile,
        snackbarHostState = snackbarHostState,
        onSetLanguagePressed = { viewModel.submitAction(PreferencesUiAction.OnLanguagePressed) },
        onSetCurrencyPressed = { viewModel.submitAction(PreferencesUiAction.OnCurrencyPressed) },
        onSetThemePressed = { viewModel.submitAction(PreferencesUiAction.OnThemePressed) },
        onLogoutPressed = { viewModel.submitAction(PreferencesUiAction.OnLogoutPressed) },
        onSetLanguageDismissed = { viewModel.submitAction(PreferencesUiAction.OnLanguageDismissed) },
        onSetCurrencyDismissed = { viewModel.submitAction(PreferencesUiAction.OnCurrencyDismissed) },
        onSetThemeDismissed = { viewModel.submitAction(PreferencesUiAction.OnThemeDismissed) },
        onSetLanguageConfirmed = { viewModel.submitAction(PreferencesUiAction.OnLanguageConfirmed(it)) },
        onSetCurrencyConfirmed = { viewModel.submitAction(PreferencesUiAction.OnCurrencyConfirmed(it)) },
        onSetThemeConfirmed = { viewModel.submitAction(PreferencesUiAction.OnThemeAppearanceConfirmed(it)) },
        modifier = modifier
    )
}

@Composable
internal fun PreferencesScreen(
    stateHolder: PreferencesUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onSetLanguagePressed: () -> Unit,
    onSetCurrencyPressed: () -> Unit,
    onSetThemePressed: () -> Unit,
    onLogoutPressed: () -> Unit,
    onSetLanguageDismissed: () -> Unit,
    onSetCurrencyDismissed: () -> Unit,
    onSetThemeDismissed: () -> Unit,
    onSetLanguageConfirmed: (String) -> Unit,
    onSetCurrencyConfirmed: (String) -> Unit,
    onSetThemeConfirmed: (ThemeAppearance) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = { Text(text = stringResource(R.string.profile)) },
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.noRippleEffect { onBackClick() }
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // Get the bottom navigation bar insets (padding)
        val bottomPadding = innerPadding.calculateBottomPadding().minus(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        val contentPadding = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = bottomPadding
        )
        PreferencesContent(
            userName = stateHolder.name,
            receiptUriImageData = stateHolder.photoImage,
            onEditProfileClick = onEditProfileClick,
            onLanguageClick = onSetLanguagePressed,
            onCurrencyClick = onSetCurrencyPressed,
            onThemeClick = onSetThemePressed,
            onLogoutClick = onLogoutPressed,
            modifier = contentPadding
        )
    }

    if (stateHolder.isThemeDialogOpen) {
        ThemePreferences(
            themeAppearance = stateHolder.themeAppearance,
            onThemeAppearanceSelected = onSetThemeConfirmed,
            onDismissRequest = onSetThemeDismissed
        )
    }
    if (stateHolder.isCurrencyDialogOpen) {
        CurrencyPreferences(
            currency = stateHolder.currencyCode,
            onSelectedCurrencyChanged = onSetCurrencyConfirmed,
            onDismissRequest = onSetCurrencyDismissed
        )
    }
    if (stateHolder.isLanguageDialogOpen) {
        LanguagePreferences(
            language = stateHolder.languageCode,
            onSelectedLanguageChanged = onSetLanguageConfirmed,
            onDismissRequest = onSetLanguageDismissed
        )
    }
}

@Composable
fun PreferencesContent(
    userName: String,
    receiptUriImageData: ImageData?,
    onEditProfileClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onThemeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.25f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)),
            contentAlignment = Alignment.Center
        ) {
            PreferencesHeader(
                userName = userName,
                profileUriImageData = receiptUriImageData,
                onEditProfileClick = onEditProfileClick
            )
        }
        PreferencesBody(
            onLanguageClick = onLanguageClick,
            onCurrencyClick = onCurrencyClick,
            onThemeClick = onThemeClick,
            onLogoutClick = onLogoutClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 12.dp)
        )
    }
}

@Composable
fun PreferencesHeader(
    userName: String,
    profileUriImageData: ImageData?,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .size(80.dp)
        ) {
            if (profileUriImageData == null) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                )
            } else {
                when (profileUriImageData) {
                    is ImageData.UriImage -> {
                        val uri = profileUriImageData.uri
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                        )
                    }
                    is ImageData.BitmapImage -> {
                        val bitmap = profileUriImageData.bitmap
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                        )
                    }
                    is ImageData.LocalPathImage -> {
                        val path = profileUriImageData.path
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(path))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Local Receipt Image",
                            modifier = Modifier
                                .size(80.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(
            onClick = onEditProfileClick,
        ) {
            Text(
                text = stringResource(R.string.edit),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun PreferencesBody(
    onLanguageClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onThemeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                text = stringResource(R.string.personal_settings),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
            STSettingsRow(
                icon = painterResource(id = R.drawable.language),
                text = stringResource(R.string.language),
                onClick = onLanguageClick
            )
            STSettingsRow(
                icon = painterResource(id = R.drawable.currency),
                text = stringResource(R.string.currency),
                onClick = onCurrencyClick
            )
            STSettingsRow(
                icon = painterResource(id = R.drawable.theme),
                text = stringResource(R.string.theme),
                onClick = onThemeClick
            )
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(
                onClick = onLogoutClick,
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.logout),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun STSettingsRow(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePreferences(
    language: String,
    onSelectedLanguageChanged: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val languages = getLanguages(context)

    var selectedLanguage by remember { mutableStateOf(language) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_language),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                )
                TextButton(
                    onClick = { onSelectedLanguageChanged(selectedLanguage) }
                ) {
                    Text(
                        text = stringResource(R.string.done)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                items(languages) { language ->
                    PreferencesItem(
                        flagIconResId = language.flagResId,
                        country = language.languageName,
                        isSelected = selectedLanguage == language.code,
                        onItemSelected = { selectedLanguage = language.code },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPreferences(
    currency: String,
    onSelectedCurrencyChanged: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencies = getCurrencies(context)

    var selectedCurrency by remember { mutableStateOf(currency) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_currency),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                )
                TextButton(
                    onClick = { onSelectedCurrencyChanged(selectedCurrency) }
                ) {
                    Text(
                        text = stringResource(R.string.done)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                items(currencies) { currency ->
                    PreferencesItem(
                        flagIconResId = currency.flagResId,
                        country = "${currency.symbol} - ${currency.name}",
                        isSelected = selectedCurrency == currency.code,
                        onItemSelected = { selectedCurrency = currency.code },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            SpendTrackButton(
                text = stringResource(R.string.done),
                onClick = { onSelectedCurrencyChanged(selectedCurrency) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun PreferencesItem(
    flagIconResId: Int,
    country: String,
    isSelected: Boolean,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemSelected(country) },
        shape = MaterialTheme.shapes.small,
        tonalElevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onItemSelected(country) }
            )
            Image(
                painter = painterResource(flagIconResId),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = country,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ThemePreferences(
    themeAppearance: ThemeAppearance,
    onThemeAppearanceSelected: (ThemeAppearance) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTheme by remember { mutableStateOf(themeAppearance) }

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 5.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.select_theme),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                    )
                    TextButton(
                        onClick = { onThemeAppearanceSelected(selectedTheme) }
                    ) {
                        Text(
                            text = stringResource(R.string.done)
                        )
                    }
                }
                Surface(
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable { selectedTheme = ThemeAppearance.DARK },
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = 5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedTheme == ThemeAppearance.DARK,
                            onClick = { selectedTheme = ThemeAppearance.DARK }
                        )
                        Text(
                            text = stringResource(R.string.dark),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Surface(
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable { selectedTheme = ThemeAppearance.LIGHT },
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = 5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedTheme == ThemeAppearance.LIGHT,
                            onClick = { selectedTheme = ThemeAppearance.LIGHT }
                        )
                        Text(
                            text = stringResource(R.string.light),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Surface(
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable { selectedTheme = ThemeAppearance.FOLLOW_SYSTEM },
                    shape = MaterialTheme.shapes.small,
                    tonalElevation = 5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedTheme == ThemeAppearance.FOLLOW_SYSTEM,
                            onClick = { selectedTheme = ThemeAppearance.FOLLOW_SYSTEM }
                        )
                        Text(
                            text = stringResource(R.string.system_default),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreferencesHeaderPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        PreferencesContent(
            userName = "Anna Smith",
            receiptUriImageData = null,
            onEditProfileClick = {},
            onLanguageClick = {},
            onCurrencyClick = {},
            onThemeClick = {},
            onLogoutClick = {}
        )
    }
}