package org.tahoe.lafs.network.services

import com.google.gson.JsonElement
import org.tahoe.lafs.network.base.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GridSyncApiRepository @Inject constructor(
    private val gridSyncApiService: GridSyncApiService
) : BaseRepository() {

    suspend fun getFolderStructure(scanUrl: String): JsonElement {
        ensureInternetConnection()
        return gridSyncApiService.getFolderStructure(scanUrl)
    }
}