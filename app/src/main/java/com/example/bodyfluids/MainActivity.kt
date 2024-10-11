package com.example.bodyfluids

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bodyfluids.ui.AppViewModel
import com.example.bodyfluids.ui.theme.BodyFluidsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BodyFluidsTheme {
                Scaffold { contentPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                            .padding(top = 56.dp, start = 16.dp, end = 16.dp)
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AppScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun AppScreen(
    appViewModel: AppViewModel = viewModel()
) {
    val appUiState by appViewModel.uiState.collectAsState()

    AppLayout(
        onValueChange = { appViewModel.updateResult(it) },
        saliva15 = appUiState.saliva15,
        saliva5 = appUiState.saliva5,
        rightEye = appUiState.rightEye,
        leftEye = appUiState.leftEye
    )
}

@Composable
fun AppLayout(
    onValueChange: (Pair<String, String>) -> Unit,
    saliva15: Double,
    saliva5: Double,
    rightEye: Int,
    leftEye: Int
) {
    Text(
        text = stringResource(R.string.app_title),
        fontSize = 8.em
    )
    Text(
        text = stringResource(R.string.version),
        fontSize = 4.em
    )

    Spacer(modifier = Modifier.height(32.dp))

    SalivaInput(15) { onValueChange("saliva15" to it) }
    SalivaInput(5) { onValueChange("saliva5" to it) }

    Spacer(modifier = Modifier.height(32.dp))

    TearInput("Höger") { onValueChange("rightEye" to it) }
    TearInput("Vänster") { onValueChange("leftEye" to it) }

    Spacer(modifier = Modifier.height(64.dp))

    SalivaResult(saliva15, false)
    SalivaResult(saliva5, true)

    Spacer(modifier = Modifier.height(16.dp))

    TearResult("Höger", rightEye)
    TearResult("Vänster", leftEye)

    Spacer(modifier = Modifier.height(16.dp))

    Row {
        StartAlarmButton(15)
        StartAlarmButton(5)
    }
}

@Composable
fun InputField(label: String, regex: Regex, keyboard: KeyboardType, updateValue: (String) -> Unit) {
    var typedText by remember { mutableStateOf("") }
    OutlinedTextField(
        label = { Text(text = label) },
        value = typedText,
        onValueChange = {
            typedText = if (regex.matches(it)) it else typedText
            updateValue(typedText)
        },
        modifier = Modifier
            .width(88.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboard)
    )
}

@Composable
fun TearInput(side: String, updateTear: (String) -> Unit) {
    val regex = remember { Regex("^(\\d{1,2})?$") }
    Row {
        Column(modifier = Modifier.width(168.dp)) {
            Text(
                text = "$side öga:",
                lineHeight = 3.5.em
            )
        }
        InputField("mm", regex, KeyboardType.Number, updateTear)
    }
}

@Composable
fun SalivaInput(minutes: Int = 0, updateSaliva: (String) -> Unit) {
    val regex = remember { Regex("^(\\d+)?(\\.)?(\\d{1,2})?$") }

    Row {
        Column(modifier = Modifier.width(168.dp)) {
            Text(
                text = "Mängd efter $minutes min:",
                lineHeight = 3.5.em
            )
        }
        InputField("ml", regex, KeyboardType.Decimal, updateSaliva)
    }
}

@Composable
fun SalivaResult(result: Double, chewingGum: Boolean) {
    val text = when (chewingGum) {
        false -> stringResource(R.string.without_chewing_gum, result / 15)
        true -> stringResource(R.string.with_chewing_gum, result / 5)
    }
    Row {
        Text(
            text = text,
            lineHeight = 2.em
        )
    }
}

@Composable
fun TearResult(side: String, amount: Int) {
    Row {
        Text(
            text = "$side öga: $amount mm",
            lineHeight = 2.em
        )
    }
}

@Composable
fun StartAlarmButton(minutes: Int) {
    val context = LocalContext.current

    Button(onClick = {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Spotta $minutes min")
        intent.putExtra(AlarmClock.EXTRA_LENGTH, minutes * 60)
        context.startActivity(intent)
    }) {
        Text(text = "Starta alarm på $minutes min")
    }
}