package org.tahoe.lafs.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_intro.*
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.get
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import javax.inject.Inject

@AndroidEntryPoint
class MagicFolderFragment : BaseFragment() {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun getLayoutId() = R.layout.fragment_magic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEventListeners()
    }

    private fun initView() {
        txtBody.text = preferences.get(SCANNER_URL, "EMPTY")
    }

    private fun initEventListeners() {

    }
}