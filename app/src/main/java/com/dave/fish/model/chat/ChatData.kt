package com.dave.fish.model.chat

import org.joda.time.DateTime
import java.util.*

/**
 * Created by soul on 2018. 2. 28..
 */
data class ChatData (
        var userName: String,
        var message: String,
        val createdAt: Date = DateTime.now().toDate()
)