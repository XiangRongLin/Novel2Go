package com.kaiserpudding.novel2go.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kaiserpudding.novel2go.database.dao.DownloadDao
import com.kaiserpudding.novel2go.database.dao.DownloadInfoDao
import com.kaiserpudding.novel2go.model.Download
import com.kaiserpudding.novel2go.model.DownloadInfo


@Database(
    entities = [Download::class, DownloadInfo::class], version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun downloadDao(): DownloadDao
    abstract fun downloadInfoDao(): DownloadInfoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tmpInstance = INSTANCE
            if (tmpInstance != null) {
                return tmpInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE download_infos
                    (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    url TEXT NOT NULL,
                    name TEXT NOT NULL,
                    is_chapter INTEGER NOT NULL,
                    toc_url TEXT
                    )"""
                )
                database.execSQL(
                    """CREATE TABLE downloads_new
                    (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    path TEXT NOT NULL,
                    url TEXT NOT NULL,
                    timestamp INTEGER NOT NULL);"""
                )
                database.execSQL(
                    """INSERT INTO downloads_new (id, title, path, url, timestamp)
                        SELECT id, title, path, url, timestamp FROM downloads"""
                )
                database.execSQL("DROP TABLE downloads")
                database.execSQL("ALTER TABLE downloads_new RENAME TO downloads")
            }
        }
    }

}
