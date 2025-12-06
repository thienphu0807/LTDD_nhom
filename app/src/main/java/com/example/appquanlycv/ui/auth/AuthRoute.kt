package com.example.appquanlycv.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appquanlycv.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun AuthRoute(
    vm: AuthViewModel,
    content: @Composable (ui: AuthUiState, onGoogleClick: () -> Unit) -> Unit
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    // 1) Nhận kết quả từ Google
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        vm.handleGoogleResult(res.data)
    }

    // 2) Hàm mở Google account chooser
    val onGoogle = {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(ctx.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(ctx, gso)
        client.signOut() // đảm bảo luôn hiện account chooser
        launcher.launch(client.signInIntent)
    }

    content(ui, onGoogle)
}
