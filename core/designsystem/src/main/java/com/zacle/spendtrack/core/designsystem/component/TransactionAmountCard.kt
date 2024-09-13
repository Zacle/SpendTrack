package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme

@Composable
fun TransactionAmountCard(
    text: String,
    amount: Int,
    color: Color,
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp),
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.surface,
                )
                Text(
                    text = "$$amount",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun TransactionAmountPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        TransactionAmountCard(
            text = "Income",
            amount = 18000,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            painter = painterResource(id = SpendTrackIcons.addIncome)
        )
    }
}