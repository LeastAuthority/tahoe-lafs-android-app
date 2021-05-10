package org.tahoe.lafs.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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
import org.tahoe.lafs.extension.get
import org.tahoe.lafs.extension.getEndPointIp
import org.tahoe.lafs.extension.getLastUpdatedText
import org.tahoe.lafs.extension.navigateWithAnim
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridApiDataHandler
import org.tahoe.lafs.network.services.GridNode
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.ui.viewmodel.GetFileStructureViewModel
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.SharedPreferenceKeys.GRID_SYNC_TIMESTAMP
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MagicFolderFragment : BaseFragment(), GridItemClickListener {

    @Inject
    lateinit var preferences: SharedPreferences

    private val getFileStructureViewModel: GetFileStructureViewModel by viewModels()
    private lateinit var gridFolderAdapter: GridFolderAdapter
    private lateinit var scannedUrl: String
    private var isDataLoaded = false

    override fun getLayoutId() = R.layout.fragment_magic_folder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannedUrl = preferences.get(SCANNER_URL, EMPTY)
        Timber.d("Scanned url = $scannedUrl")
        initView()
        initListeners()

        savedInstanceState?.getBoolean("IS_DATA_LOADED")?.let {
            isDataLoaded = it
        }

        if (!isDataLoaded) {
            loadData()
        } else {
            val gridNodes = GridApiDataHandler.getGridData(preferences)
            if (gridNodes.isNotEmpty()) {
                gridFolderAdapter =
                    GridFolderAdapter(gridNodes.sortedByDescending { it.isDir }
                        .sortedBy { it.name }, this)
                recyclerView.adapter = gridFolderAdapter
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_DATA_LOADED", isDataLoaded)
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
        (activity as HomeActivity).setToolbarText(
            "Demo Grid",
            preferences.getLong(GRID_SYNC_TIMESTAMP, Date().time)
                .getLastUpdatedText(requireContext())
        )
        (activity as HomeActivity).setNavigationViewDetails(scannedUrl.getEndPointIp())
    }

    private fun loadData() {
        getFileStructureViewModel.getAllFilesAndFolders(scannedUrl)
    }

    private fun initListeners() {
        getFileStructureViewModel.filesData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> showLoadingScreen()

                is Resource.Failure -> {
                    showContent()

                    val gridNodes = GridApiDataHandler.getGridData(preferences)
                    if (gridNodes.isNotEmpty()) {
                        gridFolderAdapter =
                            GridFolderAdapter(gridNodes.sortedByDescending { it.isDir }
                                .sortedBy { it.name }, this)
                        recyclerView.adapter = gridFolderAdapter
                    } else {
                        showError()
                    }
                }

                is Resource.Success -> {
                    showContent()

                    isDataLoaded = true

                    (activity as HomeActivity).setToolbarText(
                        "Demo Grid",
                        preferences.getLong(GRID_SYNC_TIMESTAMP, Date().time)
                            .getLastUpdatedText(requireContext())
                    )

                    Timber.d("JSON element for Magic folder = ${resource.data}")
                    val allFoldersList = resource.data

                    if (allFoldersList.isNotEmpty()) {
                        gridFolderAdapter =
                            GridFolderAdapter(allFoldersList.sortedByDescending { it.isDir }
                                .sortedBy { it.name }, this)
                        recyclerView.adapter = gridFolderAdapter
                    }
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(refreshDataEvent: RefreshDataEvent) {
        getFileStructureViewModel.getAllFilesAndFolders(scannedUrl)
    }

    override fun onGridItemClickListener(gridNode: GridNode) {
        Timber.d("Selected Node $gridNode")
        if (gridNode.isDir && gridNode.filesList.isNotEmpty()) {
            val directions = MagicFolderFragmentDirections.toDetailsFragment(gridNode)
            findNavController().navigateWithAnim(directions)
        }
    }

    override fun onDownloadItemClickListener(gridNode: GridNode) {
        // Do Nothing
    }
}