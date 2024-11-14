package solutions.aon.aonapp

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import solutions.aon.aonapp.ui.theme.Camara2Theme
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executor

class CameraActivity : ComponentActivity() {

    companion object {
        var imgBase64: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Camara2Theme {
                HomeScreen()
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun HomeScreen() {

        val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
        val context = LocalContext.current
        val cameraController = remember {
            LifecycleCameraController(context)
        }
        val lifecycle = LocalLifecycleOwner.current
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    if (permissionState.status.isGranted) {
                        CameraComposable(cameraController, lifecycle, modifier = Modifier.padding(padding))
                    } else {
                        Text(text = "Permiso Denegado", modifier = Modifier.padding(padding))
                    }

                    FloatingActionButton(
                        onClick = {
                            val executor = ContextCompat.getMainExecutor(context)
                            takePicture(cameraController, executor, context)
                        },
                        shape = CircleShape,
                        containerColor = Color.White,
                        modifier = Modifier
                            .size(80.dp)
                            .aspectRatio(1f)
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.CameraAlt,
                            contentDescription = "Tomar Foto",
                            tint = Color.Black
                        )
                    }
                }
            }
        )
    }

    private fun takePicture(cameraController: LifecycleCameraController, executor: Executor, context: Context) {
        val file = File.createTempFile("imagentest", ".jpg")
        val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()
        cameraController.takePicture(
            outputDirectory,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val fileSize = file.length()
                    println("Tamaño del archivo: $fileSize bytes")

                    if (fileSize > 0) {
                        imgBase64 = fileToBase64(file)
                        println(imgBase64)
                    } else {
                        println("El archivo está vacío.")
                    }

                    if (context is ComponentActivity) {
                        context.finish()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    println(exception)
                }
            }
        )
    }

    private fun fileToBase64(file: File): String? {
        return try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            println("Error al convertir el archivo a Base64: ${e.message}")
            null
        }
    }

    @Composable
    fun CameraComposable(
        cameraController: LifecycleCameraController,
        lifecycle: androidx.lifecycle.LifecycleOwner,
        modifier: Modifier = Modifier,
    ) {
        cameraController.bindToLifecycle(lifecycle)
        AndroidView(modifier = modifier, factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
            previewView.controller = cameraController
            previewView
        })
    }
}