package `in`.urjakendra.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.urjakendra.app.data.SessionStore
import `in`.urjakendra.app.data.UrjaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object EnterPhone : AuthUiState
    data class EnterOtp(val phone: String, val challengeId: String, val message: String? = null) : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    data object Authenticated : AuthUiState
}

class AuthViewModel(
    private val repository: UrjaRepository,
    private val sessionStore: SessionStore
) : ViewModel() {
    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.EnterPhone)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun requestOtp(rawPhone: String, locale: String) {
        val phone = rawPhone.filter(Char::isDigit).takeLast(10)
        if (phone.length != 10) {
            _state.value = AuthUiState.Error(
                if (locale == "hi") "कृपया 10 अंकों का मोबाइल नंबर दर्ज करें।"
                else "Enter a valid 10-digit mobile number."
            )
            return
        }
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            try {
                val response = repository.requestOtp(phone, locale)
                _state.value = AuthUiState.EnterOtp(phone, response.challengeId)
            } catch (_: Exception) {
                _state.value = AuthUiState.Error(
                    if (locale == "hi") "OTP भेजा नहीं जा सका। फिर प्रयास करें।"
                    else "Could not send OTP. Please try again."
                )
            }
        }
    }

    fun verifyOtp(phone: String, challengeId: String, rawOtp: String, locale: String) {
        val otp = rawOtp.filter(Char::isDigit)
        if (otp.length !in 4..8) {
            _state.value = AuthUiState.EnterOtp(
                phone, challengeId,
                if (locale == "hi") "कृपया मान्य OTP दर्ज करें।" else "Enter a valid OTP."
            )
            return
        }
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            try {
                val response = repository.verifyOtp(phone, challengeId, otp)
                sessionStore.saveToken(response.accessToken)
                _state.value = AuthUiState.Authenticated
            } catch (_: Exception) {
                _state.value = AuthUiState.EnterOtp(
                    phone, challengeId,
                    if (locale == "hi") "OTP गलत या समाप्त हो सकता है।" else "OTP may be incorrect or expired."
                )
            }
        }
    }

    fun backToPhone() { _state.value = AuthUiState.EnterPhone }
}
