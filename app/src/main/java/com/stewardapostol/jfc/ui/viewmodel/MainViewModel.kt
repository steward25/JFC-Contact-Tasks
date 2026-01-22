package com.stewardapostol.jfc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stewardapostol.jfc.data.local.Business
import com.stewardapostol.jfc.data.local.Category
import com.stewardapostol.jfc.data.local.Person
import com.stewardapostol.jfc.data.local.Tag
import com.stewardapostol.jfc.data.local.Task
import com.stewardapostol.jfc.data.local.TaskWithNames
import com.stewardapostol.jfc.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class  MainViewModel(private val repository: AppRepository) : ViewModel() {

    // --- 1. TASK STATE & ACTIONS ---
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

    fun onToggleTaskStatus(task: TaskWithNames) {
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

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSaveBusiness(name: String, email: String?, categoryIds: List<Long>) {
        viewModelScope.launch {
            val business = Business(name = name, email = email)
            repository.saveBusiness(business, categoryIds)
        }
    }

    fun onDeleteBusiness(business: Business) {
        viewModelScope.launch {
            repository.deleteBusiness(business)
        }
    }

    // --- 3. PERSON STATE & ACTIONS ---
    val allPeople: StateFlow<List<Person>> = repository.allPeople
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTags: StateFlow<List<Tag>> = repository.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSavePerson(firstName: String, lastName: String, businessId: Long?, tagIds: List<Long>) {
        viewModelScope.launch {
            val person = Person(
                firstName = firstName, 
                lastName = lastName, 
                businessId = businessId
            )
            repository.savePerson(person, tagIds)
        }
    }

    fun onDeletePerson(person: Person) {
        viewModelScope.launch {
            repository.deletePerson(person)
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
        viewModelScope.launch { repository.addTag(name) }
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