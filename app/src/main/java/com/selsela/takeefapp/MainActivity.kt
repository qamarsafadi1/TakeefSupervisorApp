package com.selsela.takeefapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.selsela.takeefapp.navigation.Destinations
import com.selsela.takeefapp.navigation.Navigation.bindToolbarTitle
import com.selsela.takeefapp.navigation.Navigation.showingBackButton
import com.selsela.takeefapp.navigation.NavigationActions
import com.selsela.takeefapp.navigation.NavigationHost
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.splash.ConfigViewModel
import com.selsela.takeefapp.ui.splash.receiveToken
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.TakeefAppTheme
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.text14Meduim
import com.selsela.takeefapp.utils.Constants
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.LocalData
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    )
    @SuppressLint(
        "UnusedMaterial3ScaffoldPaddingParameter",
        "UnusedMaterialScaffoldPaddingParameter"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: ConfigViewModel by viewModels()
        setContent {
            LaunchedEffect(Unit) {
                /**
                 * Get fcm token
                 */
                /**
                 * Get fcm token
                 */
                viewModel.getConfig()
                receiveToken()
            }
            TakeefAppTheme {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val paySheet = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
                    skipHalfExpanded = true
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getSystemService(
                        LocaleManager::class.java
                    ).applicationLocales = LocaleList(Locale.forLanguageTag(LocalData.appLocal ?: "ar"))
                }
                CompositionLocalProvider(
                    LocalMutableContext provides remember { mutableStateOf(context) },
                ) {
                    CompositionLocalProvider(
                        LocalContext provides LocalMutableContext.current.value,
                    ) {
                        // Handle layout direction based on language
                        CompositionLocalProvider(
                            LocalLayoutDirection provides
                                    if (LocalData.appLocal == "ar") LayoutDirection.Rtl
                                    else LayoutDirection.Ltr
                        ) {
                            val navController = rememberNavController()
                            HandleBackgroundNotification()


                            val currentRoute =
                                navController.currentBackStackEntryFlow.collectAsState(
                                    initial = navController.currentBackStackEntry
                                )
                            FitSystemWindow(currentRoute)
                            Box(Modifier.fillMaxSize()) {
                                Scaffold(
                                    topBar = {
                                        if (currentRoute.value?.destination?.route != Destinations.HOME_SCREEN && currentRoute.value?.destination?.route != Destinations.SPLASH_SCREEN
                                            && currentRoute.value?.destination?.route != Destinations.INTRO_SCREEN
                                            && currentRoute.value?.destination?.route != Destinations.ADDRESS_SCREEN
                                            && currentRoute.value?.destination?.route != Destinations.MY_ACCOUNT
                                            && currentRoute.value?.destination?.route != Destinations.ORDER_DETAILS_ARGS
                                            && currentRoute.value?.destination?.route != Destinations.PROFILE_SCREEN
                                            && currentRoute.value?.destination?.route != Destinations.LOGIN_SCREEN
                                            && currentRoute.value?.destination?.route != Destinations.COMPLETE_INFO_SCREEN
                                            && currentRoute.value?.destination?.route != Destinations.PENDING_ACCOUNT_SCREEN
                                            && currentRoute.value?.destination?.route != Destinations.SUCCESS_ARGS
                                            && currentRoute.value?.destination?.route != Destinations.ORDERS_SCREEN_ARGS
                                        ) {
                                            if (currentRoute.value?.destination?.route != Destinations.VERIFY_SCREEN)
                                                Color.White.ChangeStatusBarColor(true)

                                            CenterAlignedTopAppBar(
                                                title = {
                                                    currentRoute.value?.let {
                                                        val title =
                                                            navController.bindToolbarTitle(
                                                                currentRoute = it
                                                            )
                                                        Text(
                                                            text = title,
                                                            style = text14Meduim,
                                                            modifier = Modifier.fillMaxWidth(),
                                                            textAlign = TextAlign.Center,
                                                            color = if (it.destination.route == Destinations.NOTIFICATION_SCREEN) Color.White
                                                            else TextColor
                                                        )
                                                    }
                                                },
                                                navigationIcon = {
                                                    currentRoute.value?.let {
                                                        val isShowing =
                                                            navController.showingBackButton(
                                                                it
                                                            )
                                                        if (isShowing) {
                                                            IconButton(onClick = {
                                                                if (navController.previousBackStackEntry?.destination?.route != Destinations.SPLASH_SCREEN)
                                                                    navController.navigateUp()
                                                                else NavigationActions(navController = navController).navigateToHome()

                                                            }) {
                                                                Icon(
                                                                    painter = painterResource(id = R.drawable.backbutton),
                                                                    contentDescription = "",

                                                                    tint =
                                                                    SecondaryColor
                                                                )
                                                            }

                                                        }
                                                    }

                                                },
                                                actions = {
                                                    BindMenuItems(currentRoute)
                                                },
                                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                                    containerColor =
                                                    if (
                                                        currentRoute.value?.destination?.route != Destinations.VERIFY_SCREEN &&
                                                        currentRoute.value?.destination?.route != Destinations.NOTIFICATION_SCREEN
                                                    )
                                                        Color.White
                                                    else TextColor
                                                )
                                            )
                                        }
                                    }
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        color = Color.White
                                    ) {
                                        NavigationHost(navController)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsBuilder(
                Manifest.permission.POST_NOTIFICATIONS
            ).build().send()
        }
    }


    override fun onResume() {
        super.onResume()

    }

    @Composable
    private fun HandleBackgroundNotification() {
        if (intent != null && intent.extras != null) {
            val bundle = intent.extras
            if (bundle?.containsKey("action") == true) {
                val intent = when (bundle.getString("action")) {
                  "new_order","accept_additional_cost","paid_additional_cost","reject_additional_cost" -> {
                        val orderID = bundle.getString("order_id")
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://airconditionersupervisor.com/id=${orderID}".toUri(),
                            applicationContext,
                            MainActivity::class.java
                        )
                    }
                    Constants.WALLET_CHANGED->{
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://airconditionersupervisor.com/wallet".toUri(),
                            applicationContext,
                            MainActivity::class.java
                        )
                    }
                    Constants.ADMIN_REPLIED ->{
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://airconditionersupervisor.com/support".toUri(),
                            applicationContext,
                            MainActivity::class.java
                        )
                    }
                    else -> null
                }
                if (intent != null) {
                    val pendingIntent =
                        PendingIntent.getActivity(
                            this, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    pendingIntent.send()
                }
            }
        }
    }


    @Composable
    private fun BindMenuItems(currentRoute: State<NavBackStackEntry?>) {

    }

    @Composable
    private fun FitSystemWindow(currentRoute: State<NavBackStackEntry?>) {
        "${currentRoute.value?.destination?.route}".log("ROUTE")
        if (currentRoute.value?.destination?.route == Destinations.MY_ACCOUNT
            || currentRoute.value?.destination?.route == Destinations.SPLASH_SCREEN
            || currentRoute.value?.destination?.route == Destinations.ADDRESS_SCREEN
            || currentRoute.value?.destination?.route == Destinations.ORDER_DETAILS_ARGS
            || currentRoute.value?.destination?.route == Destinations.COMPLETE_INFO_SCREEN
            || currentRoute.value?.destination?.route == Destinations.PROFILE_SCREEN
            || currentRoute.value?.destination?.route == Destinations.SUCCESS_ARGS
            || currentRoute.value?.destination?.route == Destinations.ORDERS_SCREEN_ARGS
        ) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }

}

val LocalMutableContext = staticCompositionLocalOf<MutableState<Context>> {
    error("LocalMutableContext not provided")
}
