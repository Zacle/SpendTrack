package com.zacle.spendtrack.feature.onboarding.data

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zacle.spendtrack.core.designsystem.component.SpendTrackBackground
import com.zacle.spendtrack.core.designsystem.component.SpendTrackButton
import com.zacle.spendtrack.core.designsystem.theme.SpendTrackTheme
import com.zacle.spendtrack.core.ui.CommonScreen
import com.zacle.spendtrack.core.ui.UiState
import com.zacle.spendtrack.core.ui.previews.DevicePreviews
import com.zacle.spendtrack.feature.onboarding.R

@Composable
fun OnboardingRoute(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    OnboardingScreen(
        state = uiState,
        onboardingCompleted = {
            viewModel.submitAction(OnboardingUiAction.SetOnboarded)
            navigateToHome()
        },
        modifier = modifier
    )
}

@Composable
fun OnboardingScreen(
    state: UiState<OnboardingPagesModel>,
    onboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    CommonScreen(state = state) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            val pagerState = rememberPagerState {
                it.pages.size
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                PagerScreen(page = it.pages[index])
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Indicators(
                    size = it.pages.size,
                    currentPage = pagerState.currentPage
                )
            }
            SpendTrackButton(
                text = stringResource(id = R.string.get_started),
                onClick = onboardingCompleted,
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
fun PagerScreen(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .weight(2f),
            painter = painterResource(id = page.imageResId),
            contentDescription = null
        )
        Box(
            Modifier
                .padding(horizontal = 32.dp)
                .weight(1f)
        ) {
            Column {
                Text(
                    text = stringResource(id = page.titleResId),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(id = page.descriptionResId),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                )
            }
        }
    }
}

@Composable
fun Indicators(
    size: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(size) {
            Indicator(selected = it == currentPage)
        }
    }
}

@Composable
fun Indicator(
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val width by animateDpAsState(
        targetValue = if (selected) 12.dp else 8.dp,
        label = "Onboarding indicator",
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    Box(
        modifier = modifier
            .size(width)
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else Color.Gray
            )
    )
}

@DevicePreviews
@Composable
fun OnboardingScreenPreview() {
    SpendTrackTheme {
        SpendTrackBackground {
            OnboardingScreen(
                state = UiState.Success(
                    OnboardingPagesModel(
                        pages = listOf(
                            OnboardingPage.FirstPage,
                            OnboardingPage.SecondPage,
                            OnboardingPage.ThirdPage
                        )
                    )
                ),
                onboardingCompleted = {}
            )
        }
    }
}

@DevicePreviews
@Composable
fun OnboardingScreenPreview2() {
    SpendTrackTheme {
        SpendTrackBackground {
            OnboardingScreen(
                state = UiState.Success(
                    OnboardingPagesModel(
                        pages = listOf(
                            OnboardingPage.SecondPage,
                            OnboardingPage.SecondPage,
                            OnboardingPage.ThirdPage
                        )
                    )
                ),
                onboardingCompleted = {}
            )
        }
    }
}

@DevicePreviews
@Composable
fun OnboardingScreenPreview3() {
    SpendTrackTheme {
        SpendTrackBackground {
            OnboardingScreen(
                state = UiState.Success(
                    OnboardingPagesModel(
                        pages = listOf(
                            OnboardingPage.ThirdPage,
                            OnboardingPage.SecondPage,
                            OnboardingPage.ThirdPage
                        )
                    )
                ),
                onboardingCompleted = {}
            )
        }
    }
}