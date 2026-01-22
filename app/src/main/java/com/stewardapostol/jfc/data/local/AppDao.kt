package com.stewardapostol.jfc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- BUSINESS OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: Business): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinessTagCrossRef(crossRef: BusinessTagCrossRef)
    @Transaction
    suspend fun insertBusinessWithRelations(business: Business, catIds: List<Long>, tagIds: List<Long>) {
        // 1. Insert/Update the business (returns the ID if it's new, or uses existing)
        val bId = insertBusiness(business)

        // 2. Clear old links to handle "un-selecting" chips during Edit
        deleteBusinessCategoryCrossRefs(bId)
        deleteBusinessTagCrossRefs(bId)

        // 3. Insert new links
        catIds.forEach { cId ->
            insertBusinessCategoryCrossRef(BusinessCategoryCrossRef(bId, cId))
        }
        tagIds.forEach { tId ->
            insertBusinessTagCrossRef(BusinessTagCrossRef(bId, tId))
        }
    }

    @Query("DELETE FROM business_category_join WHERE businessId = :businessId")
    suspend fun deleteBusinessCategoryCrossRefs(businessId: Long)

    @Query("DELETE FROM business_tag_join WHERE businessId = :businessId")
    suspend fun deleteBusinessTagCrossRefs(businessId: Long)
    @Query("SELECT * FROM businesses ORDER BY name ASC")
    fun getAllBusinesses(): Flow<List<Business>>

    @Transaction
    @Query("SELECT * FROM businesses ORDER BY businessId DESC")
    fun getAllBusinessesWithDetails(): Flow<List<BusinessWithDetails>>

    @Query("SELECT * FROM businesses WHERE businessId = :id")
    fun getBusinessById(id: Long): Flow<Business>

    @Delete
    suspend fun deleteBusiness(business: Business)

    // --- PERSON OPERATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person): Long

    // Added: Get detailed person info (including Business Name and Tags)
    @Transaction
    @Query("SELECT * FROM people ORDER BY personId DESC")
    fun getAllPeopleWithDetails(): Flow<List<PersonWithDetails>>

    @Query("SELECT * FROM people")
    fun getAllPeople(): Flow<List<Person>>

    @Delete
    suspend fun deletePerson(person: Person)

    @Query("DELETE FROM people WHERE personId = :personId")
    suspend fun deletePersonById(personId: Long)

    // --- TASK OPERATIONS ---
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
    @Query("""
        SELECT tasks.*, 
               businesses.name AS businessName, 
               (people.firstName || ' ' || people.lastName) AS personName 
        FROM tasks 
        LEFT JOIN businesses ON tasks.relatedBusinessId = businesses.businessId
        LEFT JOIN people ON tasks.relatedPersonId = people.personId
        ORDER BY tasks.taskId DESC
    """)
    fun getAllTasksWithNames(): Flow<List<TaskWithNames>>
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
    @Update
    suspend fun updateTask(task: Task)
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

    @Transaction
    suspend fun insertPersonWithTags(person: Person, tagIds: List<Long>) {
        // 1. Insert or Update the person.
        // If personId is 0, it creates; if it matches an existing ID, it updates.
        val pId = insertPerson(person)

        // 2. Clear previous Tag associations to handle "un-checking" tags in the UI
        deletePersonTagCrossRefs(pId)

        // 3. Re-insert the currently selected tags
        tagIds.forEach { tId ->
            insertPersonTagCrossRef(PersonTagCrossRef(personId = pId, tagId = tId))
        }
    }

    @Query("DELETE FROM person_tag_join WHERE personId = :personId")
    suspend fun deletePersonTagCrossRefs(personId: Long)

    // --- TRANSACTIONAL HELPERS ---
    @Transaction
    suspend fun insertBusinessWithCategories(business: Business, categoryIds: List<Long>) {
        val businessId = insertBusiness(business)
        categoryIds.forEach { catId ->
            insertBusinessCategoryCrossRef(BusinessCategoryCrossRef(businessId, catId))
        }
    }

}
data class PersonWithDetails(
    @Embedded val person: Person,

    // Fetches the single business linked by businessId
    @Relation(
        parentColumn = "businessId",
        entityColumn = "businessId"
    )
    val business: Business?,

    // Fetches all tags via the CrossRef join table
    @Relation(
        parentColumn = "personId",
        entityColumn = "tagId",
        associateBy = Junction(PersonTagCrossRef::class)
    )
    val tags: List<Tag>
)

data class BusinessWithDetails(
    @Embedded val business: Business,

    @Relation(
        parentColumn = "businessId",
        entityColumn = "categoryId",
        associateBy = Junction(BusinessCategoryCrossRef::class)
    )
    val categories: List<Category>,

    @Relation(
        parentColumn = "businessId",
        entityColumn = "tagId",
        associateBy = Junction(BusinessTagCrossRef::class)
    )
    val tags: List<Tag>
)
