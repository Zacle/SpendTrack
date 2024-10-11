package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme

@Composable
fun TransactionDateFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
            selectedLabelColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = CircleShape,
        border = null,
        modifier = modifier
    )
}

@Preview(device = "id:Nexus S", showBackground = true)
@Composable
fun TransactionDateFilterChipPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        TransactionDateFilterChip(
            text = "Month",
            selected = true,
            onClick = {  }
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true)
@Composable
fun TransactionDateFilterChipPreviewNot(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        TransactionDateFilterChip(
            text = "Week",
            selected = false,
            onClick = {  }
        )
    }
}