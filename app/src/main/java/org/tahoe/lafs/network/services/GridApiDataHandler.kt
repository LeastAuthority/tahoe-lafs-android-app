package org.tahoe.lafs.network.services

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import timber.log.Timber
import java.net.URL

object GridApiDataHandler {

    private val TYPE_JSON = "?t=json"
    private val URI_SCHEMA = "/uri/"
    private val EMPTY = ""

    fun getGridSyncEndPointIp(scannedUrl: String): String {
        if (scannedUrl.isNotBlank()) {
            val fullUrl = scannedUrl.split(" ")[0]
            if (fullUrl.isNotEmpty()) {
                Timber.d("Full parse URL = $fullUrl")
                val ipAddress = URL(fullUrl).host
                if (ipAddress.isNotEmpty()) {
                    return ipAddress
                }
            }
        }
        return "0.0.0.0"
    }

    fun formattedUrl(scannedUrl: String) = scannedUrl.replace(" ", URI_SCHEMA).plus(TYPE_JSON)

    fun getMagicFolderGridNodes(
        rootElement: JsonElement,
        shouldShowPersonalFolder: Boolean
    ): List<GridNode> {
        val nodesList: MutableList<GridNode> = mutableListOf()
        if (rootElement.isJsonArray && rootElement.asJsonArray.count() > 1) {
            val rootChildrenElement: JsonObject? = rootElement.asJsonArray[1].asJsonObject
            rootChildrenElement?.let { rootChild ->
                val childrenJsonObject = rootChild.get("children").asJsonObject
                for (key in childrenJsonObject.keySet()) {
                    val nodeElement = childrenJsonObject.get(key).asJsonArray
                    if (nodeElement.isJsonArray && nodeElement.asJsonArray.count() > 1) {
                        val detailsElement: JsonObject? = nodeElement.asJsonArray[1].asJsonObject
                        detailsElement?.let { details ->
                            val mutable: Boolean = details.get("mutable").asBoolean

                            var verifyUri: String = EMPTY
                            if (details.has("verify_uri")) {
                                verifyUri = details.get("verify_uri").asString
                            }

                            var roUri: String = EMPTY
                            if (details.has("ro_uri")) {
                                roUri = details.get("ro_uri").asString
                            }

                            var rwUri: String = EMPTY
                            if (details.has("rw_uri")) {
                                rwUri = details.get("rw_uri").asString
                            }
                            val metadata: JsonObject? =
                                details.get("metadata").asJsonObject.get("tahoe").asJsonObject
                            val linkMoTime = metadata?.get("linkmotime")?.asDouble ?: 0.0
                            val linkCrTime = metadata?.get("linkcrtime")?.asDouble ?: 0.0

                            if (key.contains("(personal)")) {
                                if (shouldShowPersonalFolder) {
                                    nodesList.add(
                                        GridNode(
                                            verifyUri,
                                            roUri,
                                            rwUri,
                                            linkMoTime,
                                            linkCrTime,
                                            mutable,
                                            key
                                        )
                                    )
                                } else {
                                    // Don't add personal folder to list
                                }
                            } else {
                                nodesList.add(
                                    GridNode(
                                        verifyUri,
                                        roUri,
                                        rwUri,
                                        linkMoTime,
                                        linkCrTime,
                                        mutable,
                                        key
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        return nodesList
    }
}