package com.zacle.spendtrack.core.designsystem.component

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.R
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.model.Category
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days

enum class TransactionType {
    INCOME,
    EXPENSE
}

@Composable
fun TransactionCard(
    category: Category,
    transactionName: String,
    amount: Int,
    transactionDate: Instant,
    type: TransactionType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(AndroidColor.parseColor(category.color))
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .noRippleEffect { onClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
        tonalElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp),
                tonalElevation = 5.dp
            ) {
                Box(
                    modifier = Modifier
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = category.icon),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = category.name),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transactionName,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = TextUnit(9f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                val amountText = if (type == TransactionType.INCOME) "+$$amount" else "-$$amount"
                val amountColor =
                    if (type == TransactionType.INCOME)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.labelSmall,
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateToString(convertInstantToLocalDateTime(transactionDate)),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = TextUnit(9f, TextUnitType.Sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun TransactionCardPreview() {
    SpendTrackTheme {
        TransactionCard(
            category = Category(
                categoryId = "1",
                name = R.string.food_drinks,
                icon = R.drawable.food_dinning,
                color = "#FF7043"
            ),
            transactionName = "Restaurants",
            amount = 75,
            transactionDate = Clock.System.now().minus(4.days),
            type = TransactionType.EXPENSE,
            onClick = {}
        )
    }
}