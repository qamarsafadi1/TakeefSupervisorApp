package com.selsela.takeefapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.selsela.takeefapp.ui.aboutapp.AboutAppView
import com.selsela.takeefapp.ui.account.MyAccountView
import com.selsela.takeefapp.ui.auth.CompleteInfoScreen
import com.selsela.takeefapp.ui.auth.LoginView
import com.selsela.takeefapp.ui.auth.PendingAccountScreen
import com.selsela.takeefapp.ui.auth.VerifyView
import com.selsela.takeefapp.ui.general.SuccessView
import com.selsela.takeefapp.ui.home.HomeView
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
import com.selsela.takeefapp.utils.LocalData

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.SPLASH_SCREEN,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destinations.SPLASH_SCREEN) {
            SplashView() {
                if (LocalData.accessToken.isNullOrEmpty())
                    navActions.navigateToLogin()
                else {
                    LocalData.user?.status?.log("status")
                    LocalData.user?.completed?.log("status")
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
            HomeView(goToRoute = {
                navActions.navigateToOrderRoute()

            }, goToMyAccount = {
                navActions.navigateToMyAccount()
            },
                goToDetails = {
                    navActions.navigateToOrderDetails()
                },
                onPending = navActions::navigateToPendingAccount
            ) {
                navActions.navigateToAddCostScreen()
            }
        }
        composable(Destinations.LOGIN_SCREEN) {
            LoginView(
                goToTerms = navActions::navigateToTermsScreen,
                goToSupport = navActions::navigateToSupport,
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
            VerifyView() {
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
        composable(Destinations.SUCCESS) {
            SuccessView() {
                navActions.navigateToHome()
            }
        }
        composable(Destinations.MY_ACCOUNT) {
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
                    navActions.navigateToSupport()
                },
                goToProfile = {
                    navActions.navigateToProfile()
                },
                goToWallet = {
                    navActions.navigateToWallet()
                }
            )
            {
                navActions.navigateToOrders()
            }
        }
        composable(Destinations.ORDERS_SCREEN) {
            OrdersView(
                goToDetails = {
                    navActions.navigateToOrderDetails()
                }
            ) {
                navActions.navigateToOrderRoute()
            }
        }
        composable(Destinations.ORDER_ROUTE_SCREEN) {
            OrderRouteView()
        }
        composable(Destinations.ORDER_DETAILS) {
            OrderDetailsView() {
                navController.navigateUp()
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
        composable(Destinations.TECHNICAL_SUPPORT) {
            SupportScreen() {
                navController.navigateUp()
            }
        }

        composable(Destinations.PROFILE_SCREEN) {
            ProfileScreen() {
                navController.navigateUp()
            }
        }
        composable(Destinations.WALLET_SCREEN) {
            WalletScreen()
        }
        composable(Destinations.PENDING_ACCOUNT_SCREEN) {
            PendingAccountScreen() {
                navActions.navigateToHome()
            }
        }
        composable(Destinations.ADD_COST_SCREEN) {
            AddCostScreen() {
                navActions.navigateToSuccess()
            }
        }
    }
}