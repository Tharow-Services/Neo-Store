package com.machiav3lli.fdroid.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.machiav3lli.fdroid.database.entity.InstallTask
import kotlinx.coroutines.flow.Flow

// TODO make sure that apps that not uninstalled by Droid-ify still get removed
@Dao
interface InstallTaskDao : BaseDao<InstallTask> {
    suspend fun put(vararg tasks: InstallTask) {
        tasks.forEach { upsert(it) }
    }

    @Query("SELECT * FROM installTask ORDER BY added DESC")
    fun getAll(): List<InstallTask>

    @Query("SELECT * FROM installTask ORDER BY added DESC")
    fun getAllFlow(): Flow<List<InstallTask>>

    @Query("SELECT * FROM installTask WHERE cacheFileName = :fileName ORDER BY added ASC")
    fun get(fileName: String): InstallTask?

    @Query("SELECT * FROM installTask WHERE cacheFileName = :fileName ORDER BY added ASC")
    fun getFlow(fileName: String): Flow<InstallTask?>

    @Query("DELETE FROM installTask WHERE packageName = :packageName")
    fun delete(packageName: String)

    @Query("DELETE FROM installTask")
    fun emptyTable()
}