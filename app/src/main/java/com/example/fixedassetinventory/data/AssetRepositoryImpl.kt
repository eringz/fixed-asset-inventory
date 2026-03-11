package com.example.fixedassetinventory.data

import com.example.fixedassetinventory.data.dao.AssetDao
import com.example.fixedassetinventory.data.entity.Asset
import kotlinx.coroutines.flow.Flow

class AssetRepositoryImpl(private val assetDao: AssetDao) {
    fun getAllAssets(): Flow<List<Asset>> = assetDao.getAllAssets()

    suspend fun getAssetByNumber(assetNo: String): Asset? = assetDao.findAssetByNumber(assetNo)

    suspend fun insertAsset(asset: Asset) = assetDao.insertAsset(asset)

    suspend fun updateAsset(asset: Asset) = assetDao.update(asset)

    suspend fun deleteAsset(asset: Asset) = assetDao.deleteAsset(asset)

    suspend fun exists(assetNo: String): Boolean = assetDao.exists(assetNo)

    suspend fun insertAll(assets: List<Asset>) = assetDao.insertAll(assets)

    suspend fun getAllForExport(): List<Asset> = assetDao.getAllAssetsForExport()

}