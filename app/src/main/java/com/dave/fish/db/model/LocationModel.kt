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
    var createdDate: Date = Date()
    var updatedDate: Date = Date()
    lateinit var locations: RealmList<LatLonModel>
}

open class LatLonModel: RealmObject() {
    var latitude: Double = 0.0
    var longtitude: Double = 0.0
}
