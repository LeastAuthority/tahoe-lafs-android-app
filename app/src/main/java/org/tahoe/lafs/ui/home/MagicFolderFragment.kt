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
import org.tahoe.lafs.utils.Utils
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
        val scannedUrl = preferences.get(SCANNER_URL, "EMPTY")
        txtBody.text = scannedUrl
        (activity as HomeActivity).setToolbarText("Demo Grid", "Last updated: Just Now")
        (activity as HomeActivity).setNavigationViewDetails(Utils.getGridSyncEndPointIp(scannedUrl))
    }

    private fun initEventListeners() {

    }
}