package com.example.fixedassetinventory.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fixedassetinventory.data.entity.Asset
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAsset(asset: Asset): Long

    @Query("SELECT * FROM assets ORDER BY created_at ASC")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE UPPER(asset_number) = UPPER(:assetNum) LIMIT 1")
    suspend fun findAssetByNumber(assetNum: String): Asset?

    @Query("SELECT 1 FROM assets WHERE UPPER(asset_number) = UPPER(:assetNo)")
    suspend fun exists(assetNo: String): Boolean

    @Query("SELECT * FROM assets ORDER BY asset_number ASC")
    suspend fun getAllAssetsForExport(): List<Asset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<Asset>)

    @Update
    suspend fun update(asset: Asset)

    @Delete
    suspend fun deleteAsset(asset: Asset)
}