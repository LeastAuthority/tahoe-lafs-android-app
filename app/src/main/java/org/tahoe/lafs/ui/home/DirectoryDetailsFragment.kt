package org.tahoe.lafs.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.get
import org.tahoe.lafs.extension.getBaseUrl
import org.tahoe.lafs.extension.getShortCollectiveFolderName
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridApiDataHandler
import org.tahoe.lafs.network.services.GridNode
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.ui.viewmodel.GetFileStructureViewModel
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.Constants.TYPE_JSON
import org.tahoe.lafs.utils.Constants.URI_SCHEMA
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DirectoryDetailsFragment : BaseFragment(), GridItemClickListener {

    @Inject
    lateinit var preferences: SharedPreferences

    private val args: DirectoryDetailsFragmentArgs by navArgs()
    private val getFileStructureViewModel: GetFileStructureViewModel by viewModels()
    private lateinit var gridFolderAdapter: GridFolderAdapter
    private lateinit var gridNode: GridNode
    private lateinit var scannedUrl: String

    override fun getLayoutId() = R.layout.fragment_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridNode = args.gridNode
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

        (activity as HomeActivity).showBackButton()
        (activity as HomeActivity).setToolbarText(
            gridNode.name.getShortCollectiveFolderName(),
            "Last updated: Just Now"
        )
        getFileStructureViewModel.getFolderStructure(scannedUrl.getBaseUrl() + URI_SCHEMA + gridNode.roUri + TYPE_JSON)
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
                    Timber.d("JSON element for directory folder = ${resource.data}")
                    GridApiDataHandler.getAdminNodeFromROUriData(resource.data)?.let { adminNode ->
                        getFileStructureViewModel.getAdminFolderStructure(scannedUrl.getBaseUrl() + URI_SCHEMA + adminNode.roUri + TYPE_JSON)
                    }
                }
            }
        })

        getFileStructureViewModel.adminFolderStructure.observe(viewLifecycleOwner, { resource ->
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
                    Timber.d("JSON element for admin directory folder = ${resource.data}")
                    val nodesList = GridApiDataHandler.getFilesAndFoldersList(resource.data)
                        .filter { !it.deleted }
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
        getFileStructureViewModel.getFolderStructure(scannedUrl.getBaseUrl() + URI_SCHEMA + gridNode.roUri + TYPE_JSON)
    }

    override fun onGridItemClickListener(gridNode: GridNode) {
        Timber.d("Selected Node $gridNode")
        if (gridNode.isDir) {
            val directions = DirectoryDetailsFragmentDirections.toDetailsFragment(gridNode)
            findNavController().navigate(directions)
        }
    }
}