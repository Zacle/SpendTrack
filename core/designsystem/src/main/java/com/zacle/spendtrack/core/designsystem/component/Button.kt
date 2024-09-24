package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.shared_resources.R as SharedR

@Composable
fun TertiaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .noRippleEffect { onClick() },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun SpendTrackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    isUploading: Boolean = false
) {

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        )
    ) {
        if (isUploading) {
            CircularProgressIndicator(
                color = Color.White, // Adjust the color to match your design
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(20.dp)
                    .padding(vertical = 8.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun GoogleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { if (enabled) onClick() },
        shape = RoundedCornerShape(10.dp),
        color =
        if (enabled)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = SpendTrackIcons.google),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(id = SharedR.string.google_auth),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(device = "id:Nexus S", showBackground = true)
@Composable
fun TertiaryButtonPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        TertiaryButton(
            text = "See All",
            onClick = {}
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true)
@Composable
fun SpendTrackButtonPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        SpendTrackButton(
            text = "Get Started",
            onClick = {}
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true)
@Composable
fun GoogleButtonPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        GoogleButton(
            onClick = {},
            enabled = false
        )
    }
}