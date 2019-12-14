package de.klimek.kingdombuilder.service

import androidx.lifecycle.LiveData
import androidx.room.*
import de.klimek.kingdombuilder.model.Stats

@Dao
interface StatsDao {
    @Query("SELECT * FROM Stats")
    fun getAll(): LiveData<List<Stats>>

    @Query("SELECT * FROM Stats WHERE month=:month")
    fun getByMonth(month: Int): LiveData<Stats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(stats: Stats)

    @Delete
    suspend fun delete(stats: Stats)

    @Query("DELETE FROM Stats")
    suspend fun deleteAll()

    @Query("SELECT * FROM Stats ORDER BY month DESC LIMIT 1;")
    fun getLast(): LiveData<Stats>
}