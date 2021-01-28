package org.tahoe.lafs.ui.viewmodel

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.tahoe.lafs.extension.formattedFolderUrl
import org.tahoe.lafs.extension.getBaseUrl
import org.tahoe.lafs.network.base.BaseError
import org.tahoe.lafs.network.base.BaseViewModel
import org.tahoe.lafs.network.base.FailureStatusCode
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridApiDataHandler
import org.tahoe.lafs.network.services.GridNode
import org.tahoe.lafs.network.services.GridSyncApiRepository
import org.tahoe.lafs.utils.Constants.TYPE_JSON
import org.tahoe.lafs.utils.Constants.URI_SCHEMA

class GetFileStructureViewModel @ViewModelInject constructor(
    private val gridSyncApiRepository: GridSyncApiRepository,
    private val preferences: SharedPreferences
) : BaseViewModel() {

    private val _filesData = MutableLiveData<Resource<List<GridNode>>>()
    val filesData: MutableLiveData<Resource<List<GridNode>>> = _filesData

    private val _fileData = MutableLiveData<Resource<ResponseBody>>()
    val fileData: MutableLiveData<Resource<ResponseBody>> = _fileData

    fun getAllFilesAndFolders(url: String) {
        viewModelScope.launch {
            _filesData.postValue(Resource.Loading())
            try {
                coroutineScope {
                    val magicFolderRequest =
                        async { gridSyncApiRepository.getFolderStructure(url.formattedFolderUrl()) }
                    val gridNodes =
                        GridApiDataHandler.getMagicFolderGridNodes(
                            magicFolderRequest.await(),
                            false
                        )

                    for (gridNode in gridNodes) {
                        val adminUrl = url.getBaseUrl() + URI_SCHEMA + gridNode.roUri + TYPE_JSON
                        val adminFolderRequest =
                            async { gridSyncApiRepository.getFolderStructure(adminUrl) }
                        val adminGridNode =
                            GridApiDataHandler.getAdminNodeFromROUriData(adminFolderRequest.await())

                        val fileFolderUrl =
                            url.getBaseUrl() + URI_SCHEMA + adminGridNode?.roUri + TYPE_JSON
                        val filesFolderRequest =
                            async { gridSyncApiRepository.getFolderStructure(fileFolderUrl) }
                        val filesFolderNodes =
                            GridApiDataHandler.getFilesAndFoldersList(filesFolderRequest.await())

                        gridNode.filesList =
                            GridApiDataHandler.arrangeFilesAndSubFolders(filesFolderNodes)

                        GridApiDataHandler.saveGridData(gridNodes, preferences)
                    }

                    _filesData.postValue(Resource.Success(gridNodes))
                }
            } catch (exception: Exception) {
                val gridNodes = GridApiDataHandler.getGridData(preferences)
                if (gridNodes.isNotEmpty()) {
                    _filesData.postValue(Resource.Success(gridNodes))
                } else {
                    _filesData.postValue(
                        Resource.Failure(
                            FailureStatusCode.CLIENT_ERROR,
                            BaseError(400, "Bad request", exception)
                        )
                    )
                }
            }
        }
    }

    fun downloadFile(url: String) {
        getResult({ gridSyncApiRepository.downloadFile(url) }) {
            _fileData.value = it
        }
    }
}