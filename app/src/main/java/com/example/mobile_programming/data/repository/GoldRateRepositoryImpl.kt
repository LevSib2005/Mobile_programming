package com.example.mobile_programming.data.repository

import com.example.mobile_programming.data.api.CbrApi
import com.example.mobile_programming.data.dto.MetallResponse
import com.example.mobile_programming.data.local.GoldRateCache
import com.example.mobile_programming.domain.models.GoldRate
import com.example.mobile_programming.domain.repository.GoldRateRepository
import org.simpleframework.xml.core.Persister
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GoldRateRepositoryImpl(
    private val api: CbrApi,
    private val cache: GoldRateCache
) : GoldRateRepository {

    companion object {
        private const val GOLD_CODE = "1"
    }

    override suspend fun loadRate(): GoldRate {
        return try {
            val rate = fetchFromNetwork()
            cache.saveRate(rate)
            rate
        } catch (e: Exception) {
            val cachedRate = cache.getRate()
            if (cachedRate != null) {
                cachedRate
            } else {
                throw e
            }
        }
    }

    private suspend fun fetchFromNetwork(): GoldRate {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val to = Calendar.getInstance()
        val from = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }

        val body = api.getMetal(
            dateFrom = format.format(from.time),
            dateTo = format.format(to.time)
        )

        val response = body.byteStream().use { stream ->
            Persister().read(MetallResponse::class.java, stream, false)
        }

        val value = response.records
            ?.asReversed()
            ?.firstOrNull { it.code == GOLD_CODE }
            ?.buy
            ?.let { parseRub(it) }
            ?: error("Не найден курс золота в ответе ЦБ")

        return GoldRate(
            valueRubPerGram = value,
            updatedAtMs = System.currentTimeMillis()
        )
    }

    private fun parseRub(raw: String): Double? {
        return raw.trim()
            .replace(" ", "")
            .replace(',', '.')
            .toDoubleOrNull()
    }
}