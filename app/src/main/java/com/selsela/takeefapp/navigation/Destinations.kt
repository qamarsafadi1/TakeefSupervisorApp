package com.selsela.takeefapp.navigation

import androidx.navigation.NavController
import com.selsela.takeefapp.navigation.Navigation.navigateWithClearBackStack

object Destinations {
    const val SPLASH_SCREEN = "splash"
    const val INTRO_SCREEN = "intro"
    const val HOME_SCREEN = "home"
    const val LOGIN_SCREEN = "login"
    const val VERIFY_SCREEN = "verify"
    const val COMPLETE_INFO_SCREEN = "complete_info_screen"
    const val PENDING_ACCOUNT_SCREEN = "pending_account"
    const val ADD_COST_SCREEN = "add_cost_screen"
    const val ADDRESS_SCREEN = "address"
    const val SUCCESS = "success"
    const val ERROR = "error"
    const val SPECIAL_ORDER = "special_order"
    const val MY_ACCOUNT = "my_account"
    const val ORDERS_SCREEN = "orders"
    const val ORDER_ROUTE_SCREEN = "order_route_screen"
    const val ORDER_DETAILS = "order_details"
    const val NOTIFICATION_SCREEN = "notification_screen"
    const val ABOUT_APP_SCREEN = "about_app_screen"
    const val TERMS = "terms"
    const val TECHNICAL_SUPPORT = "technical_support"
    const val PROFILE_SCREEN = "profile_screen"
    const val WALLET_SCREEN = "wallet_screen"
}

class NavigationActions(private val navController: NavController) {
    fun navigateToHome() {
        navController.navigate(Destinations.HOME_SCREEN) {
            navigateWithClearBackStack(navController)
        }
    }
    fun navigateToLogin() {
        navController.navigate(Destinations.LOGIN_SCREEN)
    }

    fun navigateToVerify() {
        navController.navigate(Destinations.VERIFY_SCREEN)
    }

    fun navigateToCompleteInfo() {
        navController.navigate(Destinations.COMPLETE_INFO_SCREEN)
    }
    fun navigateToSuccess() {
        navController.navigate(Destinations.SUCCESS)
    }

    fun navigateToMyAccount() {
        navController.navigate(Destinations.MY_ACCOUNT)
    }

    fun navigateToOrders() {
        navController.navigate(Destinations.ORDERS_SCREEN)
    }

    fun navigateToOrderRoute() {
        navController.navigate(Destinations.ORDER_ROUTE_SCREEN)
    }

    fun navigateToOrderDetails() {
        navController.navigate(Destinations.ORDER_DETAILS)
    }
    fun navigateToNotification() {
        navController.navigate(Destinations.NOTIFICATION_SCREEN)
    }

    fun navigateToAboutApp() {
        navController.navigate(Destinations.ABOUT_APP_SCREEN)
    }

    fun navigateToTermsScreen() {
        navController.navigate(Destinations.TERMS)
    }

    fun navigateToSupport() {
        navController.navigate(Destinations.TECHNICAL_SUPPORT)
    }
    fun navigateToProfile() {
        navController.navigate(Destinations.PROFILE_SCREEN)
    }

    fun navigateToWallet() {
        navController.navigate(Destinations.WALLET_SCREEN)
    }
    fun navigateToPendingAccount(){
        navController.navigate(Destinations.PENDING_ACCOUNT_SCREEN)
    }
    fun navigateToAddCostScreen(){
        navController.navigate(Destinations.ADD_COST_SCREEN)
    }
}