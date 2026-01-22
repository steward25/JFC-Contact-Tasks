package com.stewardapostol.jfc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- BUSINESS OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: Business): Long

    @Query("SELECT * FROM businesses ORDER BY name ASC")
    fun getAllBusinesses(): Flow<List<Business>>

    @Query("SELECT * FROM businesses WHERE businessId = :id")
    fun getBusinessById(id: Long): Flow<Business>

    @Delete
    suspend fun deleteBusiness(business: Business)

    // --- PERSON OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person): Long

    @Query("SELECT * FROM people")
    fun getAllPeople(): Flow<List<Person>>

    // --- TASK OPERATIONS ---
    // General Task List (Filtered by Status for Tabs)
    @Transaction
    @Query("""
        SELECT tasks.*, 
               businesses.name AS businessName, 
               (people.firstName || ' ' || people.lastName) AS personName 
        FROM tasks 
        LEFT JOIN businesses ON tasks.relatedBusinessId = businesses.businessId 
        LEFT JOIN people ON tasks.relatedPersonId = people.personId 
        WHERE tasks.status = :status
    """)
    fun getTasksWithNames(status: String): Flow<List<TaskWithNames>>

    // UPDATED: Specific Business Task List (For the Details screen in your screenshot)
    @Transaction
    @Query("""
        SELECT tasks.*, 
               businesses.name AS businessName, 
               (people.firstName || ' ' || people.lastName) AS personName 
        FROM tasks 
        LEFT JOIN businesses ON tasks.relatedBusinessId = businesses.businessId 
        LEFT JOIN people ON tasks.relatedPersonId = people.personId 
        WHERE tasks.relatedBusinessId = :businessId
    """)
    fun getTasksByBusiness(businessId: Long): Flow<List<TaskWithNames>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("UPDATE tasks SET status = :status WHERE taskId = :id")
    suspend fun updateTaskStatus(id: Long, status: String)

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    // --- CATEGORY & TAG OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag)

    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<Tag>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    // --- RELATIONSHIP (CROSS-REF) OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinessCategoryCrossRef(crossRef: BusinessCategoryCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonTagCrossRef(crossRef: PersonTagCrossRef)

    // --- TRANSACTIONAL HELPERS ---
    @Transaction
    suspend fun insertBusinessWithCategories(business: Business, categoryIds: List<Long>) {
        val businessId = insertBusiness(business)
        categoryIds.forEach { catId ->
            insertBusinessCategoryCrossRef(BusinessCategoryCrossRef(businessId, catId))
        }
    }

    @Transaction
    suspend fun insertPersonWithTags(person: Person, tagIds: List<Long>) {
        val personId = insertPerson(person)
        tagIds.forEach { tagId ->
            insertPersonTagCrossRef(PersonTagCrossRef(personId, tagId))
        }
    }

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("DELETE FROM people WHERE personId = :personId")
    suspend fun deletePersonById(personId: Long)


}