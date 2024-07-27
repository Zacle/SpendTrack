package com.zacle.spendtrack.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.ui.previews.DevicePreviews

@Composable
fun <T : Any> CommonScreen(
    state: UiState<T>,
    tryAgain: (() -> Unit)? = null,
    onSuccess: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Error -> {
            Error(state.errorMessage, tryAgain)
        }
        is UiState.Success -> {
            onSuccess(state.data)
        }
    }
}

@Composable
fun Error(
    errorMessage: String,
    tryAgain: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.error),
                contentDescription = null
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )
            if (tryAgain != null) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = tryAgain) {
                        Text(text = stringResource(id = R.string.try_again))
                    }
                }
            }
        }
    }
}

@Composable
fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

@DevicePreviews
@Composable
fun ErrorPreview() {
    SpendTrackTheme {
        SpendTrackBackground {
            Error(errorMessage = "An unknown error occurred", tryAgain = {})
        }
    }
}