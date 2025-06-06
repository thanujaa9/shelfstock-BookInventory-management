package com.example.shelfstock.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shelfstock.data.Book
import com.example.shelfstock.viewmodel.BookViewModel
import androidx.compose.ui.res.stringResource
import com.example.shelfstock.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment

// Partial update to AddEditBookScreen.kt - only showing relevant changes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBookScreen(
    onSave: (Book) -> Unit,
    onCancel: () -> Unit,
    initialBook: Book? = null,
    viewModel: BookViewModel = viewModel()
) {
    // Use rememberSaveable to survive configuration changes
    var name by rememberSaveable { mutableStateOf(initialBook?.name ?: "") }
    var category by rememberSaveable { mutableStateOf(initialBook?.category ?: "") }
    var quantity by rememberSaveable { mutableStateOf(initialBook?.quantity?.toString() ?: "") }
    var price by rememberSaveable { mutableStateOf(initialBook?.price?.toString() ?: "") }
    var language by rememberSaveable { mutableStateOf(initialBook?.language ?: "") }
    var author by rememberSaveable { mutableStateOf(initialBook?.author ?: "") }
    var description by rememberSaveable { mutableStateOf(initialBook?.description ?: "") }

    // State for validation errors
    var nameError by rememberSaveable { mutableStateOf("") }
    var categoryError by rememberSaveable { mutableStateOf("") }
    var quantityError by rememberSaveable { mutableStateOf("") }
    var priceError by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // Collect loading state
    val context = LocalContext.current

    // Observe selectedBook from ViewModel
    val selectedBook by viewModel.selectedBook.collectAsState()



    // Update the form fields when selectedBook changes (for editing)
    LaunchedEffect(selectedBook) {
        selectedBook?.let { book ->
            name = book.name
            category = book.category
            quantity = book.quantity.toString()
            price = book.price.toString()
            language = book.language
            author = book.author
            description = book.description
        }
    }

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

    val onSaveClick = {
        val isNameValid = name.isNotBlank()
        nameError = if (isNameValid) "" else context.getString(R.string.name_cannot_be_empty)

        val isCategoryValid = category.isNotBlank()
        categoryError = if (isCategoryValid) "" else context.getString(R.string.category_cannot_be_empty)

        val isQuantityValid =
            quantity.isNotBlank() && quantity.toIntOrNull() != null && quantity.toInt() >= 0
        quantityError = when {
            quantity.isBlank() -> context.getString(R.string.quantity_cannot_be_empty)
            quantity.toIntOrNull() == null -> context.getString(R.string.invalid_quantity)
            quantity.toInt() < 0 -> context.getString(R.string.quantity_cannot_be_negative)
            else -> ""
        }

        val isPriceValid =
            price.isNotBlank() && price.toDoubleOrNull() != null && price.toDouble() >= 0
        priceError = when {
            price.isBlank() -> context.getString(R.string.price_cannot_be_empty)
            price.toDoubleOrNull() == null -> context.getString(R.string.invalid_price)
            price.toDouble() < 0 -> context.getString(R.string.price_cannot_be_negative)
            else -> ""
        }

        if (isNameValid && isCategoryValid && isQuantityValid && isPriceValid) {
            val newBook = Book(
                id = initialBook?.id ?: 0,
                name = name,
                category = category,
                quantity = quantity.toIntOrNull() ?: 0,
                price = price.toDoubleOrNull() ?: 0.0,
                language = language.ifBlank { "Not Provided" },
                author = author.ifBlank { "Not Provided" },
                description = description.ifBlank { "Not Provided" }
            )
            onSave(newBook)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text(if (initialBook == null) stringResource(R.string.add_book) else stringResource(R.string.edit_book)) })
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onCancel, enabled = !isLoading) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSaveClick, enabled = !isLoading) {
                    // Show a loading indicator in the button if loading
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF5B429A))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    isError = nameError.isNotEmpty(),
                    supportingText = {
                        if (nameError.isNotEmpty()) {
                            Text(nameError)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(stringResource(R.string.category)) },
                    isError = categoryError.isNotEmpty(),
                    supportingText = {
                        if (categoryError.isNotEmpty()) {
                            Text(categoryError)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(stringResource(R.string.quantity)) },
                    isError = quantityError.isNotEmpty(),
                    supportingText = {
                        if (quantityError.isNotEmpty()) {
                            Text(quantityError)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.price)) },
                    isError = priceError.isNotEmpty(),
                    supportingText = {
                        if (priceError.isNotEmpty()) {
                            Text(priceError)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("Language") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }}