package com.stewardapostol.jfc.data.repository

import com.stewardapostol.jfc.data.local.AppDao
import com.stewardapostol.jfc.data.local.Business
import com.stewardapostol.jfc.data.local.BusinessWithDetails
import com.stewardapostol.jfc.data.local.Category
import com.stewardapostol.jfc.data.local.Person
import com.stewardapostol.jfc.data.local.PersonWithDetails
import com.stewardapostol.jfc.data.local.Tag
import com.stewardapostol.jfc.data.local.Task
import com.stewardapostol.jfc.data.local.TaskWithNames
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    // --- Task Streams ---

    val allTasks: Flow<List<TaskWithNames>> = appDao.getAllTasksWithNames()
    fun getOpenTasks(): Flow<List<TaskWithNames>> = appDao.getTasksWithNames("Open")
    fun getCompletedTasks(): Flow<List<TaskWithNames>> = appDao.getTasksWithNames("Completed")
    fun getTasksByBusiness(businessId: Long): Flow<List<TaskWithNames>> =
        appDao.getTasksByBusiness(businessId)
    suspend fun saveTask(task: Task) = appDao.insertTask(task)
    suspend fun updateTask(task: Task) { appDao.updateTask(task) }
    suspend fun toggleTaskStatus(taskId: Long, isCurrentlyOpen: Boolean) {
        val newStatus = if (isCurrentlyOpen) "Completed" else "Open"
        appDao.updateTaskStatus(taskId, newStatus)
    }

    suspend fun deleteTask(taskId: Long) = appDao.deleteTaskById(taskId)

    // --- Business Operations ---
    val allBusinesses: Flow<List<Business>> = appDao.getAllBusinesses()

    // In AppRepository.kt
    val allBusinessesWithDetails: Flow<List<BusinessWithDetails>> =
        appDao.getAllBusinessesWithDetails()

    suspend fun insertBusinessWithRelations(business: Business, catIds: List<Long>, tagIds: List<Long>) {
        appDao.insertBusinessWithRelations(business, catIds, tagIds)
    }

    suspend fun deleteBusiness(business: Business) = appDao.deleteBusiness(business)

    // --- Person Operations ---

    val allPeople: Flow<List<Person>> = appDao.getAllPeople()

    suspend fun savePerson(person: Person, selectedTagIds: List<Long>) {
        appDao.insertPersonWithTags(person, selectedTagIds)
    }

    suspend fun onDeletePerson(personId: Long) { appDao.deletePersonById(personId) }
    // --- Management Operations ---
    val allCategories: Flow<List<Category>> = appDao.getAllCategories()
    val allTags: Flow<List<Tag>> = appDao.getAllTags()
    suspend fun insertCategory(category: Category) { appDao.insertCategory(category)}
    suspend fun addTag(name: String) = appDao.insertTag(Tag(tagName = name))
    suspend fun insertTag(tag: Tag) { appDao.insertTag(tag)}
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

    val allPeopleWithDetails: Flow<List<PersonWithDetails>> = appDao.getAllPeopleWithDetails()

    suspend fun savePersonWithTags(person: Person, tagIds: List<Long>) {
        appDao.insertPersonWithTags(person, tagIds)
    }

    suspend fun deletePerson(personId: Long) {
        appDao.deletePersonById(personId)
    }
}