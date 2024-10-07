package com.zacle.spendtrack.feature.budget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zacle.spendtrack.core.designsystem.component.CategoryDropdown
import com.zacle.spendtrack.core.designsystem.component.STSlider
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.component.TransactionEntryAmount
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.model.Category
import com.zacle.spendtrack.core.shared_resources.R
import kotlin.math.min

@Composable
fun EmptyBudgetScreen(
    onCreateBudget: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val title = stringResource(if (enabled) R.string.setup_budget else R.string.budget_previous)
    val description = stringResource(
        if (enabled) R.string.setup_budget_description else R.string.budget_previous_description
    )

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
            SpendTrackButton(
                text = stringResource(R.string.get_started),
                onClick = onCreateBudget,
                enabled = enabled,
                modifier = modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CreateBudgetContent(
    amount: Int,
    categories: List<Category>,
    selectedCategoryId: String,
    receiveAlert: Boolean,
    receiveAlertPercentage: Int,
    recurrent: Boolean,
    onAmountChanged: (Int) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onReceiveAlertChanged: (Boolean) -> Unit,
    onReceiveAlertPercentageChanged: (Int) -> Unit,
    onRecurrentChanged: (Boolean) -> Unit,
    onSaveBudget: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.3f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            TransactionEntryAmount(
                onAmountChanged = onAmountChanged,
                amount = amount,
                title = stringResource(R.string.budget_amount),
                contentColor = contentColor
            )
        }
        BudgetEntry(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            receiveAlert = receiveAlert,
            receiveAlertPercentage = receiveAlertPercentage,
            recurrent = recurrent,
            onCategorySelected = onCategorySelected,
            onReceiveAlertChanged = onReceiveAlertChanged,
            onReceiveAlertPercentageChanged = onReceiveAlertPercentageChanged,
            onRecurrentChanged = onRecurrentChanged,
            onSaveBudget = onSaveBudget,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun BudgetEntry(
    categories: List<Category>,
    selectedCategoryId: String,
    receiveAlert: Boolean,
    receiveAlertPercentage: Int,
    recurrent: Boolean,
    onCategorySelected: (Category) -> Unit,
    onReceiveAlertChanged: (Boolean) -> Unit,
    onReceiveAlertPercentageChanged: (Int) -> Unit,
    onRecurrentChanged: (Boolean) -> Unit,
    onSaveBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CategoryDropdown(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.repeat),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.repeat_budget),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = recurrent,
                    onCheckedChange = onRecurrentChanged
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.receive_alert),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.receive_alert_description),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = receiveAlert,
                    onCheckedChange = onReceiveAlertChanged
                )
            }
            if (receiveAlert) {
                STSlider(
                    value = receiveAlertPercentage.toFloat(),
                    onValueChange = {
                        onReceiveAlertPercentageChanged(it.toInt())
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SpendTrackButton(
                text = stringResource(R.string.save),
                onClick = onSaveBudget,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun BudgetCircularIndicator(
    totalAmount: Float,
    remainingAmount: Float,
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 130.dp,
    strokeWidth: Dp = 15.dp,
    backgroundIndicatorColor: Color = Color.LightGray,
    progressGradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary, Color(0xFF71AA13)
    )
) {
    // Amount spent
    val amountSpent = totalAmount - remainingAmount
    // Calculate the progress as a ratio of remaining to total
    val progress =
        if (amountSpent >= totalAmount) 1f
        else amountSpent / totalAmount

    // Box to center the remaining amount text
    Box(
        modifier = modifier
            .size(indicatorSize),
        contentAlignment = Alignment.Center
    ) {
        // Draw the circular progress bar
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Define sizes and positions
            val size = min(size.width, size.height)
            val innerPadding = strokeWidth.toPx() / 2
            val radius = size / 2 - innerPadding

            // Create the gradient brush for the progress arc
            val progressGradientBrush = Brush.linearGradient(
                colors = progressGradientColors
            )

            // Draw the background circular track
            drawArc(
                color = backgroundIndicatorColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Draw the progress circular track with round cap
            drawArc(
                brush = progressGradientBrush,
                startAngle = 0f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = Offset(innerPadding, innerPadding),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        // Remaining amount text at the center of the circle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$${amountSpent.toInt()}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Text(
                text = stringResource(R.string.budget_spent),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EmptyBudgetScreenPreview() {
    SpendTrackTheme {
        EmptyBudgetScreen(onCreateBudget = {}, enabled = false)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateBudgetContentPreview() {
    SpendTrackTheme {
        CreateBudgetContent(
            amount = 0,
            categories = emptyList(),
            selectedCategoryId = "",
            receiveAlert = true,
            receiveAlertPercentage = 80,
            recurrent = false,
            onAmountChanged = {},
            onCategorySelected = {},
            onReceiveAlertChanged = {},
            onReceiveAlertPercentageChanged = {},
            onRecurrentChanged = {},
            onSaveBudget = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetCircularIndicatorPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        BudgetCircularIndicator(
            totalAmount = 3200f,
            remainingAmount = 2000f,
            modifier = modifier
        )
    }
}