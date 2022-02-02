package com.example.part2_chapter4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey val unid:Int?,
    @ColumnInfo(name="expression") val expression: String?,
    @ColumnInfo(name="result") val result: String?

)