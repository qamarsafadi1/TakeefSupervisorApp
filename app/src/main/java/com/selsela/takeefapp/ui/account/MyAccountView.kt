package com.selsela.takeefapp.ui.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.account.components.Header
import com.selsela.takeefapp.ui.auth.AuthUiState
import com.selsela.takeefapp.ui.auth.AuthViewModel
import com.selsela.takeefapp.ui.common.LanguageSheet
import com.selsela.takeefapp.ui.splash.ChangeNavigationBarColor
import com.selsela.takeefapp.ui.splash.ChangeStatusBarOnlyColor
import com.selsela.takeefapp.ui.splash.ConfigViewModel
import com.selsela.takeefapp.ui.theme.ColorAccent
import com.selsela.takeefapp.ui.theme.Gray3
import com.selsela.takeefapp.ui.theme.LightBlue
import com.selsela.takeefapp.ui.theme.LogoutBg
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text10NoLines
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text13
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.ui.theme.text14Meduim
import com.selsela.takeefapp.ui.theme.text16Medium
import com.selsela.takeefapp.utils.Common
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Constants.FINISHED
import com.selsela.takeefapp.utils.Constants.UNDER_PROGRESS
import com.selsela.takeefapp.utils.Extensions
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.convertToDecimalPatter
import com.selsela.takeefapp.utils.Extensions.Companion.showAlertDialog
import com.selsela.takeefapp.utils.Extensions.Companion.withDelay
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyAccountView(
    viewModel: AuthViewModel = hiltViewModel(),
    configViewModel: ConfigViewModel = hiltViewModel(),
    onBack: () -> Unit,
    goToAboutApp: () -> Unit,
    goToNotification: () -> Unit,
    goToTerms: () -> Unit,
    goToSupport: () -> Unit,
    goToProfile: () -> Unit,
    goToWallet: () -> Unit,
    goToLogin: () -> Unit,
    goToCurrentOrArchive: (Int) -> Unit,
    goToOrder: () -> Unit,
) {
    Color.Transparent.ChangeStatusBarOnlyColor()
    TextColor.ChangeNavigationBarColor()
    val coroutineScope = rememberCoroutineScope()
    val languageSheet = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val viewState: AuthUiState by viewModel.uiState.collectAsStateLifecycleAware(AuthUiState())
    val context = LocalContext.current

    AccountViewContent(
        uiState = viewState,
        viewModel,
        onBack,
        goToWallet,
        goToCurrentOrArchive,
        goToOrder,
        goToProfile,
        goToNotification,
        goToSupport,
        goToTerms,
        goToAboutApp,
        goToLogin,
        coroutineScope,
        languageSheet,
        configViewModel
    )

    /**
     * Handle Ui state from flow
     */

    LaunchedEffect(Unit) {
        if (LocalData.accessToken.isNullOrEmpty().not()) {
            if (!viewModel.isLoaded) {
                viewModel.me()
            }
        }
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

    Extensions.OnLifecycleEvent { _, event ->
        // do stuff on event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.me()
            }

            else -> {}
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun AccountViewContent(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    goToWallet: () -> Unit,
    goToCurrentOrArchive: (Int) -> Unit,
    goToOrder: () -> Unit,
    goToProfile: () -> Unit,
    goToNotification: () -> Unit,
    goToSupport: () -> Unit,
    goToTerms: () -> Unit,
    goToAboutApp: () -> Unit,
    goToLogin: () -> Unit,
    coroutineScope: CoroutineScope,
    languageSheet: ModalBottomSheetState,
    configViewModel: ConfigViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TextColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(207.dp)
                .background(TextColor)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .padding(horizontal = 16.dp)
        ) {
            val context = LocalContext.current
            // SCREEN HEADER
            var user by remember {
                viewModel.user
            }
            Header(
                user,
                viewModel.userLoggedIn.value,
                onBack = {
                    onBack()
                }
            ) {
                if (it == Constants.LOG_OUT) {
                    context.showAlertDialog(
                        context.getString(R.string.logout),
                        context.getString(R.string.logout_lbl),
                        context.getString(R.string.yes),
                        context.getString(R.string.no),
                    ) {
                        LocalData.clearData()
                        user = null
                        viewModel.userLoggedIn.value = false
                        goToLogin()
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                     .fillMaxWidth()
            ) {
                WalletCard(uiState) {
                    goToWallet()
                }
                OrderCards(
                    uiState = uiState,
                    goToCurrent = {
                        goToCurrentOrArchive(it)
                    }) {
                    goToOrder()
                }
                SettingsCards(
                    uiState,
                    goToProfile = {
                        goToProfile()
                    }
                ) {
                    goToNotification()
                }
                Text(
                    text = stringResource(id = R.string.settings),
                    style = text12,
                    color = SecondaryColor,
                    modifier = Modifier.paddingTop(41)
                )
                Row(
                    modifier = Modifier
                        .paddingTop(25.9)
                        .clickable {
                            goToSupport()
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.chat_text),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(id = R.string.help_and_support),
                        style = text14,
                        color = Color.White,
                        modifier = Modifier.padding(start = 18.6.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .paddingTop(31)
                        .clickable {
                            goToTerms()
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.terms),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(id = R.string.terms_confaitions),
                        style = text14,
                        color = Color.White,
                        modifier = Modifier.padding(start = 18.6.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .paddingTop(31)
                        .clickable {
                            goToAboutApp()
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aboutapp),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(id = R.string.about_app),
                        style = text14,
                        color = Color.White,
                        modifier = Modifier.padding(start = 18.6.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .paddingTop(31)
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                if (languageSheet.isVisible)
                                    languageSheet.hide()
                                else languageSheet.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.langauage),
                            contentDescription = ""
                        )
                        Text(
                            text = stringResource(id = R.string.langague),
                            style = text14,
                            color = Color.White,
                            modifier = Modifier.padding(start = 18.6.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (LocalData.appLocal == "ar") "العربية" else "English",
                            style = text12,
                            color = SecondaryColor
                        )
                        Image(
                            painter = painterResource(id = R.drawable.languagearrow),
                            contentDescription = "",
                            modifier = Modifier.padding(start = 12.3.dp)
                        )
                    }
                }


            }
        }
        LanguageSheet(languageSheet) {
            configViewModel.getConfig()
            coroutineScope.launch {
                if (languageSheet.isVisible)
                    languageSheet.hide()
                else languageSheet.animateTo(ModalBottomSheetValue.Expanded)
            }
            onBack()
        }
    }
}

@Composable
private fun SettingsCards(

    uiState: AuthUiState,
    goToProfile: () -> Unit,
    goToNotification: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 17.dp)
            .paddingTop(12)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .requiredHeight(88.dp)
                .clickable {
                    goToProfile()
                },
            shape = RoundedCornerShape(13.dp),
            elevation = 20.dp,
            backgroundColor = LogoutBg,
            border = BorderStroke(width = 1.dp, Gray3)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = ""
                )
                Text(
                    text = stringResource(id = R.string.my_account),
                    style = text12,
                    color = SecondaryColor,
                    modifier = Modifier.paddingTop(10)

                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .requiredHeight(88.dp)
                .clickable {
                    goToNotification()
                },
            shape = RoundedCornerShape(13.dp),
            elevation = 20.dp,
            backgroundColor = LogoutBg,
            border = BorderStroke(width = 1.dp, Gray3)

        ) {
            Box {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.notifiication),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(id = R.string.notifications),
                        style = text12,
                        color = SecondaryColor,
                        modifier = Modifier.paddingTop(10)

                    )
                }

                if (uiState.user?.newNotifications != 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 16.dp, end = 10.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(ColorAccent)
                            .size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${uiState.user?.newNotifications}",
                            style = text10NoLines,
                            color = Color.White,
                            modifier = Modifier
                                .paddingTop(1.5)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCards(
    uiState: AuthUiState,
    goToCurrent: (Int) -> Unit,
    goToOrder: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 17.dp)
            .paddingTop(15)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .requiredHeight(88.dp)
                .clickable {
                    goToOrder()
                },
            shape = RoundedCornerShape(13.dp),
            elevation = 20.dp,
            backgroundColor = LogoutBg
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${uiState.user?.newOrders?.convertToDecimalPatter()}",
                    style = text16Medium,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.new_orders),
                    style = text12,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .requiredHeight(88.dp)
                .clickable {
                    goToCurrent(UNDER_PROGRESS)
                },
            shape = RoundedCornerShape(13.dp),
            elevation = 20.dp,
            backgroundColor = LightBlue
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${uiState.user?.processingOrders?.convertToDecimalPatter()}",
                    style = text16Medium,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.on_progress_orders),
                    style = text12,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .requiredHeight(88.dp)
                .clickable {
                    goToCurrent(FINISHED)
                },
            shape = RoundedCornerShape(13.dp),
            elevation = 20.dp,
            backgroundColor = Purple40
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.archive),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Text(
                    text = stringResource(id = R.string.archive),
                    style = text12,
                    color = Color.White,
                    modifier = Modifier.paddingTop(10)
                )
            }
        }
    }
}

@Composable
fun WalletCard(
    uiState: AuthUiState,
    goToWallet: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 17.dp)
            .paddingTop(20)
            .requiredHeight(84.dp)
            .clickable {
                goToWallet()
            },
        shape = RoundedCornerShape(13.dp),
        elevation = 20.dp,
        backgroundColor = LogoutBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wallet),
                    contentDescription = ""
                )
                Text(
                    text = stringResource(R.string.wallet_balance_1),
                    style = text13,
                    color = SecondaryColor,
                    modifier = Modifier.padding(start = 10.5.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${uiState.user?.balance}",
                    style = text14Meduim,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.currency_1, Extensions.getCurrency()),
                    style = text13,
                    color = SecondaryColor
                )
            }
        }
    }
}
