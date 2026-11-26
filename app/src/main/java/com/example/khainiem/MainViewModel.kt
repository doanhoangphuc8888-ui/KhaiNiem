package com.example.khainiem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private const val STYLE = "Trả lời bằng tiếng Việt, trung lập, chính xác. Với tôn giáo, học thuật hay quan điểm gây tranh cãi, trình bày đúng như truyền thống hay trường phái đó tự hiểu, nêu rõ chỗ còn tranh luận, không đứng về phía nào. Chỉ trả về JSON hợp lệ, không kèm văn bản hay markdown khác."
private const val CONCEPT_SYSTEM = "Bạn là từ điển khái niệm. $STYLE Định dạng: {\"name\":\"\",\"category\":\"Đạo đức|Tâm lý|Triết học|Tôn giáo|Khác\",\"definition\":\"1-2 câu\",\"details\":[\"3-4 ý chính\"],\"example\":\"\",\"related\":[\"2-4 khái niệm liên quan\"]}"
private const val COMPARE_SYSTEM = "Bạn giúp so sánh hai khái niệm (a là khái niệm thứ nhất, b là thứ hai). $STYLE Định dạng: {\"summary\":\"1-2 câu\",\"similarities\":[\"2-4 ý\"],\"differences\":[{\"aspect\":\"khía cạnh\",\"a\":\"\",\"b\":\"\"}],\"example\":\"một tình huống minh họa cả hai\",\"conclusion\":\"1-2 câu\"}. Mảng differences có 3-5 dòng."

private fun JSONArray?.list(): List<String> = if (this == null) emptyList() else List(length()) { getString(it) }
private fun parse(raw: String) = JSONObject(raw.substring(raw.indexOf('{'), raw.lastIndexOf('}') + 1))

class MainViewModel : ViewModel() {
    var tab by mutableStateOf(0)
    var apiKey by mutableStateOf(Prefs.key)
    var showKey by mutableStateOf(false)
    var detail by mutableStateOf<Concept?>(null)
    var query by mutableStateOf("")
    var category by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var a by mutableStateOf("")
    var b by mutableStateOf("")
    var result by mutableStateOf<Comparison?>(null)
    val aiConcepts = mutableStateListOf<Concept>()

    private fun key(s: String) = s.substringBefore(" (").trim().lowercase()
    private fun find(name: String) = (BUILT_IN + aiConcepts).firstOrNull { key(it.name) == key(name) }

    private fun launchTask(block: suspend () -> Unit) {
        viewModelScope.launch {
            loading = true; error = null
            if (apiKey.isBlank()) { showKey = true; error = "Cần nhập khóa API trước (nút Khóa API)"; loading = false; return@launch }
            try { block() } catch (e: Exception) { error = e.message ?: "Không kết nối được" }
            loading = false
        }
    }

    fun open(name: String) {
        find(name)?.let { detail = it; return }
        launchTask {
            val o = parse(ClaudeApi.ask(CONCEPT_SYSTEM, "Khái niệm: $name", apiKey))
            val c = Concept(
                o.optString("name", name), o.optString("category", "Khác"), o.getString("definition"),
                o.optJSONArray("details").list(), o.optString("example"), o.optJSONArray("related").list(), true
            )
            aiConcepts.add(0, c)
            detail = c
        }
    }

    fun compare() {
        val x = a.trim(); val y = b.trim()
        launchTask {
            val ctx = listOfNotNull(find(x), find(y)).joinToString("\n") { "${it.name}: ${it.definition}" }
            val o = parse(ClaudeApi.ask(COMPARE_SYSTEM, "a = \"$x\", b = \"$y\".\n$ctx", apiKey))
            val arr = o.getJSONArray("differences")
            result = Comparison(
                x, y, o.optString("summary"), o.optJSONArray("similarities").list(),
                List(arr.length()) { i -> arr.getJSONObject(i).let { DiffRow(it.optString("aspect"), it.optString("a"), it.optString("b")) } },
                o.optString("example"), o.optString("conclusion")
            )
        }
    }
}
