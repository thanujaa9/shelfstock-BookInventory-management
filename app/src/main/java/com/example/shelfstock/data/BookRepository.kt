// BookRepository.kt
package com.example.shelfstock.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class BookRepository(private val bookDao: BookDao) {

    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()
        .catch { e ->
            // Log the exception
            e.printStackTrace()
            emit(emptyList())
        }

    suspend fun insert(book: Book): Result<Unit> = try {
        bookDao.insertBook(book)
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun update(book: Book): Result<Unit> = try {
        bookDao.updateBook(book)
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun delete(book: Book): Result<Unit> = try {
        bookDao.deleteBook(book)
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun getBookById(id: Int): Result<Book?> = try {
        val book = bookDao.getBookById(id)
        Result.success(book)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    fun searchBooks(query: String): Flow<List<Book>> =
        bookDao.searchBooksByName(query)
            .catch { e ->
                // Log the exception
                e.printStackTrace()
                emit(emptyList())
            }
}