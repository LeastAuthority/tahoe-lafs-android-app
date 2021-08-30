package org.tahoe.lafs.ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_scan.*
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.performBackPress
import org.tahoe.lafs.extension.set
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.ui.home.HomeActivity
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_TOKEN
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import timber.log.Timber
import javax.inject.Inject
import org.tahoe.lafs.model.QRCodeContents

@AndroidEntryPoint
open class ScannerFragment : BaseFragment() {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var qrCodeScanner: QRCodeScanner

    override fun getLayoutId() = R.layout.fragment_scan

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkCameraPermissions()

        if (permissionGranted) {
            qrCodeScanner.init(requireContext(), scanner_view)
            qrCodeScanner.scan(this::handleScannerText, this::handleScannerError)
        }
    }

    private fun handleScannerText(text: String) {
        Timber.d("Scanned value is = $text")

        activity?.runOnUiThread {
            QRCodeContents.parseContents(text).fold({ contents ->
                preferences.set(SCANNER_URL, contents.url.url.toString())
                preferences.set(SCANNER_TOKEN, contents.token)
                startActivity(Intent(activity, HomeActivity::class.java))
                activity?.finish()
            }, { error ->
                Toast.makeText(
                    requireContext(), error.message, Toast.LENGTH_SHORT
                ).show()
                performBackPress()
            })
        }
    }

    private fun handleScannerError(error: Exception) {
        activity?.runOnUiThread {
            Toast.makeText(
                requireContext(), "Camera initialization error: $error",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
                qrCodeScanner.init(requireContext(), scanner_view)
                qrCodeScanner.scan(this::handleScannerText, this::handleScannerError)
            } else {
                permissionGranted = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionGranted) {
            qrCodeScanner.scan(this::handleScannerText, this::handleScannerError)
        }
    }

    override fun onPause() {
        if (permissionGranted) {
            qrCodeScanner.release()
        }
        super.onPause()
    }
}