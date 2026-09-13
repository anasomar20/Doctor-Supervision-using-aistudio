package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Project
import com.example.data.model.Student
import com.example.data.model.Supervisor
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE supervisorId = :supervisorId ORDER BY createdAt DESC")
    fun getProjectsForSupervisor(supervisorId: String): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun getProjectById(projectId: Long): Flow<Project?>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectByIdDirect(projectId: Long): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE projectId = :projectId ORDER BY isLeader DESC, fullName ASC")
    fun getStudentsForProject(projectId: Long): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE projectId IN (SELECT id FROM projects WHERE supervisorId = :supervisorId) ORDER BY fullName ASC")
    fun getStudentsForSupervisor(supervisorId: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :studentId LIMIT 1")
    suspend fun getStudentById(studentId: Long): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudentById(studentId: Long)

    @Query("DELETE FROM students WHERE projectId = :projectId")
    suspend fun deleteStudentsByProject(projectId: Long)

    @Query("UPDATE students SET isLeader = 0 WHERE projectId = :projectId")
    suspend fun clearLeadersForProject(projectId: Long)
}

@Dao
interface SupervisorDao {
    @Query("SELECT * FROM supervisors WHERE email = :email LIMIT 1")
    suspend fun getSupervisorByEmail(email: String): Supervisor?

    @Query("SELECT * FROM supervisors WHERE id = :id LIMIT 1")
    suspend fun getSupervisorById(id: String): Supervisor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupervisor(supervisor: Supervisor)

    @Update
    suspend fun updateSupervisor(supervisor: Supervisor)
}
