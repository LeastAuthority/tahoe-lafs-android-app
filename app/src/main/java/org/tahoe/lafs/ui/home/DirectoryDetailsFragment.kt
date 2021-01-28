package org.tahoe.lafs.ui.home

import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.tahoe.lafs.R
import org.tahoe.lafs.extension.*
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridNode
import org.tahoe.lafs.ui.base.BaseFragment
import org.tahoe.lafs.ui.viewmodel.GetFileStructureViewModel
import org.tahoe.lafs.utils.Constants
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.FileUtils
import org.tahoe.lafs.utils.SharedPreferenceKeys
import org.tahoe.lafs.utils.SharedPreferenceKeys.SCANNER_URL
import timber.log.Timber
import java.util.*
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
    private var selectedGridNode: GridNode? = null

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
            preferences.getLong(SharedPreferenceKeys.GRID_SYNC_TIMESTAMP, Date().time)
                .getLastUpdatedText(requireContext())
        )

        if (gridNode.filesList.isNotEmpty()) {
            gridFolderAdapter =
                GridFolderAdapter(
                    gridNode.filesList.sortedWith(
                        compareBy({ !it.isDir },
                            { it.name })
                    ), this
                )
            recyclerView.adapter = gridFolderAdapter
        }
    }

    private fun initListeners() {
        getFileStructureViewModel.filesData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> showLoadingScreen()

                is Resource.Failure -> {
                    showContent()
                    showError()
                }

                is Resource.Success -> {
                    showContent()
                    Timber.d("JSON element for Magic folder = ${resource.data}")
                    //TODO: Find specific node and display data here
                }
            }
        })

        getFileStructureViewModel.fileData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.download_started),
                        Toast.LENGTH_LONG
                    ).show()

                }

                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.download_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Resource.Success -> {
                    showContent()
                    if (FileUtils.checkSDCardStatus()) {
                        val writtenToDisk: Boolean = writeResponseBodyToDisk(resource.data)
                        Timber.d("file download was a success? $writtenToDisk")

                        if(writtenToDisk){
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.download_successfully),
                                Toast.LENGTH_LONG
                            ).show()
                        }
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
            val directions = DirectoryDetailsFragmentDirections.toDetailsFragment(gridNode)
            findNavController().navigateWithAnim(directions)
        }
    }

    override fun onDownloadItemClickListener(gridNode: GridNode) {
        if (gridNode.roUri.isNotEmpty()) {
            selectedGridNode = gridNode

            checkStoragePermissions()

            if (permissionGranted) {
                getFileStructureViewModel.downloadFile(scannedUrl.getBaseUrl() + Constants.URI_SCHEMA + gridNode.roUri)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.grant_download_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        selectedGridNode?.name?.let { fileName ->
            FileUtils.saveFile(
                body,
                FileUtils.createOrGetFile(
                    fileName,
                    FileUtils.getFolderName(requireContext())
                ).absolutePath
            )
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFileStructureViewModel.downloadFile(scannedUrl.getBaseUrl() + Constants.URI_SCHEMA + selectedGridNode?.roUri)
            }
        }
    }
}