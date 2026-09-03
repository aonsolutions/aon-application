package solutions.aon.aonapp

import MyWebChromeClient
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.max
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import solutions.aon.app.R
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import solutions.aon.aonapp.ui.theme.Camara2Theme

const val PAGE_URL = "https://aon.solutions"

/** Tiempo mínimo que se ve el splash, para que no parpadee en cargas rápidas. */
private const val SPLASH_MIN_MS = 600L

/** Tope: si la web no carga, el splash se retira igualmente. */
private const val SPLASH_MAX_MS = 8000L

class MainActivity : ComponentActivity() {

    private val aonJs: AonJs by lazy { AonJs(null, this) }
    private lateinit var webChromeClient: MyWebChromeClient

    /** Controla el splash a pantalla completa que se dibuja sobre el WebView. */
    private var showSplash by mutableStateOf(true)
    private var pageLoaded by mutableStateOf(false)

    /** Sube en cada onPageFinished: cada carga borra los insets inyectados. */
    private var pageLoads by mutableStateOf(0)
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
        // Borde a borde explícito: en API 35+ el sistema ya lo impone, y así el
        // comportamiento es el mismo en Android 6..14.
        enableEdgeToEdge()
        // El splash arranca con fondo oscuro, así que los iconos van claros.
        applyStatusBarAppearance(dark = true)
        webChromeClient = MyWebChromeClient(this)
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
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Borde a borde de verdad: el WebView ocupa toda la pantalla,
                        // también detrás de las barras del sistema y del recorte, y es
                        // la web la que pinta esas zonas. Su alto lo recibe en las
                        // variables CSS --sat/--sar/--sab/--sal (ver publishInsets).
                        // El teclado sí encoge la vista: la web recoloca su footer
                        // según window.innerHeight, y así sigue funcionando.
                        WebViewScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                        )
                        // El splash también va a pantalla completa.
                        if (showSplash) {
                            Image(
                                painter = painterResource(R.drawable.splash),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    delay(SPLASH_MIN_MS)
                    withTimeoutOrNull(SPLASH_MAX_MS) {
                        snapshotFlow { pageLoaded }.first { it }
                    }
                    showSplash = false
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        webChromeClient.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun WebViewScreen(modifier: Modifier = Modifier) {
        val mainActivity = LocalContext.current as MainActivity
        var webView by remember { mutableStateOf<WebView?>(null) }

        // Barras del sistema y recorte de pantalla. El teclado queda fuera a
        // proposito: ese lo absorbe imePadding encogiendo el WebView.
        val bars = WindowInsets.systemBars.asPaddingValues()
        val cutout = WindowInsets.displayCutout.asPaddingValues()
        val top = max(bars.calculateTopPadding(), cutout.calculateTopPadding())
        val bottom = max(bars.calculateBottomPadding(), cutout.calculateBottomPadding())
        val left = max(
            bars.calculateLeftPadding(LayoutDirection.Ltr),
            cutout.calculateLeftPadding(LayoutDirection.Ltr)
        )
        val right = max(
            bars.calculateRightPadding(LayoutDirection.Ltr),
            cutout.calculateRightPadding(LayoutDirection.Ltr)
        )

        // Se reenvian en cada carga (la inyeccion se pierde) y cada vez que
        // cambian: girar el movil, mostrarse la barra de gestos, plegables...
        LaunchedEffect(webView, mainActivity.pageLoads, top, right, bottom, left) {
            webView?.let { publishInsets(it, top, right, bottom, left) }
        }

        AndroidView(
            // Obligatorio: dentro de un Box la vista se mediría a contenido (tamaño 0).
            // Surface sí propagaba las restricciones mínimas, por eso antes no hacía falta.
            modifier = modifier,
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            mainActivity.pageLoaded = true
                            mainActivity.pageLoads++
                        }
                    }
                    settings.javaScriptEnabled = true
                    settings.userAgentString = "solutions.aon.android.35"
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.loadWithOverviewMode = true
                    settings.setSupportZoom(false)
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.textZoom = 100
                    webChromeClient = mainActivity.webChromeClient
                    WebView.setWebContentsDebuggingEnabled(true)

                    // El puente JS y la carga inicial van aqui, no en `update`.
                    // `update` se ejecuta en CADA recomposicion (al llamar la web a
                    // changeStatusBarColor, al retirarse el splash, al cambiar los
                    // insets con el teclado...), y volver a llamar a loadUrl recargaba
                    // la web entera: la SPA arrancaba de cero y restauraba la ultima
                    // empresa, deshaciendo la navegacion en curso.
                    addJavascriptInterface(
                        AonJs(this, context).apply { setMainActivity(mainActivity) },
                        "Android"
                    )
                    val url = mainActivity
                        .getSharedPreferences("preferences", MODE_PRIVATE)
                        .getString("url", PAGE_URL) ?: PAGE_URL
                    loadUrl(url)
                    webView = this
                }
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

    /**
     * La franja de la barra de estado la pinta ya la propia web (su cabecera llega
     * hasta arriba), así que aquí solo queda ajustar el color de los iconos del
     * sistema para que se sigan viendo sobre ese fondo.
     */
    fun changeStatusBarColorHex(colorHex: String, dark: Boolean) {
        runOnUiThread { applyStatusBarAppearance(dark) }
    }

    /**
     * Pasa los insets a la web en las mismas variables CSS que ya usa en iOS desde
     * env(safe-area-inset-*), que en Android valen siempre 0. Tambien las deja en
     * window.AonInsets y avisa con el evento `aoninsets` para el codigo que las
     * necesite en JS.
     */
    private fun publishInsets(view: WebView, top: Dp, right: Dp, bottom: Dp, left: Dp) {
        val js = """
            (function() {
              var insets = {
                top: ${top.value.toInt()},
                right: ${right.value.toInt()},
                bottom: ${bottom.value.toInt()},
                left: ${left.value.toInt()}
              };
              var style = document.documentElement.style;
              style.setProperty('--sat', insets.top + 'px');
              style.setProperty('--sar', insets.right + 'px');
              style.setProperty('--sab', insets.bottom + 'px');
              style.setProperty('--sal', insets.left + 'px');
              window.AonInsets = insets;
              window.dispatchEvent(new CustomEvent('aoninsets', { detail: insets }));
            })();
        """.trimIndent()
        view.evaluateJavascript(js, null)
    }

    /**
     * Sustituye a decorView.systemUiVisibility y SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
     * obsoletos desde API 30. `dark` indica fondo oscuro, así que los iconos van claros.
     */
    private fun applyStatusBarAppearance(dark: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = !dark
    }

    fun changeUrl(url: String) {
        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("url", url)
        editor.apply()
        Log.v("TAG", url)
    }

}
