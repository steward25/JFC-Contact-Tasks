package com.stewardapostol.jfc.data.repository

import com.stewardapostol.jfc.data.local.AppDao
import com.stewardapostol.jfc.data.local.Business
import com.stewardapostol.jfc.data.local.Category
import com.stewardapostol.jfc.data.local.Person
import com.stewardapostol.jfc.data.local.Tag
import com.stewardapostol.jfc.data.local.Task
import com.stewardapostol.jfc.data.local.TaskWithNames
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    // --- Task Streams ---
    fun getOpenTasks(): Flow<List<TaskWithNames>> = appDao.getTasksWithNames("Open")
    fun getCompletedTasks(): Flow<List<TaskWithNames>> = appDao.getTasksWithNames("Completed")
    fun getTasksByBusiness(businessId: Long): Flow<List<TaskWithNames>> =
        appDao.getTasksByBusiness(businessId)
    suspend fun saveTask(task: Task) = appDao.insertTask(task)
    suspend fun toggleTaskStatus(taskId: Long, isCurrentlyOpen: Boolean) {
        val newStatus = if (isCurrentlyOpen) "Completed" else "Open"
        appDao.updateTaskStatus(taskId, newStatus)
    }

    suspend fun deleteTask(taskId: Long) = appDao.deleteTaskById(taskId)

    // --- Business Operations ---
    val allBusinesses: Flow<List<Business>> = appDao.getAllBusinesses()

    suspend fun saveBusiness(business: Business, selectedCategoryIds: List<Long>) {
        // Uses the DAO @Transaction to ensure both business and links are saved
        appDao.insertBusinessWithCategories(business, selectedCategoryIds)
    }

    suspend fun deleteBusiness(business: Business) = appDao.deleteBusiness(business)

    // --- Person Operations ---
    val allPeople: Flow<List<Person>> = appDao.getAllPeople()

    suspend fun savePerson(person: Person, selectedTagIds: List<Long>) {
        appDao.insertPersonWithTags(person, selectedTagIds)
    }

    suspend fun deletePerson(person: Person) = appDao.deletePerson(person)

    // --- Management Operations ---
    val allCategories: Flow<List<Category>> = appDao.getAllCategories()
    val allTags: Flow<List<Tag>> = appDao.getAllTags()
    suspend fun insertCategory(category: Category) { appDao.insertCategory(category)}
    suspend fun addTag(name: String) = appDao.insertTag(Tag(tagName = name))
    suspend fun updateCategory(category: Category) {
        appDao.updateCategory(category)
    }
    suspend fun updateTag(tag: Tag) {
        appDao.updateTag(tag)
    }

    // --- Delete Methods (Optional but helpful) ---
    suspend fun deleteCategory(category: Category) {
        appDao.deleteCategory(category)
    }

    suspend fun deleteTag(tag: Tag) {
        appDao.deleteTag(tag)
    }
}