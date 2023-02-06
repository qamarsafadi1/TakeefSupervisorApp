package com.selsela.takeefapp.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.auth.AuthUiState
import com.selsela.takeefapp.ui.auth.AuthViewModel
import com.selsela.takeefapp.ui.common.AsyncImage
import com.selsela.takeefapp.ui.common.EditText
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.InputEditText
import com.selsela.takeefapp.ui.profile.delete.DeleteAccountSheet
import com.selsela.takeefapp.ui.splash.ChangeNavigationBarColor
import com.selsela.takeefapp.ui.splash.ChangeStatusBarOnlyColor
import com.selsela.takeefapp.ui.theme.BorderColor
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.TextFieldBg
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.ui.theme.text14Bold
import com.selsela.takeefapp.ui.theme.text14Meduim
import com.selsela.takeefapp.ui.theme.text14Secondary
import com.selsela.takeefapp.ui.theme.text14White
import com.selsela.takeefapp.utils.Common
import com.selsela.takeefapp.utils.Extensions
import com.selsela.takeefapp.utils.Extensions.Companion.collectAsStateLifecycleAware
import com.selsela.takeefapp.utils.Extensions.Companion.showSuccess
import com.selsela.takeefapp.utils.LocalData
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import de.palm.composestateevents.EventEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    vm: AuthViewModel = hiltViewModel(),
    goToLogin: () -> Unit,
    onBack: () -> Unit
) {
    Color.Transparent.ChangeStatusBarOnlyColor()
    Color.White.ChangeNavigationBarColor()

    var fromDelete = false
    val coroutineScope = rememberCoroutineScope()
    val deleteAccountSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val viewState: AuthUiState by vm.uiState.collectAsStateLifecycleAware(AuthUiState())
    val context = LocalContext.current

    var user by remember {
        vm.user
    }
    ProfileContent(
        viewState,
        onBack,
        onClick = vm::updateProfile,
        onDelete = {
            fromDelete = true
            vm.deleteAccount()
        },
        vm, coroutineScope, deleteAccountSheetState
    )

    /**
     * Handle Ui state from flow
     */

    EventEffect(
        event = viewState.onSuccess,
        onConsumed = vm::onSuccess
    ) { message ->
        if (fromDelete.not())
            context.showSuccess(message)
        else {
            LocalData.clearData()
            user = null
            vm.userLoggedIn.value = false
            goToLogin()
        }
    }
    EventEffect(
        event = viewState.onDeleteAccount,
        onConsumed = vm::onDeleteAccount
    ) { message ->
            LocalData.clearData()
            user = null
            vm.userLoggedIn.value = false
            goToLogin()
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
private fun ProfileContent(
    viewState: AuthUiState,
    onBack: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    vm: AuthViewModel,
    coroutineScope: CoroutineScope,
    deleteAccountSheetState: ModalBottomSheetState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .padding(bottom = 40.dp)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .requiredHeight(88.dp)
                    .background(Color.White)
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
                Text(
                    text = stringResource(id = R.string.profile),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = text14Meduim
                )
                ElasticButton(
                    onClick = { onClick() },
                    title = stringResource(id = R.string.save_lbl),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 14.dp)
                        .width(107.dp)
                        .height(44.dp),
                    isLoading = viewState.isLoading
                )
            }

            ImageChooser(vm)
            Column(
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(TextColor, RoundedCornerShape(33.dp))
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ProfileForm(
                    vm,
                    onAreaClick = {
                    }, onCityClick = {
                    },
                    onDistrictClick = {
                    })
            }

            Spacer(modifier = Modifier.weight(1f))
            Row(
                Modifier
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.delete_account),
                        style = text14Bold,
                        color = TextColor
                    )
                    Text(
                        text = stringResource(id = R.string.delete_account_lbl_1),
                        style = text12,
                        color = SecondaryColor,
                        modifier = Modifier.paddingTop(12)
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        if (deleteAccountSheetState.isVisible)
                            deleteAccountSheetState.hide()
                        else deleteAccountSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }

                }) {
                    Image(
                        painter = painterResource(id = R.drawable.deletetrash),
                        contentDescription = ""
                    )
                }
            }
        }
        DeleteAccountSheet(sheetState = deleteAccountSheetState,viewState) {
            onDelete()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProfileForm(
    vm: AuthViewModel,
    onAreaClick: () -> Unit,
    onCityClick: () -> Unit,
    onDistrictClick: () -> Unit
) {

    Column(
        Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
    ) {

        Text(
            text = stringResource(R.string.full_name),
            style = text11,
            modifier = Modifier.padding(top = 26.dp)
        )
        InputEditText(
            onValueChange = {
                vm.name.value = it
            },
            text = vm.name.value,
            hint = stringResource(R.string.name),
            inputType = KeyboardType.Text,
            modifier = Modifier
                .padding(top = 5.dp)
                .fillMaxWidth(1f)
                .requiredHeight(48.dp),
            isValid = vm.isNameValid.value,
            validationMessage = vm.errorMessageName.value,
            borderColor = vm.validateNameBorderColor()
        )
        Text(
            text = stringResource(R.string.email),
            style = text11,
            modifier = Modifier.padding(top = 16.dp)
        )

        InputEditText(
            onValueChange = {
                vm.email.value = it
            },
            text = vm.email.value,
            hint = stringResource(R.string.email),
            inputType = KeyboardType.Email,
            modifier = Modifier
                .padding(top = 9.dp)
                .fillMaxWidth(1f)
                .requiredHeight(48.dp)
                .fillMaxWidth(),
            isValid = vm.isValid.value,
            validationMessage = vm.errorMessage.value,
            borderColor = vm.validateBorderColor()
        )

        Text(
            text = stringResource(R.string.mobile),
            style = text11,
            modifier = Modifier.padding(top = 16.dp)
        )
        EditTextView(vm)
        CityAreaView(
            vm,
            onAreaClick = { onAreaClick() }) {
            onCityClick()
        }

        DistrictView(
            vm,
            Modifier
                .padding(top = 9.dp)
                .fillMaxWidth()
        ) {
            onDistrictClick()
        }
    }
}

@Composable
private fun EditTextView(vm: AuthViewModel) {
    EditText(
        onValueChange = {
            vm.mobile.value = it
        },
        text = vm.mobile.value,
        textStyle = text14Secondary,
        hint = "59XXXXXXX",
        inputType = KeyboardType.Phone,
        trailing = {
            Text(
                text = "966", style = text14,
                color = SecondaryColor.copy(0.67f)
            )
        },
        modifier = Modifier.padding(top = 9.dp),
        enabled = false,
        textColor =  SecondaryColor.copy(0.67f)
    )
}


@Composable
private fun CityAreaView(
    vm: AuthViewModel,
    onAreaClick: () -> Unit,
    onCityClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .paddingTop(16)
            .fillMaxWidth()

    ) {
        CityView(
            vm.selectedAreaName,
            Modifier.weight(1f)
        ) {
            onCityClick()
        }
        Spacer(modifier = Modifier.width(width = 8.dp))
        AreaView(
            vm.selectedCityName,
            modifier = Modifier.weight(1f)
        ) {
            onAreaClick()
        }
    }
}

@Composable
private fun AreaView(
    name: MutableState<String>,
    modifier: Modifier,
    onAreaClick: () -> Unit
) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.city),
            style = text11
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .requiredHeight(46.dp)
                .background(TextFieldBg, RoundedCornerShape(8.dp))
                .border(1.dp, color = BorderColor, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onAreaClick()
                })
                .padding(horizontal = 16.dp)

        ) {
            val area by remember {
                name
            }
            Text(
                if (area.isEmpty().not()) area else
                    stringResource(id = R.string.city),
                style = text11,
                color = SecondaryColor.copy(0.39f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CityView(
    name: MutableState<String>,
    weight: Modifier,
    onCityClick: () -> Unit
) {

    Column(weight) {
        Text(
            text = stringResource(R.string.area),
            style = text11
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .requiredHeight(46.dp)
                .background(TextFieldBg, RoundedCornerShape(8.dp))
                .border(1.dp, color = BorderColor, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onCityClick()
                })
                .padding(horizontal = 16.dp)

        ) {
            val city by remember {
                name
            }
            Text(
                if (city.isEmpty().not()) city else
                    stringResource(id = R.string.area),
                style = text11,
                color = SecondaryColor.copy(0.39f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DistrictView(
    vm: AuthViewModel,
    weight: Modifier,
    onCityClick: () -> Unit
) {

    Column(weight) {
        Text(
            text = stringResource(R.string.district),
            style = text11
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .requiredHeight(46.dp)
                .background(TextFieldBg, RoundedCornerShape(8.dp))
                .border(1.dp, color = BorderColor, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onCityClick()
                })
                .padding(horizontal = 16.dp)

        ) {
            val district by remember {
                vm.selectedDistrictName
            }
            Text(
                if (district.isEmpty().not()) district else
                    stringResource(id = R.string.district),
                style = text11,
                color = SecondaryColor.copy(0.39f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
}

@Composable
private fun ImageChooser(viewModel: AuthViewModel) {
    val context = LocalContext.current
    var imageUri by remember {
        mutableStateOf<String>(LocalData.user?.avatar ?: "")
    }
    var isImageCaptured by remember {
        mutableStateOf<Boolean>(false)
    }
    val imagePicker = Extensions.mStartActivityForResult(
        context = context,
    ) { file, bitmap ->
        if (file != null) {
            isImageCaptured = true
            imageUri = file.absolutePath
            viewModel.avatar = file
        }
    }
    Box(modifier = Modifier.defaultMinSize(minHeight = 115.dp, minWidth = 115.dp)) {
        imageUri.let {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
                    .shadow(
                        elevation = 10.dp,
                        shape = CircleShape,
                        clip = false
                    )
            ) {
                AsyncImage(
                    it,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .size(98.dp)
                        .shadow(
                            elevation = 25.dp,
                            shape = CircleShape,
                            clip = false
                        ),
                    contentScale = ContentScale.Crop
                )
            }


        }
        IconButton(
            onClick = {
                isImageCaptured = false
                Extensions.uploadImages(context, imagePicker, false)
            },
            modifier = Modifier
                .padding(top = 25.dp, end = 10.dp)
                .align(Alignment.BottomStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.changeimage),
                contentDescription = ""
            )
        }
    }
}
