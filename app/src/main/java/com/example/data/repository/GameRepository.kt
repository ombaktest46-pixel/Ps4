package com.example.data.repository

import com.example.data.dao.GameDao
import com.example.data.entity.Game
import kotlinx.coroutines.flow.Flow

/**
 * Repository layer managing game library operations, bridging Room DAO and the Presentation layer.
 */
class GameRepository(private val gameDao: GameDao) {

    val allGames: Flow<List<Game>> = gameDao.getAllGames()

    fun getGamesByPlatform(platform: String): Flow<List<Game>> {
        return gameDao.getGamesByPlatform(platform)
    }

    fun getGameById(id: Long): Flow<Game?> {
        return gameDao.getGameById(id)
    }

    suspend fun getGameCount(): Int {
        return gameDao.getGameCount()
    }

    suspend fun insertGame(game: Game): Long {
        return gameDao.insertGame(game)
    }

    suspend fun insertGames(games: List<Game>): List<Long> {
        return gameDao.insertGames(games)
    }

    suspend fun updateGame(game: Game) {
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: Game): Int {
        return gameDao.deleteGame(game)
    }

    suspend fun deleteGameById(id: Long) {
        gameDao.deleteGameById(id)
    }

    suspend fun deleteAllGames() {
        gameDao.deleteAllGames()
    }

    suspend fun getAllGamesList(): List<Game> {
        return gameDao.getAllGamesList()
    }
}
