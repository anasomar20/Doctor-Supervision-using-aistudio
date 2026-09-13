package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Project
import com.example.data.model.Student
import com.example.data.model.Supervisor
import com.example.data.repository.DoctorSupervisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val titleAr: String) {
    HOME("مشاريعي"),
    STUDENTS("الطلاب"),
    PROFILE("الملف الشخصي")
}

class SupervisionViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = DoctorSupervisionRepository(
        database.projectDao(),
        database.studentDao(),
        database.supervisorDao()
    )

    // Auth State
    private val _currentUser = MutableStateFlow<Supervisor?>(null)
    val currentUser: StateFlow<Supervisor?> = _currentUser.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Navigation State
    private val _selectedTab = MutableStateFlow(AppTab.HOME)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _viewingProjectId = MutableStateFlow<Long?>(null)
    val viewingProjectId: StateFlow<Long?> = _viewingProjectId.asStateFlow()

    private val _isAddEditProjectOpen = MutableStateFlow(false)
    val isAddEditProjectOpen: StateFlow<Boolean> = _isAddEditProjectOpen.asStateFlow()

    private val _editingProject = MutableStateFlow<Project?>(null)
    val editingProject: StateFlow<Project?> = _editingProject.asStateFlow()

    private val _isAddEditStudentOpen = MutableStateFlow(false)
    val isAddEditStudentOpen: StateFlow<Boolean> = _isAddEditStudentOpen.asStateFlow()

    private val _editingStudent = MutableStateFlow<Student?>(null)
    val editingStudent: StateFlow<Student?> = _editingStudent.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Search queries
    val projectSearchQuery = MutableStateFlow("")
    val studentSearchQuery = MutableStateFlow("")

    // Raw data flows
    val rawProjects: StateFlow<List<Project>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getProjects(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawStudents: StateFlow<List<Student>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getStudentsForSupervisor(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Projects
    val filteredProjects: StateFlow<List<Project>> = combine(
        rawProjects,
        projectSearchQuery
    ) { projects, query ->
        if (query.isBlank()) {
            projects
        } else {
            val q = query.trim().lowercase()
            projects.filter {
                it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Students
    val filteredStudents: StateFlow<List<Student>> = combine(
        rawStudents,
        studentSearchQuery
    ) { students, query ->
        if (query.isBlank()) {
            students
        } else {
            val q = query.trim().lowercase()
            students.filter {
                it.fullName.lowercase().contains(q) || it.universityId.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current viewed project
    val currentProject: StateFlow<Project?> = _viewingProjectId.flatMapLatest { id ->
        if (id != null) {
            repository.getProject(id)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentProjectStudents: StateFlow<List<Student>> = _viewingProjectId.flatMapLatest { id ->
        if (id != null) {
            repository.getStudentsForProject(id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            // Auto login default demo doctor for immediate ready-to-test experience
            val defaultSupervisor = repository.login("dr.khaled@university.edu.sa", "password123").getOrNull()
            _currentUser.value = defaultSupervisor
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.login(email, pass)
            _isAuthLoading.value = false
            result.onSuccess { supervisor ->
                _currentUser.value = supervisor
                _selectedTab.value = AppTab.HOME
                _isSettingsOpen.value = false
                _viewingProjectId.value = null
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message ?: "فشل تسجيل الدخول"
            }
        }
    }

    fun register(fullName: String, email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.register(fullName, email, pass)
            _isAuthLoading.value = false
            result.onSuccess { supervisor ->
                _currentUser.value = supervisor
                _selectedTab.value = AppTab.HOME
                _isSettingsOpen.value = false
                _viewingProjectId.value = null
                onSuccess()
            }.onFailure { err ->
                _authError.value = err.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _viewingProjectId.value = null
        _isSettingsOpen.value = false
        _isAddEditProjectOpen.value = false
        _isAddEditStudentOpen.value = false
        _selectedTab.value = AppTab.HOME
    }

    fun updatePassword(currentPass: String, newPass: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.updatePassword(user.id, currentPass, newPass)
            res.onSuccess {
                _currentUser.value = user.copy(password = newPass)
                onResult(true, "تم تحديث كلمة المرور بنجاح")
            }.onFailure {
                onResult(false, it.message ?: "فشل تحديث كلمة المرور")
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
        _viewingProjectId.value = null
        _isSettingsOpen.value = false
    }

    fun openProjectDetails(projectId: Long) {
        _viewingProjectId.value = projectId
    }

    fun closeProjectDetails() {
        _viewingProjectId.value = null
    }

    fun openAddProject() {
        _editingProject.value = null
        _isAddEditProjectOpen.value = true
    }

    fun openEditProject(project: Project) {
        _editingProject.value = project
        _isAddEditProjectOpen.value = true
    }

    fun closeAddEditProject() {
        _isAddEditProjectOpen.value = false
        _editingProject.value = null
    }

    fun saveProject(
        title: String,
        description: String,
        status: String,
        notes: String,
        githubLink: String,
        proposalLink: String,
        srsLink: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val existing = _editingProject.value
            val project = Project(
                id = existing?.id ?: 0L,
                title = title.trim(),
                description = description.trim(),
                supervisorId = user.id,
                status = status,
                notes = notes.trim(),
                githubLink = githubLink.trim(),
                proposalLink = proposalLink.trim(),
                srsLink = srsLink.trim(),
                createdAt = existing?.createdAt ?: System.currentTimeMillis()
            )
            repository.saveProject(project)
            closeAddEditProject()
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_viewingProjectId.value == projectId) {
                _viewingProjectId.value = null
            }
        }
    }

    fun openAddStudent(preselectedProjectId: Long? = null) {
        _editingStudent.value = preselectedProjectId?.let {
            Student(projectId = it, fullName = "", universityId = "")
        }
        _isAddEditStudentOpen.value = true
    }

    fun openEditStudent(student: Student) {
        _editingStudent.value = student
        _isAddEditStudentOpen.value = true
    }

    fun closeAddEditStudent() {
        _isAddEditStudentOpen.value = false
        _editingStudent.value = null
    }

    fun saveStudent(
        fullName: String,
        universityId: String,
        phone: String,
        projectId: Long,
        isLeader: Boolean
    ) {
        viewModelScope.launch {
            val existing = _editingStudent.value
            val student = Student(
                id = existing?.id ?: 0L,
                fullName = fullName.trim(),
                universityId = universityId.trim(),
                phone = phone.trim(),
                projectId = projectId,
                isLeader = isLeader,
                createdAt = existing?.createdAt ?: System.currentTimeMillis()
            )
            repository.saveStudent(student)
            closeAddEditStudent()
        }
    }

    fun deleteStudent(studentId: Long) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
        }
    }

    fun openSettings() {
        _isSettingsOpen.value = true
        _viewingProjectId.value = null
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }
}
