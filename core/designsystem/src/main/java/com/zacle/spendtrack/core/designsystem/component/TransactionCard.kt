package com.zacle.spendtrack.core.designsystem.component

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.model.util.convertInstantToLocalDateTime
import com.zacle.spendtrack.core.model.util.dateToString
import com.zacle.spendtrack.core.shared_resources.R
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days
import android.graphics.Color as AndroidColor

enum class TransactionType {
    INCOME,
    EXPENSE
}

@Composable
fun TransactionCard(
    category: Category,
    currencySymbol: String,
    transactionName: String,
    amount: Int,
    transactionDate: Instant,
    type: TransactionType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(AndroidColor.parseColor(category.color))
    STOutline(
        modifier = modifier
            .fillMaxWidth()
            .noRippleEffect { onClick() }
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
                        painter = painterResource(
                            id = CategoryKeyResource.getIconResourceForCategory(
                                categoryKey = category.key
                            )
                        ),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = CategoryKeyResource.getStringResourceForCategory(
                        context = LocalContext.current,
                        categoryKey = category.key
                    ),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = transactionName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                val amountText = if (type == TransactionType.INCOME) "+$amount" else "-$amount"
                val amountColor =
                    if (type == TransactionType.INCOME)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                Text(
                    text = amountText + currencySymbol,
                    style = MaterialTheme.typography.bodySmall,
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dateToString(convertInstantToLocalDateTime(transactionDate)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun EmptyTransaction(
    text: String,
    iconResId: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 10.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(42.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmptyTransactionPreview() {
    SpendTrackTheme {
        EmptyTransaction(
            iconResId = R.drawable.no_transaction,
            text = stringResource(id = R.string.no_transactions)
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun TransactionCardPreview() {
    SpendTrackTheme {
        TransactionCard(
            category = Category(
                categoryId = "1",
                key = "savings_investments",
                icon = R.drawable.savings_investment,
                color = "#FF7043"
            ),
            transactionName = "Restaurants",
            amount = 75,
            transactionDate = Clock.System.now().minus(4.days),
            type = TransactionType.EXPENSE,
            onClick = {},
            currencySymbol = ".د.ب"
        )
    }
}