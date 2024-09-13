package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons

@Composable
fun STDropdown(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .noRippleEffect { onClick() },
        shape = CircleShape,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        color = Color.Transparent
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = SpendTrackIcons.dropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 2.dp)
            )
        }
    }
}

@Preview(device = "id:Nexus S", showBackground = true)
@Composable
fun STDropdownPreview() {
    STDropdown(
        text = "Categories",
        onClick = {}
    )
}