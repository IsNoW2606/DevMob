package com.example.simongame.view

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.example.simongame.view.effect.dropShadow
import com.example.simongame.viewmodel.BestScoreViewModel
import com.example.simongame.viewmodel.uistate.GameDataUiState

@Composable
fun BestScoreView(viewModel: BestScoreViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by viewModel.uiState.collectAsState()

    BestScoreScreen(uiState)
}

@Composable
fun BestScoreScreen(uiState: BestScoreViewModel.UiState = BestScoreViewModel.UiState()) {
    BestScoreBody {
        BestScoreTab(
            bestScores = uiState.bestScores
        )
    }
}

@Composable
fun BestScoreBody(content: @Composable ColumnScope.() -> Unit) {
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
fun BestScoreTab(modifier: Modifier = Modifier, bestScores: List<GameDataUiState> = emptyList()) {
    // Color
    val tabColor = colorResource(id = R.color.tab)

    // Dimen
    val padding = dimensionResource(id = R.dimen.padding_regular)

    val cornerRadius = dimensionResource(id = R.dimen.corner_radius_medium)
    val shape = RoundedCornerShape(cornerRadius)

    Column(
        modifier = modifier
            .fillMaxSize()
            .dropShadow(shape = shape, offsetY = padding)
            .background(tabColor, shape)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(padding)
    ) {
        BestScoreTabHeader()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(padding.div(2))
        ) {
            val count = bestScores.size
            items(count) {
                val item = bestScores[it]

                BestScoreItem(
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
fun BestScoreTabHeader() {
    val text = stringResource(id = R.string.best_score)
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
fun BestScoreItem(
    player: String = stringResource(id = R.string.example),
    picture: Bitmap? = null,
    score: Int = 12,
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
        BestScorePictureItem(picture = picture)
        BestScoreTextItem(player = player, score = score)
    }
}


@Composable
private fun BestScorePictureItem(
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
private fun BestScoreTextItem(player: String, score: Int) {
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

@Preview
@Composable
fun BestScoreViewPreview() {
    BestScoreScreen()
}