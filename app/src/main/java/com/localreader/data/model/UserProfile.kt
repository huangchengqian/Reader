package com.localreader.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val nickname: String = "读者",
    val avatarPath: String? = null,
)
