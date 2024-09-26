package solutions.aon.aonapp

import android.os.Bundle
import android.os.CancellationSignal
import android.print.PageRange
import java.io.FileOutputStream

import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class PdfPrintDocumentAdapter(private val filePath: String) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val builder = PrintDocumentInfo.Builder("Document")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        callback.onLayoutFinished(builder, oldAttributes != newAttributes)
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {
        var `in`: FileInputStream? = null
        var out: FileOutputStream? = null

        try {
            `in` = FileInputStream(File(filePath))
            out = FileOutputStream(destination.fileDescriptor)

            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (`in`.read(buffer).also { bytesRead = it } > 0) {
                out.write(buffer, 0, bytesRead)
            }

            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: FileNotFoundException) {
            // Handle error
            callback.onWriteFailed(e.message)
        } finally {
            try {
                `in`?.close()
                out?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

