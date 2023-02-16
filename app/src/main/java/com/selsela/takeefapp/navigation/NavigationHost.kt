package com.selsela.takeefapp.navigation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.aboutapp.AboutAppView
import com.selsela.takeefapp.ui.account.MyAccountView
import com.selsela.takeefapp.ui.auth.CompleteInfoScreen
import com.selsela.takeefapp.ui.auth.LoginView
import com.selsela.takeefapp.ui.auth.PendingAccountScreen
import com.selsela.takeefapp.ui.auth.VerifyView
import com.selsela.takeefapp.ui.general.SuccessView
import com.selsela.takeefapp.ui.home.HomeView
import com.selsela.takeefapp.ui.home.OrderViewModel
import com.selsela.takeefapp.ui.notification.NotificationView
import com.selsela.takeefapp.ui.order.AddCostScreen
import com.selsela.takeefapp.ui.order.OrderDetailsView
import com.selsela.takeefapp.ui.order.OrderRouteView
import com.selsela.takeefapp.ui.order.OrdersView
import com.selsela.takeefapp.ui.profile.ProfileScreen
import com.selsela.takeefapp.ui.splash.SplashView
import com.selsela.takeefapp.ui.support.SupportScreen
import com.selsela.takeefapp.ui.terms.TermsView
import com.selsela.takeefapp.ui.wallet.WalletScreen
import com.selsela.takeefapp.utils.Constants.NOT_VERIFIED
import com.selsela.takeefapp.utils.Constants.VERIFIED
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.Extensions.Companion.showError
import com.selsela.takeefapp.utils.Extensions.Companion.whatsappContact
import com.selsela.takeefapp.utils.LocalData

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.SPLASH_SCREEN,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
) {
    val uri = "https://airconditionersupervisor.com"

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destinations.SPLASH_SCREEN) {
            SplashView() {
                if (LocalData.accessToken.isNullOrEmpty())
                    navActions.navigateToLogin()
                else {
                    LocalData.user?.status?.log("status")
                    LocalData.user?.completed?.log("status")
                    if (LocalData.user?.verifiedFromManagement == NOT_VERIFIED) {
                        navActions.navigateToPendingAccount()
                        return@SplashView
                    }
                    if (LocalData.user?.status == VERIFIED && LocalData.user?.completed == 1) {
                        LocalData.user?.isBlock = 0
                        if (LocalData.user?.verifiedFromManagement != NOT_VERIFIED)
                            navActions.navigateToHome()
                        else navActions.navigateToPendingAccount()
                    } else {
                        if (LocalData.user?.status != VERIFIED && LocalData.user?.completed == 1) {
                            LocalData.user?.isBlock = 1
                            navActions.navigateToHome()
                        } else
                            navActions.navigateToCompleteInfo()
                    }
                }
            }
        }
        composable(Destinations.HOME_SCREEN) {
            val context = LocalContext.current
            HomeView(goToMyAccount = {
                navActions.navigateToMyAccount()
            }, goToDetails = {
                navActions.navigateToOrderDetails(it)
            },
                onPending = navActions::navigateToPendingAccount, goToCost = {
                    navActions.navigateToAddCostScreen(it)
                }) { latLng, supervisorLatLbg ->
                try {
                    val mapUri =
                        Uri.parse("http://maps.google.com/maps?saddr=${latLng.latitude},${latLng.longitude} &daddr=${supervisorLatLbg.latitude},${supervisorLatLbg.longitude} &dirflg=d")
                    val intent = Intent(Intent.ACTION_VIEW, mapUri)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    context.showError(context.getString(R.string.please_download_app))
                }
            }
        }
        composable(Destinations.LOGIN_SCREEN) {
            val context = LocalContext.current
            LoginView(
                goToTerms = navActions::navigateToTermsScreen,
                goToSupport = {
                    if (LocalData.accessToken.isNullOrEmpty().not()) {
                        navActions.navigateToSupport()
                    } else {
                        context.whatsappContact(LocalData.configurations?.whatsapp ?: "")

                    }
                },
                goToHome = navActions::navigateToHome,
                goToVerify = navActions::navigateToVerify
            )
        }
        composable(Destinations.COMPLETE_INFO_SCREEN) {
            CompleteInfoScreen(
                goToPending = {
                    if (LocalData.user?.status == VERIFIED && LocalData.user?.verifiedFromManagement != NOT_VERIFIED)
                        navActions.navigateToHome()
                    else navActions.navigateToPendingAccount()
                }
            ) {
                navController.navigateUp()
            }
        }
        composable(Destinations.VERIFY_SCREEN) {
            val context = LocalContext.current
            VerifyView(goToSupport = {
                if (LocalData.accessToken.isNullOrEmpty().not()) {
                    navActions.navigateToSupport()
                } else {
                    context.whatsappContact(LocalData.configurations?.whatsapp ?: "")

                }
            }) {
                if (LocalData.user?.status == VERIFIED && LocalData.user?.completed == 1) {
                    LocalData.user?.isBlock = 0
                    if (LocalData.user?.verifiedFromManagement != NOT_VERIFIED)
                        navActions.navigateToHome()
                    else navActions.navigateToPendingAccount()
                } else {
                    navActions.navigateToCompleteInfo()
                }
            }
        }
        composable(Destinations.SUCCESS_ARGS) {
            val id = it.arguments?.getString("id") ?: ""

            SuccessView(id.toInt()) {
                navActions.navigateToHome()
            }
        }
        composable(Destinations.MY_ACCOUNT) {
            val context = LocalContext.current
            MyAccountView(
                onBack = {
                    navController.navigateUp()
                },
                goToNotification = {
                    navActions.navigateToNotification()
                },
                goToAboutApp = {
                    navActions.navigateToAboutApp()
                },
                goToTerms = {
                    navActions.navigateToTermsScreen()
                },
                goToSupport = {
                    if (LocalData.accessToken.isNullOrEmpty().not())
                        navActions.navigateToSupport()
                    else {
                        context.whatsappContact(LocalData.configurations?.whatsapp ?: "")
                    }
                },
                goToProfile = {
                    navActions.navigateToProfile()
                },
                goToWallet = {
                    navActions.navigateToWallet()
                },
                goToCurrentOrArchive = navActions::navigateToOrders,
                goToLogin = {
                    navActions.navigateToLogin()
                }
            )
            {
                navActions.navigateToHome()
            }
        }
        composable(Destinations.ORDERS_SCREEN_ARGS) {
            val caseID = it.arguments?.getString("case") ?: ""
            OrdersView(
                caseID.toInt(),
                goToDetails = {
                    navActions.navigateToOrderDetails(it)
                },
                goToCost = navActions::navigateToAddCostScreen
            ) {
                navController.navigateUp()
            }
        }
        composable(Destinations.ORDER_ROUTE_SCREEN) {
            OrderRouteView()
        }
        composable(
            Destinations.ORDER_DETAILS_ARGS,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/id={id}" })
        ) {
            val context = LocalContext.current
            navController.previousBackStackEntry?.destination?.route?.log("navController")
            val parentEntry = remember(it) {

                when (navController.previousBackStackEntry?.destination?.route) {
                    Destinations.ORDERS_SCREEN_ARGS -> navController.getBackStackEntry(navController.previousBackStackEntry?.destination?.route!!)
                    else  -> navController.getBackStackEntry(navController.previousBackStackEntry?.destination?.route!!)
                }
            }

            val parentViewModel = hiltViewModel<OrderViewModel>(parentEntry)
            parentViewModel.uiState.log("parentViewModel")
            val id = it.arguments?.getString("id") ?: ""
            OrderDetailsView(id.toInt(),
                viewModel = parentViewModel,
                goToCost = {
                    navActions.navigateToAddCostScreen(it)
                },
                onRouteClick = { latLng, supervisorLatLbg ->
                    try {
                        val mapUri =
                            Uri.parse("http://maps.google.com/maps?saddr=${latLng.latitude},${latLng.longitude} &daddr=${supervisorLatLbg.latitude},${supervisorLatLbg.longitude} &dirflg=d")
                        val intent = Intent(Intent.ACTION_VIEW, mapUri)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        context.showError(context.getString(R.string.please_download_app))
                    }
                }) {
                navController.previousBackStackEntry?.destination?.route?.log("navController")
                if (navController.previousBackStackEntry?.destination?.route == Destinations.SPLASH_SCREEN)
                    navActions.navigateToHome()
                else navController.navigateUp()
            }

            BackHandler() {
                if (navController.previousBackStackEntry?.destination?.route == Destinations.SPLASH_SCREEN)
                    navActions.navigateToHome()
                else navController.navigateUp()
            }
        }
        composable(Destinations.NOTIFICATION_SCREEN) {
            NotificationView()
        }
        composable(Destinations.ABOUT_APP_SCREEN) {
            AboutAppView()
        }
        composable(Destinations.TERMS) {
            TermsView()
        }
        composable(Destinations.TECHNICAL_SUPPORT,
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/support" })
        ) {
            SupportScreen()
            BackHandler {
                if (navController.previousBackStackEntry?.destination?.route != Destinations.SPLASH_SCREEN)
                    navController.navigateUp()
                else navActions.navigateToHome()
            }
        }

        composable(Destinations.PROFILE_SCREEN) {
            ProfileScreen(
                goToLogin = navActions::navigateToLogin
            ) {
                navController.navigateUp()
            }
        }
        composable(Destinations.WALLET_SCREEN,
            deepLinks = listOf(navDeepLink { uriPattern = "$uri/wallet" })
        ) {
            WalletScreen()
            navController.previousBackStackEntry?.destination?.route?.log("destinations")
            BackHandler {
                if (navController.previousBackStackEntry?.destination?.route != Destinations.SPLASH_SCREEN)
                    navController.navigateUp()
                else navActions.navigateToHome()
            }
        }
        composable(Destinations.PENDING_ACCOUNT_SCREEN,) {
            val context = LocalContext.current
            PendingAccountScreen(
                goToSupport = {
                    if (LocalData.accessToken.isNullOrEmpty().not())
                        navActions.navigateToSupport()
                    else {
                        context.whatsappContact(LocalData.configurations?.whatsapp ?: "")
                    }
                }
            ) {
                navActions.navigateToHome()
            }
        }
        composable(Destinations.ADD_COST_SCREEN_ARGS) {
            val id = it.arguments?.getString("id") ?: ""
            AddCostScreen(id.toInt()) {
                navActions.navigateToSuccess(id)
            }
        }
    }
}