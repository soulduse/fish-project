package com.dave.fish.ui.map

import android.location.Address
import android.location.Geocoder
import com.dave.fish.MyApplication
import com.dave.fish.R
import com.dave.fish.util.DLog
import java.io.IOException


/**
 * Created by soul on 2018. 2. 10..
 */
object GeoUtil {

    fun getAddress(lat: Double, lon: Double): String{
        val context = MyApplication.context!!
        var addresses = mutableListOf<Address>()
        var errorMessage = ""
        var address: String ?= null

        try {
            addresses = Geocoder(context).getFromLocation(lat, lon, 1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = context.getString(R.string.service_not_available)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = context.getString(R.string.invalid_lat_long_used)
        }

        if(addresses.isEmpty()) {
            if(errorMessage.isEmpty()) {
                errorMessage = context.getString(R.string.no_address_found)
            }
        }else{
            val addressItem = addresses.first()
            val addressFragments = (0 .. addressItem.maxAddressLineIndex).map { i ->
                addressItem.getAddressLine(i)
                        .filterNot {
                            DLog.w("filterNot --> $it")
                            context.resources.getString(R.string.korea)?.contains(it)!!
                        }
            }

            address = addressFragments.first()
        }

        return address ?: errorMessage
    }
}
