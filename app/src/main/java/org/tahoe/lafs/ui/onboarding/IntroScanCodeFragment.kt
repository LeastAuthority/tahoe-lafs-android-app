package org.tahoe.lafs.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.tahoe.lafs.R
import org.tahoe.lafs.databinding.FragmentIntroScanCodeBinding
import org.tahoe.lafs.extension.navigateWithAnim
import org.tahoe.lafs.ui.base.BaseFragment

@AndroidEntryPoint
class IntroScanCodeFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_intro_scan_code

    private lateinit var binding: FragmentIntroScanCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentIntroScanCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventListeners()
    }

    private fun initEventListeners() {
        binding.btnScanQrCode.setOnClickListener {
            findNavController().navigateWithAnim(IntroScanCodeFragmentDirections.toScannerFragment())
        }
    }
}