package org.tahoe.lafs.ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.budiyev.android.codescanner.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_scan.*
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.set
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.ui.home.HomeActivity
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : BaseFragment() {

    @Inject
    lateinit var preferences: SharedPreferences

    private lateinit var codeScanner: CodeScanner

    override fun getLayoutId() = R.layout.fragment_scan

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkCameraPermissions()

        if (permissionGranted) {
            initCodeScanner()
            addCallbacks()
            codeScanner.startPreview()
        }
    }

    private fun initCodeScanner() {
        codeScanner = CodeScanner(requireContext(), scanner_view)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.TWO_DIMENSIONAL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
    }

    private fun addCallbacks() {
        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                preferences.set(SCANNER_URL, it.text)
                startActivity(Intent(activity, HomeActivity::class.java))
                activity?.finish()
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread {
                Toast.makeText(
                    requireContext(), "Camera initialization error: ${it.message}",
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