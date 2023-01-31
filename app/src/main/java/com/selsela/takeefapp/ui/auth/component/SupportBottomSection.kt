package com.selsela.takeefapp.ui.auth.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text12Meduim


@Composable
fun SupportBottomSection(modifier: Modifier,
                         goToSupport: () -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logosmall),
            contentDescription = ""
        )

        Spacer(modifier = Modifier.width(19.5.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .requiredHeight(51.dp)
                .background(LightBlue.copy(0.07f), shape = RoundedCornerShape(25.dp))
        ) {
            Text(text = stringResource(R.string.facing_problem), style = text12)
            Text(
                text = stringResource(R.string.support_lbl),
                style = text12Meduim,
                color = LightBlue,
                modifier = Modifier.padding(start = 6.dp)
                    .clickable {
                        goToSupport()
                    }
            )
        }


    }
}
