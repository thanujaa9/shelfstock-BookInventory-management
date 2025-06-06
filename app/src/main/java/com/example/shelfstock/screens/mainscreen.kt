package com.example.shelfstock.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Visibility // Use Visibility for view icon
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.shelfstock.data.Book
import com.example.shelfstock.viewmodel.BookViewModel

// Partial update to MainScreen.kt - only showing relevant changes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BookViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Book) -> Unit,
    onDeleteClick: (Book) -> Unit,
    onItemClick: (Book) -> Unit,
    onNavigateToNotifications: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    val bookList = viewModel.allBooks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var bookToDelete by remember { mutableStateOf<Book?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // Collect loading state
    val primaryColor = Color(0xFF5B429A)

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            val snackbarResult = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Dismiss"
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                viewModel.clearErrorMessage()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ShelfStock",
                        color = Color.White, // Set title color to white
                        fontWeight = FontWeight.Bold, // Make the title bold
                        fontFamily = FontFamily.SansSerif
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor, // Set background to purple
                ),
                actions = {
                    // Notification button in top right corner of TopAppBar
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Welcome Text and Search Bar remain the same

            // Show loading indicator if loading
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (bookList.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //val bookIcon = painterResource(id = R.drawable.ic_launcher_foreground) // Use your actual icon here
                        Icon(
                            imageVector = Icons.Filled.Book,
                            contentDescription = "No Books",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No books available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )}
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    items(bookList.value) { book ->
                        BookItem(
                            book = book,
                            onEditClick = { onEditClick(book) },
                            onDeleteClick = {
                                bookToDelete = book
                                showDialog = true
                            },
                            onItemClick = { onItemClick(book) }
                        )
                    }
                }
            }

            if (showDialog && bookToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false; bookToDelete = null },
                    title = { Text("Confirm Delete", color = Color(0xFF5B429A)) }, // Directly set color here
                    text = {
                        Text(
                            "Are you sure you want to delete '${bookToDelete?.name}'?",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                bookToDelete?.let { viewModel.delete(it) }
                                showDialog = false
                                bookToDelete = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5B429A),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false; bookToDelete = null },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF5B429A)
                            )
                        ) {
                            Text("Cancel")
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun BookItem(
    book: Book,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    val primaryColor = Color(0xFF5B429A)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) { // weight is added
                    Text(
                        text = book.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = primaryColor
                    )
                    Text(
                        text = "Category: ${book.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row {
                        Text(
                            text = "Qty: ${book.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = ", Price: $${book.price}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp)) // Add some space
                Row {
                    IconButton(onClick = onItemClick) {  // Changed to onItemClick
                        Icon(
                            Icons.Filled.Visibility,  // Use the Visibility icon
                            contentDescription = "View",
                            modifier = Modifier.size(20.dp),
                            tint = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(20.dp),
                            tint = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookItemPreview() {
    // Dummy book data for the preview
    val dummyBook = Book(id = 1, name = "Sample Book", category = "Fiction", quantity = 10, price = 25.99, language = "English", author = "John Doe", description = "A sample book description.")
    BookItem(book = dummyBook, onEditClick = {}, onDeleteClick = {}, onItemClick = {})
}