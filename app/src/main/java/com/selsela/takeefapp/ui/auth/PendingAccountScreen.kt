package com.selsela.takeefapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.auth.component.SupportBottomSection
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.theme.CircleColor
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text16
import com.selsela.takeefapp.ui.theme.text16Line
import com.selsela.takeefapp.ui.theme.text16Medium
import com.selsela.takeefapp.ui.theme.text18
import com.selsela.takeefapp.utils.Extensions.Companion.BroadcastReceiver
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.selsela.takeefapp.ui.splash.ConfigViewModel
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text12Meduim
import com.selsela.takeefapp.utils.Constants.VERIFIED_ADMIN
import com.selsela.takeefapp.utils.Constants.VERIFIED_MANAGEMENT
import com.selsela.takeefapp.utils.Extensions.Companion.OnLifecycleEvent
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.log
import de.palm.composestateevents.EventEffect

@Preview
@Composable
fun PendingAccountScreen(
    vm: AuthViewModel = hiltViewModel(),
    goToSupport: () -> Unit,
    onFinish: () -> Unit
) {
    Color.White.ChangeStatusBarColor()
    val viewState: AuthUiState by vm.uiState.collectAsStateLifecycleAware(AuthUiState())

    PendingAccountContent(goToSupport)
    BroadcastReceiver(
        context = LocalContext.current,
        action = VERIFIED_MANAGEMENT,
    ) {

        onFinish()
    }
    OnLifecycleEvent { _, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                vm.me()
                "heyPending".log()
            }

            else -> {}
        }
    }

    EventEffect(
        event = viewState.onVerfied,
        onConsumed = vm::onVerfied
    ) { message ->
        if (message == VERIFIED_ADMIN)
            onFinish()
    }

}

@Composable
private fun PendingAccountContent(
    goToSupport: () -> Unit,
    ) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = Modifier
                    .paddingTop(108)
                    .clip(CircleShape)
                    .background(CircleColor)
                    .size(304.dp),
                contentAlignment = Alignment.Center
            ) {

                LottieAnimationView(
                    raw = R.raw.man,
                    modifier = Modifier.size(281.dp)
                )
                LottieAnimationView(
                    raw = R.raw.waitingicon,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.TopStart)
                        .size(115.dp)
                )

            }
            Spacer(modifier = Modifier.height(56.dp))
            Text(
                text = stringResource(R.string.welcome_1),
                style = text16,
                color = TextColor
            )
            Text(
                text = LocalData.user?.name ?: "",
                style = text16Medium,
                color = Purple40,
                modifier = Modifier.paddingTop(9)
            )
            Text(
                text = stringResource(R.string.under_review),
                style = text18,
                color = TextColor,
                modifier = Modifier.paddingTop(23)
            )
            Text(
                text = stringResource(R.string.under_review_lbl) +
                        stringResource(R.string.under_review_lbl_1),
                style = text16Line,
                color = TextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingTop(23),
                textAlign = TextAlign.Center
            )

        }
        SupportBottomSection(
            Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
                .padding(bottom = 43.dp)
        ) {
            goToSupport()
        }
    }
}

@Composable
fun BlockedDialog(isShowing: Boolean) {
    if (isShowing) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .background(TextColor, RoundedCornerShape(42.dp))
            ) {

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            vertical = 45.dp,
                            horizontal = 12.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    LottieAnimationView(
                        raw = R.raw.blockedacount,
                        modifier = Modifier.size(134.dp)
                    )

                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = stringResource(R.string.disabled_account),
                        style = text18,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.disabled_account_lbl_1) +
                                stringResource(R.string.disabled_account_lbl),
                        modifier = Modifier
                            .paddingTop(32)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = text16Line,
                        color = Color.White.copy(0.74f)
                    )

                }

                Row(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .wrapContentSize()
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logosmallwhite),
                        contentDescription = ""
                    )

                    Spacer(modifier = Modifier.width(19.5.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .requiredHeight(51.dp)
                            .background(LightBlue.copy(0.07f), shape = RoundedCornerShape(25.dp))
                    ) {
                        Text(
                            text = stringResource(R.string.facing_problem),
                            style = text11,
                            color = Color.White.copy(0.85f)
                        )
                        Text(
                            text = stringResource(R.string.support_lbl),
                            style = text12Meduim,
                            color = LightBlue,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }
    }
}



