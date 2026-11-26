package com.example.khainiem

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ClaudeApi {
    // Đổi tên model tại đây nếu cần
    private const val MODEL = "claude-sonnet-5-5"

    suspend fun ask(system: String, user: String, key: String): String = withContext(Dispatchers.IO) {
        val conn = (URL("https://api.anthropic.com/v1/messages").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15000
            readTimeout = 60000
            doOutput = true
            setRequestProperty("content-type", "application/json")
            setRequestProperty("x-api-key", key)
            setRequestProperty("anthropic-version", "2023-06-01")
        }
        val body = JSONObject()
            .put("model", MODEL).put("max_tokens", 1800).put("system", system)
            .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", user)))
        conn.outputStream.use { it.write(body.toString().toByteArray()) }
        val code = conn.responseCode
        val text = (if (code in 200..299) conn.inputStream else (conn.errorStream ?: conn.inputStream))
            .bufferedReader().use { it.readText() }
        if (code !in 200..299) error("Lỗi API ($code): " + text.take(200))
        val content = JSONObject(text).getJSONArray("content")
        buildString {
            for (i in 0 until content.length()) {
                val b = content.getJSONObject(i)
                if (b.optString("type") == "text") append(b.getString("text"))
            }
        }
    }
}

object Prefs {
    private var sp: SharedPreferences? = null
    fun init(c: Context) { sp = c.getSharedPreferences("khainiem", Context.MODE_PRIVATE) }
    var key: String
        get() = sp?.getString("key", "") ?: ""
        set(v) { sp?.edit()?.putString("key", v)?.apply() }
}
