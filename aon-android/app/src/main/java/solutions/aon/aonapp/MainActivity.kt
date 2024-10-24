package solutions.aon.aonapp

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import solutions.aon.aonapp.ui.theme.Camara2Theme

const val PAGE_URL = "http://192.168.2.45:8080/beta"

class MainActivity : ComponentActivity() {
    private val aonJs: AonJs by lazy { AonJs(null, this) }
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    aonJs.getPosition("{}")
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                    aonJs.getPosition("{}")
                }
                else -> {
                    Log.v("TAG", "Location permission not granted")
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )


        setContent {
            Camara2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WebViewScreen()
                }
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun WebViewScreen() {
        val mainActivity = LocalContext.current as MainActivity
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                        }
                    }
                    settings.javaScriptEnabled = true
                    settings.userAgentString = "solutions.aon.android"
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.loadWithOverviewMode = true
                    settings.setSupportZoom(false)
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT

                    WebView.setWebContentsDebuggingEnabled(true)
                }
            },
            update = {
                it.addJavascriptInterface(AonJs(it, it.context).apply {
                    setMainActivity(mainActivity)
                }, "Android")
                it.loadUrl(PAGE_URL)
            }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun WebViewScreenPreview() {
        Camara2Theme {
            WebViewScreen()
        }
    }

    fun changeStatusBarColorHex(colorHex: String, dark: Boolean) {
        runOnUiThread {
            val color = Color.parseColor(colorHex)
            window?.statusBarColor = color
            if (!dark)
                window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

}
