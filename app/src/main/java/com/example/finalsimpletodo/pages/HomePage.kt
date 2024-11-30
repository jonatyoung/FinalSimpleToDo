package com.example.finalsimpletodo.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finalsimpletodo.viewmodel.TodoViewModel
import com.example.finalsimpletodo.viewmodel.AuthViewModel
import com.example.finalsimpletodo.viewmodel.AuthState
import com.example.finalsimpletodo.data.model.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    todoViewModel: TodoViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var todoTitle by remember { mutableStateOf("") }
    var updatingTodo: TodoItem? by remember { mutableStateOf(null) }

    val todos by todoViewModel.todos.collectAsState()
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Todo List") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.signout()
                        todoViewModel.clearTodos()
                    }) {
                        Icon(Icons.Default.ExitToApp, "Sign Out")
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(Icons.Default.Add, "Add Todo")
                }
                if (updatingTodo != null) {
                    FloatingActionButton(
                        onClick = {
                            showUpdateDialog = true
                            todoTitle = updatingTodo?.title ?: ""
                        }
                    ) {
                        Icon(Icons.Default.Edit, "Update Todo")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = todos,
                key = { it.id }
            ) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggleComplete = { todoViewModel.toggleTodoComplete(todo.id, it) },
                    onDelete = { todoViewModel.deleteTodo(todo.id) },
                    onUpdate = {
                        updatingTodo = todo
                        todoTitle = todo.title
                        showUpdateDialog = true
                    }
                )
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    todoTitle = ""
                },
                title = { Text("Add Todo") },
                text = {
                    TextField(
                        value = todoTitle,
                        onValueChange = { todoTitle = it },
                        label = { Text("Todo Title") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (todoTitle.isNotEmpty()) {
                                todoViewModel.addTodo(todoTitle)
                                showAddDialog = false
                                todoTitle = ""
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            todoTitle = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showUpdateDialog) {
            AlertDialog(
                onDismissRequest = {
                    showUpdateDialog = false
                    todoTitle = ""
                    updatingTodo = null
                },
                title = { Text("Update Todo") },
                text = {
                    TextField(
                        value = todoTitle,
                        onValueChange = { todoTitle = it },
                        label = { Text("Todo Title") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (todoTitle.isNotEmpty() && updatingTodo != null) {
                                todoViewModel.updateTodo(updatingTodo!!.id, todoTitle)
                                showUpdateDialog = false
                                todoTitle = ""
                                updatingTodo = null
                            }
                        }
                    ) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showUpdateDialog = false
                            todoTitle = ""
                            updatingTodo = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun TodoItemCard(
    todo: TodoItem,
    onToggleComplete: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = todo.completed,
                    onCheckedChange = onToggleComplete
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                IconButton(onClick = onUpdate) {
                    Icon(Icons.Default.Edit, "Update Todo")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete Todo")
                }
            }
        }
    }
}