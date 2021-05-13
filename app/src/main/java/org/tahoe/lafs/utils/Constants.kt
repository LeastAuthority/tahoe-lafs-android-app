package org.tahoe.lafs.utils

object Constants {
    const val SPLASH_TIME_OUT: Long = 500
    const val TYPE_JSON = "?t=json"
    const val URI_SCHEMA = "/uri/"
    const val DIR_NODE = "dirnode"
    const val EMPTY = ""
    const val COLLECTIVE_TEXT = "(collective)"
    const val PERSONAL_TEXT = "(personal)"
    const val ADMIN_NODE = "admin"
    const val SUBFOLDER_SUFFIX = "@_"
    const val RO_DIR_VALUE = "URI:LIT:"
    const val ONE_SECOND: Long = 1000
    const val ONE_MINUTE: Long = ONE_SECOND * 60
    const val ONE_HOUR: Long = ONE_MINUTE * 60
    const val ONE_DAY: Long = ONE_HOUR * 24
    const val TAHOE_LAFS_FOLDER = "/Tahoe-LAFS/"
    const val BEGIN_CERTIFICATE_TAG = "-----BEGIN CERTIFICATE-----\n"
    const val END_CERTIFICATE_TAG = "-----END CERTIFICATE-----"
}

object SharedPreferenceKeys {
    const val SCANNER_URL = "SCANNER_URL"
    const val SCANNER_TOKEN = "SCANNER_TOKEN"
    const val DIR_NODE_DATA = "DIR_NODE_DATA"
    const val GRID_DATA = "GRID_DATA"
    const val GRID_SYNC_TIMESTAMP = "GRID_SYNC_TIMESTAMP"
}