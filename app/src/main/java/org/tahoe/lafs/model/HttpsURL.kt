package org.tahoe.lafs.model

import java.net.URL

/** A wrapper class to ensure an underlying https URL */
class HttpsURL private constructor(val url: URL) {
  companion object {
    fun fromURL(url: URL): Result<HttpsURL> =
        if (url.protocol.equals("https")) {
          Result.success(HttpsURL(url))
        } else {
          Result.failure(IllegalArgumentException("Invalid protocol for URL: ${url.protocol}"))
        }
  }
}
