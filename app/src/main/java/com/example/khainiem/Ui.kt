package com.example.khainiem

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private val T @Composable get() = MaterialTheme.typography
private val C @Composable get() = MaterialTheme.colorScheme

@Composable
fun App(vm: MainViewModel = viewModel()) {
    val d = vm.detail
    BackHandler(enabled = d != null) { vm.detail = null }
    if (vm.showKey) KeyDialog(vm)
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(vm.tab == 0, { vm.tab = 0; vm.detail = null }, { Icon(Icons.Default.Search, null) }, label = { Text("Tra cứu") })
            NavigationBarItem(vm.tab == 1, { vm.tab = 1; vm.detail = null }, { Icon(Icons.Default.Menu, null) }, label = { Text("So sánh") })
        }
    }) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when {
                d != null -> DetailScreen(vm, d)
                vm.tab == 0 -> SearchScreen(vm)
                else -> CompareScreen(vm)
            }
            if (vm.loading) LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun Block(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = T.titleSmall, color = C.primary)
        content()
    }
}

@Composable
fun SearchScreen(vm: MainViewModel) {
    val q = vm.query.trim()
    val list = (vm.aiConcepts + BUILT_IN).filter {
        (vm.category == null || it.category == vm.category) &&
            (q.isEmpty() || it.name.contains(q, true) || it.definition.contains(q, true))
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Khái niệm", style = T.headlineMedium, modifier = Modifier.weight(1f))
                TextButton({ vm.showKey = true }) { Text("Khóa API") }
            }
        }
        item {
            OutlinedTextField(
                value = vm.query, onValueChange = { vm.query = it }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tìm hoặc hỏi một khái niệm…") }, singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { if (q.isNotEmpty()) vm.open(q) })
            )
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CATEGORIES.forEach { c ->
                    FilterChip(vm.category == c, { vm.category = if (vm.category == c) null else c }, { Text(c) })
                }
            }
        }
        if (q.isNotEmpty()) item {
            Button({ vm.open(q) }, Modifier.fillMaxWidth(), enabled = !vm.loading) { Text("Hỏi AI về “$q”") }
        }
        vm.error?.let { e -> item { Text(e, color = C.error) } }
        items(list) { c ->
            ElevatedCard(Modifier.fillMaxWidth().clickable { vm.detail = c }) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(c.name, style = T.titleMedium)
                    Text(c.category + if (c.fromAi) " · AI" else "", style = T.labelMedium, color = C.primary)
                    Text(c.definition, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun DetailScreen(vm: MainViewModel, c: Concept) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { TextButton({ vm.detail = null }) { Text("← Quay lại") } }
        item {
            Column {
                Text(c.name, style = T.headlineMedium)
                Text(c.category + if (c.fromAi) " · do AI tạo, nên đối chiếu thêm nguồn khác" else "", style = T.labelMedium, color = C.primary)
            }
        }
        item { Text(c.definition, style = T.bodyLarge) }
        if (c.details.isNotEmpty()) item { Block("Ý chính") { c.details.forEach { Text("• $it") } } }
        if (c.example.isNotBlank()) item { Block("Ví dụ") { Text(c.example) } }
        if (c.related.isNotEmpty()) item {
            Block("Khái niệm liên quan") {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    c.related.forEach { r -> AssistChip({ vm.open(r) }, { Text(r) }) }
                }
            }
        }
        vm.error?.let { e -> item { Text(e, color = C.error) } }
        item {
            Button({ vm.a = c.name; vm.result = null; vm.tab = 1; vm.detail = null }, Modifier.fillMaxWidth()) {
                Text("So sánh với khái niệm khác")
            }
        }
    }
}

@Composable
fun CompareScreen(vm: MainViewModel) {
    val examples = listOf("Lòng trắc ẩn" to "Lòng tốt", "Lòng trắc ẩn" to "Sự đồng cảm", "Từ bi" to "Lòng thương hại", "Chủ nghĩa khắc kỷ" to "Vô vi")
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("So sánh", style = T.headlineMedium) }
        item { OutlinedTextField(vm.a, { vm.a = it }, Modifier.fillMaxWidth(), label = { Text("Khái niệm thứ nhất") }, singleLine = true) }
        item { OutlinedTextField(vm.b, { vm.b = it }, Modifier.fillMaxWidth(), label = { Text("Khái niệm thứ hai") }, singleLine = true) }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                examples.forEach { (x, y) -> AssistChip({ vm.a = x; vm.b = y }, { Text("$x - $y") }) }
            }
        }
        item {
            Button({ vm.compare() }, Modifier.fillMaxWidth(), enabled = !vm.loading && vm.a.isNotBlank() && vm.b.isNotBlank()) {
                Text("So sánh")
            }
        }
        vm.error?.let { e -> item { Text(e, color = C.error) } }
        vm.result?.let { r ->
            item { Card(Modifier.fillMaxWidth()) { Text(r.summary, Modifier.padding(16.dp), style = T.bodyLarge) } }
            item { Block("Giống nhau") { r.similarities.forEach { Text("• $it") } } }
            item { Text("Khác nhau", style = T.titleSmall, color = C.primary) }
            items(r.differences) { row ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(row.aspect, style = T.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) { Text(r.a, style = T.labelMedium, color = C.primary); Text(row.a) }
                            Column(Modifier.weight(1f)) { Text(r.b, style = T.labelMedium, color = C.primary); Text(row.b) }
                        }
                    }
                }
            }
            if (r.example.isNotBlank()) item { Block("Tình huống minh họa") { Text(r.example) } }
            item { Block("Kết luận") { Text(r.conclusion) } }
        }
    }
}

@Composable
fun KeyDialog(vm: MainViewModel) {
    var t by remember { mutableStateOf(vm.apiKey) }
    AlertDialog(
        onDismissRequest = { vm.showKey = false },
        title = { Text("Khóa API Claude") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Lấy khóa tại console.anthropic.com. Khóa chỉ lưu trong máy của bạn.")
                OutlinedTextField(t, { t = it }, singleLine = true, label = { Text("sk-ant-...") })
            }
        },
        confirmButton = { TextButton({ vm.apiKey = t.trim(); Prefs.key = t.trim(); vm.showKey = false }) { Text("Lưu") } },
        dismissButton = { TextButton({ vm.showKey = false }) { Text("Đóng") } }
    )
}
