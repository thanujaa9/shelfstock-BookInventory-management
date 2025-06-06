package com.example.shelfstock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shelfstock.data.BookDatabase
import com.example.shelfstock.data.BookRepository
import com.example.shelfstock.data.NotificationRepository
import com.example.shelfstock.screens.*
import com.example.shelfstock.viewmodel.BookViewModel
import com.example.shelfstock.viewmodel.BookViewModelFactory
import com.example.shelfstock.viewmodel.NotificationViewModel
import com.example.shelfstock.viewmodel.NotificationViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = BookDatabase.getDatabase(applicationContext)
        val bookRepository = BookRepository(database.bookDao())
        val notificationRepository = NotificationRepository(database.notificationDao())

        // Update BookViewModelFactory to include notificationRepository
        val bookViewModelFactory = BookViewModelFactory(bookRepository, notificationRepository)
        val notificationViewModelFactory = NotificationViewModelFactory(notificationRepository)

        setContent {
            val navController = rememberNavController()
            val bookViewModel: BookViewModel = viewModel(factory = bookViewModelFactory)
            val notificationViewModel: NotificationViewModel = viewModel(factory = notificationViewModelFactory)

            MyApp(
                navController = navController,
                bookViewModel = bookViewModel,
                notificationViewModel = notificationViewModel
            )
        }
    }
}

@Composable
fun MyApp(
    navController: NavHostController,
    bookViewModel: BookViewModel,
    notificationViewModel: NotificationViewModel
) {
    NavHost(navController = navController, startDestination = NavigationRoutes.WelcomeScreen) {

        // Welcome screen
        composable(NavigationRoutes.WelcomeScreen) {
            WelcomeScreen(
                onNavigateToMain = {
                    navController.navigate(NavigationRoutes.MainScreen) {
                        popUpTo(NavigationRoutes.WelcomeScreen) { inclusive = true }
                    }
                }
            )
        }

        // Notification screen
        composable(NavigationRoutes.NotificationScreen) {
            NotificationScreen(
                viewModel = notificationViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Main book list screen with notification button
        composable(NavigationRoutes.MainScreen) {
            MainScreen(
                viewModel = bookViewModel,
                onAddClick = {
                    bookViewModel.clearSelectedBook()
                    navController.navigate(NavigationRoutes.AddEditBookScreen)
                },
                onEditClick = { book ->
                    bookViewModel.setSelectedBook(book)
                    navController.navigate("${NavigationRoutes.AddEditBookScreen}?bookId=${book.id}")
                },
                onDeleteClick = { book -> bookViewModel.delete(book) },
                onItemClick = { book -> navController.navigate("${NavigationRoutes.ItemDetailScreen}/${book.id}") },
                onNavigateToNotifications = { navController.navigate(NavigationRoutes.NotificationScreen) },
                searchQuery = bookViewModel.searchQuery.collectAsState().value,
                onSearchQueryChange = bookViewModel::updateSearchQuery
            )
        }

        // Add/Edit book screen
        composable(
            route = NavigationRoutes.AddEditBookScreen + "?bookId={bookId}",
            arguments = listOf(navArgument("bookId") { nullable = true })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()

            LaunchedEffect(bookId) {
                if (bookId != null) {
                    bookViewModel.getBookById(bookId)
                } else {
                    bookViewModel.clearSelectedBook()
                }
            }

            val selectedBook = bookViewModel.selectedBook.collectAsState().value
            val isBookSaved = bookViewModel.isBookSaved.collectAsState().value

            LaunchedEffect(isBookSaved) {
                if (isBookSaved) {
                    navController.navigate(NavigationRoutes.MainScreen) {
                        popUpTo(NavigationRoutes.MainScreen) { inclusive = true }
                        launchSingleTop = true
                    }
                    bookViewModel.resetIsBookSaved()
                }
            }

            AddEditBookScreen(
                onSave = { book ->
                    if (book.id == 0) {
                        bookViewModel.insert(book)
                    } else {
                        bookViewModel.update(book)
                    }
                },
                onCancel = { navController.popBackStack() },
                initialBook = selectedBook,
                viewModel = bookViewModel
            )
        }

        // Item Detail Screen
        composable(
            route = NavigationRoutes.ItemDetailScreen + "/{bookId}",
            arguments = listOf(navArgument("bookId") {})
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()

            if (bookId != null) {
                LaunchedEffect(bookId) {
                    bookViewModel.getBookById(bookId)
                }

                val selectedBook = bookViewModel.selectedBook.collectAsState().value
                selectedBook?.let { book ->
                    ItemDetailScreen(book = book, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}