package com.dave.fish.db.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by soul on 2018. 2. 4..
 */
open class LocationModel: RealmObject() {
    @PrimaryKey
    var id: Long = 0L
    var createdAt: Date ?= null
    var updatedAt: Date = Date()
    var fixedLat: Double = 0.0
    var fixedLon: Double = 0.0
    var locations: RealmList<LatLonModel> ?= null
}

open class LatLonModel: RealmObject() {
    var latitude: Double = 0.0
    var longtitude: Double = 0.0
}
