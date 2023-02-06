package com.selsela.takeefapp.ui.support

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.selsela.takeefapp.R
import com.selsela.takeefapp.data.auth.model.support.Reply
import com.selsela.takeefapp.ui.auth.AuthViewModel
import com.selsela.takeefapp.ui.auth.SupportUiState
import com.selsela.takeefapp.ui.common.InputEditText
import com.selsela.takeefapp.ui.common.LottieAnimationView
import com.selsela.takeefapp.ui.theme.Gray3
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.SecondaryColor2
import com.selsela.takeefapp.ui.theme.Shimmer
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text10
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text12White
import com.selsela.takeefapp.utils.Common
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import de.palm.composestateevents.EventEffect

@Preview
@Composable
fun SupportScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val viewState: SupportUiState by viewModel.contactUiState.collectAsStateLifecycleAware(
        SupportUiState()
    )

    SupportContent(
        viewModel,
        viewState
    ) {
        viewModel.contactOrReplay()
    }
    /**
     * Handle Ui state from flow
     */

    LaunchedEffect(Unit) {
        if (!viewModel.isLoaded)
            viewModel.getContacts()
    }

    EventEffect(
        event = viewState.onFailure,
        onConsumed = viewModel::onFailure
    ) { error ->
        Common.handleErrors(
            error.responseMessage,
            error.errors,
            context
        )
    }
    EventEffect(
        event = viewState.onSuccess,
        onConsumed = viewModel::onSuccess
    ) { contactId ->
        viewModel.contactId = contactId
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SupportContent(
    viewModel: AuthViewModel,
    supportUiState: SupportUiState,
    sendMessage: (String) -> Unit
) {
    val messages = mutableListOf<Reply>().toMutableStateList()
    if (supportUiState.contactReplay != null) {
        messages.clear()
        supportUiState.contactReplay.replies?.forEach {
            messages.add(it)
        }
    }
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size)
    }
    if (supportUiState.isLoading.not() ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                Modifier
                    .padding(bottom = 73.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.89f)
                    .background(Color.White)
                    .padding(start = 29.dp, end = 20.dp),
                state = listState
            ) {
                items(messages) {
                    if (it.adminId != 1)
                        MeItem(
                            Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            it
                        )
                    else AdminItem(
                        Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        it
                    )
                }
            }
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 28.dp)
                    .fillMaxWidth()
                    .requiredHeight(93.dp)
                    .background(TextColor, RoundedCornerShape(25.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                //horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(Modifier.fillMaxWidth()
                    .requiredHeight(48.dp)
                    .weight(1f),
                    horizontalArrangement = Arrangement.Start) {
                    MessageEditText(viewModel,Modifier.fillMaxSize())
                }

                Row(Modifier.fillMaxWidth()
                    .weight(0.2f),
                horizontalArrangement = Arrangement.End) {
                    Image(painter = painterResource(id = R.drawable.send), contentDescription = "",
                        modifier = Modifier
                            .width(38.dp)
                            .height(38.dp)
                            .clickable {
                                if (viewModel.message.value
                                        .isEmpty()
                                        .not()
                                ) {
                                    val reply = Reply(adminId = 0, message = viewModel.message.value)
                                    if (supportUiState.contactReplay != null) {
                                        messages.clear()
                                        supportUiState.contactReplay.replies?.add(reply)
                                    } else messages.add(reply)
                                    sendMessage(viewModel.message.value)
                                    viewModel.message.value = ""
                                }
                            })


                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Shimmer, RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimationView(raw = R.raw.whiteloading)
            }

        }

    }
}

@Composable
fun AdminItem(modifier: Modifier, reply: Reply) {
    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.Center) {

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(width = 1.dp, color = Gray3, shape = CircleShape)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mjloho), contentDescription = "",
                    modifier = Modifier
                        .width(20.dp)
                        .height(19.dp)
                )
            }
            Text(
                text = stringResource(R.string.admin),
                style = text10,
                color = TextColor,
                modifier = Modifier.paddingTop(6)
            )

        }
        Spacer(modifier = Modifier.width(17.dp))
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    LightBlue.copy(0.10f), RoundedCornerShape(
                        topEnd = 23.dp, topStart = 23.dp,
                        bottomEnd = 23.dp, bottomStart = 0.dp
                    )
                )
                .padding(horizontal = 18.dp, vertical = 24.dp)
        ) {
            Text(
                text = reply.message ?: "",
                style = text12,
                color = TextColor,

                )
        }
    }

}

@Composable
fun MeItem(modifier: Modifier, reply: Reply) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.width(17.dp))
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    Purple40.copy(0.10f), RoundedCornerShape(
                        topEnd = 23.dp, topStart = 23.dp,
                        bottomEnd = 0.dp, bottomStart = 23.dp
                    )
                )
                .padding(horizontal = 18.dp, vertical = 24.dp)
        ) {
            Text(
                text = reply.message ?: "",
                style = text12,
                color = TextColor,

                )
        }
    }

}


@Composable
private fun MessageEditText(viewModel: AuthViewModel, modifier: Modifier) {
    InputEditText(
        text =
        viewModel.message.value,
        modifier = modifier,
        onValueChange = {
            viewModel.message.value = it
        },
        textStyle = text12White,
        hint = stringResource(id = R.string.write_message_here),
        cornerRaduis = 30.dp,
        fillMax = 0.5f,
        hintColor = SecondaryColor2.copy(0.67f)

    )
}