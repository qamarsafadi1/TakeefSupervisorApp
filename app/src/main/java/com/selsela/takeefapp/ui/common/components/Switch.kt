package com.selsela.takeefapp.ui.common.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.Red
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.SwitchColor
import com.selsela.takeefapp.ui.theme.SwitchStrokeColor
import com.selsela.takeefapp.ui.theme.SwitchStrokeUnCheckedColor
import com.selsela.takeefapp.ui.theme.SwitchUnCheckedColor
import com.selsela.takeefapp.ui.theme.text12


@Composable
fun Switch(
    modifier: Modifier,
    width: Dp = 44.dp,
    height: Dp = 21.dp,
    checkedTrackColor: Color = SwitchStrokeColor,
    uncheckedTrackColor: Color = SwitchStrokeUnCheckedColor,
    bgColor: Color = SwitchColor,
    uncheckBgColor: Color = SwitchUnCheckedColor,
    gapBetweenThumbAndTrackEdge: Dp = 4.dp,
    borderWidth: Dp = 1.dp,
    cornerSize: Int = 50,
    iconInnerPadding: Dp = 4.dp,
) {

    // this is to disable the ripple effect
    val interactionSource = remember {
        MutableInteractionSource()
    }

    // state of the switch
    var switchOn by remember {
        mutableStateOf(true)
    }

    // for moving the thumb
    val alignment by animateAlignmentAsState(if (switchOn) 1f else -1f)

    // outer rectangle with border
    Row(
       modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = if (switchOn) stringResource(R.string.available) else stringResource(R.string.busy),
        style = text12,
        color = SecondaryColor)
        Spacer(modifier = Modifier.width(7.dp))
        Box(
            modifier = Modifier
                .background(
                    color = if (switchOn) bgColor else uncheckBgColor,
                    RoundedCornerShape(percent = cornerSize)
                )
                .size(width = width, height = height)
                .border(
                    width = borderWidth,
                    color = if (switchOn) checkedTrackColor else uncheckedTrackColor,
                    shape = RoundedCornerShape(percent = cornerSize)
                )
                .clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    switchOn = !switchOn
                },
            contentAlignment = Alignment.Center
        ) {

            // this is to add padding at the each horizontal side
            Box(
                modifier = Modifier
                    .padding(
                        start = gapBetweenThumbAndTrackEdge,
                        end = gapBetweenThumbAndTrackEdge
                    )
                    .fillMaxSize(),
                contentAlignment = alignment
            ) {

                // thumb with icon
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = if (switchOn) Purple40 else Red)
                        .size(13.dp)
                        .padding(all = iconInnerPadding)
                        .shadow(
                            9.dp, clip = true, shape = CircleShape,
                            ambientColor = Color.Black.copy(0.16f),
                            spotColor = Color.Black.copy(0.16f)
                        )
                )
            }
        }
    }

    // gap between switch and the text
}

@Composable
private fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetBiasValue)
    return derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) }
}