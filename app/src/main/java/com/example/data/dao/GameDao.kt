package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.Game
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for managing saved games in the library.
 * Supports inserting, deleting, and fetching saved games.
 */
@Dao
interface GameDao {

    // ==========================================
    // INSERT OPERATIONS
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<Game>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>): List<Long>

    // ==========================================
    // DELETE OPERATIONS
    // ==========================================

    @Delete
    suspend fun delete(game: Game): Int

    @Delete
    suspend fun deleteGame(game: Game): Int

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: Long)

    @Query("DELETE FROM games")
    suspend fun deleteAll()

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    // ==========================================
    // FETCH OPERATIONS (Saved Games List)
    // ==========================================

    @Query("SELECT * FROM games ORDER BY id ASC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games ORDER BY id ASC")
    fun getSavedGames(): Flow<List<Game>>

    @Query("SELECT * FROM games ORDER BY id ASC")
    fun getGames(): Flow<List<Game>>

    @Query("SELECT * FROM games ORDER BY id ASC")
    fun getAll(): Flow<List<Game>>

    @Query("SELECT * FROM games ORDER BY id ASC")
    suspend fun getAllGamesList(): List<Game>

    @Query("SELECT * FROM games ORDER BY id ASC")
    suspend fun getSavedGamesList(): List<Game>

    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    fun getGameById(id: Long): Flow<Game?>

    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    suspend fun getGameByIdOnce(id: Long): Game?

    @Query("SELECT * FROM games WHERE platform = :platform ORDER BY id ASC")
    fun getGamesByPlatform(platform: String): Flow<List<Game>>

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGameCount(): Int

    // ==========================================
    // UPDATE OPERATIONS
    // ==========================================

    @Update
    suspend fun update(game: Game)

    @Update
    suspend fun updateGame(game: Game)
}
