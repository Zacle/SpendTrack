package com.zacle.spendtrack.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.shared_resources.R as SharedR

const val TOP_APP_BAR_PADDING = 64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun STTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actionIcon: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    navigationIconContentColor: Color = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = { actionIcon() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            titleContentColor = titleContentColor,
            navigationIconContentColor = navigationIconContentColor,
            actionIconContentColor = actionIconContentColor
        ),
        modifier = modifier
            .padding(horizontal = 16.dp)
    )
}

@Preview(device = "spec:id=reference_foldable,shape=Normal,width=673,height=841,unit=dp,dpi=420", showBackground = true)
@Composable
fun STTopAppBarPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        STTopAppBar(
            title = {
                Text(text = stringResource(id = SharedR.string.google_auth))
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = SpendTrackIcons.arrowBack,
                        contentDescription = null
                    )
                }
            },
            actionIcon = {}
        )
    }
}