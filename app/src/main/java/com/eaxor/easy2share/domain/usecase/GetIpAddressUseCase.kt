package com.eaxor.easy2share.domain.usecase

import android.util.Log
import java.net.Inet4Address
import java.net.NetworkInterface
import javax.inject.Inject

class GetIpAddressUseCase @Inject constructor() {
    operator fun invoke(): String {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress ?: "---------"
        } catch (ex: Exception) {
            Log.e("IpAddressUseCase", "Unable to get IP address: " + ex.message)
            "---------"
        }
    }
}