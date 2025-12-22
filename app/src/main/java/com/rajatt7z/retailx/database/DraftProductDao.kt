package com.rajatt7z.retailx.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DraftProductDao {
    @Query("SELECT * FROM draft_products ORDER BY createdAt DESC")
    suspend fun getAllDrafts(): List<DraftProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftProduct)

    @Delete
    suspend fun deleteDraft(draft: DraftProduct)

    @Query("DELETE FROM draft_products WHERE id = :id")
    suspend fun deleteDraftById(id: Long)
}
