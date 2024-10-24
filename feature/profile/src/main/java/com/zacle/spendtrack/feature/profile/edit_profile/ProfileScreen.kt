package com.zacle.spendtrack.feature.profile.edit_profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zacle.spendtrack.core.designsystem.component.PicturePickerModalBottomSheet
import com.zacle.spendtrack.core.designsystem.component.STOutlinedTextField
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.model.ImageData
import com.zacle.spendtrack.core.shared_resources.R
import java.io.File

@Composable
fun ProfileRoute(
    navigateUp: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val stateHolder by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collect { event ->
            when (event) {
                is ProfileUiEvent.NavigateToLogin -> navigateToLogin()
                is ProfileUiEvent.BlankNameError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
                is ProfileUiEvent.InvalidNameError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
                is ProfileUiEvent.ShortNameError -> {
                    showSnackbar(snackbarHostState, context.getString(event.messageResId))
                }
            }
        }
    }

    ProfileScreen(
        stateHolder = stateHolder,
        onFirstNameChanged = { viewModel.submitAction(ProfileUiAction.OnFirstNameChanged(it)) },
        onLastNameChanged = { viewModel.submitAction(ProfileUiAction.OnLastNameChanged(it)) },
        onSaveClicked = { viewModel.submitAction(ProfileUiAction.OnSaveClicked) },
        onProfileChanged = { viewModel.submitAction(ProfileUiAction.OnProfileSelected(it)) },
        navigateUp = navigateUp,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

internal suspend fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
    snackbarHostState.showSnackbar(message = message)
}

@Composable
fun ProfileScreen(
    stateHolder: ProfileUiState,
    snackbarHostState: SnackbarHostState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onProfileChanged: (ImageData?) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.edit_profile))
                },
                navigationIcon = {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable { navigateUp() }
                    )
                }
            )
        },
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val contentPadding = Modifier.padding(innerPadding)
        ProfileContent(
            stateHolder = stateHolder,
            onFirstNameChanged = onFirstNameChanged,
            onLastNameChanged = onLastNameChanged,
            onSaveClicked = onSaveClicked,
            onProfileChanged = onProfileChanged,
            modifier = contentPadding
        )
    }
}

@Composable
fun ProfileContent(
    stateHolder: ProfileUiState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onProfileChanged: (ImageData?) -> Unit,
    modifier: Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        PreferencesHeader(
            userName = stateHolder.firstName + " " + stateHolder.lastName,
            profileUriImageData = stateHolder.profileImage,
            onProfileChanged = onProfileChanged
        )
        Spacer(modifier = Modifier.height(24.dp))
        ProfileBody(
            stateHolder = stateHolder,
            onFirstNameChanged = onFirstNameChanged,
            onLastNameChanged = onLastNameChanged
        )
        Spacer(modifier = Modifier.weight(1f))
        SpendTrackButton(
            text = stringResource(id = R.string.save),
            onClick = onSaveClicked,
            isUploading = stateHolder.isSaving
        )
    }
}

@Composable
fun PreferencesHeader(
    userName: String,
    profileUriImageData: ImageData?,
    onProfileChanged: (ImageData?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showProfileDialog by remember { mutableStateOf(false) }
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
                .size(100.dp)
        ) {
            if (profileUriImageData == null) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
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
                                .size(100.dp)
                        )
                    }
                    is ImageData.BitmapImage -> {
                        val bitmap = profileUriImageData.bitmap
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
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
                                .size(100.dp)
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
            onClick = { showProfileDialog = true },
        ) {
            Text(
                text = stringResource(R.string.change_profile),
                style = MaterialTheme.typography.labelLarge
            )
        }
        TextButton(
            onClick = { onProfileChanged(null) },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.error.copy(0.4f)
            )
        ) {
            Text(
                text = stringResource(R.string.remove_profile),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    if (showProfileDialog) {
        PicturePickerModalBottomSheet(
            onAttachmentSelected = {
                if (profileUriImageData == null)
                    onProfileChanged(it)
                showProfileDialog = false
            },
            dismissPicturePickerDialog = { showProfileDialog = false }
        )
    }
}

@Composable
fun ProfileBody(
    stateHolder: ProfileUiState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        STOutlinedTextField(
            name = stateHolder.firstName,
            placeholder = stringResource(id = R.string.first_name),
            onValueChange = { onFirstNameChanged(it) },
            errorResId = stateHolder.firstNameError?.errorMessageResId
        )
        STOutlinedTextField(
            name = stateHolder.lastName,
            placeholder = stringResource(id = R.string.last_name),
            onValueChange = { onLastNameChanged(it) },
            errorResId = stateHolder.lastNameError?.errorMessageResId
        )
        STOutlinedTextField(
            name = stateHolder.email,
            placeholder = stringResource(id = R.string.email),
            onValueChange = { },
            enabled = false
        )
    }
}