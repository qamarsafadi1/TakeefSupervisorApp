package com.selsela.takeefapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.qamar.elasticview.ElasticView
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.auth.BlockedDialog
import com.selsela.takeefapp.ui.common.components.Switch
import com.selsela.takeefapp.ui.order.cell.NextOrderItem
import com.selsela.takeefapp.ui.order.cell.OrderItem
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.theme.NoRippleTheme
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.text12Meduim
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Extensions
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.LocalData

@Composable
fun HomeView(
    goToRoute: () -> Unit,
    goToMyAccount: () -> Unit,
    goToDetails: () -> Unit,
    onPending: () -> Unit,
    goToCost: () -> Unit,
) {
    Color.White.ChangeStatusBarColor()
    // hide ripple effect
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 19.dp)
                    .padding(top = 25.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ElasticView(
                        modifier = Modifier.align(Alignment.TopStart),
                        onClick = { goToMyAccount() }) {
                        Image(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = "",
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.logosmall),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                    Switch(
                        Modifier.align(Alignment.TopEnd)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
                LazyColumn(Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = stringResource(R.string.current_order),
                            style = text12Meduim,
                            color = SecondaryColor
                        )

                        OrderItem(onClick = {
                            goToRoute()
                        }) {
                            goToCost()
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.upcoming_orders),
                            style = text12Meduim,
                            color = SecondaryColor
                        )
                    }

                    items(2) {
                        NextOrderItem(onClick = {
                            goToDetails()
                        }) {

                        }
                    }

                }
            }

        }
    }
    Extensions.BroadcastReceiver(
        context = LocalContext.current,
        action = Constants.UN_VERIFIED_MANAGEMENT,
    ) {
        onPending()
    }
    var isBlocked by remember {
        mutableStateOf(LocalData.user?.isBlock == 1)
    }
    Extensions.BroadcastReceiver(
        context = LocalContext.current,
        action = Constants.BLOCKED,
    ) {
        isBlocked = true
        isBlocked.log("isBlocked")
    }
    Extensions.BroadcastReceiver(
        context = LocalContext.current,
        action = Constants.UN_BLOCKED,
    ) {
        isBlocked = false
        isBlocked.log("isBlockedUn")

    }

    BlockedDialog(isBlocked)
}


