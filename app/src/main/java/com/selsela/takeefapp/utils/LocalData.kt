package com.selsela.takeefapp.utils

import com.orhanobut.hawk.Hawk
import com.selsela.takeefapp.data.auth.model.auth.User
import com.selsela.takeefapp.data.auth.model.wallet.WalletResponse
import com.selsela.takeefapp.data.config.model.AcType
import com.selsela.takeefapp.data.config.model.Case
import com.selsela.takeefapp.data.config.model.Configurations
import com.selsela.takeefapp.data.config.model.RateProperitiesSupervisor
import com.selsela.takeefapp.data.config.model.Service
import com.selsela.takeefapp.data.config.model.WorkPeriod
import com.selsela.takeefapp.data.config.model.city.Area
import com.selsela.takeefapp.data.config.model.payments.Payment


open class LocalData {
    companion object {

        fun clearData() {
            accessToken = ""
            user = null
            userWallet = null
        }

        var accessToken: String? = Hawk.get("token", "")
            set(value) {
                field = value
                Hawk.put("token", value)
            }
        var user: User? = Hawk.get("user")
            set(value) {
                field = value
                Hawk.put("user", value)
            }
        var appLocal: String = Hawk.get("appLocal", "ar")
            set(value) {
                field = value
                Hawk.put("appLocal", value)
            }
    var isOnApp: Boolean = Hawk.get("isOnApp", false)
            set(value) {
                field = value
                Hawk.put("isOnApp", value)
            }

        var firstLaunch: Boolean = Hawk.get("firstLaunch", true)
            set(value) {
                field = value
                Hawk.put("firstLaunch", value)
            }
        var fcmToken: String = Hawk.get("fcmToken", "")
            set(value) {
                field = value
                Hawk.put("fcmToken", value)
            }


        var configurations: Configurations? = Hawk.get("configurations")
            set(value) {
                field = value
                Hawk.put("configurations", value)
            }
        var cases: List<Case>? = Hawk.get("cases")
            set(value) {
                field = value
                Hawk.put("cases", value)
            }

        var terms: com.selsela.takeefapp.data.config.model.page.Page? = Hawk.get("terms")
            set(value) {
                field = value
                Hawk.put("terms", value)
            }
        var aboutApp: com.selsela.takeefapp.data.config.model.page.Page? = Hawk.get("aboutApp")
            set(value) {
                field = value
                Hawk.put("aboutApp", value)
            }
        var ciites: List<Area>? = Hawk.get("ciites")
            set(value) {
                field = value
                Hawk.put("ciites", value)
            }
        var paymentsType: List<Payment>? = Hawk.get("paymentsType")
            set(value) {
                field = value
                Hawk.put("paymentsType", value)
            }
        var acTypes: List<AcType>? = Hawk.get("acTypes")
            set(value) {
                field = value
                Hawk.put("acTypes", value)
            }

        var services: List<Service>? = Hawk.get("services")
            set(value) {
                field = value
                Hawk.put("services", value)
            }
        var workPeriods: List<WorkPeriod>? = Hawk.get("workPeriods")
            set(value) {
                field = value
                Hawk.put("workPeriods", value)
            }

        var rateItems: List<RateProperitiesSupervisor>? = Hawk.get("rateItems")
            set(value) {
                field = value
                Hawk.put("rateItems", value)
            }
        var userWallet: WalletResponse? = Hawk.get("wallet")
            set(value) {
                field = value
                Hawk.put("wallet", value)
            }
    }

}