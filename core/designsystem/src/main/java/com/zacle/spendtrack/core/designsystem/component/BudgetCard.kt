package com.zacle.spendtrack.core.designsystem.component

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.designsystem.util.CategoryKeyResource
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.shared_resources.R

@Composable
fun BudgetCard(
    budget: Budget,
    currency: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(AndroidColor.parseColor(budget.category.color))
    val isBudgetExceeded = budget.remainingAmount <= 0
    val progress =
        if (isBudgetExceeded) 1f
        else (budget.amount - budget.remainingAmount).toFloat() / budget.amount.toFloat()
    val totalExpense =
        if (isBudgetExceeded) (budget.amount - budget.remainingAmount).toInt()
        else budget.remainingAmount.toInt()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .noRippleEffect { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
        tonalElevation = 2.dp,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CategoryChip(
                    name = CategoryKeyResource.getStringResourceForCategory(
                        context = LocalContext.current,
                        categoryKey = budget.category.key
                    ),
                    color = color,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                )
                if (isBudgetExceeded) {
                    Icon(
                        painter = painterResource(id = R.drawable.warning),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
            val remainingAmount =
                if (isBudgetExceeded) 0
                else budget.remainingAmount.toInt()
            Text(
                text = stringResource(id = R.string.remaining) + " $remainingAmount$currency",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 8.dp)
            )
            ProgressIndicator(
                progress = progress,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
            Text(
                text = "$totalExpense$currency " + stringResource(id = R.string.of) + " ${budget.amount.toInt()}$currency",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            if (isBudgetExceeded) {
                Text(
                    text = stringResource(id = R.string.exceeded_limit),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
fun ProgressIndicator(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
) {
    Canvas(
        modifier = modifier
            .height(8.dp)
    ) {
        drawLine(
            color = trackColor,
            cap = StrokeCap.Round,
            strokeWidth = size.height,
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = size.width, y = 0f)
        )
        if (progress > 0f) {
            drawLine(
                color = color,
                cap = StrokeCap.Round,
                strokeWidth = size.height,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = size.width * progress, y = 0f)
            )
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(12.dp)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun CategoryChipPreview() {
    SpendTrackTheme {
        CategoryChip(
            name = "Medical",
            color = Color(0XFFFF7043)
        )
    }
}


@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun BudgetCardPreview() {
    SpendTrackTheme {
        BudgetCard(
            budget = Budget(
                category = Category(
                    categoryId = "1",
                    key = "food_dining",
                    icon = R.drawable.food_dinning,
                    color = "#FF7043"
                ),
                amount = 1000.0,
                remainingAmount = 500.0
            ),
            onClick = {  },
            modifier = Modifier.padding(16.dp),
            currency = "$"
        )
    }
}

@Preview(device = "id:Nexus S", showBackground = true, showSystemUi = true)
@Composable
fun BudgetCardExceedPreview() {
    SpendTrackTheme {
        BudgetCard(
            budget = Budget(
                category = Category(
                    categoryId = "1",
                    key = "food_dining",
                    icon = R.drawable.travel,
                    color = "#FF7043"
                ),
                amount = 1000.0,
                remainingAmount = -200.0
            ),
            onClick = {  },
            modifier = Modifier.padding(16.dp),
            currency = "лв"
        )
    }
}