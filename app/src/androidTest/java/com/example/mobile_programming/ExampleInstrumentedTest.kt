package com.example.mobile_programming

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mobile_programming.data.dao.ScoreDao
import com.example.mobile_programming.data.dao.UserDao
import com.example.mobile_programming.data.entity.ScoreEntity
import com.example.mobile_programming.data.entity.UserEntity
import com.example.mobile_programming.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScoreDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var scoreDao: ScoreDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        scoreDao = db.scoreDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // пустая

    @Test
    fun emptyDb_top_returnsEmptyList() = runBlocking {
        val top = scoreDao.getTop(3)
        assertTrue("Топ на пустой базе должен быть пустым", top.isEmpty())
    }

    @Test
    fun emptyDb_personal_returnsNull() = runBlocking {
        val personal = scoreDao.getPersonal(999L)
        assertNull("Личный рекорд несуществующего юзера должен быть null", personal)
    }

    @Test
    fun emptyDb_topByDifficulty_returnsEmptyList() = runBlocking {
        val top = scoreDao.getTopByDifficulty(3, 5)
        assertTrue(top.isEmpty())
    }

    // одинаковые очки

    @Test
    fun sameScore_stableOrderByIdDesc() = runBlocking {
        val id1 = userDao.insert(newUser("ivanov"))
        val id2 = userDao.insert(newUser("petrov"))

        // Оба с одинаковыми очками, одинаковым временем
        val now = 1000L
        scoreDao.upsert(newScore(userId = id1, score = 10, updatedAt = now))
        scoreDao.upsert(newScore(userId = id2, score = 10, updatedAt = now))

        val top = scoreDao.getTop(3)

        assertEquals(2, top.size)
        // Петров записан вторым → у него id больше → он выше (id DESC)
        assertEquals("petrov", top[0].playerName.substringBefore(" ").lowercase())
        assertEquals("ivanov", top[1].playerName.substringBefore(" ").lowercase())
    }

    @Test
    fun sameScore_differentTime_earlierWins() = runBlocking {
        val id1 = userDao.insert(newUser("ivanov"))
        val id2 = userDao.insert(newUser("petrov"))

        // Одинаковые очки, но разное время: Петров был раньше
        scoreDao.upsert(newScore(userId = id1, score = 10, updatedAt = 2000L))
        scoreDao.upsert(newScore(userId = id2, score = 10, updatedAt = 1000L))

        val top = scoreDao.getTop(3)

        assertEquals(2, top.size)
        // Петров (updatedAt меньше) должен быть выше
        assertEquals("petrov", top[0].playerName.substringBefore(" ").lowercase())
        assertEquals("ivanov", top[1].playerName.substringBefore(" ").lowercase())
    }

    // фильтр по сложности

    @Test
    fun difficultyFilter_returnsOnlyMatching() = runBlocking {
        val id1 = userDao.insert(newUser("ivanov"))
        val id2 = userDao.insert(newUser("petrov"))

        scoreDao.upsert(newScore(userId = id1, score = 20, difficulty = 4, updatedAt = 1000L))
        scoreDao.upsert(newScore(userId = id2, score = 15, difficulty = 7, updatedAt = 1000L))

        val top4 = scoreDao.getTopByDifficulty(3, 4)
        val top7 = scoreDao.getTopByDifficulty(3, 7)
        val top5 = scoreDao.getTopByDifficulty(3, 5)

        assertEquals(1, top4.size)
        assertEquals("ivanov", top4[0].playerName.substringBefore(" ").lowercase())

        assertEquals(1, top7.size)
        assertEquals("petrov", top7[0].playerName.substringBefore(" ").lowercase())

        assertTrue("На скорости 5 никто не играл", top5.isEmpty())
    }


    @Test
    fun upsert_doesNotCreateDuplicate() = runBlocking {
        val id1 = userDao.insert(newUser("ivanov"))

        scoreDao.upsert(newScore(userId = id1, score = 10, updatedAt = 1000L))
        scoreDao.upsert(newScore(userId = id1, score = 20, updatedAt = 2000L))

        val top = scoreDao.getTop(10)
        assertEquals("Должна быть ровно одна запись рекорда", 1, top.size)
        assertEquals(20, top[0].score)
    }


    @Test
    fun deleteUser_cascadesToScore() = runBlocking {
        val id1 = userDao.insert(newUser("ivanov"))
        scoreDao.upsert(newScore(userId = id1, score = 10, updatedAt = 1000L))

        assertEquals(1, scoreDao.getTop(3).size)

        userDao.deleteById(id1)

        assertTrue("Рекорд удалённого юзера должен исчезнуть", scoreDao.getTop(3).isEmpty())
    }


    private fun newUser(login: String) = UserEntity(
        login = login,
        password = "pass",
        fio = login.replaceFirstChar { it.uppercase() } + " Тестов",
        gender = "Мужской",
        course = 1,
        birthDate = "01.01.2000",
        zodiac = "Козерог"
    )

    private fun newScore(
        userId: Long,
        score: Int,
        difficulty: Int = 5,
        roundTime: Int = 60,
        updatedAt: Long
    ) = ScoreEntity(
        userId = userId,
        score = score,
        difficulty = difficulty,
        roundTime = roundTime,
        updatedAt = updatedAt
    )
}