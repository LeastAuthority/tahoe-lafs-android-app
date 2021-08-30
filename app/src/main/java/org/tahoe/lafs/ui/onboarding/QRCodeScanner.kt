package org.tahoe.lafs.ui.onboarding

import android.content.Context
import android.view.ViewGroup

interface QRCodeScanner {
  /** Any setup needed for the QR code scanner within a given context */
  fun init(ctx: Context, view: ViewGroup): Unit
  /** Scans a QR code and runs the given continuations */
  fun scan(success: (String) -> Unit, failure: (Exception) -> Unit): Unit
  /** Frees any resources allocated for the scanner */
  fun release(): Unit
}

