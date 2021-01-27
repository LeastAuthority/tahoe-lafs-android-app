package org.tahoe.lafs.network.services

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.tahoe.lafs.utils.Constants.ADMIN_NODE
import org.tahoe.lafs.utils.Constants.DIR_NODE
import org.tahoe.lafs.utils.Constants.EMPTY
import org.tahoe.lafs.utils.Constants.PERSONAL_TEXT
import timber.log.Timber

object GridApiDataHandler {

    /**
     * Get children elements from root level
     */
    private fun getChildrenFromRootElement(rootElement: JsonElement): JsonObject? {
        if (rootElement.isJsonArray && rootElement.asJsonArray.count() > 1) {
            val rootChildrenElement: JsonObject? = rootElement.asJsonArray[1].asJsonObject
            rootChildrenElement?.let { rootChild ->
                return rootChild.get(CHILDREN).asJsonObject
            }
        }
        return null
    }

    /**
     * Gets GridNode from lower level element
     */
    private fun getGridNode(
        gridJsonObject: JsonObject?,
        key: String,
        dirOrFileNode: String
    ): GridNode? {
        gridJsonObject?.let { details ->
            val mutable: Boolean = details.get(MUTABLE).asBoolean

            var verifyUri: String = EMPTY
            if (details.has(VERIFY_URI)) {
                verifyUri = details.get(VERIFY_URI).asString
            }

            var roUri: String = EMPTY
            if (details.has(RO_URI)) {
                roUri = details.get(RO_URI).asString
            }

            var rwUri: String = EMPTY
            if (details.has(RW_URI)) {
                rwUri = details.get(RW_URI).asString
            }

            var size: Long = 0
            if (details.has(SIZE)) {
                size = details.get(SIZE).asLong
            }

            var format: String = EMPTY
            if (details.has(FORMAT)) {
                format = details.get(FORMAT).asString
            }

            var deleted = false
            var linkMoTime = 0.0
            var linkCrTime = 0.0
            var lastDownloadTime = 0.0
            if (details.has(METADATA)) {
                val metadata: JsonObject = details.get(METADATA).asJsonObject
                val tahoe = metadata.get(TAHOE).asJsonObject
                linkMoTime = tahoe.get(LINK_MO_TIME).asDouble
                linkCrTime = tahoe.get(LINK_CR_TIME).asDouble

                if (metadata.has(DELETED)) {
                    deleted = metadata.get(DELETED).asBoolean
                }

                if (metadata.has(LAST_DOWNLOAD_TIME)) {
                    lastDownloadTime = metadata.get(LAST_DOWNLOAD_TIME).asDouble
                }
            }

            return GridNode(
                verifyUri = verifyUri,
                roUri = roUri,
                rwUri = rwUri,
                linkMoTime = linkMoTime,
                linkCrTime = linkCrTime,
                mutable = mutable,
                name = key,
                isDir = dirOrFileNode == DIR_NODE,
                size = size,
                deleted = deleted,
                lastDownloadedTimestamp = lastDownloadTime,
                format = format
            )
        }
        return null
    }

    /**
     * Parse the Magic link data and get list of folder at root level
     */
    fun getMagicFolderGridNodes(
        rootElement: JsonElement,
        shouldShowPersonalFolder: Boolean
    ): List<GridNode> {
        val nodesList: MutableList<GridNode> = mutableListOf()
        val childrenJsonObject = getChildrenFromRootElement(rootElement)
        childrenJsonObject?.let { childrenObject ->
            for (key in childrenObject.keySet()) {
                val nodeElement = childrenObject.get(key).asJsonArray
                if (nodeElement.isJsonArray && nodeElement.asJsonArray.count() > 1) {
                    val dirOrFileNode = nodeElement.asJsonArray[0].asString
                    val gridNode = getGridNode(
                        gridJsonObject = nodeElement.asJsonArray[1].asJsonObject,
                        key = key,
                        dirOrFileNode = dirOrFileNode
                    )
                    gridNode?.let { node ->
                        if (key.contains(PERSONAL_TEXT)) {
                            if (shouldShowPersonalFolder) {
                                nodesList.add(node)
                            } else {
                                Timber.d("Ignore the personal folder with name = $key")
                            }
                        } else {
                            nodesList.add(node)
                        }
                    }
                }
            }
        }
        return nodesList
    }

    /**
     * Get Admin Node element to get subfolder and file structure
     */
    fun getAdminNodeFromROUriData(rootElement: JsonElement): GridNode? {
        val childrenJsonObject = getChildrenFromRootElement(rootElement)
        childrenJsonObject?.let { childrenObject ->
            for (key in childrenObject.keySet()) {
                val nodeElement = childrenObject.get(key).asJsonArray
                if (key == ADMIN_NODE && nodeElement.isJsonArray && nodeElement.asJsonArray.count() > 1) {
                    val dirOrFileNode = nodeElement.asJsonArray[0].asString
                    return getGridNode(
                        gridJsonObject = nodeElement.asJsonArray[1].asJsonObject,
                        key = key,
                        dirOrFileNode = dirOrFileNode
                    )
                }
            }
        }
        return null
    }

    /**
     * Parse JSON element and retrieves List of Grid Nodes which represents files & folder
     */
    fun getFilesAndFoldersList(rootElement: JsonElement): List<GridNode> {
        val nodesList: MutableList<GridNode> = mutableListOf()
        val childrenJsonObject = getChildrenFromRootElement(rootElement)
        childrenJsonObject?.let { childrenObject ->
            for (key in childrenObject.keySet()) {
                val nodeElement = childrenObject.get(key).asJsonArray
                if (nodeElement.isJsonArray && nodeElement.asJsonArray.count() > 1) {
                    val dirOrFileNode = nodeElement.asJsonArray[0].asString
                    val gridNode = getGridNode(
                        gridJsonObject = nodeElement.asJsonArray[1].asJsonObject,
                        key = key,
                        dirOrFileNode = dirOrFileNode
                    )
                    gridNode?.let { node ->
                        nodesList.add(node)
                    }
                }
            }
        }
        return nodesList
    }
}