package com.example.jettipapp

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerson
import com.example.jettipapp.util.calculateTotaltip
import com.example.jettipapp.widgets.RoundIconButton
import java.time.temporal.TemporalAmount

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            JetTipAppTheme {
            // A surface container using the 'background' color from the theme
            MyApp {
//                    TopHeader()
                MAinContent()
            }

//            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {

    Surface(
        modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colors.background
    ) {
        content()
    }

}

@Preview
@Composable
fun TopHeader(TotalPerPerson: Double = 133.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))), color = Color(0xFF7CD2B3)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(TotalPerPerson)
            Text(
                text = "Total per person",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp,
            )
            Text(
                text = "$$total",
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 22.sp,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MAinContent() {

    val splitByState = remember {
        mutableStateOf(1)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm(
            splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
        ) {}
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            Text(text = "hello Again")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {


    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()


    val sliderColors = SliderDefaults.colors(
        thumbColor = Color(0xFF7CD2B3), // Set the color of the thumb
        activeTrackColor = Color(0xFF7CD2B3), // Set the color of the active track
        inactiveTrackColor = Color.LightGray // Set the color of the inactive track
    )


    TopHeader(TotalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions

                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()

                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color(0xFF7CD2B3), // Set the outline color when focused
                    unfocusedIndicatorColor = Color(0xFF7CD2B3), // Set the outline color when unfocused
                    disabledIndicatorColor = Color.Transparent
                ),
                labelTextColor = Color(0xFF7CD2B3)

            )
            if (validState) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            splitByState.value =
                                if (splitByState.value > 1) splitByState.value - 1 else 1
                            totalPerPersonState.value =
                                calculateTotalPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )

                        })
                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            if (splitByState.value < range.last) {
                                splitByState.value = splitByState.value + 1
                                totalPerPersonState.value =
                                    calculateTotalPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )

                            }
                        })

                    }
                }

                //Tip Row

                Row(
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))

                    Text(
                        text = "$ ${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage %")

                    Spacer(modifier = Modifier.height(14.dp))

                    //Slider
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotaltip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )
                            totalPerPersonState.value =
                                calculateTotalPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )
                        },
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp
                        ),
                        colors = sliderColors,
                        steps = 5,
                        onValueChangeFinished = {

                        })

                }

            } else {
                Box() {

                }
            }
        }
    }
}



