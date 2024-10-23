package solutions.aon.aonapp
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject
import solutions.aon.camara2.R
import java.io.File

class AonJs(private val webView: WebView?, private val context: Context) : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        @JvmStatic
        var mainActivity: MainActivity? = null
            private set

        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        var webViewStatic: WebView? = null
            private set
    }

    init {
        webViewStatic = webView
    }

    fun setMainActivity(mainActivity: MainActivity) {
        AonJs.mainActivity = mainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    @JavascriptInterface
    fun openCamera(data:String) {
        val intent = Intent(context, CameraActivity::class.java)
        context.startActivity(intent)
        waitCamera()

    }

    @OptIn(ExperimentalPermissionsApi::class)
    @JavascriptInterface
    fun openBarcode(data: String) {
        val intent = Intent(context,BarCodeActivity::class.java)
        context.startActivity(intent)
        waitBarcode()
    }

    private fun waitCamera(){
        val base64 = CameraActivity.imgBase64
        if(base64 != null){
            val base64JSON = JSONObject()
            base64JSON.put("content",base64)
            mainActivity?.runOnUiThread {
                Log.v("TAG", "Foto: $base64")
                webView?.evaluateJavascript("receiveImage(${base64JSON})", null)
            }
        } else {
            Thread.sleep(100)
            waitCamera()
        }
    }

    private fun waitBarcode() {
        val barCode = BarCodeAnalyser.lastScannedBarcode
        if(barCode != null) {
            Log.v("TAG","$barCode")
            val barCodeJSON = JSONObject()
            barCodeJSON.put("code",barCode)
            mainActivity?.runOnUiThread {
                Log.v("TAG", "Codigo de barras: $barCode")
                webView?.evaluateJavascript("receiveBarcode(${barCodeJSON})", null)
            }
        } else {
            Thread.sleep(100)
            waitBarcode()
        }

    }

    @JavascriptInterface
    fun openFile(data:String) {
        val json = JSONObject(data)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.renfe.com/content/dam/renfe/es/General/PDF-y-otros/Ejemplo-de-descarga-pdf.pdf"))
        context.startActivity(intent)
    }

    @JavascriptInterface
    fun printFile(data: String) {
        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val json = JSONObject(data)
        //val url = json.getString("url")
        val url = "https://www.industrialhama.com/wp-content/uploads/2018/06/Ejemplo-pdf.pdf"
        val title = "prueba"
        val fileName = "$title.pdf"
        downloadFile(context,url,title,title)
        Thread.sleep(1000)
        val filePath = File(downloadsDirectory, fileName).absolutePath
        printPDF(filePath)
    }

    @JavascriptInterface
    fun downloadFile(data:String) {
        val json = JSONObject(data)
        val title = json.getString("title")
        Log.v("TAG", "download file")
        downloadFile(context, "https://www.industrialhama.com/wp-content/uploads/2018/06/Ejemplo-pdf.pdf", title, title)

        // Verificar si el archivo está descargado
        val fileName = "$title.pdf"
        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDirectory, fileName)

        // Esperar hasta que el archivo esté descargado
        while (!file.exists()) {

        }
        //openDownloadedFile(Environment.DIRECTORY_DOWNLOADS, fileName)
        Toast.makeText(context, "PDF descargado", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    @JavascriptInterface
    fun getPosition(data: String)  {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()
        val positionJson = JSONObject()
        fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    positionJson.put("latitude", location.latitude)
                    positionJson.put("longitude", location.longitude)
                    val positionString = positionJson.toString()
                    if(webView != null) {
                        mainActivity?.runOnUiThread {
                            Log.v("TAG", "Position: $positionString")
                            webView.evaluateJavascript("receivePosition(${positionJson})", null)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.v("TAG", "Error: ${e.message}")
            }
    }

    @JavascriptInterface
    fun changeUrl(data: String) {
        val json = JSONObject(data)
        val url = json.getString("url")
        mainActivity?.runOnUiThread{
            webView?.loadUrl(url)
        }
    }

    @JavascriptInterface
    fun changeStatusBarColor(data: String) {
        val json = JSONObject(data)
        val color = json.getString("color")
        mainActivity?.changeStatusBarColorHex(color)
        Log.v("TAG",isDarkModeActivated(context).toString())
    }

    private fun downloadFile(context: Context, url: String, title: String, description: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.pdf") // Especifica el directorio de descarga y el nombre del archivo
            .setAllowedOverMetered(true) // Permitir descargas en conexiones de datos móviles

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun openDownloadedFile(directory: String, fileName: String): String {
        val file = File(Environment.getExternalStoragePublicDirectory(directory), fileName)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)

        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No se encontró una aplicación para abrir el PDF", Toast.LENGTH_SHORT).show()
        }
        return file.absolutePath
    }

    private fun printPDF(filePath: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val jobName = "${context.getString(R.string.app_name)} Document"
            val printAdapter = PdfPrintDocumentAdapter(filePath)
            printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder().build()
            )
        } catch (e: Exception) {
            Log.e("TAG", "Error al imprimir PDF: ${e.message}")
            Toast.makeText(context, "Error al imprimir PDF", Toast.LENGTH_SHORT).show()
        }
    }

    fun isDarkModeActivated(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

}
