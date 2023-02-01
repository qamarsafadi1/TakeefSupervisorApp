package com.selsela.takeefapp.ui.auth

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selsela.jobsapp.utils.validatePhone
import com.selsela.jobsapp.utils.validateRequired
import com.selsela.takeefapp.R
import com.selsela.takeefapp.data.auth.model.auth.User
import com.selsela.takeefapp.data.auth.model.notifications.Notification
import com.selsela.takeefapp.data.auth.model.support.ContactReplies
import com.selsela.takeefapp.data.auth.model.support.contacts.Contact
import com.selsela.takeefapp.data.auth.model.wallet.WalletResponse
import com.selsela.takeefapp.data.auth.repository.AuthRepository
import com.selsela.takeefapp.data.config.model.city.Area
import com.selsela.takeefapp.data.config.model.city.Children
import com.selsela.takeefapp.data.config.model.city.City
import com.selsela.takeefapp.ui.theme.BorderColor
import com.selsela.takeefapp.ui.theme.Red
import com.selsela.takeefapp.utils.Constants.NOT_VERIFIED
import com.selsela.takeefapp.utils.Extensions.Companion.log
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.retrofit.model.ErrorsData
import com.selsela.takeefapp.utils.retrofit.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * UiState for the Auth
 */
data class AuthUiState(
    val responseMessage: String = "",
    val user: User? = LocalData.user,
    val onSuccess: StateEventWithContent<String> = consumed(),
    val isLoading: Boolean = false,
    val onFailure: StateEventWithContent<ErrorsData> = consumed(),
)

data class WalletUiState(
    val responseMessage: String = "",
    val wallet: WalletResponse? = LocalData.userWallet,
    val onSuccess: StateEvent = consumed,
    val isLoading: Boolean = false,
    val onFailure: StateEventWithContent<ErrorsData> = consumed(),
)

data class NotificationUiState(
    val notifications: List<Notification>? = listOf(),
    val isLoading: Boolean = false,
    val onFailure: StateEventWithContent<ErrorsData> = consumed(),
)

data class SupportUiState(
    val contacts: List<ContactReplies>? = listOf(),
    val contactReplay: ContactReplies? = null,
    val isLoading: Boolean = false,
    val onSuccess: StateEventWithContent<Int> = consumed(),
    val onFailure: StateEventWithContent<ErrorsData> = consumed(),
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val application: Application,
    private val repository: AuthRepository
) : ViewModel() {

    /**
     * Validation Variables
     */
    var mobile: MutableState<String> = mutableStateOf(
        if (LocalData.user?.status != NOT_VERIFIED)
            LocalData.user?.mobile ?: ""
        else ""
    )
    var name: MutableState<String> = mutableStateOf(LocalData.user?.name ?: "")
    var email: MutableState<String> = mutableStateOf(LocalData.user?.email ?: "")
    var areaID: MutableState<Int> = mutableStateOf(-1)
    var cityId: MutableState<Int> = mutableStateOf(-1)
    var districtID: MutableState<Int> = mutableStateOf(-1)
    var code: MutableState<String> = mutableStateOf("")
    var errorMessage: MutableState<String> = mutableStateOf("")
    var errorMessageName: MutableState<String> = mutableStateOf("")
    var errorMessageCity: MutableState<String> = mutableStateOf("")
    var errorMessageArea: MutableState<String> = mutableStateOf("")
    var selectedAreaName = mutableStateOf("")
    var selectedCityName = mutableStateOf("")
    var selectedDistrictName = mutableStateOf("")
    var errorMessageDistrict: MutableState<String> = mutableStateOf("")
    var isValid: MutableState<Boolean> = mutableStateOf(true)
    var isNameValid: MutableState<Boolean> = mutableStateOf(true)
    var isCityValid: MutableState<Boolean> = mutableStateOf(true)
    var isAreaValid: MutableState<Boolean> = mutableStateOf(true)
    var isDistrictValid: MutableState<Boolean> = mutableStateOf(true)
    var avatar: File? = null
    var isLoaded = false
    var message: MutableState<String> = mutableStateOf("")
    var contactId = -1
    val userLoggedIn = mutableStateOf(LocalData.accessToken.isNullOrEmpty().not())
    val user = mutableStateOf(LocalData.user)

    /**
     * State Subscribers
     */
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    private val _walletUiState = MutableStateFlow(WalletUiState())
    val walletUiState: StateFlow<WalletUiState> = _walletUiState.asStateFlow()
    private val _notificationUiState = MutableStateFlow(NotificationUiState())
    val notificationUiState: StateFlow<NotificationUiState> = _notificationUiState.asStateFlow()
    private val _contactUiState = MutableStateFlow(SupportUiState())
    val contactUiState: StateFlow<SupportUiState> = _contactUiState.asStateFlow()

    private var state: AuthUiState
        get() = _uiState.value
        set(newState) {
            _uiState.update { newState }
        }
    private var walletState: WalletUiState
        get() = _walletUiState.value
        set(newState) {
            _walletUiState.update { newState }
        }
    private var notificationState: NotificationUiState
        get() = _notificationUiState.value
        set(newState) {
            _notificationUiState.update { newState }
        }
    private var supportState: SupportUiState
        get() = _contactUiState.value
        set(newState) {
            _contactUiState.update { newState }
        }

    /**
     * Form Validation
     */
    private fun isMobileValid(): Boolean {
        val message = mobile.value.validatePhone(application.applicationContext)
        if (message == "") {
            isValid.value = true
        } else {
            isValid.value = false
            errorMessage.value = message
        }
        return isValid.value
    }

    private fun isCodeValid(): Boolean {
        code.value.log("code.value")
        val message = code.value.validateRequired(
            application.applicationContext, application.getString(
                R.string.verify_code
            )
        )
        if (message == "") {
            if (code.value != LocalData.user?.activationCode) {
                isValid.value = false
                errorMessage.value = application.getString(
                    R.string.unvalid_code
                )
            } else isValid.value = true
        } else {
            isValid.value = false
            errorMessage.value = message
        }
        return isValid.value
    }

    private fun profileInfoIsValid(): Boolean {
        val nameValidationMessage = name.value.validateRequired(
            application.applicationContext, application.getString(
                R.string.name
            )
        )
        val cityValidationMessage = cityId.value.validateRequired(
            application.applicationContext, application.getString(
                R.string.city
            )
        )
        val areaValidationMessage = areaID.value.validateRequired(
            application.applicationContext, application.getString(
                R.string.area
            )
        )
        val districtValidationMessage = districtID.value.validateRequired(
            application.applicationContext, application.getString(
                R.string.district
            )
        )
        if (nameValidationMessage == "" &&
            cityValidationMessage == "" && areaValidationMessage == "" &&
            districtValidationMessage == ""
        ) {
            isNameValid.value = true
            isCityValid.value = true
            isAreaValid.value = true
            isDistrictValid.value = true
            isValid.value = true
        } else {
            if (nameValidationMessage != "" &&
                cityValidationMessage != "" && areaValidationMessage != "" &&
                districtValidationMessage != ""
            ) {
                isValid.value = false
                isNameValid.value = false
                isCityValid.value = false
                isAreaValid.value = false
                isDistrictValid.value = false
                errorMessageCity.value = cityValidationMessage
                errorMessageArea.value = areaValidationMessage
                errorMessageDistrict.value = districtValidationMessage
                errorMessageName.value = nameValidationMessage
            } else {
                if (nameValidationMessage != "") {
                    errorMessageName.value = nameValidationMessage
                    errorMessage.value = ""
                    errorMessageCity.value = ""
                    errorMessageArea.value = ""
                    errorMessageDistrict.value = ""
                    isValid.value = true
                    isAreaValid.value = true
                    isCityValid.value = true
                    isDistrictValid.value = true
                    isNameValid.value = false
                    return false
                }
                if (areaValidationMessage != "") {
                    errorMessageArea.value = areaValidationMessage
                    errorMessageName.value = ""
                    errorMessageCity.value = ""
                    errorMessageDistrict.value = ""
                    isAreaValid.value = false
                    isNameValid.value = true
                    isCityValid.value = true
                    isDistrictValid.value = true
                    isNameValid.value = true
                    return false
                }
                if (cityValidationMessage != "") {
                    errorMessageCity.value = cityValidationMessage
                    errorMessageName.value = ""
                    errorMessageArea.value = ""
                    errorMessageDistrict.value = ""
                    isAreaValid.value = true
                    isNameValid.value = true
                    isCityValid.value = false
                    isDistrictValid.value = true
                    isNameValid.value = true
                    return false
                } else {
                    errorMessageCity.value = ""
                    errorMessageName.value = ""
                    errorMessageArea.value = ""
                    errorMessageDistrict.value = districtValidationMessage
                    isAreaValid.value = true
                    isNameValid.value = true
                    isCityValid.value = true
                    isDistrictValid.value = false
                    isNameValid.value = true
                    return false
                }
            }
        }
        return isNameValid.value && isCityValid.value && isAreaValid.value && isDistrictValid.value
    }

    fun validateBorderColor(): Color {
        return if (errorMessage.value.isNotEmpty() && isValid.value.not())
            Red
        else BorderColor
    }

    fun validateNameBorderColor(): Color {
        return if (errorMessageName.value.isNotEmpty() && isNameValid.value.not())
            Red
        else BorderColor
    }

    fun validateCityBorderColor(): Color {
        return if (errorMessageCity.value.isNotEmpty() && isCityValid.value.not())
            Red
        else BorderColor
    }

    fun validateAreaBorderColor(): Color {
        return if (errorMessageArea.value.isNotEmpty() && isAreaValid.value.not())
            Red
        else BorderColor
    }

    fun validateDisrtictBorderColor(): Color {
        return if (errorMessageDistrict.value.isNotEmpty() && isDistrictValid.value.not())
            Red
        else BorderColor
    }

    fun setSelectedArea(area: String, areaId: Int) {
        selectedAreaName.value = area
        areaID.value = areaId
    }

    fun setSelectedCity(city: String, cityId: Int) {
        selectedCityName.value = city
        this.cityId.value = cityId
    }

    fun setSelectedDistrict(city: String, cityId: Int) {
        selectedDistrictName.value = city
        districtID.value = cityId
    }


    fun getCitiesOfAreas(): List<City> {
        return LocalData.ciites?.find {
            it.id == areaID.value
        }?.cities ?: listOf()
    }

    fun getDistrictOfCities(): List<Children> {
        return LocalData.ciites?.find {
            it.id == areaID.value
        }?.cities?.find { it.id == cityId.value }?.children ?: listOf()
    }

    fun <T> searchCities(query: String, list: List<T>?): MutableState<List<T>?> {
        val cities = if (query == "")
            list
        else {
            list?.filter {
                when (it) {
                    is Area -> it.name.contains(query)
                    is City -> it.name.contains(query)
                    else -> (it as Children).name.contains(query)
                }
            }
        }
        return mutableStateOf(cities)
    }


    /////////////////////////////////////////  API REQUESTS  ///////////////////////////////////////

    /**
     * API Requests
     */
    fun auth() {
        if (isMobileValid()) {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true
                )
                repository.auth(mobile.value)
                    .collect { result ->
                        val authUiState = when (result.status) {
                            Status.SUCCESS -> {
                                AuthUiState(
                                    responseMessage = result.message ?: "",
                                    onSuccess = triggered(result.data?.status ?: ""),
                                )
                            }

                            Status.LOADING ->
                                AuthUiState(
                                    isLoading = true
                                )

                            Status.ERROR -> AuthUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                ),
                                responseMessage = result.message ?: "",
                            )
                        }
                        state = authUiState
                    }
            }
        }
    }

    fun verifyCode() {
        if (isCodeValid()) {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true
                )
                repository.verifyCode(code = code.value)
                    .collect { result ->
                        val authUiState = when (result.status) {
                            Status.SUCCESS -> {
                                AuthUiState(
                                    responseMessage = result.message ?: "",
                                    onSuccess = triggered(result.data?.status ?: ""),
                                )
                            }

                            Status.LOADING ->
                                AuthUiState(
                                    isLoading = true
                                )

                            Status.ERROR -> AuthUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                ),
                                responseMessage = result.message ?: "",
                            )
                        }
                        state = authUiState
                    }
            }
        }
    }

    fun resendCode() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )
            repository.resendCode()
                .collect { result ->
                    val authUiState = when (result.status) {
                        Status.SUCCESS -> {
                            AuthUiState(
                                responseMessage = result.message ?: "",
                                onSuccess = triggered(result.data?.status ?: ""),
                            )
                        }

                        Status.LOADING ->
                            AuthUiState(
                                isLoading = true
                            )

                        Status.ERROR -> AuthUiState(
                            onFailure = triggered(
                                ErrorsData(
                                    result.errors,
                                    result.message,
                                )
                            ),
                            responseMessage = result.message ?: "",
                        )
                    }
                    state = authUiState
                }
        }
    }

    fun completeInfo() {
        if (profileInfoIsValid()) {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true
                )
                repository.completeInfo(
                    name.value,
                    areaID.value,
                    cityId.value,
                    districtID.value
                )
                    .collect { result ->
                        val authUiState = when (result.status) {
                            Status.SUCCESS -> {
                                AuthUiState(
                                    responseMessage = result.message ?: "",
                                    onSuccess = triggered(result.data?.status ?: ""),
                                )
                            }

                            Status.LOADING ->
                                AuthUiState(
                                    isLoading = true
                                )

                            Status.ERROR -> AuthUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                ),
                                responseMessage = result.message ?: "",
                            )
                        }
                        state = authUiState
                    }
            }
        }
    }


    fun me() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )
            repository.meRequest()
                .collect { result ->
                    val authUiState = when (result.status) {
                        Status.SUCCESS -> {
                            isLoaded = true
                            AuthUiState(
                                responseMessage = result.message ?: "",
                                onSuccess = triggered(result.data?.status ?: ""),
                                user = result.data
                            )
                        }

                        Status.LOADING ->
                            AuthUiState(
                                isLoading = true
                            )

                        Status.ERROR -> AuthUiState(
                            onFailure = triggered(
                                ErrorsData(
                                    result.errors,
                                    result.message,
                                )
                            ),
                            responseMessage = result.message ?: "",
                        )
                    }
                    state = authUiState
                }
        }
    }

    fun updateFcm() {
        viewModelScope.launch {
            repository.updateFCM()
        }
    }

    fun updateProfile() {
        if (profileInfoIsValid()) {
            viewModelScope.launch {
                state = state.copy(
                    isLoading = true
                )
                repository.updateProfile(
                    avatar = avatar,
                    name = name.value,
                    email = email.value,
                    mobile = mobile.value
                )
                    .collect { result ->
                        val authUiState = when (result.status) {
                            Status.SUCCESS -> {
                                AuthUiState(
                                    responseMessage = result.message ?: "",
                                    onSuccess = triggered(result.message ?: ""),
                                )
                            }

                            Status.LOADING ->
                                AuthUiState(
                                    isLoading = true
                                )

                            Status.ERROR -> AuthUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                ),
                                responseMessage = result.message ?: "",
                            )
                        }
                        state = authUiState
                    }
            }
        }
    }

    fun wallet() {
        viewModelScope.launch {
            walletState = walletState.copy(
                isLoading = true
            )
            repository.getWallet()
                .collect { result ->
                    val walletUiState = when (result.status) {
                        Status.SUCCESS -> {
                            isLoaded = true
                            WalletUiState(
                                responseMessage = result.message ?: "",
                                onSuccess = triggered,
                                wallet = result.data
                            )
                        }

                        Status.LOADING ->
                            WalletUiState(
                                isLoading = true
                            )

                        Status.ERROR ->
                            WalletUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                ),
                                responseMessage = result.message ?: "",
                            )
                    }
                    walletState = walletUiState
                }
        }
    }

    fun getNotification() {
        viewModelScope.launch {
            notificationState = notificationState.copy(
                isLoading = true
            )
            repository.getNotification()
                .collect { result ->
                    val notificationUiState = when (result.status) {
                        Status.SUCCESS -> {
                            isLoaded = true
                            NotificationUiState(
                                notifications = result.data?.notifications
                            )
                        }

                        Status.LOADING ->
                            NotificationUiState(
                                isLoading = true
                            )

                        Status.ERROR ->
                            NotificationUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                )
                            )
                    }
                    notificationState = notificationUiState
                }
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            notificationState = notificationState.copy(
                isLoading = true
            )
            repository.deleteNotification(id)
                .collect { result ->
                    val notificationUiState = when (result.status) {
                        Status.SUCCESS -> {
                            isLoaded = true
                            NotificationUiState(
                                notifications = result.data?.notifications
                            )
                        }

                        Status.LOADING ->
                            NotificationUiState(
                                isLoading = true
                            )

                        Status.ERROR ->
                            NotificationUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                )
                            )
                    }
                    notificationState = notificationUiState
                }
        }
    }

    fun getContacts() {
        viewModelScope.launch {
            supportState = supportState.copy(
                isLoading = true
            )
            repository.getContacts()
                .collect { result ->
                    val supportUiState = when (result.status) {
                        Status.SUCCESS -> {
                            isLoaded = true
                            SupportUiState(
                                contacts = result.data,
                                onSuccess = triggered(
                                    if (result.data.isNullOrEmpty().not()) result.data?.last()?.id
                                        ?: -1
                                    else -1
                                ),
                                contactReplay = if (result.data.isNullOrEmpty()
                                        .not()
                                ) result.data?.last()
                                else null

                            )
                        }

                        Status.LOADING ->
                            SupportUiState(
                                isLoading = true
                            )

                        Status.ERROR ->
                            SupportUiState(
                                onFailure = triggered(
                                    ErrorsData(
                                        result.errors,
                                        result.message,
                                    )
                                )
                            )
                    }
                    supportState = supportUiState
                }
        }
    }

    fun contactOrReplay() {
        viewModelScope.launch {
            supportState = supportState.copy(
                isLoading = true
            )
            val request = if (contactId == -1) repository.contactAdmin(message.value)
            else repository.replySupport(
                message.value,
                contact_id = contactId
            )
            request.collect { result ->
                val supportUiState = when (result.status) {
                    Status.SUCCESS -> {
                        isLoaded = true
                        SupportUiState(
                            contactReplay = result.data?.contactReplies,
                        )
                    }

                    Status.LOADING ->
                        SupportUiState(
                            isLoading = true
                        )

                    Status.ERROR ->
                        SupportUiState(
                            onFailure = triggered(
                                ErrorsData(
                                    result.errors,
                                    result.message,
                                )
                            )
                        )
                }
                supportState = supportUiState
            }
        }
    }

    /**
     * reset handlers
     */
    fun onSuccess() {
        state = state.copy(onSuccess = consumed())
        walletState = walletState.copy(onSuccess = consumed)
    }

    fun onFailure() {
        state = state.copy(onFailure = consumed())
        walletState = walletState.copy(onFailure = consumed())
    }

}
