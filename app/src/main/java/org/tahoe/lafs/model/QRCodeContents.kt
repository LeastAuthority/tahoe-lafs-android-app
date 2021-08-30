package org.tahoe.lafs.model

import java.net.URL
import org.tahoe.lafs.extension.ResultExtensions
import org.tahoe.lafs.extension.ResultExtensions.flatMap

/** A wrapper class for valid QR codes expected for pairing */
data class QRCodeContents(val url: HttpsURL, val token: String) {
  companion object {
    fun parseContents(s: String): Result<QRCodeContents> {
      val parts = s.split(" ")

      return if (parts.size <= 1) {
        Result.failure(IllegalArgumentException("Invalid QR code"))
      } else {
        val (url, token) = parts
        val validatedUrl = Result.success(url).mapCatching { URL(it) }.flatMap(HttpsURL::fromURL)
        val validatedToken =
            if (token.isNotEmpty()) {
              Result.success(token)
            } else {
              Result.failure(IllegalArgumentException("Token has 0 length"))
            }

        ResultExtensions.zip(validatedUrl, validatedToken).map { (url, token) ->
          QRCodeContents(url, token)
        }
      }
    }
  }
}
