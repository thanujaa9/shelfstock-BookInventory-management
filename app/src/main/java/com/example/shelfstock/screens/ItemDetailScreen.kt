package com.example.shelfstock.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalContext
import com.example.shelfstock.data.Book // Import Book data class

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(book: Book, onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Book Details", // More generic title
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // White back arrow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF5B429A)), // Purple TopAppBar
                actions = {
                    // Share button added here
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain" // Set the type of data you're sharing
                            val bookDetails = """
                                Book Name: ${book.name}
                                Category: ${book.category}
                                Quantity: ${book.quantity}
                                Price: $${book.price}
                                Language: ${book.language}
                                Author: ${book.author}
                                Description: ${book.description}
                            """.trimIndent() // Create string of book details
                            putExtra(Intent.EXTRA_TEXT, bookDetails) // Put the text in the intent
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Book Details")) // Start share activity
                    }) {
                        Icon(
                            Icons.Filled.Share, // Use share icon
                            contentDescription = "Share",
                            tint = Color.White // White share icon
                        )
                    }
                }
            )
        },
        content = { padding -> // Use 'content' lambda and 'padding' parameter
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(), // Make the column fill the entire screen
                verticalArrangement = Arrangement.Center, // Center content vertically
                horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(12.dp), // Rounded corners for the card
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp), // Space between items in the card
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Book Name
                        Text(
                            text = book.name,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFF5B429A) // Consistent color
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Divider(color = Color.LightGray, thickness = 1.dp)

                        // Category
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Category:",
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.width(100.dp) // Fixed width for label
                            )
                            Text(
                                text = book.category,
                                style = TextStyle(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Quantity
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Quantity:",
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = book.quantity.toString(),
                                style = TextStyle(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Price
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Price:",
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = "$${book.price}",
                                style = TextStyle(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Language
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Language:",
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = book.language,
                                style = TextStyle(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Author
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Author:",
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = book.author,
                                style = TextStyle(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Description
                        Column {
                            Text(
                                text = "Description:",
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = book.description,
                                style = TextStyle(fontSize = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    )
}