# app overview
simple todo app

## screenshots
### tasks added and included
![Screenshot_20251004_233938_TodoApp](https://github.com/user-attachments/assets/b9425147-50bb-42f2-b446-c22772b7abfa)
### no tasks
![Screenshot_20251004_233951_TodoApp](https://github.com/user-attachments/assets/a54f0554-d40d-4b31-a3a1-bb36a5d5d31b)
### tried to add an empty class
![Screenshot_20251004_233946_TodoApp](https://github.com/user-attachments/assets/4c050367-6e51-42b2-aba0-80472e26bdb0)

## concepts used
### data class
```kt
data class TodoItem(val id: Int, val text: String, var isCompleted: Boolean = false)
```
### state
```kt
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
```
- rememberSaveable for todos to survive changes
- mutableStateOf to track changes in todo list
### state hoisting
```kt
// parent TodoScreen holds state
var inputText by rememberSaveable { mutableStateOf("") }

// child InputRow is stateless and receives state and passes event
InputRow(
    text = inputText,
    onTextChange = { inputText = it },
    onAdd = {
```
