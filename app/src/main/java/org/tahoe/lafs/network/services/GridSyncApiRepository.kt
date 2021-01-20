package org.tahoe.lafs.network.services

import org.tahoe.lafs.network.base.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GridSyncApiRepository @Inject constructor(
    private val gridSyncApiService: GridSyncApiService
) : BaseRepository() {

    suspend fun getMagicFolder(scanUrl: String): Unit {
        ensureInternetConnection()
        return gridSyncApiService.getMagicFolder(scanUrl)
    }
}