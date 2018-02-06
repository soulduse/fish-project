package com.dave.fish.common

/**
 * Created by soul on 2018. 2. 6..
 */
object DistanceUtil {

    // This function converts decimal degrees to radians
    private fun deg2rad(deg: Double): Double = deg * Math.PI / 180.0

    // This function converts radians to decimal degrees
    private fun rad2deg(rad: Double): Double = rad * 180 / Math.PI

    /**
     * 두 지점간의 거리 계산
     * 참고 : http://www.geodatasource.com/developers/java
     * @param lat1 지점 1 위도
     * *
     * @param lon1 지점 1 경도
     * *
     * @param lat2 지점 2 위도
     * *
     * @param lon2 지점 2 경도
     * *
     * @param unit 거리 표출단위
     * *
     * @return
     */
    fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double, unit: String): Double {
        val theta = lng1 - lng2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))

        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515

        if (unit === "K") {
            dist *= 1.609344
        } else if (unit === "M") {
            dist *= 1609.344
        }

        return dist
    }
}