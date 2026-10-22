package `in`.urjakendra.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import `in`.urjakendra.app.data.SessionStore
import `in`.urjakendra.app.data.UrjaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * App entry gate:
 * - validates a stored token against the authenticated profile endpoint;
 * - shows OTP login if there is no valid session;
 * - opens the existing app shell after authentication.
 */
@Composable
fun AuthHost(
    appContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val sessionStore = remember { SessionStore(context) }
    val repository = remember { UrjaRepository() }
    var checking by remember { mutableStateOf(true) }
    var authenticated by remember { mutableStateOf(false) }
    var hindi by remember { mutableStateOf(false) }

    val factory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repository, sessionStore) as T
            }
        }
    }
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        val token = sessionStore.getToken()
        authenticated = if (token.isNullOrBlank()) {
            false
        } else {
            try {
                withContext(Dispatchers.IO) {
                    repository.getProfile(token)
                    true
                }
            } catch (_: Exception) {
                sessionStore.clear()
                false
            }
        }
        checking = false
    }

    if (checking) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (authenticated) {
        appContent()
    } else {
        AuthScreen(
            viewModel = authViewModel,
            hindi = hindi,
            onAuthenticated = { authenticated = true }
        )
    }
}
