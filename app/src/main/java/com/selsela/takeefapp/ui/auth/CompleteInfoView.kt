package com.selsela.takeefapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.selsela.takeefapp.R
import com.selsela.takeefapp.ui.common.AppLogoImage
import com.selsela.takeefapp.ui.common.ElasticButton
import com.selsela.takeefapp.ui.common.InputEditText
import com.selsela.takeefapp.ui.common.ListedBottomSheet
import com.selsela.takeefapp.ui.splash.ChangeStatusBarColor
import com.selsela.takeefapp.ui.theme.BorderColor
import com.selsela.takeefapp.ui.theme.ButtonBg
import com.selsela.takeefapp.ui.theme.Purple40
import com.selsela.takeefapp.ui.theme.SecondaryColor
import com.selsela.takeefapp.ui.theme.SecondaryColor2
import com.selsela.takeefapp.ui.theme.TextColor
import com.selsela.takeefapp.ui.theme.TextFieldBg
import com.selsela.takeefapp.ui.theme.text11
import com.selsela.takeefapp.ui.theme.text12
import com.selsela.takeefapp.ui.theme.text14
import com.selsela.takeefapp.ui.theme.text16Line
import com.selsela.takeefapp.ui.theme.text16Medium
import com.selsela.takeefapp.ui.theme.text18
import com.selsela.takeefapp.utils.ModifiersExtension.paddingTop
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun CompleteInfoScreen(
    goToPending: () -> Unit,
    onBack: () -> Unit
) {
    Color.Transparent.ChangeStatusBarColor()
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
                    onArea = {
                        coroutineScope.launch {
                            if (areaSheetState.isVisible)
                                areaSheetState.hide()
                            else areaSheetState.animateTo(ModalBottomSheetValue.Expanded)

                        }
                    }
                ){
                    coroutineScope.launch {
                        if (citySheetState.isVisible)
                            citySheetState.hide()
                        else citySheetState.animateTo(ModalBottomSheetValue.Expanded)

                    }
                }
            }
            AppLogoImage(modifier = Modifier.paddingTop(85)
                .width(139.dp)
                .height(39.dp))

            ElasticButton(
                onClick = {
                    goToPending()
                }, title = stringResource(id = R.string.send_order),
                modifier = Modifier
                    .padding(bottom = 73.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .requiredHeight(48.dp)
                    .align(Alignment.BottomCenter)
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

        ListedBottomSheet(sheetState = citySheetState)
        ListedBottomSheet(sheetState = areaSheetState)


    }
}

@Composable
private fun CompleteInfoForm(
    onArea: () -> Unit,
    onCity: () -> Unit
) {
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
        var name by remember {
            mutableStateOf("")
        }
        InputEditText(
            onValueChange = {
                name = it
            },
            text = name,
            hint = stringResource(R.string.full_name),
            inputType = KeyboardType.Text,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.area),
            style = text11,
            color = SecondaryColor2.copy(0.85f),
            modifier = Modifier.padding(top = 16.dp)
        )
        AreaView(){
            onArea()
        }
        Text(
            text = stringResource(R.string.city),
            style = text11,
            color = SecondaryColor2.copy(0.85f),
            modifier = Modifier.padding(top = 16.dp)
        )
        CityView(){
            onCity()
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
private fun CityView(
    onCityClick: () -> Unit
) {

    Column(Modifier.fillMaxWidth()) {
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
            Text(
                stringResource(id = R.string.city),
                style = text14,
                color = SecondaryColor.copy(0.39f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
}

@Composable
private fun AreaView(
    onArea: () -> Unit
) {

    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .requiredHeight(46.dp)
                .background(TextFieldBg, RoundedCornerShape(8.dp))
                .border(1.dp, color = BorderColor, RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    onArea()
                })
                .padding(horizontal = 16.dp)

        ) {
            Text(
                stringResource(id = R.string.area),
                style = text14,
                color = SecondaryColor.copy(0.39f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }
}