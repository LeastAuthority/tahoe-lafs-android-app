package org.tahoe.lafs.utils

import okhttp3.Dns
import timber.log.Timber
import java.net.InetAddress
import java.util.logging.Logger

class DnsOverride(private val dns: Dns) : Dns {
    private val overrides = linkedMapOf<String, String>()

    private fun put(host: String, target: String) {
        overrides[host] = target
    }

    override fun lookup(hostname: String): List<InetAddress> {
        Timber.d("lookup is in progress for $hostname")
        val override = overrides[hostname]

        if (override != null) {
            Timber.d("Using Dns Override ($hostname): $override")
            return listOf(InetAddress.getByName(override))
        }

        return dns.lookup(hostname)
    }

    companion object {
        private val logger = Logger.getLogger(DnsOverride::class.java.name)

        fun build(dns: Dns, resolveStrings: List<String>): DnsOverride {
            val dnsOverride = DnsOverride(dns)

            for (resolveString in resolveStrings) {
                val parts = resolveString.trim().split(":", limit = 2)

                if (parts.size != 2) {
                    Timber.e("Invalid resolve string '$resolveString'")
                }

                dnsOverride.put(parts[0], parts[1])
            }

            return dnsOverride
        }
    }
}
