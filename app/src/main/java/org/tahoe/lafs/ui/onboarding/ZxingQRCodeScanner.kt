package org.tahoe.lafs.ui.onboarding

import android.content.Context
import android.view.ViewGroup
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import java.lang.Exception as Exception1

class ZxingQRCodeScanner : QRCodeScanner {
  private lateinit var codeScanner: CodeScanner

  class InvalidCodeType(codeType: BarcodeFormat) : Exception1("Invalid code type $codeType")

  override fun init(ctx: Context, view: ViewGroup) {
    codeScanner = CodeScanner(ctx, view as CodeScannerView)
    codeScanner.camera = CodeScanner.CAMERA_BACK
    codeScanner.formats = CodeScanner.TWO_DIMENSIONAL_FORMATS
    codeScanner.autoFocusMode = AutoFocusMode.SAFE
    codeScanner.scanMode = ScanMode.SINGLE
    codeScanner.isAutoFocusEnabled = true
    codeScanner.isFlashEnabled = false
  }

  override fun scan(success: (String) -> Unit, failure: (Exception) -> Unit): Unit {
    codeScanner.decodeCallback =
        DecodeCallback {
          if (it.barcodeFormat == BarcodeFormat.QR_CODE) {
            success(it.text)
          } else {
            failure(InvalidCodeType(it.barcodeFormat))
          }
        }

    codeScanner.errorCallback = ErrorCallback { failure(it) }

    codeScanner.startPreview()
  }

  override fun release() {
    codeScanner.releaseResources()
  }
}

