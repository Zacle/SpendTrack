package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zacle.spendtrack.core.shared_resources.R
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration.Companion.days

@Composable
fun PeriodPicker(
    selectedPeriod: Instant,
    onSelectedPeriodChanged: (Instant) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDate = selectedPeriod.toLocalDateTime(TimeZone.currentSystemDefault())

    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    var transactionYear by remember { mutableIntStateOf(selectedDate.year) }
    var transactionMonth by remember { mutableIntStateOf(selectedDate.month.value) }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { transactionYear -= 1 },
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                    AnimatedContent(
                        targetState = transactionYear,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                        },
                        modifier = Modifier.align(Alignment.Center),
                        label = "Year Animation"
                    ) { targetYear ->
                        Text(
                            text = "$targetYear",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    IconButton(
                        onClick = { transactionYear += 1 },
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                        enabled = transactionYear < currentDate.year,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Month.entries) { month ->
                        val isSelected = month.value == transactionMonth

                        // Animate the background and content colors
                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            label = "",
                            animationSpec = tween(500)
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "",
                            animationSpec = tween(500)
                        )

                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = backgroundColor,
                            contentColor = contentColor
                            ,
                            modifier = Modifier
                                .noRippleEffect {
                                    transactionMonth = month.value
                                }
                        ) {
                            Text(
                                text = month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(16.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier
                                .noRippleEffect { onDismissRequest() }
                        ) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .noRippleEffect {
                                    onSelectedPeriodChanged(
                                        selectedDate
                                            .toJavaLocalDateTime()
                                            .withMonth(transactionMonth)
                                            .withYear(transactionYear)
                                            .toKotlinLocalDateTime()
                                            .toInstant(TimeZone.currentSystemDefault())
                                    )
                                    onDismissRequest()
                                }
                        ) {
                            Text(
                                text = stringResource(id = R.string.done),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PeriodPickerPreview() {
    MaterialTheme {
        PeriodPicker(
            selectedPeriod = Clock.System.now().minus(689.days),
            onSelectedPeriodChanged = {},
            onDismissRequest = {}
        )
    }
}