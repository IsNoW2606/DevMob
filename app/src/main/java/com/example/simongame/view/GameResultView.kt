package com.example.simongame.view

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simongame.AppViewModelProvider
import com.example.simongame.R
import com.example.simongame.intent.ShareResult
import com.example.simongame.view.components.ElevatedButton
import com.example.simongame.viewmodel.GameResultViewModel

@Composable
fun GameResultView(
    onNavigateToHome: () -> Unit,
    onNavigateToGame: () -> Unit,
    viewModel: GameResultViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val shareResult =
        rememberLauncherForActivityResult(contract = ShareResult(), onResult = { /* IGNORE */ })

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::onRequestPermissionResult
    )
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = viewModel::onTakePictureResult
    )

    if (uiState.requestingCameraPermission) {
        permissionRequest.launch(Manifest.permission.CAMERA)
    }

    if (uiState.openingCamera) {
        takePicture.launch()
        viewModel.onCameraOpened()
    }

    GameResultScreen(uiState) { action ->
        when (action) {
            // Player input
            is GameResultUiAction.OnPlayerNameValueChanged -> viewModel.onPlayerNameValueChanged(
                action.playerName
            )

            is GameResultUiAction.OnDeletePictureButtonClicked -> viewModel.onPictureDelete()

            is GameResultUiAction.OnSaveButtonClicked -> viewModel.onSaveButtonClicked()

            // Navigation
            is GameResultUiAction.OnHomeButtonClicked -> onNavigateToHome()
            is GameResultUiAction.OnReplayButtonClicked -> onNavigateToGame()

            // Intent event
            is GameResultUiAction.OnShareButtonClicked -> shareResult.launch(uiState.score)
            is GameResultUiAction.OnTakePictureButtonClicked -> viewModel.onOpeningCamera()
        }
    }
}

interface GameResultUiAction {
    data class OnPlayerNameValueChanged(val playerName: String) : GameResultUiAction
    object OnSaveButtonClicked : GameResultUiAction
    object OnDeletePictureButtonClicked : GameResultUiAction

    data class OnPlayerPictureTaken(val playerPicture: Bitmap) : GameResultUiAction

    // Navigation event
    object OnHomeButtonClicked : GameResultUiAction
    object OnReplayButtonClicked : GameResultUiAction

    // Intent event
    object OnShareButtonClicked : GameResultUiAction
    object OnTakePictureButtonClicked : GameResultUiAction
}

@Composable
fun GameResultScreen(
    uiState: GameResultViewModel.UiState,
    uiAction: (GameResultUiAction) -> Unit
) {
    GameResultBody {
        val (bigText, scoreText, playerInput, navigationButton, homeButton) = createRefs()

        BigText(
            modifier = Modifier.constrainAs(bigText) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent, 0.20f)
            },
            text = if (uiState.hasSavedGame) stringResource(id = R.string.try_again) else stringResource(
                id = R.string.save_score
            )
        )

        ScoreText(
            modifier = Modifier.constrainAs(scoreText) {
                centerVerticallyTo(parent)
                centerHorizontallyTo(parent)
            },
            score = uiState.score
        )

        if (uiState.hasSavedGame) {
            SecondaryButton(
                modifier = Modifier.constrainAs(navigationButton) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent, 0.75f)
                },
                onReplayClicked = { uiAction(GameResultUiAction.OnReplayButtonClicked) },
                onShareClicked = { uiAction(GameResultUiAction.OnShareButtonClicked) }
            )

            HomeButton(modifier = Modifier.constrainAs(homeButton) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent, 0.9f)
            },
                onClick = { uiAction(GameResultUiAction.OnHomeButtonClicked) }
            )
        } else {
            PlayerInput(modifier = Modifier.constrainAs(playerInput) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent, 0.75f)
            },
                value = uiState.playerName,
                picture = uiState.playerPicture,
                onValueChange = { value ->
                    uiAction(
                        GameResultUiAction.OnPlayerNameValueChanged(
                            value
                        )
                    )
                },
                onTakePictureButtonClick = { uiAction(GameResultUiAction.OnTakePictureButtonClicked) },
                onDeletePictureButtonClick = { uiAction(GameResultUiAction.OnDeletePictureButtonClicked) }
            )

            SaveButton(modifier = Modifier.constrainAs(homeButton) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent, 0.9f)
            },
                enabled = uiState.saveButtonEnable,
                onClick = { uiAction(GameResultUiAction.OnSaveButtonClicked) }
            )
        }
    }
}

@Composable
fun GameResultBody(content: @Composable ConstraintLayoutScope.() -> Unit) {
    val padding = dimensionResource(id = R.dimen.padding_body)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray.copy(alpha = 0.8f))
            .padding(padding),
        content = content
    )
}

@Composable
fun BigText(modifier: Modifier = Modifier, text: String = stringResource(id = R.string.example)) {
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)
    val borderColor = colorResource(id = R.color.text_light_border)

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_large)
    val padding = PaddingValues(
        vertical = fontSize.value.dp.div(4),
        horizontal = fontSize.value.dp
    )

    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_medium)

    Box(modifier = modifier) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = borderColor,
                drawStyle = Stroke(width = strokeWidth.value, join = StrokeJoin.Round)
            )
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(top = fontSize.value.dp.div(8)),
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = text,
            textAlign = TextAlign.Center,
            color = borderColor
        )
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(padding),
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = text,
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}

@Composable
fun ScoreText(modifier: Modifier = Modifier, score: Int = 0) {
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)
    val borderColor = colorResource(id = R.color.score_text_border)

    val radialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
            return RadialGradientShader(
                colors = listOf(
                    borderColor.copy(alpha = 0.4f),
                    borderColor.copy(alpha = 0.12f),
                    Color(0x00FFFFFF)
                ),
                center = size.center,
                radius = biggerDimension / 2f,
                colorStops = listOf(0.0f, 0.3f, 1f)
            )
        }
    }

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_extra_large)
    val padding = PaddingValues(
        vertical = fontSize.value.dp,
        horizontal = fontSize.value.dp
    )

    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_extra_large)

    Box(modifier = modifier) {
        Text(
            modifier = Modifier
                .background(radialGradient)
                .padding(padding),
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = "$score",
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = borderColor,
                drawStyle = Stroke(width = strokeWidth.value, join = StrokeJoin.Round)
            )
        )
        Text(
            modifier = Modifier
                .padding(padding)
                .padding(top = fontSize.value.dp.div(8)),
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = "$score",
            textAlign = TextAlign.Center,
            color = borderColor
        )
        Text(
            modifier = modifier
                .padding(padding),
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = "$score",
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}

@Composable
fun PlayerInput(
    modifier: Modifier = Modifier,
    value: String = stringResource(id = R.string.example),
    picture: Bitmap? = null,
    onValueChange: (String) -> Unit,
    onTakePictureButtonClick: () -> Unit,
    onDeletePictureButtonClick: () -> Unit
) {
    val fontSize = dimensionResource(id = R.dimen.font_size_regular)
    val gap = fontSize.value.div(2).dp

    Row(modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(gap)) {
        PlayerPicture(picture, onTakePictureButtonClick, onDeletePictureButtonClick)
        PlayerTextField(value, onValueChange)
    }

}

@Composable
fun PlayerPicture(picture: Bitmap?, onTakePictureClick: () -> Unit, onDeletePictureClick: () -> Unit) {
    // Color
    val backgroundColor = colorResource(id = R.color.input_light)
    val strokeColor = colorResource(id = R.color.stroke_light)

    // Dimen
    val padding = dimensionResource(id = R.dimen.icon_large_padding)

    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_small)

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_small)
    val shape = RoundedCornerShape(cornerRadius)

    if (picture != null) {
        val iconSize = 20.dp
        val offsetInPx = LocalDensity.current.run { (iconSize / 2).roundToPx() }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .border(strokeWidth, strokeColor, shape),
                contentScale = ContentScale.Crop,
                bitmap = picture.asImageBitmap(),
                contentDescription = stringResource(id = R.string.player_picture)
            )

            IconButton(
                onClick = onDeletePictureClick,
                modifier = Modifier
                    .offset {
                        IntOffset(x = +offsetInPx, y = -offsetInPx)
                    }
                    .clip(shape)
                    .background(Color.Red)
                    .size(iconSize)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    tint = Color.White,
                    contentDescription = "",
                )
            }
        }
    } else {
        IconButton(
            modifier = Modifier
                .background(backgroundColor, shape = shape)
                .fillMaxHeight()
                .aspectRatio(1f),
            onClick = onTakePictureClick
        ) {
            Icon(
                modifier = Modifier
                    .padding(padding)
                    .border(strokeWidth, strokeColor),
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_camera_alt_24),
                contentDescription = stringResource(id = R.string.player_picture)
            )
        }
    }
}

@Composable
private fun PlayerTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))
    val placeholder = stringResource(id = R.string.player_placeholder)

    // Color
    val strokeColor = colorResource(id = R.color.stroke_light)

    // Dimen
    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_small)

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_small)
    val shape = RoundedCornerShape(cornerRadius)

    TextField(
        modifier = Modifier
            .border(strokeWidth, strokeColor, shape)
            .clip(shape),
        placeholder = { Text(text = placeholder) },
        textStyle = TextStyle(
            fontFamily = fontFamily
        ),
        singleLine = true,
        value = value,
        shape = shape,
        onValueChange = onValueChange
    )
}

@Composable
fun SaveButton(modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    // Color
    val iconColor = colorResource(id = R.color.text_light)
    val strokeColor = colorResource(id = R.color.stroke_light)
    val saveButton = colorResource(id = R.color.save_button)

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.icon_large)
    val padding = dimensionResource(id = R.dimen.icon_large_padding)

    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_small)

    val cornerRadius = dimensionResource(id = R.dimen.circular_button_corner_radius)
    val shape = RoundedCornerShape(cornerRadius)

    ElevatedButton(
        modifier = modifier
            .border(strokeWidth, strokeColor, shape),
        buttonColor = saveButton,
        shape = shape,
        offset = strokeWidth,
        enabled = enabled,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .size(fontSize)
                .padding(padding),
            imageVector = Icons.Filled.Done,
            tint = iconColor,
            contentDescription = "Save game"
        )
    }
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    onReplayClicked: () -> Unit,
    onShareClicked: () -> Unit,
) {
    // Color
    val iconColor = colorResource(id = R.color.text_light)
    val strokeColor = colorResource(id = R.color.stroke_light)
    val replayButton = colorResource(id = R.color.share_button)
    val shareButton = colorResource(id = R.color.share_button)

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_regular)
    val gap = fontSize.value.dp

    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_small)

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_small)
    val shape = RoundedCornerShape(cornerRadius)

    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        IconButton(
            modifier = Modifier
                .background(replayButton, shape)
                .border(strokeWidth, strokeColor, shape)
                .fillMaxHeight()
                .aspectRatio(1f),
            onClick = onReplayClicked
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                tint = iconColor,
                contentDescription = "Replay game"
            )
        }

        IconButton(
            modifier = Modifier
                .background(shareButton, shape)
                .border(strokeWidth, strokeColor, shape)
                .fillMaxHeight()
                .aspectRatio(1f),
            onClick = onShareClicked
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                tint = iconColor,
                contentDescription = "Share score"
            )
        }
    }
}

@Composable
fun HomeButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    // Color
    val iconColor = colorResource(id = R.color.text_light)
    val strokeColor = colorResource(id = R.color.stroke_light)
    val homeButton = colorResource(id = R.color.home_button)

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.icon_large)
    val padding = dimensionResource(id = R.dimen.icon_large_padding)

    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_small)

    val cornerRadius = dimensionResource(id = R.dimen.circular_button_corner_radius)
    val shape = RoundedCornerShape(cornerRadius)

    ElevatedButton(
        modifier = modifier
            .border(strokeWidth, strokeColor, shape),
        buttonColor = homeButton,
        shape = shape,
        offset = strokeWidth,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .size(fontSize)
                .padding(padding),
            imageVector = Icons.Filled.Home,
            tint = iconColor,
            contentDescription = "Navigate to home screen"
        )
    }
}

@Preview
@Composable
fun GameResultViewPreview() {
    val uiState = GameResultViewModel.UiState()

    GameResultScreen(uiState) {
        // Handle ui action here
    }
}