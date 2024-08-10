package com.zacle.spendtrack.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.zacle.spendtrack.core.designsystem.R
import com.zacle.spendtrack.core.designsystem.icon.SpendTrackIcons
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun STTopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actionIcon: @Composable () -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = navigationIcon,
        actions = { actionIcon() },
        colors = colors,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:id=reference_foldable,shape=Normal,width=673,height=841,unit=dp,dpi=420", showBackground = true)
@Composable
fun STTopAppBarPreview(modifier: Modifier = Modifier) {
    SpendTrackTheme {
        STTopAppBar(
            titleRes = R.string.google_auth,
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