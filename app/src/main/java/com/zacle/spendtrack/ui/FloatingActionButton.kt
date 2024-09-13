package com.zacle.spendtrack.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.R
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons

@Composable
fun ExpandedFloatingActionButton(
    painter: Painter,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = color,
        shape = CircleShape
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun STFloatingActionButton(
    onAddNewIncome: () -> Unit,
    onAddNewExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = expanded, label = "expanded")
    val rotate by transition.animateFloat(
        label = "rotation",
        transitionSpec = { tween(durationMillis = 500, easing = FastOutSlowInEasing) }
    ) {
        if (expanded) 315f else 0f
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically() + slideInVertically(),
            exit = fadeOut() + shrinkVertically() + slideOutVertically()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExpandedFloatingActionButton(
                    painter = painterResource(id = SpendTrackIcons.addIncome),
                    onClick = onAddNewIncome,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                Spacer(modifier = Modifier.width(24.dp))
                ExpandedFloatingActionButton(
                    painter = painterResource(id = SpendTrackIcons.addExpense),
                    onClick = onAddNewExpense,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FloatingActionButton(
            onClick = { expanded = !expanded },
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
                defaultElevation = 12.dp
            ),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                painter = painterResource(id = SpendTrackIcons.add),
                contentDescription = stringResource(R.string.add_transaction),
                tint = Color.White,
                modifier = Modifier.rotate(rotate)
            )
        }
    }
}