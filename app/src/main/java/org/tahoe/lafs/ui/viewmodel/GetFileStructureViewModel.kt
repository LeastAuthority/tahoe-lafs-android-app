package org.tahoe.lafs.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import org.tahoe.lafs.network.base.BaseViewModel
import org.tahoe.lafs.network.base.Resource
import org.tahoe.lafs.network.services.GridSyncApiRepository

class GetFileStructureViewModel @ViewModelInject constructor(
    private val gridSyncApiRepository: GridSyncApiRepository
) : BaseViewModel() {

    private val _magicFolderData = MutableLiveData<Resource<Unit>>()
    val magicFolderData: MutableLiveData<Resource<Unit>> = _magicFolderData

    fun getMagicFolder(url: String) {
        getResult({ gridSyncApiRepository.getMagicFolder(url) }) {
            _magicFolderData.value = it
        }
    }
}