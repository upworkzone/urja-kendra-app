package in.urjakendra.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    hindi: Boolean,
    onAuthenticated: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val locale = if (hindi) "hi" else "en"

    LaunchedEffect(state) {
        if (state is AuthUiState.Authenticated) onAuthenticated()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            if (hindi) "URJA KENDRA में आपका स्वागत है" else "Welcome to URJA KENDRA",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))
        Text("ऊर्जा बनाओ, ऊर्जा बचाओ, पैसे कमाओ...")
        Spacer(Modifier.height(24.dp))

        when (val current = state) {
            AuthUiState.EnterPhone -> {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter(Char::isDigit).take(10) },
                    label = { Text(if (hindi) "मोबाइल नंबर" else "Mobile number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.requestOtp(phone, locale) }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (hindi) "OTP भेजें" else "Send OTP")
                }
            }
            is AuthUiState.EnterOtp -> {
                Text(if (hindi) "OTP भेजा गया: ${current.phone}" else "OTP sent to ${current.phone}")
                current.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it.filter(Char::isDigit).take(8) },
                    label = { Text(if (hindi) "OTP दर्ज करें" else "Enter OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.verifyOtp(current.phone, current.challengeId, otp, locale) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (hindi) "सत्यापित करें" else "Verify") }
                Button(onClick = viewModel::backToPhone, modifier = Modifier.fillMaxWidth()) {
                    Text(if (hindi) "नंबर बदलें" else "Change number")
                }
            }
            AuthUiState.Loading -> {
                CircularProgressIndicator()
                Text(if (hindi) "कृपया प्रतीक्षा करें..." else "Please wait...")
            }
            is AuthUiState.Error -> {
                Text(current.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.requestOtp(phone, locale) }) {
                    Text(if (hindi) "फिर प्रयास करें" else "Try again")
                }
            }
            AuthUiState.Authenticated -> Unit
        }
    }
}
