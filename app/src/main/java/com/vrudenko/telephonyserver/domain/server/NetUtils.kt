package com.vrudenko.telephonyserver.domain.server

import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import java.util.regex.Pattern

class NetUtils {

    companion object {

        private val log by lazyLogger()

        /**
         * Ipv4 address check.
         */
        private val IPV4_PATTERN = Pattern.compile(
            "^(" + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$"
        )

        /**
         * Get local Ip address.
         */
        fun getLocalIPAddress(): InetAddress? {
            var enumeration: Enumeration<NetworkInterface>? = null
            try {
                enumeration = NetworkInterface.getNetworkInterfaces()
            } catch (e: SocketException) {
                log.error("getLocalIPAddress:NetworkInterface.getNetworkInterfaces error", e)
            }
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    val nif = enumeration.nextElement()
                    val inetAddresses = nif.inetAddresses
                    if (inetAddresses != null) {
                        while (inetAddresses.hasMoreElements()) {
                            val inetAddress = inetAddresses.nextElement()
                            if (!inetAddress.isLoopbackAddress && isIPv4Address(inetAddress.hostAddress)) {
                                return inetAddress
                            }
                        }
                    }
                }
            }
            return null
        }

        /**
         * Check if valid IPV4 address.
         *
         * @param input the address string to check for validity.
         *
         * @return True if the input parameter is a valid IPv4 address.
         */
        private fun isIPv4Address(input: String?): Boolean {
            return input?.let {
                IPV4_PATTERN.matcher(it).matches()
            } ?: false
        }
    }

}