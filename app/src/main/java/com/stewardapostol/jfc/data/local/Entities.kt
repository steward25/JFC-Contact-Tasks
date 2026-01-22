package com.stewardapostol.jfc.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey



@Entity(tableName = "businesses")
data class Business(
    @PrimaryKey(autoGenerate = true) val businessId: Long = 0,
    val name: String,
    val email: String? = null
)

@Entity(
    tableName = "people",
    foreignKeys = [
        ForeignKey(
            entity = Business::class,
            parentColumns = ["businessId"],
            childColumns = ["businessId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("businessId")] // Indexing foreign keys improves database performance
)
data class Person(
    @PrimaryKey(autoGenerate = true) val personId: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val businessId: Long? = null
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val title: String,
    val description: String,
    val status: String = "Open",
    val relatedBusinessId: Long? = null,
    val relatedPersonId: Long? = null
)

data class TaskWithNames(
    @Embedded val task: Task,
    val businessName: String?,
    val personName: String?
)

@Entity(
    tableName = "categories",
    indices = [Index(value = ["categoryName"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val categoryName: String
)
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true) val tagId: Long = 0,
    val tagName: String,
    val color: Int = 0xFF7F7F7F.toInt()
)

@Entity(
    tableName = "business_category_join",
    primaryKeys = ["businessId", "categoryId"],
    foreignKeys = [
        ForeignKey(entity = Business::class, parentColumns = ["businessId"], childColumns = ["businessId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Category::class, parentColumns = ["categoryId"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("categoryId")]
)
data class BusinessCategoryCrossRef(
    val businessId: Long,
    val categoryId: Long
)

@Entity(
    tableName = "business_tag_join",
    primaryKeys = ["businessId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Business::class,
            parentColumns = ["businessId"],
            childColumns = ["businessId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Tag::class, parentColumns = ["tagId"], childColumns = ["tagId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class BusinessTagCrossRef(
    val businessId: Long,
    val tagId: Long
)

@Entity(
    tableName = "person_tag_join",
    primaryKeys = ["personId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = Person::class, parentColumns = ["personId"], childColumns = ["personId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Tag::class, parentColumns = ["tagId"], childColumns = ["tagId"], onDelete = ForeignKey.CASCADE)
    ]
)

data class PersonTagCrossRef(
    val personId: Long,
    val tagId: Long
)
