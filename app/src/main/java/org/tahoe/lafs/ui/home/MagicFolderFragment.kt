package org.tahoe.lafs.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_magic.*
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.get
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridApiDataHandler
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.ui.viewmodel.GetFileStructureViewModel
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MagicFolderFragment : BaseFragment() {

    @Inject
    lateinit var preferences: SharedPreferences

    private val getFileStructureViewModel: GetFileStructureViewModel by viewModels()
    private lateinit var gridMagicFolderAdapter: GridMagicFolderAdapter

    override fun getLayoutId() = R.layout.fragment_magic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListeners()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        val scannedUrl = preferences.get(SCANNER_URL, "EMPTY")
        (activity as HomeActivity).setToolbarText("Demo Grid", "Last updated: Just Now")
        (activity as HomeActivity).setNavigationViewDetails(
            GridApiDataHandler.getGridSyncEndPointIp(
                scannedUrl
            )
        )

        getFileStructureViewModel.getMagicFolder(GridApiDataHandler.formattedUrl(scannedUrl))
    }

    private fun initListeners() {
        getFileStructureViewModel.magicFolderData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> showLoadingScreen()

                is Resource.Failure -> {
                    showContent()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Success -> {
                    showContent()
                    Timber.d("JSON element for Magic folder = ${resource.data}")
                    val nodesList = GridApiDataHandler.getMagicFolderGridNodes(
                        rootElement = resource.data,
                        shouldShowPersonalFolder = false
                    )

                    if (nodesList.isNotEmpty()) {
                        gridMagicFolderAdapter = GridMagicFolderAdapter(nodesList)
                        recyclerView.adapter = gridMagicFolderAdapter
                    }
                }
            }
        })
    }
}