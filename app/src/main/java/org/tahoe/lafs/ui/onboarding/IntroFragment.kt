package org.tahoe.lafs.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.tahoe.lafs.R
import org.tahoe.lafs.databinding.FragmentIntroBinding
import org.tahoe.lafs.extension.navigateWithAnim
import org.tahoe.lafs.ui.base.BaseFragment

@AndroidEntryPoint
class IntroFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_intro

    private lateinit var binding: FragmentIntroBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventListeners()
    }

    private fun initEventListeners() {
        binding.btnGetStarted.setOnClickListener {
            findNavController().navigateWithAnim(IntroFragmentDirections.toIntroScanCodeFragment())
        }
    }
}