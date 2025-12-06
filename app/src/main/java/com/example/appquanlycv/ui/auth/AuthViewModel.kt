package com.example.appquanlycv.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException // NEW
import com.google.android.gms.common.api.CommonStatusCodes // NEW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignedIn: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui

    // -------------------------
    // GOOGLE RESULT HANDLER
    // -------------------------
    fun handleGoogleResult(data: Intent?) {
        _ui.value = _ui.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                // Lấy kết quả với ApiException để có statusCode
                val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java) // CHANGED

                val idToken = account.idToken
                if (idToken.isNullOrEmpty()) {
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        errorMessage = "Google trả về idToken = null. Kiểm tra default_web_client_id và SHA-1/SHA-256."
                    )
                    return@launch
                }

                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).awaitKtx()

                _ui.value = _ui.value.copy(
                    isLoading = false,
                    isSignedIn = true
                )

            } catch (e: ApiException) {
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    errorMessage = friendlyGoogleError(e)
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Không thể đăng nhập bằng Google."
                )
            }
        }
    }

    // -------------------------
    // SIGN OUT
    // -------------------------
    fun signOutAll(clientFactory: () -> com.google.android.gms.auth.api.signin.GoogleSignInClient) {
        auth.signOut()
        // Có thể đợi signOut() / revokeAccess() async, nhưng thường không bắt buộc:
        clientFactory().signOut()
        clientFactory().revokeAccess() // để lần sau luôn hiện account chooser
        _ui.value = AuthUiState()
    }

    // -------------------------
    // AUTO LOGIN (SILENT LOGIN)
    // -------------------------
    fun trySilentSignIn(onDone: (Boolean) -> Unit) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            _ui.value = _ui.value.copy(isSignedIn = true)
            onDone(true)
        } else {
            onDone(false)
        }
    }

    // -------------------------
    // FRIENDLY ERROR MSG
    // -------------------------
    private fun friendlyGoogleError(e: ApiException): String {
        return when (e.statusCode) {
            12501 /* SIGN_IN_CANCELLED */ ->
                "Bạn đã huỷ đăng nhập."
            12502 /* SIGN_IN_CURRENTLY_IN_PROGRESS */ ->
                "Đang có phiên đăng nhập khác, vui lòng thử lại."
            12500 /* SIGN_IN_FAILED */ ->
                "Đăng nhập thất bại. Kiểm tra cấu hình Google Sign-In (Web Client ID, SHA-1)."
            CommonStatusCodes.NETWORK_ERROR ->
                "Lỗi mạng. Vui lòng kiểm tra kết nối internet."
            CommonStatusCodes.DEVELOPER_ERROR, // thường thấy là code = 10
            10 /* DEVELOPER_ERROR */ ->
                "DEVELOPER_ERROR (10): Thiếu/ sai SHA-1 hoặc default_web_client_id không khớp."
            else -> "Lỗi Google Sign-In (${e.statusCode}): ${e.message ?: "Không rõ"}"
        }
    }
}

// -------------------------
// AWAIT EXTENSION
// -------------------------
private suspend fun <T> Task<T>.awaitKtx(): T =
    suspendCancellableCoroutine { c ->
        addOnCompleteListener { t ->
            if (t.isSuccessful) c.resume(t.result)
            else c.resumeWithException(
                t.exception ?: RuntimeException("Task failed")
            )
        }
    }
