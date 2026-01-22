package com.stewardapostol.jfc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stewardapostol.jfc.data.local.Business
import com.stewardapostol.jfc.data.local.BusinessWithDetails
import com.stewardapostol.jfc.data.local.Category
import com.stewardapostol.jfc.data.local.Person
import com.stewardapostol.jfc.data.local.PersonWithDetails
import com.stewardapostol.jfc.data.local.Tag
import com.stewardapostol.jfc.data.local.Task
import com.stewardapostol.jfc.data.local.TaskWithNames
import com.stewardapostol.jfc.data.repository.AppRepository
import com.stewardapostol.jfc.ui.utils.ColorGenerator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class  MainViewModel(private val repository: AppRepository) : ViewModel() {


    // Observe the detailed list for the UI
    val peopleList: StateFlow<List<PersonWithDetails>> = repository.allPeopleWithDetails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Streams for the Open and Completed Task lists
    val openTasks: StateFlow<List<TaskWithNames>> = repository.getOpenTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTasksByBusiness(businessId: Long) = repository.getTasksByBusiness(businessId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UPDATED: Save task with Title and Description
    fun onSaveTask(title: String, description: String, businessId: Long?, personId: Long?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description, // Ensure your Task data class has this field
                status = "Open",
                relatedBusinessId = businessId,
                relatedPersonId = personId
            )
            repository.saveTask(task)
        }
    }

    val completedTasks: StateFlow<List<TaskWithNames>> = repository.getCompletedTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onToggleTaskStatusWithNames(task: TaskWithNames) {
        viewModelScope.launch {
            // Checks current status and flips it
            val isOpen = task.task.status == "Open"
            repository.toggleTaskStatus(task.task.taskId, isOpen)
        }
    }

    fun onDeleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }



    // --- 2. BUSINESS STATE & ACTIONS ---
    val allBusinesses: StateFlow<List<Business>> = repository.allBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBusinessesWithDetails: StateFlow<List<BusinessWithDetails>> =
        repository.allBusinessesWithDetails
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Updated to accept the business object to preserve the ID during Edits
    fun onSaveBusiness(business: Business, categories: List<Category>, tags: List<Tag>) {
        viewModelScope.launch {
            val categoryIds = categories.map { it.categoryId }
            val tagIds = tags.map { it.tagId }

            // This call handles both Update (if ID > 0) and Insert (if ID is 0)
            repository.insertBusinessWithRelations(business, categoryIds, tagIds)
        }
    }

    fun onDeleteBusiness(business: Business) {
        viewModelScope.launch {
            repository.deleteBusiness(business)
        }
    }

    // --- 3. PERSON STATE & ACTIONS ---

    val allPeopleWithDetails: StateFlow<List<PersonWithDetails>> =
        repository.allPeopleWithDetails
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val allPeople: StateFlow<List<Person>> = repository.allPeople
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTags: StateFlow<List<Tag>> = repository.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // MainViewModel.kt
    val allTasks: StateFlow<List<TaskWithNames>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onToggleTaskStatus(task: Task) {
        viewModelScope.launch {
            val newStatus = if (task.status == "Open") "Completed" else "Open"
            repository.updateTask(task.copy(status = newStatus))
        }
    }
    fun onSavePerson(person: Person, tags: List<Tag>) {
        viewModelScope.launch {
            val tagIds = tags.map { it.tagId }
            repository.savePersonWithTags(person, tagIds)
        }
    }
    fun onDeletePerson(personId: Long) {
        viewModelScope.launch {
            repository.onDeletePerson(personId)
        }
    }
    // --- 4. MANAGEMENT (SETTINGS) ACTIONS ---
    fun onAddCategory(name: String, onDuplicate: () -> Unit, onSuccess: () -> Unit) {
        // Case-insensitive check
        val exists = allCategories.value.any { it.categoryName.equals(name, ignoreCase = true) }

        if (exists) {
            onDuplicate()
        } else {
            viewModelScope.launch {
                repository.insertCategory(Category(categoryName = name.trim()))
                onSuccess()
            }
        }
    }

    fun onAddTag(name: String) {
        viewModelScope.launch {
            val newTag = Tag(
                tagName = name,
                color = ColorGenerator.getRandomColor()
            )
            repository.insertTag(newTag)
        }
    }

    fun onUpdateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun onUpdateTag(tag: Tag) {
        viewModelScope.launch {
            repository.updateTag(tag)
        }
    }

    fun onDeleteCategory(category: Category) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }

    fun onDeleteTag(tag: Tag) {
        viewModelScope.launch { repository.deleteTag(tag) }
    }

    fun onUndoDeleteCategory(categoryName: String) {
        viewModelScope.launch {
            repository.insertCategory(Category(categoryName = categoryName))
        }
    }

    fun onUndoDeleteTag(tagName: String) {
        onAddTag(tagName)
    }
}