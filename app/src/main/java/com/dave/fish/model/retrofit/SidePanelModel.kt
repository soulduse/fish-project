package com.dave.fish.model.retrofit

/**
 * Created by soul on 2017. 8. 27..
 */
data class SidePanelModel(
        val data: List<SidePanelData>
)

data class SidePanelData(
        val dateSun: String,
        val dateMoon: String,
        val obsPostId: String,
        val title: String,
        val address: String,
        val obsLat: Double,
        val obsLon: Double,
        val lvl1: String,
        val lvl2: String,
        val lvl3: String,
        val lvl4: String,
        val sdName: String,
        val sdTime: String
)