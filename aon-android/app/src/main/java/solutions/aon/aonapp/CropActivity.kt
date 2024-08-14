package solutions.aon.aonapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CropActivity : ComponentActivity() {

    private var imgBase64: String? = null

    private val cropActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                val resultUri = UCrop.getOutput(intent)
                resultUri?.let { uri ->
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imgBase64 = bitmapToBase64(bitmap)
                    // Regresar a la actividad anterior con el resultado
                    val resultIntent = Intent().apply {
                        putExtra("imgBase64", imgBase64)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filePath = intent.getStringExtra("filePath") ?: return
        val bitmap = BitmapFactory.decodeFile(filePath)

        // Guardar el bitmap en un archivo temporal
        val tempFile = File.createTempFile("cropImage", ".jpg", cacheDir)
        val outputStream: OutputStream = FileOutputStream(tempFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val sourceUri = Uri.fromFile(tempFile)
        val destinationUri = Uri.fromFile(File(cacheDir, "croppedImage.jpg"))

        startCrop(sourceUri, destinationUri)
    }

    private fun startCrop(sourceUri: Uri, destinationUri: Uri) {
        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(100)
            setFreeStyleCropEnabled(true)
        }

        val intent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .getIntent(this)

        cropActivityResultLauncher.launch(intent)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
