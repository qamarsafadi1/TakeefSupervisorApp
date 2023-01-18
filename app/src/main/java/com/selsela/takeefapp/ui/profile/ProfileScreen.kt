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
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.common.EditText
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.InputEditText
import com.selsela.takeefapp.ui.common.ListedBottomSheet
import com.selsela.takeefapp.ui.profile.delete.DeleteAccountSheet
import com.selsela.takeefapp.ui.splash.ChangeNavigationBarColor
import com.selsela.takeefapp.ui.splash.ChangeStatusBarOnlyColor
import com.selsela.takeefapp.ui.theme.BorderColor
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.SecondaryColor2
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.TextFieldBg
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.ui.theme.text14Bold
import com.selsela.takeefapp.ui.theme.text14Meduim
import com.selsela.takeefapp.ui.theme.text14White
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {
    Color.Transparent.ChangeStatusBarOnlyColor()
    Color.White.ChangeNavigationBarColor()

    val coroutineScope = rememberCoroutineScope()
    val paySheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
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
                    onClick = { /*TODO*/ },
                    title =  stringResource(id = R.string.save_lbl),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 14.dp)
                        .width(107.dp)
                        .height(44.dp)

                )

            }
            Box(modifier = Modifier.defaultMinSize(minHeight = 115.dp, minWidth = 115.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.placeholder2),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .size(98.dp)
                )
                IconButton(
                    onClick = { /*TODO*/ },
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
                    onAreaClick = {
                        coroutineScope.launch {
                            if (areaSheetState.isVisible)
                                areaSheetState.hide()
                            else
                                areaSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    }
                ) {
                    coroutineScope.launch {
                        if (citySheetState.isVisible)
                            citySheetState.hide()
                        else
                            citySheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }

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
                        text =  stringResource(id = R.string.delete_account),
                        style = text14Bold,
                        color = TextColor
                    )
                    Text(
                        text =  stringResource(id = R.string.delete_account_lbl_1),
                        style = text12,
                        color = SecondaryColor,
                        modifier = Modifier.paddingTop(12)
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        if (paySheetState.isVisible)
                            paySheetState.hide()
                        else paySheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }

                }) {
                    Image(
                        painter = painterResource(id = R.drawable.deletetrash),
                        contentDescription = ""
                    )
                }
            }
        }
        DeleteAccountSheet(sheetState = paySheetState) {
        }
        ListedBottomSheet(sheetState = citySheetState)
        ListedBottomSheet(sheetState = areaSheetState)


    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProfileForm(
    onAreaClick: () -> Unit,
    onCityClick: () -> Unit
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
        var name by remember {
            mutableStateOf("")
        }
        InputEditText(
            onValueChange = {
                name = it
            },
            text = name,
            hint = stringResource(R.string.name),
            inputType = KeyboardType.Text,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.email),
            style = text11,
            modifier = Modifier.padding(top = 9.dp)
        )
        var email by remember {
            mutableStateOf("")
        }
        InputEditText(
            onValueChange = {
                email = it
            },
            text = email,
            hint = stringResource(R.string.email),
            inputType = KeyboardType.Email,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.mobile),
            style = text11,
            modifier = Modifier.padding(top = 6.8.dp)
        )
        EditTextView()
        CityAreaView(onAreaClick = { onAreaClick() }) {
            onCityClick()
        }
        var district by remember {
            mutableStateOf("")
        }
        Text(
            text = stringResource(R.string.district),
            style = text11,
            color = SecondaryColor2.copy(0.85f),
            modifier = Modifier.padding(top = 16.dp)
        )
        InputEditText(
            onValueChange = {
                district = it
            },
            text = district,
            hint = stringResource(R.string.district),
            inputType = KeyboardType.Text,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun EditTextView() {
    var mobile by remember {
        mutableStateOf("")
    }
    EditText(
        onValueChange = {
            mobile = it
        },
        text = mobile,
        textStyle = text14White,
        hint = "59XXXXXXX",
        inputType = KeyboardType.Phone,
        trailing = {
            Text(
                text = "966", style = text14,
                color = SecondaryColor.copy(0.67f)
            )
        },
        modifier = Modifier.padding(top = 16.dp)
    )
}


@Composable
private fun CityAreaView(
    onAreaClick: () -> Unit,
    onCityClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .paddingTop(16)
            .fillMaxWidth()
    ) {
        CityView(Modifier.weight(1f)) {
            onCityClick()
        }
        Spacer(modifier = Modifier.width(width = 8.dp))
        AreaView(
            modifier = Modifier.weight(1f)
        ) {
            onAreaClick()
        }
    }
}

@Composable
private fun AreaView(
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
                mutableStateOf("")
            }
            Text(
                if (area.isEmpty().not()) area else
                    stringResource(id = R.string.city),
                style = text11,
                color = if (area.isEmpty().not()) Color.White else SecondaryColor.copy(0.39f),
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
                mutableStateOf("")
            }
            Text(
                if (city.isEmpty().not()) city else
                    stringResource(id = R.string.area),
                style = text11,
                color = if (city.isEmpty().not()) Color.White else SecondaryColor.copy(0.39f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
}