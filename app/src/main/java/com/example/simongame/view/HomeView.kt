package com.example.simongame.view

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.core.graphics.drawable.toIcon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simongame.AppViewModelProvider
import com.example.simongame.R
import com.example.simongame.view.components.ElevatedButton
import com.example.simongame.view.effect.dropShadow
import com.example.simongame.view.effect.shimmerLoadingAnimation
import com.example.simongame.viewmodel.HomeViewModel
import com.example.simongame.viewmodel.uistate.GameDataUiState
import java.security.AllPermission

@Composable
fun HomeView(
    onNavigateToGameView: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(uiState) { action ->
        when (action) {
            HomeUiAction.OnButtonClicked -> onNavigateToGameView()
        }
    }
}

interface HomeUiAction {
    object OnButtonClicked : HomeUiAction
}

@Composable
fun HomeScreen(
    uiState: HomeViewModel.UiState = HomeViewModel.UiState(),
    uiAction: (HomeUiAction) -> Unit = {}
) {
    HomeBody {
        val (tab, button) = createRefs()

        if (uiState.lastScores.isNotEmpty()) {
            ScoreTab(
                modifier = Modifier.constrainAs(tab) {
                    centerVerticallyTo(parent)
                    centerHorizontallyTo(parent)
                },
                lastScores = uiState.lastScores
            )
        }
        PlayButton(
            modifier = Modifier.constrainAs(button) {
                centerVerticallyTo(parent, 0.75f)
                centerHorizontallyTo(parent)
            },
            onClick = { uiAction(HomeUiAction.OnButtonClicked) }
        )
    }
}

@Composable
fun HomeBody(content: @Composable ConstraintLayoutScope.() -> Unit) {
    val padding = dimensionResource(id = R.dimen.padding_body)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        content = content
    )
}

@Composable
fun ScoreTab(modifier: Modifier = Modifier, lastScores: List<GameDataUiState>) {
    // Color
    val tabColor = colorResource(id = R.color.tab)

    // Dimen
    val padding = dimensionResource(id = R.dimen.padding_regular)

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_medium)
    val shape = RoundedCornerShape(cornerRadius)

    Column(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .dropShadow(shape = shape, offsetY = padding)
            .background(tabColor, shape)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(padding)
    ) {
        ScoreTabHeader()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(padding.div(2))
        ) {
            val count = lastScores.size.coerceAtMost(3)
            items(count) {
                val item = lastScores[it]

                LastScoreItem(
                    item.playerName,
                    item.playerPicture,
                    item.score,
                    it == count - 1
                )
            }
        }
    }
}

@Composable
fun ScoreTabHeader() {
    val text = stringResource(id = R.string.last_score)
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)
    val headerColor = colorResource(id = R.color.tab_header)

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_regular)
    val padding = PaddingValues(
        vertical = fontSize.value.dp.div(4),
        horizontal = fontSize.value.dp
    )

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_medium)
    val shape = RoundedCornerShape(cornerRadius)

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerColor, shape)
            .padding(padding),
        fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
        fontFamily = fontFamily,
        text = text,
        textAlign = TextAlign.Center,
        color = textColor,
        style = TextStyle.Default
    )
}

@Composable
fun LastScoreItem(
    player: String = stringResource(id = R.string.example),
    picture: Bitmap? = null,
    score: Int = 0,
    lastItem: Boolean = false
) {
    val gap = dimensionResource(id = R.dimen.padding_regular).div(2)

    var modifier = Modifier.height(IntrinsicSize.Min)

    if (lastItem)
        modifier = modifier.padding(bottom = gap)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        LastScorePictureItem(picture = picture)
        LastScoreTextItem(player = player, score = score)
    }
}

@Composable
private fun LastScorePictureItem(
    picture: Bitmap? = null
) {
    // Dimen
    val iconSize = dimensionResource(id = R.dimen.icon_size)

    // Color
    val backgroundColor = colorResource(id = R.color.input_light)
    val strokeColor = colorResource(id = R.color.stroke_light)

    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_small)

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_small)
    val shape = RoundedCornerShape(cornerRadius)

    Row(
        modifier = Modifier
            .dropShadow(shape, offsetY = strokeWidth)
            .background(backgroundColor, shape = shape)
            .size(iconSize)
            .aspectRatio(1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (picture != null) {
            Image(
                modifier = Modifier
                    .border(strokeWidth, strokeColor, shape)
                    .clip(shape),
                bitmap = picture.asImageBitmap(),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.player_picture)
            )
        } else {
            Icon(
                modifier = Modifier
                    .border(strokeWidth, strokeColor),
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(id = R.string.player_picture)
            )
        }
    }
}


@Composable
private fun LastScoreTextItem(player: String, score: Int) {
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)
    val rowColor = colorResource(id = R.color.tab_row)
    val strokeColor = colorResource(id = R.color.stroke_light)

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_regular)
    val padding = PaddingValues(
        vertical = fontSize.value.dp.div(4),
        horizontal = fontSize.value.dp.div(2)
    )
    val strokeWidth = dimensionResource(id = R.dimen.stroke_width_small)

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_small)
    val shape = RoundedCornerShape(cornerRadius)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(rowColor, shape)
            .dropShadow(shape, offsetY = strokeWidth)
            .border(strokeWidth, strokeColor, shape)
            .padding(padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = player,
            textAlign = TextAlign.Center,
            color = textColor,
            style = TextStyle.Default
        )
        Text(
            fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
            fontFamily = fontFamily,
            text = "$score",
            textAlign = TextAlign.Center,
            color = textColor,
            style = TextStyle.Default
        )
    }
}

@Composable
fun PlayButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val text = stringResource(id = R.string.play)
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)
    val buttonColor = colorResource(id = R.color.button_action)
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

    ElevatedButton(
        modifier = modifier,
        buttonColor = buttonColor,
        onClick = onClick,
        shape = shape,
        offset = strokeWidth
    ) {
        Text(
            modifier = Modifier
                .shimmerLoadingAnimation(shape = shape)
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
}

@Preview
@Composable
fun HomeViewPreview() {
    val uiState = HomeViewModel.UiState(
        listOf(
            GameDataUiState(
                12,
                "Pierre",
                BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.image)
            ),
            GameDataUiState(
                12,
                "Pierre",
                null
            )
        )
    )

    HomeScreen(uiState)
}