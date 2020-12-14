package org.tahoe.lafs.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_code_scanner.*
import org.tahoe.lafs.R


class ScanCodeActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val RC_PERMISSION = 10
    private var permissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_scanner)

        checkPermissions()

        if (permissionGranted) {
            initCodeScanner()
            addCallbacks()
            codeScanner.startPreview()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionGranted = false
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    RC_PERMISSION
                )
            } else {
                permissionGranted = true
            }
        } else {
            permissionGranted = true
        }
    }

    private fun initCodeScanner() {
        codeScanner = CodeScanner(this, scanner_view)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.TWO_DIMENSIONAL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
    }

    private fun addCallbacks() {
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan Result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
                initCodeScanner()
                addCallbacks()
                codeScanner.startPreview()
            } else {
                permissionGranted = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionGranted) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (permissionGranted) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }
}