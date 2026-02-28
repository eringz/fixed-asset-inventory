    package com.example.fixedassetinventory.data

    import android.content.Context
    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase
    import com.example.fixedassetinventory.data.dao.AssetDao
    import com.example.fixedassetinventory.data.dao.UserDao
    import com.example.fixedassetinventory.data.entity.Asset
    import com.example.fixedassetinventory.data.entity.User


    @Database(entities = [Asset::class, User::class], version = 2, exportSchema = false)
    abstract class AppDatabase: RoomDatabase() {
        abstract fun userDao(): UserDao
        abstract fun assetDao(): AssetDao

        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "fixed_asset_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    instance
                }
            }
        }
    }