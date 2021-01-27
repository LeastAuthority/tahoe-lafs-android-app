package org.tahoe.lafs.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_magic_folder.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.formattedFolderUrl
import org.tahoe.lafs.extension.get
import org.tahoe.lafs.extension.getEndPointIp
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridApiDataHandler
import org.tahoe.lafs.network.services.GridNode
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.ui.viewmodel.GetFileStructureViewModel
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MagicFolderFragment : BaseFragment(), GridItemClickListener {

    @Inject
    lateinit var preferences: SharedPreferences

    private val getFileStructureViewModel: GetFileStructureViewModel by viewModels()
    private lateinit var gridFolderAdapter: GridFolderAdapter
    private lateinit var scannedUrl: String

    override fun getLayoutId() = R.layout.fragment_magic_folder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannedUrl = preferences.get(SCANNER_URL, EMPTY)
        initView()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        (activity as HomeActivity).showDrawerButton()
        (activity as HomeActivity).setToolbarText("Demo Grid", "Last updated: Just Now")
        (activity as HomeActivity).setNavigationViewDetails(scannedUrl.getEndPointIp())

        getFileStructureViewModel.getFolderStructure(scannedUrl.formattedFolderUrl())
    }

    private fun initListeners() {
        getFileStructureViewModel.folderStructure.observe(viewLifecycleOwner, { resource ->
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
                        gridFolderAdapter = GridFolderAdapter(nodesList, this)
                        recyclerView.adapter = gridFolderAdapter
                    }
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(refreshDataEvent: RefreshDataEvent) {
        getFileStructureViewModel.getFolderStructure(scannedUrl.formattedFolderUrl())
    }

    override fun onGridItemClickListener(gridNode: GridNode) {
        Timber.d("Selected Node $gridNode")
        if (gridNode.isDir) {
            val directions = MagicFolderFragmentDirections.toDetailsFragment(gridNode)
            findNavController().navigate(directions)
        }
    }
}