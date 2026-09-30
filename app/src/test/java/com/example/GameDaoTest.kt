package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.dao.GameDao
import com.example.data.entity.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GameDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var gameDao: GameDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        gameDao = database.gameDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetGameById() = runTest {
        val game = Game(
            id = 1L,
            title = "Elden Ring",
            platform = "Winlator PC",
            iconPath = "content://media/external/images/123",
            packageName = "com.winlator"
        )
        gameDao.insertGame(game)

        val retrieved = gameDao.getGameByIdOnce(1L)
        assertNotNull(retrieved)
        assertEquals("Elden Ring", retrieved?.title)
        assertEquals("Winlator PC", retrieved?.platform)
        assertEquals("content://media/external/images/123", retrieved?.iconPath)
        assertEquals("com.winlator", retrieved?.packageName)
    }

    @Test
    fun getAllGamesFlow() = runTest {
        val game1 = Game(id = 1L, title = "Game 1", platform = "Switch Yuzu", iconPath = null, packageName = "org.yuzu.yuzu_emu")
        val game2 = Game(id = 2L, title = "Game 2", platform = "Android Native", iconPath = null, packageName = "com.mobile.legends")
        gameDao.insertGames(listOf(game1, game2))

        val games = gameDao.getAllGames().first()
        assertEquals(2, games.size)
        assertTrue(games.any { it.id == 1L && it.platform == "Switch Yuzu" })
        assertTrue(games.any { it.id == 2L && it.platform == "Android Native" })
    }

    @Test
    fun getSavedGamesList() = runTest {
        val game = Game(title = "God of War II", platform = "AetherSX2 PS2", iconPath = "/path/gow.png", packageName = "xyz.aethersx2.android")
        val id = gameDao.insert(game)
        assertTrue(id > 0)

        val savedList = gameDao.getSavedGamesList()
        assertTrue(savedList.isNotEmpty())
        assertEquals("God of War II", savedList.first().title)
    }

    @Test
    fun deleteGameById() = runTest {
        val game = Game(id = 99L, title = "To Delete", platform = "Winlator PC")
        gameDao.insertGame(game)

        assertEquals(1, gameDao.getGameCount())
        gameDao.deleteGameById(99L)

        val deleted = gameDao.getGameByIdOnce(99L)
        assertNull(deleted)
        assertEquals(0, gameDao.getGameCount())
    }
}
