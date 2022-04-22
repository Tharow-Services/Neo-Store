package com.looker.droidify.ui.compose.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private enum class SelectionState { Unselected, Selected }

private class CategoryChipTransition(
    cornerRadius: State<Dp>,
    contentColor: State<Color>,
    checkScale: State<Float>
) {
    val cornerRadius by cornerRadius
    val contentColor by contentColor
    val checkScale by checkScale
}

@Composable
private fun categoryChipTransition(selected: Boolean): CategoryChipTransition {
    val transition = updateTransition(
        targetState = if (selected) SelectionState.Selected else SelectionState.Unselected,
        label = "chip_transition"
    )
    val corerRadius = transition.animateDp(label = "chip_corner") { state ->
        when (state) {
            SelectionState.Unselected -> 10.dp
            SelectionState.Selected -> 28.dp
        }
    }
    val contentColor = transition.animateColor(label = "chip_content_alpha") { state ->
        when (state) {
            SelectionState.Unselected -> MaterialTheme.colorScheme.surface
            SelectionState.Selected -> MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.8f)
        }
    }
    val checkScale = transition.animateFloat(
        label = "chip_check_scale",
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow) }
    ) { state ->
        when (state) {
            SelectionState.Unselected -> 0.6f
            SelectionState.Selected -> 1f
        }
    }
    return remember(transition) {
        CategoryChipTransition(corerRadius, contentColor, checkScale)
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean = false,
    onSelected: (Boolean) -> Unit = {}
) {
    val categoryChipTransitionState = categoryChipTransition(selected = isSelected)

    Surface(
        modifier = Modifier
            .graphicsLayer {
                shape = RoundedCornerShape(categoryChipTransitionState.cornerRadius)
                clip = true
            },
        color = categoryChipTransitionState.contentColor
    ) {
        Row(
            modifier = Modifier
                .toggleable(value = isSelected, onValueChange = onSelected)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .wrapContentSize()
                        .graphicsLayer {
                            scaleX = categoryChipTransitionState.checkScale
                            scaleY = categoryChipTransitionState.checkScale
                        }
                )
            }
            Text(
                text = category,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SelectableChipRow(
    modifier: Modifier = Modifier,
    list: List<String>,
    onClick: (String) -> Unit
) {
    var selected by remember { mutableStateOf(list[0]) }

    LazyRow(
        modifier = modifier.height(54.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(list) { category ->
            CategoryChip(
                category = category,
                isSelected = category == selected,
                onSelected = {
                    selected = category
                    onClick(selected)
                }
            )
        }
    }
}