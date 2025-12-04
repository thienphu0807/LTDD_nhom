package com.example.appquanlycv

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appquanlycv.reminder.TaskReminderScheduler
import com.example.appquanlycv.ui.auth.AuthScreen
import com.example.appquanlycv.ui.home.TaskScreen
import com.example.appquanlycv.ui.home.TaskViewModel
import com.example.appquanlycv.ui.home.TaskViewModelFactory
import com.example.appquanlycv.ui.splash.SplashScreen
import com.example.appquanlycv.ui.theme.AppquanlycvTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (application as TaskApplication).repository,
            TaskReminderScheduler(applicationContext)
        )
    }

    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        requestNotificationPermissionIfNeeded()

        setContent {
            AppquanlycvTheme {
                var showSplash by remember { mutableStateOf(true) }
                var currentUser by remember { mutableStateOf(firebaseAuth.currentUser) }
                var authError by remember { mutableStateOf<String?>(null) }
                var isLoading by remember { mutableStateOf(false) }

                // Task UI state
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // ---- Google Sign-In client (remember theo context) ----
                val context = this
                val googleSignInClient = remember {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build()
                    GoogleSignIn.getClient(context, gso)
                }

                // ---- Xử lý credential Firebase ----
                val handleCredential: (AuthCredential) -> Unit = { credential ->
                    isLoading = true
                    authError = null
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                currentUser = firebaseAuth.currentUser
                            } else {
                                authError = task.exception?.localizedMessage
                                    ?: getString(R.string.auth_generic_error)
                            }
                        }
                }

                // ---- Launcher nhận kết quả Google ----
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val data = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        handleCredential(credential)
                    } catch (e: Exception) {
                        isLoading = false
                        authError = e.localizedMessage ?: getString(R.string.auth_generic_error)
                    }
                }
                if (showSplash) {
                    SplashScreen(onFinish = { showSplash = false })
                } else if (currentUser == null) {
                    // ----- AUTH -----
                    AuthScreen(
                        isLoading = isLoading,
                        errorMessage = authError,
                        onGoogleLogin = {
                            isLoading = true
                            authError = null
                            // signOut trước để luôn hiện account chooser
                            googleSignInClient.signOut().addOnCompleteListener {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        },
                        onFacebookLogin = {} // giữ tham số nhưng không dùng; có thể xoá tham số trong AuthScreen
                    )
                } else {
                    // ----- HOME -----
                    TaskScreen(
                        uiState = uiState,
                        onAddTask = viewModel::addTask,
                        onSelectDate = viewModel::selectDate,
                        onToggleCompletion = viewModel::toggleCompletion,
                        onToggleReminder = viewModel::toggleReminder,
                        onDeleteTask = viewModel::deleteTask,
                        onLogout = {
                            firebaseAuth.signOut()
                            googleSignInClient.signOut()
                            currentUser = null
                        }

                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST
                )
            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST = 1001
    }
}

