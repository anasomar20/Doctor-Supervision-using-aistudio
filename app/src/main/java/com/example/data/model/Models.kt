package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val supervisorId: String,
    val status: String, // "pending", "in_progress", "completed"
    val notes: String = "",
    val githubLink: String = "",
    val proposalLink: String = "",
    val srsLink: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val universityId: String,
    val phone: String = "",
    val projectId: Long,
    val isLeader: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "supervisors")
data class Supervisor(
    @PrimaryKey
    val id: String, // UID
    val fullName: String,
    val email: String,
    val password: String,
    val academicTitle: String = "أستاذ مشارك"
)

enum class ProjectStatus(val key: String, val titleAr: String) {
    PENDING("pending", "لم يبدأ"),
    IN_PROGRESS("in_progress", "قيد التنفيذ"),
    COMPLETED("completed", "مكتمل");

    companion object {
        fun fromKey(key: String): ProjectStatus {
            return entries.find { it.key == key } ?: PENDING
        }
    }
}
