package org.tahoe.lafs.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_intro_scan_code.*
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.navigateWithAnim
import org.tahoe.lafs.ui.base.BaseFragment

@AndroidEntryPoint
class IntroScanCodeFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_intro_scan_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventListeners()
    }

    private fun initEventListeners() {
        btnScanQrCode.setOnClickListener {
            findNavController().navigateWithAnim(IntroScanCodeFragmentDirections.toScannerFragment())
        }
    }
}