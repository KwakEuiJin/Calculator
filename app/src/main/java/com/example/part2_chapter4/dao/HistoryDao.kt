package com.example.part2_chapter4.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.part2_chapter4.model.History

@Dao
interface HistoryDao{

    @Query("SELECT * FROM History")
    fun getAll():List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM History")
    fun delectAll()
}