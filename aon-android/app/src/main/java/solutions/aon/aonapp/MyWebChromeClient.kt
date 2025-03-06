import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class MyWebChromeClient(private val activity: Activity) : WebChromeClient() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val FILE_CHOOSER_REQUEST_CODE = 1000

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        this.filePathCallback = filePathCallback

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"  // Permite seleccionar cualquier tipo de archivo
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        activity.startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), FILE_CHOOSER_REQUEST_CODE)
        return true
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            val result = if (resultCode == Activity.RESULT_OK && data != null) {
                arrayOf(data.data!!)
            } else {
                emptyArray()
            }

            filePathCallback?.onReceiveValue(result)
            filePathCallback = null
        }
    }
}
