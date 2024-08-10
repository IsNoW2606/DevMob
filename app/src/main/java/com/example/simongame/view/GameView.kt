package com.example.simongame.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simongame.AppViewModelProvider
import com.example.simongame.R
import com.example.simongame.view.components.ElevatedButton
import com.example.simongame.view.effect.shimmerLoadingAnimation
import com.example.simongame.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameView(
    onBackPressed: () -> Unit,
    onGameFinished: (Int) -> Unit,
    viewModel: GameViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.isGameFinished) {
        if (uiState.isGameFinished) {
            delay(500)
            onGameFinished(uiState.level)
        }
    }

    BackHandler(!uiState.isGameFinished) {
        onBackPressed()
    }

    GameScreen(uiState) { action ->
        when(action) {
            is GameUiAction.OnStartGameClicked -> viewModel.onStartGameClicked()

            // On light clicked
            is GameUiAction.OnGreenLightClicked -> if (!uiState.isShowingSequence) viewModel.onGreenLightClicked()
            is GameUiAction.OnRedLightClicked -> if (!uiState.isShowingSequence) viewModel.onRedLightClicked()
            is GameUiAction.OnYellowLightClicked -> if (!uiState.isShowingSequence) viewModel.onYellowLightClicked()
            is GameUiAction.OnBlueLightClicked -> if (!uiState.isShowingSequence) viewModel.onBlueLightClicked()

            // On light animation launched
            is GameUiAction.OnGreenLightAnimationLaunched -> viewModel.onGreenLightAnimationLaunched()
            is GameUiAction.OnRedLightAnimationLaunched -> viewModel.onRedLightAnimationLaunched()
            is GameUiAction.OnYellowLightAnimationLaunched -> viewModel.onYellowLightAnimationLaunched()
            is GameUiAction.OnBlueLightAnimationLaunched -> viewModel.onBlueLightAnimationLaunched()
        }
    }
}

interface GameUiAction {
    object OnStartGameClicked: GameUiAction

    object OnGreenLightClicked: GameUiAction
    object OnRedLightClicked: GameUiAction
    object OnYellowLightClicked: GameUiAction
    object OnBlueLightClicked: GameUiAction

    // On light animation launched
    object OnGreenLightAnimationLaunched: GameUiAction
    object OnRedLightAnimationLaunched: GameUiAction
    object OnYellowLightAnimationLaunched: GameUiAction
    object OnBlueLightAnimationLaunched: GameUiAction
}

@Composable
fun GameScreen(
    uiState: GameViewModel.UiState,
    uiAction: (GameUiAction) -> Unit
) {
    GameBody {
        LevelLabel(uiState.level)
        LightSection(uiState, uiAction)
    }

    AnimatedVisibility(
        visible = !uiState.isGameStarted,
        exit = fadeOut(
            animationSpec = tween(300, easing = EaseOut)
        )
    ) {
        StartGameOverlay(onClick = { uiAction(GameUiAction.OnStartGameClicked) })
    }
}

@Composable
fun GameBody(content: @Composable ColumnScope.() -> Unit) {
    val padding = dimensionResource(id = R.dimen.padding_body)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
fun LevelLabel(
    level: Int = stringResource(id = R.string.number_example).toInt()
) {
    val text = "${stringResource(id = R.string.level)} $level"
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)
    val buttonColor = colorResource(id = R.color.label_color)
    val buttonChangeColor = colorResource(id = R.color.is_showing_sequence)
    val strokeColor = colorResource(id = R.color.stroke_light)

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_medium)
    val padding = PaddingValues(
        horizontal = fontSize.value.dp.times(2),
        vertical = fontSize.value.dp.div(4)
    )
    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_medium)

    val cornerRadius = dimensionResource(id = R.dimen.button_corner_radius)
    val shape = RoundedCornerShape(cornerRadius)

    val colorAnimation = remember { Animatable(buttonColor) }

    LaunchedEffect(key1 = level) {
        colorAnimation.snapTo(buttonChangeColor)
        colorAnimation.animateTo(buttonColor, tween(500))
    }
    Text(
        modifier = Modifier
            .background(colorAnimation.value, shape)
            .border(strokeWidth, strokeColor, shape)
            .padding(padding),
        fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
        fontFamily = fontFamily,
        text = text,
        textAlign = TextAlign.Center,
        color = textColor,
        style = TextStyle.Default,
    )
}

@Composable
fun LightSection(
    uiState: GameViewModel.UiState,
    uiAction: (GameUiAction) -> Unit,
) {
    val gap = dimensionResource(id = R.dimen.padding_body)

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(gap)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(gap)
        ) {
            // Green Light
            LightButton(
                offColor = colorResource(id = R.color.green_off),
                onColor = colorResource(id = R.color.green_on),
                launchingLightAnimation = uiState.isLaunchingGreenLightAnimation,
                onLightAnimationLaunched = { uiAction(GameUiAction.OnGreenLightAnimationLaunched) },
                onClick = { uiAction(GameUiAction.OnGreenLightClicked) })

            // Red Light
            LightButton(
                offColor = colorResource(id = R.color.red_off),
                onColor = colorResource(id = R.color.red_on),
                launchingLightAnimation = uiState.isLaunchingRedLightAnimation,
                onLightAnimationLaunched = { uiAction(GameUiAction.OnRedLightAnimationLaunched) },
                onClick = { uiAction(GameUiAction.OnRedLightClicked) })
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(gap)
        ) {
            // Yellow Light
            LightButton(
                offColor = colorResource(id = R.color.yellow_off),
                onColor = colorResource(id = R.color.yellow_on),
                launchingLightAnimation = uiState.isLaunchingYellowLightAnimation,
                onLightAnimationLaunched = { uiAction(GameUiAction.OnYellowLightAnimationLaunched) },
                onClick = { uiAction(GameUiAction.OnYellowLightClicked) })

            // Blue Light
            LightButton(
                offColor = colorResource(id = R.color.blue_off),
                onColor = colorResource(id = R.color.blue_on),
                launchingLightAnimation = uiState.isLaunchingBlueLightAnimation,
                onLightAnimationLaunched = { uiAction(GameUiAction.OnBlueLightAnimationLaunched) },
                onClick = { uiAction(GameUiAction.OnBlueLightClicked) })
        }

    }
}

@Composable
fun RowScope.LightButton(
    modifier: Modifier = Modifier,
    offColor: Color,
    onColor: Color,
    launchingLightAnimation: Boolean,
    onLightAnimationLaunched: () -> Unit,
    onClick: () -> Unit
) {
    // Color
    val strokeColor = colorResource(id = R.color.stroke_light)

    // Dimen
    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_medium)

    val cornerRadius = dimensionResource(id = R.dimen.button_corner_radius)
    val shape = RoundedCornerShape(cornerRadius)

    val color = remember {
        Animatable(offColor)
    }

    var animationKey by remember { mutableIntStateOf(0) }
    if (launchingLightAnimation) {
        animationKey++
    }

    LaunchedEffect(key1 = animationKey) {
        if (animationKey > 0) {
            onLightAnimationLaunched()
            color.snapTo(onColor)
            color.animateTo(offColor, animationSpec = tween(800))
        }
    }

    ElevatedButton(
        modifier = modifier
            .fillMaxSize()
            .weight(1f)
            .border(strokeWidth, strokeColor, shape),
        buttonColor = color.value,
        onClick = onClick,
        shape = shape,
        offset = strokeWidth
    ) {}
}


@Composable
fun StartGameOverlay(onClick: () -> Unit) {
    val text = stringResource(id = R.string.start_game)
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)

    // Gradient
    val gradient = Brush.horizontalGradient(
        listOf(
            Color.DarkGray.copy(alpha = 0f),
            Color.DarkGray.copy(alpha = 0.05f),
            Color.DarkGray.copy(alpha = 0.2f),
            Color.DarkGray.copy(alpha = 0.4f),
            Color.DarkGray.copy(alpha = 0.7f),
            Color.DarkGray.copy(alpha = 0.4f),
            Color.DarkGray.copy(alpha = 0.2f),
            Color.DarkGray.copy(alpha = 0.05f),
            Color.DarkGray.copy(alpha = 0f)
        )
    )

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_medium)
    val padding = PaddingValues(
        vertical = fontSize.value.dp.div(4)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray.copy(alpha = 0.8f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(padding)
                    .background(gradient)
                    .shimmerLoadingAnimation()
                    .fillMaxWidth(),
                fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
                fontFamily = fontFamily,
                text = text,
                textAlign = TextAlign.Center,
                color = textColor,
                style = TextStyle.Default,
            )
        }
    }
}

@Preview
@Composable
fun GameViewPreview() {
    val uiState = GameViewModel.UiState()

    GameScreen(uiState) {
        // Handle ui action here
    }
}