package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.local.StudentDao
import com.example.data.local.SupervisorDao
import com.example.data.model.Project
import com.example.data.model.Student
import com.example.data.model.Supervisor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class DoctorSupervisionRepository(
    private val projectDao: ProjectDao,
    private val studentDao: StudentDao,
    private val supervisorDao: SupervisorDao
) {
    fun getProjects(supervisorId: String): Flow<List<Project>> {
        return projectDao.getProjectsForSupervisor(supervisorId)
    }

    fun getProject(projectId: Long): Flow<Project?> {
        return projectDao.getProjectById(projectId)
    }

    suspend fun getProjectDirect(projectId: Long): Project? {
        return withContext(Dispatchers.IO) {
            projectDao.getProjectByIdDirect(projectId)
        }
    }

    fun getStudentsForProject(projectId: Long): Flow<List<Student>> {
        return studentDao.getStudentsForProject(projectId)
    }

    fun getStudentsForSupervisor(supervisorId: String): Flow<List<Student>> {
        return studentDao.getStudentsForSupervisor(supervisorId)
    }

    suspend fun saveProject(project: Project): Long {
        return withContext(Dispatchers.IO) {
            if (project.id == 0L) {
                projectDao.insertProject(project)
            } else {
                projectDao.updateProject(project)
                project.id
            }
        }
    }

    suspend fun deleteProject(projectId: Long) {
        withContext(Dispatchers.IO) {
            studentDao.deleteStudentsByProject(projectId)
            projectDao.deleteProjectById(projectId)
        }
    }

    suspend fun saveStudent(student: Student): Long {
        return withContext(Dispatchers.IO) {
            if (student.isLeader) {
                // Ensure only one leader per project
                studentDao.clearLeadersForProject(student.projectId)
            }
            if (student.id == 0L) {
                studentDao.insertStudent(student)
            } else {
                studentDao.updateStudent(student)
                student.id
            }
        }
    }

    suspend fun deleteStudent(studentId: Long) {
        withContext(Dispatchers.IO) {
            studentDao.deleteStudentById(studentId)
        }
    }

    suspend fun login(email: String, password: String): Result<Supervisor> {
        return withContext(Dispatchers.IO) {
            val supervisor = supervisorDao.getSupervisorByEmail(email.trim().lowercase())
            if (supervisor == null) {
                Result.failure(Exception("لم يتم العثور على حساب بهذا البريد الإلكتروني"))
            } else if (supervisor.password != password) {
                Result.failure(Exception("كلمة المرور غير صحيحة"))
            } else {
                Result.success(supervisor)
            }
        }
    }

    suspend fun register(fullName: String, email: String, password: String): Result<Supervisor> {
        return withContext(Dispatchers.IO) {
            val normalizedEmail = email.trim().lowercase()
            val existing = supervisorDao.getSupervisorByEmail(normalizedEmail)
            if (existing != null) {
                Result.failure(Exception("البريد الإلكتروني مسجل مسبقاً"))
            } else {
                val newSupervisor = Supervisor(
                    id = UUID.randomUUID().toString(),
                    fullName = fullName.trim(),
                    email = normalizedEmail,
                    password = password,
                    academicTitle = "مشرف مشاريع تخرج"
                )
                supervisorDao.insertSupervisor(newSupervisor)
                Result.success(newSupervisor)
            }
        }
    }

    suspend fun updatePassword(supervisorId: String, currentPass: String, newPass: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val supervisor = supervisorDao.getSupervisorById(supervisorId)
            if (supervisor == null) {
                Result.failure(Exception("المشرف غير موجود"))
            } else if (supervisor.password != currentPass) {
                Result.failure(Exception("كلمة المرور الحالية غير صحيحة"))
            } else {
                supervisorDao.updateSupervisor(supervisor.copy(password = newPass))
                Result.success(Unit)
            }
        }
    }

    suspend fun seedInitialDataIfNeeded() {
        withContext(Dispatchers.IO) {
            val defaultEmail = "dr.khaled@university.edu.sa"
            val existing = supervisorDao.getSupervisorByEmail(defaultEmail)
            val supervisorId = if (existing == null) {
                val demoDoctor = Supervisor(
                    id = "supervisor_demo_101",
                    fullName = "د. خالد العتيبي",
                    email = defaultEmail,
                    password = "password123",
                    academicTitle = "أستاذ مشارك - قسم علوم الحاسب"
                )
                supervisorDao.insertSupervisor(demoDoctor)
                demoDoctor.id
            } else {
                existing.id
            }

            // Check if projects exist
            val projects = projectDao.getProjectByIdDirect(1L)
            if (projects == null) {
                val proj1Id = projectDao.insertProject(
                    Project(
                        id = 0,
                        title = "نظام إدارة العيادات الذكي باستخدام IoT",
                        description = "منصة سحابية متكاملة لربط الأجهزة الطبية ومتابعة المواعيد وسجلات المرضى في الوقت الفعلي مع لوحة تحكم ذكية للأطباء.",
                        supervisorId = supervisorId,
                        status = "in_progress",
                        notes = "تم اعتماد مخطط المعمارية، موعد التسليم الأولي نهاية الشهر القادم.",
                        githubLink = "https://github.com/example/smart-clinic-iot",
                        proposalLink = "https://example.com/clinic-proposal.pdf",
                        srsLink = "https://example.com/clinic-srs.pdf",
                        createdAt = System.currentTimeMillis() - (15L * 24 * 3600 * 1000)
                    )
                )

                studentDao.insertStudent(
                    Student(
                        fullName = "أحمد عبد الله الزهراني",
                        universityId = "441203492",
                        phone = "966501234567",
                        projectId = proj1Id,
                        isLeader = true
                    )
                )
                studentDao.insertStudent(
                    Student(
                        fullName = "محمد عبد الرحمن الشهري",
                        universityId = "441208821",
                        phone = "966551234567",
                        projectId = proj1Id,
                        isLeader = false
                    )
                )
                studentDao.insertStudent(
                    Student(
                        fullName = "فيصل سعود الغامدي",
                        universityId = "441204519",
                        phone = "966541234567",
                        projectId = proj1Id,
                        isLeader = false
                    )
                )

                val proj2Id = projectDao.insertProject(
                    Project(
                        id = 0,
                        title = "منصة التخرج التعاونية للجامعات",
                        description = "نظام لإدارة ومتابعة مشاريع التخرج ورفع التقارير الأسبوعية وتوثيق الاجتماعات الإشرافية بين الأساتذة والطلاب.",
                        supervisorId = supervisorId,
                        status = "completed",
                        notes = "تمت المناقشة النهائية بنجاح وحصل المشروع على تقدير ممتاز.",
                        githubLink = "https://github.com/example/grad-platform",
                        proposalLink = "https://example.com/grad-proposal.pdf",
                        srsLink = "https://example.com/grad-srs.pdf",
                        createdAt = System.currentTimeMillis() - (45L * 24 * 3600 * 1000)
                    )
                )

                studentDao.insertStudent(
                    Student(
                        fullName = "عبد العزيز ناصر القحطاني",
                        universityId = "440109283",
                        phone = "966509876543",
                        projectId = proj2Id,
                        isLeader = true
                    )
                )
                studentDao.insertStudent(
                    Student(
                        fullName = "ريان فهد المطيري",
                        universityId = "440105432",
                        phone = "966559876543",
                        projectId = proj2Id,
                        isLeader = false
                    )
                )

                val proj3Id = projectDao.insertProject(
                    Project(
                        id = 0,
                        title = "تطبيق كشف التزييف العميق بالفيديو (Deepfake)",
                        description = "تطبيق ذكاء اصطناعي يعتمد على شبكات الـ CNN المتطورة لتحليل مقاطع الفيديو واكتشاف التلاعب بالوجوه بنسب دقة عالية.",
                        supervisorId = supervisorId,
                        status = "pending",
                        notes = "المشروع في مرحلة جمع وتجهيز مجموعات البيانات المرجعية.",
                        githubLink = "https://github.com/example/deepfake-detector",
                        proposalLink = "https://example.com/deepfake-proposal.pdf",
                        srsLink = "",
                        createdAt = System.currentTimeMillis() - (5L * 24 * 3600 * 1000)
                    )
                )

                studentDao.insertStudent(
                    Student(
                        fullName = "تركي سلطان الحربي",
                        universityId = "442301982",
                        phone = "966531122334",
                        projectId = proj3Id,
                        isLeader = true
                    )
                )
            }
        }
    }
}
