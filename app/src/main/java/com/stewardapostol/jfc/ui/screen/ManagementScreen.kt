package com.stewardapostol.jfc.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.stewardapostol.jfc.data.local.Category
import com.stewardapostol.jfc.data.local.Tag
import com.stewardapostol.jfc.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ManagementScreen(viewModel: MainViewModel) {

    // 1. Context and Scopes
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 2. Data from ViewModel
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val tags by viewModel.allTags.collectAsState(initial = emptyList())

    // 3. UI States for Dialogs
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var editingTag by remember { mutableStateOf<Tag?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingTag by remember { mutableStateOf<Tag?>(null) }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Management Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Categories Section ---
        ManagementSectionWithData(
            title = "Business Categories",
            items = categories,
            labelProvider = { it.categoryName },
            onAddClick = { showCategoryDialog = true },
            onItemClick = { editingCategory = it },
            onDeleteClick = { deletingCategory = it }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // --- Tags Section ---
        ManagementSectionWithData(
            title = "Tags",
            items = tags,
            labelProvider = { it.tagName },
            onAddClick = { showTagDialog = true },
            onItemClick = { editingTag = it },
            onDeleteClick = { deletingTag = it }
        )
    }
    }

    // --- ADD DIALOGS ---
    if (showCategoryDialog) {
        AddItemDialog("Add Category", { showCategoryDialog = false }) { name ->
            viewModel.onAddCategory(
                name = name,
                onDuplicate = {
                    Toast.makeText(context, "Category '$name' already exists!", Toast.LENGTH_LONG).show()
                },
                onSuccess = {
                    showCategoryDialog = false
                    Toast.makeText(context, "Category added", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    if (showTagDialog) {
        AddItemDialog(
            title = "Add Tag",
            onDismiss = { showTagDialog = false }
        ) { name ->
            viewModel.onAddTag(name)
            showTagDialog = false
            Toast.makeText(context, "Tag '$name' added", Toast.LENGTH_SHORT).show()
        }
    }

    // --- EDIT/UPDATE DIALOGS ---
    editingCategory?.let { category ->
        AddItemDialog(
            title = "Update Category",
            initialText = category.categoryName,
            onDismiss = { editingCategory = null },
            onConfirm = { newName ->
                viewModel.onUpdateCategory(category.copy(categoryName = newName))
                editingCategory = null
                Toast.makeText(context, "Updated to $newName", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // --- UPDATE TAG DIALOG ---
    editingTag?.let { tag ->
        AddItemDialog(
            title = "Update Tag",
            initialText = tag.tagName,
            onDismiss = { editingTag = null },
            onConfirm = { newName ->
                viewModel.onUpdateTag(tag.copy(tagName = newName))
                editingTag = null // Closes the dialog
                Toast.makeText(context, "Tag updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // --- DELETE CONFIRMATION DIALOGS ---
    deletingCategory?.let { category ->
        DeleteConfirmDialog(
            name = category.categoryName,
            onDismiss = { deletingCategory = null }
        ) {
            val nameToRestore = category.categoryName // Capture the name
            viewModel.onDeleteCategory(category)
            deletingCategory = null // Close dialog immediately

            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Deleted $nameToRestore",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onUndoDeleteCategory(nameToRestore)
                }
            }
        }
    }

    // --- DELETE TAG WITH UNDO ---
    deletingTag?.let { tag ->
        DeleteConfirmDialog(
            name = tag.tagName,
            onDismiss = { deletingTag = null }
        ) {
            val nameToRestore = tag.tagName
            viewModel.onDeleteTag(tag)
            deletingTag = null

            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Deleted $nameToRestore",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onUndoDeleteTag(nameToRestore)
                }
            }
        }
    }
}