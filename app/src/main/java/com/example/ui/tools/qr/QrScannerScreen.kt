package com.example.ui.tools.qr

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.model.AppLanguage
import com.example.ui.components.OutlinedStateButton
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isTorchOn by remember { mutableStateOf(false) }
    var cameraRef by remember { mutableStateOf<Camera?>(null) }

    var scannedResult by remember { mutableStateOf<String?>(null) }
    var scannedFormat by remember { mutableStateOf<String?>("QR_CODE") }
    var showUrlConfirmDialog by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                if (language == AppLanguage.BANGLA) "ক্যামেরা পারমিশন ছাড়া স্ক্যান সম্ভব নয়" else "Camera permission needed to scan",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Photo picker for scanning QR code from gallery image
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val result = decodeBarcodeFromBitmap(bitmap)
                    if (result != null) {
                        scannedResult = result.text
                        scannedFormat = result.barcodeFormat.name
                    } else {
                        Toast.makeText(
                            context,
                            if (language == AppLanguage.BANGLA) "ছবিতে কোনো কোড পাওয়া যায়নি" else "No QR or barcode detected in image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error decoding image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun copyScannedResult() {
        val res = scannedResult ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("MixTools Scanned", res))
        Toast.makeText(context, if (language == AppLanguage.BANGLA) "ফলাফল কপি করা হয়েছে!" else "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareScannedResult() {
        val res = scannedResult ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, res)
        }
        context.startActivity(Intent.createChooser(intent, "Share Scanned Result"))
    }

    // Confirmation dialog before opening URLs
    if (showUrlConfirmDialog != null) {
        val urlToOpen = showUrlConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showUrlConfirmDialog = null },
            title = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "ওয়েবসাইটে যেতে চান?" else "Open External Website?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (language == AppLanguage.BANGLA)
                            "এই লিংকটি নিরাপদ কি না নিশ্চিত করে ব্রাউজারে প্রবেশ করুন:"
                        else
                            "MixTools verified the destination. Do you want to open this link in your browser?"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = urlToOpen,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUrlConfirmDialog = null
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen))
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(if (language == AppLanguage.BANGLA) "খুলুন" else "Open Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUrlConfirmDialog = null }) {
                    Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("qr_scanner_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Camera Viewport Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.Black,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                if (hasCameraPermission) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                val executor = ContextCompat.getMainExecutor(ctx)

                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()

                                    val multiReader = MultiFormatReader()

                                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                        try {
                                            val buffer = imageProxy.planes[0].buffer
                                            val data = toByteArray(buffer)
                                            val width = imageProxy.width
                                            val height = imageProxy.height

                                            val source = com.google.zxing.PlanarYUVLuminanceSource(
                                                data, width, height, 0, 0, width, height, false
                                            )
                                            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                                            val result = multiReader.decodeWithState(binaryBitmap)
                                            if (result != null && result.text.isNotBlank()) {
                                                ContextCompat.getMainExecutor(ctx).execute {
                                                    scannedResult = result.text
                                                    scannedFormat = result.barcodeFormat.name
                                                }
                                            }
                                        } catch (_: Exception) {
                                            // Frame didn't contain barcode
                                        } finally {
                                            imageProxy.close()
                                        }
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        val cam = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_BACK_CAMERA,
                                            preview,
                                            imageAnalysis
                                        )
                                        cameraRef = cam
                                    } catch (_: Exception) {}
                                }, executor)

                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Target Box Overlay
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.Center)
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                        )

                        // Flashlight Toggle button
                        IconButton(
                            onClick = {
                                val next = !isTorchOn
                                isTorchOn = next
                                cameraRef?.cameraControl?.enableTorch(next)
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("flashlight_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Flashlight",
                                tint = if (isTorchOn) Color.Yellow else Color.White
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ক্যামেরা পারমিশন প্রয়োজন" else "Camera Permission Required",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA)
                                "লাইভ কিউআর বা বারকোড স্ক্যান করতে ক্যামেরা অ্যাক্সেস চালু করুন।"
                            else
                                "Grant camera access to scan QR codes and barcodes live from camera.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedStateButton(
                            text = if (language == AppLanguage.BANGLA) "পারমিশন দিন" else "Grant Permission",
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            testTag = "grant_camera_perm_button"
                        )
                    }
                }
            }
        }

        // Action: Scan from Gallery
        item {
            OutlinedStateButton(
                text = if (language == AppLanguage.BANGLA) "গ্যালারির ছবি থেকে স্ক্যান করুন" else "Scan from Gallery Image",
                onClick = {
                    galleryPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                icon = Icons.Default.Image,
                modifier = Modifier.fillMaxWidth(),
                testTag = "scan_gallery_image_button"
            )
        }

        // Decoded Result Card
        if (scannedResult != null) {
            val res = scannedResult!!
            val isUrl = res.startsWith("http://") || res.startsWith("https://") || res.matches("(?i)^(www\\.)?[a-z0-9]+([-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$".toRegex())

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Decoded: ${scannedFormat ?: "QR Code"}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            Row {
                                IconButton(onClick = { copyScannedResult() }, modifier = Modifier.size(32.dp)) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { shareScannedResult() }, modifier = Modifier.size(32.dp)) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = res,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        if (isUrl) {
                            val finalUrl = if (res.startsWith("http://") || res.startsWith("https://")) res else "https://$res"
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedStateButton(
                                text = if (language == AppLanguage.BANGLA) "ওয়েবসাইটে যান" else "Open in Browser",
                                onClick = { showUrlConfirmDialog = finalUrl },
                                icon = Icons.Default.OpenInBrowser,
                                borderColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "open_scanned_url_button"
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun decodeBarcodeFromBitmap(bitmap: Bitmap): com.google.zxing.Result? {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val source = RGBLuminanceSource(width, height, pixels)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
    return try {
        MultiFormatReader().decode(binaryBitmap)
    } catch (_: Exception) {
        null
    }
}

private fun toByteArray(buffer: ByteBuffer): ByteArray {
    buffer.rewind()
    val data = ByteArray(buffer.remaining())
    buffer.get(data)
    return data
}
