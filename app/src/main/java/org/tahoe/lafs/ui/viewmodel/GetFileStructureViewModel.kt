package org.tahoe.lafs.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import org.tahoe.lafs.network.base.BaseViewModel
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridSyncApiRepository

class GetFileStructureViewModel @ViewModelInject constructor(
    private val gridSyncApiRepository: GridSyncApiRepository
) : BaseViewModel() {

    private val _folderStructure = MutableLiveData<Resource<JsonElement>>()
    val folderStructure: MutableLiveData<Resource<JsonElement>> = _folderStructure

    private val _adminFolderStructure = MutableLiveData<Resource<JsonElement>>()
    val adminFolderStructure: MutableLiveData<Resource<JsonElement>> = _adminFolderStructure

    fun getFolderStructure(url: String) {
        getResult({ gridSyncApiRepository.getFolderStructure(url) }) {
            _folderStructure.value = it
        }
    }

    fun getAdminFolderStructure(url: String) {
        getResult({ gridSyncApiRepository.getFolderStructure(url) }) {
            _adminFolderStructure.value = it
        }
    }
}