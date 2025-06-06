// BookViewModel.kt
package com.example.shelfstock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shelfstock.data.Book
import com.example.shelfstock.data.BookRepository
import com.example.shelfstock.data.Notification
import com.example.shelfstock.data.NotificationType
import com.example.shelfstock.data.NotificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _books = repository.allBooks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allBooks: StateFlow<List<Book>> = _searchQuery
        .debounce(300)
        .combine(_books) { query, books ->
            if (query.isBlank()) {
                books
            } else {
                books.filter { book ->
                    book.name.contains(query, ignoreCase = true) ||
                            book.category.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook

    private val _isBookSaved = MutableStateFlow(false)
    val isBookSaved: StateFlow<Boolean> = _isBookSaved.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insert(book: Book) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.insert(book)

            result.fold(
                onSuccess = {
                    _isBookSaved.value = true
                    _errorMessage.value = null

                    // Create notification for new book
                    val notification = Notification(
                        title = "Book Added",
                        message = "${book.name} has been added to your inventory",
                        type = NotificationType.BOOK_ADDED
                    )
                    notificationRepository.insertNotification(notification)
                },
                onFailure = { e ->
                    _errorMessage.value = "Error adding book: ${e.localizedMessage ?: "Unknown error"}"
                    _isBookSaved.value = false
                }
            )
        } catch (e: Exception) {
            _errorMessage.value = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
            _isBookSaved.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun update(book: Book) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.update(book)

            result.fold(
                onSuccess = {
                    _isBookSaved.value = true
                    _errorMessage.value = null

                    // Create notification for updated book
                    val notification = Notification(
                        title = "Book Updated",
                        message = "${book.name} has been updated",
                        type = NotificationType.BOOK_UPDATED
                    )
                    notificationRepository.insertNotification(notification)
                },
                onFailure = { e ->
                    _errorMessage.value = "Error updating book: ${e.localizedMessage ?: "Unknown error"}"
                    _isBookSaved.value = false
                }
            )
        } catch (e: Exception) {
            _errorMessage.value = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
            _isBookSaved.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun delete(book: Book) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.delete(book)

            result.fold(
                onSuccess = {
                    _errorMessage.value = null

                    // Create notification for deleted book
                    val notification = Notification(
                        title = "Book Deleted",
                        message = "${book.name} has been removed from your inventory",
                        type = NotificationType.BOOK_DELETED
                    )
                    notificationRepository.insertNotification(notification)
                },
                onFailure = { e ->
                    _errorMessage.value = "Error deleting book: ${e.localizedMessage ?: "Unknown error"}"
                }
            )
        } catch (e: Exception) {
            _errorMessage.value = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
            _isLoading.value = false
        }
    }

    fun getBookById(id: Int) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.getBookById(id)

            result.fold(
                onSuccess = { book ->
                    _selectedBook.value = book
                    _errorMessage.value = null
                },
                onFailure = { e ->
                    _errorMessage.value = "Error retrieving book: ${e.localizedMessage ?: "Unknown error"}"
                }
            )
        } catch (e: Exception) {
            _errorMessage.value = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
            _isLoading.value = false
        }
    }

    fun setSelectedBook(book: Book?) {
        _selectedBook.value = book
    }

    fun clearSelectedBook() {
        _selectedBook.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun resetIsBookSaved() {
        _isBookSaved.value = false
    }
}

class BookViewModelFactory(
    private val repository: BookRepository,
    private val notificationRepository: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(repository, notificationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}