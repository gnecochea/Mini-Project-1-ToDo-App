package com.example.todoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.TodoAppTheme

data class TodoItem(val id: Int, val text: String, var isCompleted: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("onCreated is called")

        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TodoScreen(modifier: Modifier = Modifier) {
    var todos by rememberSaveable(stateSaver = listSaver(
        save = { list -> list.map { "${it.id}|${it.text}|${it.isCompleted}" } },
        restore = { saved ->
            saved.map {
                val parts = it.split("|")
                TodoItem(parts[0].toInt(), parts[1], parts[2].toBoolean())
            }
        }
    )) { mutableStateOf(listOf<TodoItem>()) }

    var inputText by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = modifier.padding(16.dp)) {
        InputRow(
            text = inputText,
            onTextChange = { inputText = it },
            onAdd = {
                val trimmed = inputText.trim()
                if (trimmed.isEmpty()) {
                    Toast.makeText(context, "Task cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    todos = todos + TodoItem(id = todos.size + 1, text = trimmed)
                    inputText = ""
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TodoSection(
            title = "Items",
            items = todos.filter { !it.isCompleted },
            onToggle = { item -> todos = todos.map { if (it.id == item.id) it.copy(isCompleted = true) else it } },
            onDelete = { item -> todos = todos.filterNot { it.id == item.id } }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TodoSection(
            title = "Completed Items",
            items = todos.filter { it.isCompleted },
            onToggle = { item -> todos = todos.map { if (it.id == item.id) it.copy(isCompleted = false) else it } },
            onDelete = { item -> todos = todos.filterNot { it.id == item.id } }
        )
    }
}

@Composable
fun InputRow(text: String, onTextChange: (String) -> Unit, onAdd: () -> Unit) {
    Row {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Enter a task") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onAdd) { Text("Add") }
    }
}

@Composable
fun TodoSection(
    title: String,
    items: List<TodoItem>,
    onToggle: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    if (items.isEmpty()) {
        Text("No $title yet", style = MaterialTheme.typography.bodyMedium)
    } else {
        Text(title, style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(items, key = { it.id }) { item ->
                TodoRow(item = item, onToggle = onToggle, onDelete = onDelete)
            }
        }
    }
}

@Composable
fun TodoRow(item: TodoItem, onToggle: (TodoItem) -> Unit, onDelete: (TodoItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Checkbox(checked = item.isCompleted, onCheckedChange = { onToggle(item) })
            Text(item.text, modifier = Modifier.padding(start = 8.dp))
        }
        IconButton(onClick = { onDelete(item) }) {
            Icon(Icons.Default.Close, contentDescription = "Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoPreview() {
    TodoAppTheme {
        TodoScreen()
    }
}
