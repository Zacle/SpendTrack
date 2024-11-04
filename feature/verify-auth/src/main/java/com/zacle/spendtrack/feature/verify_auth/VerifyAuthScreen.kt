package com.zacle.spendtrack.feature.verify_auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.component.STTopAppBar
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.shared_resources.R
import com.zacle.spendtrack.core.ui.previews.DevicePreviews

@Composable
fun VerifyAuthRoute(
    navigateUp: () -> Unit,
    onRestartApp: () -> Unit,
    modifier: Modifier = Modifier,
) {

    VerifyAuthScreen(
        navigateUp = navigateUp,
        onRestartApp = onRestartApp,
        modifier = modifier
    )
}

@Composable
fun VerifyAuthScreen(
    navigateUp: () -> Unit,
    onRestartApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            STTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.verify_auth_title))
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
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    ) { innerPadding ->
        VerifyAuthContent(
            onRestartApp = onRestartApp,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun VerifyAuthContent(
    onRestartApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.email_verification_text),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 64.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        SpendTrackButton(
            text = stringResource(id = R.string.already_verified),
            onClick = onRestartApp
        )
    }
}

@DevicePreviews
@Composable
fun VerifyAuthContentPreview() {
    SpendTrackTheme {
        SpendTrackBackground {
            VerifyAuthContent(
                onRestartApp = {}
            )
        }
    }
}