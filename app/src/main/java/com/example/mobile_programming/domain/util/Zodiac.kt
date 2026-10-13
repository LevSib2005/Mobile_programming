package com.example.mobile_programming.domain.util

object Zodiac {

    fun fromDate(day: Int, month: Int): String = when (month) {
        1 -> if (day <= 19) "Козерог" else "Водолей"
        2 -> if (day <= 18) "Водолей" else "Рыбы"
        3 -> if (day <= 20) "Рыбы" else "Овен"
        4 -> if (day <= 19) "Овен" else "Телец"
        5 -> if (day <= 20) "Телец" else "Близнецы"
        6 -> if (day <= 20) "Близнецы" else "Рак"
        7 -> if (day <= 22) "Рак" else "Лев"
        8 -> if (day <= 22) "Лев" else "Дева"
        9 -> if (day <= 22) "Дева" else "Весы"
        10 -> if (day <= 22) "Весы" else "Скорпион"
        11 -> if (day <= 21) "Скорпион" else "Стрелец"
        12 -> if (day <= 21) "Стрелец" else "Козерог"
        else -> "Неизвестно"
    }
}