package com.example.mobile_programming

class Player {
    var fio: String? = null
    var gender: String? = null
    var course: Int = 0
    var difficulty: Int = 0
    var birthDate: String? = null
    var zodiac: String? = null

    override fun toString(): String {
        return "ФИО: " + fio + "\n" +
                "Пол: " + gender + "\n" +
                "Курс: " + course + "\n" +
                "Уровень сложности: " + difficulty + "\n" +
                "Дата рождения: " + birthDate + "\n" +
                "Знак зодиака: " + zodiac
    }
}
