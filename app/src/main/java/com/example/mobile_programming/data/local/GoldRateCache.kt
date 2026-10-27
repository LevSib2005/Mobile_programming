package com.example.mobile_programming.data.local

import android.content.Context
import com.example.mobile_programming.domain.models.GoldRate

class GoldRateCache(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("gold_rate_cache", Context.MODE_PRIVATE)

    fun saveRate(rate: GoldRate) {
        prefs.edit()
            .putFloat(KEY_VALUE, rate.valueRubPerGram.toFloat())
            .putLong(KEY_TIME, rate.updatedAtMs)
            .apply()
    }

    fun getRate(): GoldRate? {
        val value = prefs.getFloat(KEY_VALUE, -1f)
        val time = prefs.getLong(KEY_TIME, 0L)

        return if (value >= 0f) {
            GoldRate(valueRubPerGram = value.toDouble(), updatedAtMs = time)
        } else {
            null
        }
    }

    companion object {
        private const val KEY_VALUE = "rate_value"
        private const val KEY_TIME = "rate_time"
    }
}