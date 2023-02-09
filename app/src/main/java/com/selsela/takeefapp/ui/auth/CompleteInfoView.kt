package com.selsela.takeefapp.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.common.AppLogoImage
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.InputEditText
import com.selsela.takeefapp.ui.common.ListedBottomSheet
import com.selsela.takeefapp.ui.splash.ChangeNavigationBarColor
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.theme.BorderColor
import com.selsela.takeefapp.ui.theme.Red
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.SecondaryColor2
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.TextFieldBg
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.ui.theme.text16Medium
import com.selsela.takeefapp.utils.Common
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.showError
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CompleteInfoScreen(
    vm: AuthViewModel = hiltViewModel(),
    goToPending: () -> Unit,
    onBack: () -> Unit
) {
    Color.Transparent.ChangeStatusBarColor(true)
    TextColor.ChangeNavigationBarColor()

    val viewState: AuthUiState by vm.uiState.collectAsStateLifecycleAware(AuthUiState())
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val citySheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val areaSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val districtSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )

    CompleteInfoContent(
        vm,
        viewState,
        coroutineScope,
        areaSheetState,
        citySheetState,
        districtSheetState,
        vm::completeInfo,
        onBack
    )

    /**
     * Handle Ui state from flow
     */

    EventEffect(
        event = viewState.onSuccess,
        onConsumed = vm::onSuccess
    ) { status ->
        vm.updateFcm()
        goToPending()
    }

    EventEffect(
        event = viewState.onFailure,
        onConsumed = vm::onFailure
    ) { error ->
        Common.handleErrors(
            error.responseMessage,
            error.errors,
            context
        )
    }

}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun CompleteInfoContent(
    vm: AuthViewModel,
    viewState: AuthUiState,
    coroutineScope: CoroutineScope,
    areaSheetState: ModalBottomSheetState,
    citySheetState: ModalBottomSheetState,
    districtSheetState: ModalBottomSheetState,
    goToPending: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 0.dp)
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(TextColor, RoundedCornerShape(33.dp))
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CompleteInfoForm(
                    vm,
                    onArea = {
                        coroutineScope.launch {
                            if (areaSheetState.isVisible)
                                areaSheetState.hide()
                            else areaSheetState.animateTo(ModalBottomSheetValue.Expanded)

                        }
                    },
                    onCity = {
                        coroutineScope.launch {
                            if (citySheetState.isVisible) {
                                citySheetState.hide()
                            } else {
                                citySheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    }
                ) {
                    coroutineScope.launch {
                        if (districtSheetState.isVisible) {
                            districtSheetState.hide()
                        } else {
                            districtSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    }
                }
            }
            AppLogoImage(
                modifier = Modifier
                    .paddingTop(85)
                    .width(139.dp)
                    .height(39.dp)
            )

            ElasticButton(
                onClick = {
                    goToPending()
                }, title = stringResource(id = R.string.send_order),
                modifier = Modifier
                    .padding(bottom = 73.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .requiredHeight(48.dp)
                    .align(Alignment.BottomCenter),
                isLoading = viewState.isLoading
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .requiredHeight(88.dp)
                .padding(top = 30.dp)
                .padding(horizontal = 6.dp),
        ) {

            IconButton(
                onClick = { onBack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.backbutton),
                    contentDescription = ""
                )
            }
        }

        ListedBottomSheet(
            viewModel = vm,
            sheetState = areaSheetState,
            title = stringResource(id = R.string.area),
            ciites = LocalData.ciites,
            onSelectedItem = vm::setSelectedArea
        ) {
            coroutineScope.launch {
                if (areaSheetState.isVisible)
                    areaSheetState.hide()
                else
                    areaSheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }

        ListedBottomSheet(
            viewModel = vm,
            sheetState = citySheetState,
            title = stringResource(id = R.string.city),
            ciites = vm.getCitiesOfAreas(),
            onSelectedItem = vm::setSelectedCity
        ) {
            coroutineScope.launch {
                if (citySheetState.isVisible)
                    citySheetState.hide()
                else
                    citySheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }
        ListedBottomSheet(
            viewModel = vm,
            sheetState = districtSheetState,
            title = stringResource(id = R.string.district),
            ciites = vm.getDistrictOfCities(),
            onSelectedItem = vm::setSelectedDistrict
        ) {
            coroutineScope.launch {
                if (districtSheetState.isVisible)
                    districtSheetState.hide()
                else
                    districtSheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }
//        ListedBottomSheet(sheetState = districtSheetState)


    }
}

@Composable
private fun CompleteInfoForm(
    vm: AuthViewModel,
    onArea: () -> Unit,
    onCity: () -> Unit,
    onDistrict: () -> Unit
) {
    val context = LocalContext.current
    Column(
        Modifier
            .padding(bottom = 39.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.welcome),
            style = text12,
            color = Color.White,
            modifier = Modifier
                .paddingTop(34)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.complete_info),
            modifier = Modifier
                .paddingTop(12)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = text16Medium,
            color = Color.White
        )

        Text(
            text = stringResource(R.string.full_name),
            style = text11,
            color = SecondaryColor2.copy(0.85f),
            modifier = Modifier.padding(top = 29.dp)
        )
        InputEditText(
            onValueChange = {
                vm.name.value = it
                if (it.isEmpty() || vm.isNameValid.value.not()) {
                    vm.isNameValid.value = true
                    vm.errorMessageName.value = ""
                }
            },
            text = vm.name.value,
            hint = stringResource(R.string.full_name),
            inputType = KeyboardType.Text,
            modifier = Modifier.padding(top = 16.dp)
                .fillMaxWidth(1f)
                .requiredHeight(48.dp),
            isValid = vm.isNameValid.value,
            validationMessage = vm.errorMessageName.value,
            borderColor = vm.validateNameBorderColor()

        )
        Text(
            text = stringResource(R.string.area),
            style = text11,
            color = SecondaryColor2.copy(0.85f),
            modifier = Modifier.padding(top = 16.dp)
        )
        AreaView(
            title = vm.selectedAreaName.value,
            isValid = vm.isAreaValid.value,
            validationMessage = vm.errorMessageArea.value,
            borderColor = vm.validateAreaBorderColor()
        ) {
            onArea()
            if (vm.isAreaValid.value.not()) {
                vm.isAreaValid.value = true
                vm.errorMessageCity.value = ""
            }
        }
        Text(
            text = stringResource(R.string.city),
            style = text11,
            color = SecondaryColor2.copy(0.85f),
            modifier = Modifier.padding(top = 16.dp)
        )
        CityView(
            title = vm.selectedCityName.value,
            isValid = vm.isCityValid.value,
            validationMessage = vm.errorMessageCity.value,
            borderColor = vm.validateCityBorderColor()
        ) {
            if (vm.areaID.value != -1)
                onCity()
        }

        Text(
            text = stringResource(R.string.district),
            style = text11,
            color = SecondaryColor2.copy(0.85f),
            modifier = Modifier.padding(top = 16.dp)
        )
        DistrictView(
            title = vm.selectedDistrictName.value,
            isValid = vm.isDistrictValid.value,
            validationMessage = vm.errorMessageDistrict.value,
            borderColor = vm.validateDisrtictBorderColor()
        ) {
            if (vm.getDistrictOfCities().isEmpty().not())
                onDistrict()
            else context.showError(context.getString(R.string.no_district))
        }

    }
}

@Composable
private fun CityView(
    title: String,
    borderColor: Color = BorderColor,
    isValid: Boolean = true,
    validationMessage: String = "",
    onCityClick: () -> Unit
) {
    val color: Color by animateColorAsState(
        borderColor
    )

    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .requiredHeight(46.dp)
                .background(TextFieldBg, RoundedCornerShape(8.dp))
                .border(1.dp, color = color, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onCityClick()

                })
                .padding(horizontal = 16.dp)

        ) {
            Text(
                title.ifEmpty { stringResource(id = R.string.city) },
                style = text14,
                color = if (title.isEmpty())
                    SecondaryColor.copy(0.39f)
                else Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
    Row(Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = isValid.not()) {
            Text(
                text = validationMessage,
                style = text12,
                color = Red,
                modifier = Modifier.paddingTop(10)
            )
        }
    }

}

@Composable
private fun AreaView(
    title: String,
    borderColor: Color = BorderColor,
    isValid: Boolean = true,
    validationMessage: String = "",
    onArea: () -> Unit
) {
    val color: Color by animateColorAsState(
        borderColor
    )
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .requiredHeight(46.dp)
                .background(TextFieldBg, RoundedCornerShape(8.dp))
                .border(1.dp, color = color, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onArea()
                })
                .padding(horizontal = 16.dp)

        ) {
            Text(
                title.ifEmpty { stringResource(id = R.string.area) },
                style = text14,
                color =
                if (title.isEmpty())
                    SecondaryColor.copy(0.39f)
                else Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
    Row(Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = isValid.not()) {
            Text(
                text = validationMessage,
                style = text12,
                color = Red,
                modifier = Modifier.paddingTop(10)
            )
        }
    }

}

@Composable
private fun DistrictView(
    title: String,
    borderColor: Color = BorderColor,
    isValid: Boolean = true,
    validationMessage: String = "",
    onDistrict: () -> Unit
) {
    val color: Color by animateColorAsState(
        borderColor
    )
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .requiredHeight(46.dp)
                .background(TextFieldBg, RoundedCornerShape(8.dp))
                .border(1.dp, color = color, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onDistrict()
                })
                .padding(horizontal = 16.dp)

        ) {
            Text(
                title.ifEmpty { stringResource(id = R.string.district) },
                style = text14,
                color = if (title.isEmpty())
                    SecondaryColor.copy(0.39f)
                else Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
    Row(Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = isValid.not()) {
            Text(
                text = validationMessage,
                style = text12,
                color = Red,
                modifier = Modifier.paddingTop(10)
            )
        }
    }

}
