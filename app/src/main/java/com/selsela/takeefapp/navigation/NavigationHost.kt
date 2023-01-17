package com.selsela.takeefapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.selsela.takeefapp.ui.aboutapp.AboutAppView
import com.selsela.takeefapp.ui.account.MyAccountView
import com.selsela.takeefapp.ui.address.AddressView
import com.selsela.takeefapp.ui.address.SearchAddressView
import com.selsela.takeefapp.ui.auth.CompleteInfoScreen
import com.selsela.takeefapp.ui.auth.LoginView
import com.selsela.takeefapp.ui.auth.PendingAccountScreen
import com.selsela.takeefapp.ui.auth.VerifyView
import com.selsela.takeefapp.ui.general.SuccessView
import com.selsela.takeefapp.ui.home.HomeView
import com.selsela.takeefapp.ui.intro.IntroView
import com.selsela.takeefapp.ui.notification.NotificationView
import com.selsela.takeefapp.ui.order.AddCostScreen
import com.selsela.takeefapp.ui.order.OrderDetailsView
import com.selsela.takeefapp.ui.order.OrderRouteView
import com.selsela.takeefapp.ui.order.OrdersView
import com.selsela.takeefapp.ui.order.ReviewOrderView
import com.selsela.takeefapp.ui.order.special.PlaceSpecialOrderView
import com.selsela.takeefapp.ui.order.special.SpecialOrderDetailsView
import com.selsela.takeefapp.ui.order.special.SpecialOrders
import com.selsela.takeefapp.ui.profile.ProfileScreen
import com.selsela.takeefapp.ui.splash.SplashView
import com.selsela.takeefapp.ui.support.SupportScreen
import com.selsela.takeefapp.ui.terms.TermsView
import com.selsela.takeefapp.ui.wallet.WalletScreen
import com.selsela.takeefapp.utils.LocalData

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.HOME_SCREEN,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destinations.SPLASH_SCREEN) {
            SplashView() {
                navActions.navigateToLogin()
            }
        }
        composable(Destinations.INTRO_SCREEN) {
            IntroView() {
                navActions.navigateToHome()
            }
        }
        composable(Destinations.HOME_SCREEN) {
            HomeView(goToRoute = {
                navActions.navigateToOrderRoute()

            }, goToMyAccount = {
                navActions.navigateToMyAccount()
            }) {
                navActions.navigateToAddCostScreen()
            }
        }
        composable(Destinations.LOGIN_SCREEN) {
            LoginView() {
                navActions.navigateToVerify()
            }
        }
        composable(Destinations.COMPLETE_INFO_SCREEN) {
            CompleteInfoScreen(
                goToPending = {
                    navActions.navigateToPendingAccount()
                }
            ) {
                navController.navigateUp()
            }
        }
        composable(Destinations.VERIFY_SCREEN) {
            VerifyView() {
                navActions.navigateToCompleteInfo()
            }
        }
        composable(Destinations.ADDRESS_SCREEN) {
            AddressView(
                goToSearchView = { query ->
                    val queryResult = query.ifEmpty { "none" }
                    navActions.navigateToSearchAddress(queryResult)
                }) {
                navActions.navigateToReviewOrder()
            }
        }
        composable(Destinations.SEARCH_ADDRESS_SCREEN_WITH_ARGUMENT) {
            val query = it.arguments?.getString("query") ?: ""
            SearchAddressView(query)
        }
        composable(Destinations.REVIEW_ORDER) {
            ReviewOrderView {
                navActions.navigateToSuccess()
            }
        }
        composable(Destinations.SUCCESS) {
            SuccessView(){
            }
        }
        composable(Destinations.SPECIAL_ORDER) {
            PlaceSpecialOrderView()
        }
        composable(Destinations.MY_ACCOUNT) {
            MyAccountView(
                onBack = {
                    navController.navigateUp()
                },
                goToSpecialOrders = {
                    navActions.navigateToSpecialOrders()
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
        composable(Destinations.SPECIAL_ORDERS) {
            SpecialOrders() {
                navActions.navigateToSpecialOrderDetails()
            }
        }
        composable(Destinations.SPECIAL_ORDERS_DETAILS) {
            SpecialOrderDetailsView()
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
            SupportScreen()
        }
        composable(Destinations.PROFILE_SCREEN) {
            ProfileScreen() {
                navController.navigateUp()
            }
        }
        composable(Destinations.WALLET_SCREEN) {
            WalletScreen()
        }
        composable(Destinations.PENDING_ACCOUNT_SCREEN){
            PendingAccountScreen(){
                navActions.navigateToHome()
            }
        }
        composable(Destinations.ADD_COST_SCREEN){
            AddCostScreen(){
                navActions.navigateToSuccess()
            }
        }
    }
}