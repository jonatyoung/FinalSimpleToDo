package com.example.finalsimpletodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalsimpletodo.data.model.TodoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos

    init {
        fetchTodos()
    }

    private fun fetchTodos() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("todos")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener

                    val todoList = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(TodoItem::class.java)?.copy(id = doc.id)
                    } ?: emptyList()

                    _todos.value = todoList
                }
        }
    }

    fun addTodo(title: String) {
        val userId = auth.currentUser?.uid ?: return

        val newTodo = hashMapOf(
            "title" to title,
            "completed" to false,
            "userId" to userId
        )

        viewModelScope.launch {
            db.collection("todos").add(newTodo)
        }
    }

    fun updateTodo(todoId: String, newTitle: String) {
        viewModelScope.launch {
            db.collection("todos")
                .document(todoId)
                .update("title", newTitle)
        }
    }

    fun toggleTodoComplete(todoId: String, isComplete: Boolean) {
        viewModelScope.launch {
            db.collection("todos")
                .document(todoId)
                .update("completed", isComplete)
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            db.collection("todos")
                .document(todoId)
                .delete()
        }
    }

    fun clearTodos() {
        _todos.value = emptyList()
    }
}