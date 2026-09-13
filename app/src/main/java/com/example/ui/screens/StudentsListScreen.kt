package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.data.model.Student
import com.example.ui.components.CustomTextField
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StudentCard
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

@Composable
fun StudentsListScreen(
    students: List<Student>,
    projects: List<Project>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddStudentClick: () -> Unit,
    onEditStudentClick: (Student) -> Unit,
    onDeleteStudent: (Long) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var studentToDeleteId by remember { mutableStateOf<Long?>(null) }

    val projectNameMap = remember(projects) {
        projects.associate { it.id to it.title }
    }

    fun openWhatsApp(phone: String) {
        try {
            val cleanPhone = phone.filter { it.isDigit() }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق واتساب", Toast.LENGTH_SHORT).show()
        }
    }

    if (studentToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { studentToDeleteId = null },
            title = { Text("تأكيد حذف الطالب") },
            text = { Text("هل أنت متأكد من حذف هذا الطالب؟ سيتم تحديث عدد طلاب المشروع تلقائياً.") },
            confirmButton = {
                Button(
                    onClick = {
                        studentToDeleteId?.let { onDeleteStudent(it) }
                        studentToDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDanger)
                ) {
                    Text("حذف الطالب")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDeleteId = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = onOpenDrawer,
                                modifier = Modifier.testTag("students_drawer_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "فتح القائمة الجانبية",
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "قائمة الطلاب",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        }

                        Surface(
                            color = BrandPrimary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${students.size} طلاب",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("ابحث بالاسم أو الرقم الجامعي...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = BrandPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "مسح البحث"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = BrandCardBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("students_search_input")
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddStudentClick,
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("students_add_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة طالب جديد",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackground)
                .padding(innerPadding)
        ) {
            if (students.isEmpty()) {
                EmptyStateView(
                    title = if (searchQuery.isNotBlank()) "لا توجد نتائج بحث" else "لا يوجد طلاب مسجلين بعد",
                    subtitle = if (searchQuery.isNotBlank()) "تحقق من صحة الاسم أو الرقم الجامعي المدخل" else "أضف طلاب مشاريع التخرج وتواصل معهم مباشرة عبر واتساب",
                    icon = Icons.Outlined.Group,
                    actionButtonText = if (searchQuery.isBlank() && projects.isNotEmpty()) "إضافة طالب جديد" else null,
                    onActionClick = if (searchQuery.isBlank() && projects.isNotEmpty()) onAddStudentClick else null,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(students, key = { it.id }) { student ->
                        StudentCard(
                            student = student,
                            projectName = projectNameMap[student.projectId] ?: "مشروع تخرج",
                            onEdit = { onEditStudentClick(student) },
                            onDelete = { studentToDeleteId = student.id },
                            onWhatsApp = { openWhatsApp(student.phone) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditStudentDialog(
    editingStudent: Student?,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onSave: (fullName: String, universityId: String, phone: String, projectId: Long, isLeader: Boolean) -> Unit
) {
    var fullName by remember { mutableStateOf(editingStudent?.fullName ?: "") }
    var universityId by remember { mutableStateOf(editingStudent?.universityId ?: "") }
    var phone by remember { mutableStateOf(editingStudent?.phone ?: "") }
    var selectedProjectId by remember {
        mutableStateOf(
            if (editingStudent != null && editingStudent.projectId != 0L) {
                editingStudent.projectId
            } else {
                projects.firstOrNull()?.id ?: 0L
            }
        )
    }
    var isLeader by remember { mutableStateOf(editingStudent?.isLeader ?: false) }

    var fullNameError by remember { mutableStateOf("") }
    var projectError by remember { mutableStateOf("") }
    var isProjectDropdownExpanded by remember { mutableStateOf(false) }

    val isEdit = editingStudent != null && editingStudent.id != 0L

    fun validateAndSubmit() {
        var hasError = false
        if (fullName.isBlank()) {
            fullNameError = "يرجى إدخال اسم الطالب"
            hasError = true
        } else {
            fullNameError = ""
        }

        if (selectedProjectId == 0L) {
            projectError = "يرجى اختيار المشروع المرتبط"
            hasError = true
        } else {
            projectError = ""
        }

        if (!hasError) {
            onSave(fullName, universityId, phone, selectedProjectId, isLeader)
        }
    }

    val selectedProjectTitle = projects.find { it.id == selectedProjectId }?.title ?: "اختر المشروع..."

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "تعديل بيانات الطالب" else "إضافة طالب جديد",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = ""
                    },
                    label = "اسم الطالب الكامل *",
                    placeholder = "مثال: عبد الله أحمد الأحمد",
                    leadingIcon = Icons.Default.Person,
                    isError = fullNameError.isNotBlank(),
                    errorMessage = fullNameError,
                    testTag = "student_dialog_name_input"
                )

                CustomTextField(
                    value = universityId,
                    onValueChange = { universityId = it },
                    label = "الرقم الجامعي (اختياري)",
                    placeholder = "441203921",
                    leadingIcon = Icons.Outlined.Badge,
                    testTag = "student_dialog_university_id_input"
                )

                CustomTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "رقم الواتساب (اختياري)",
                    placeholder = "966501234567",
                    leadingIcon = Icons.Default.Phone,
                    testTag = "student_dialog_phone_input"
                )

                // Project Selector
                Column {
                    Text(
                        text = "المشروع التابع له *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isProjectDropdownExpanded = true }
                                .testTag("student_project_selector")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedProjectTitle,
                                    fontSize = 14.sp,
                                    color = if (selectedProjectId != 0L) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = isProjectDropdownExpanded,
                            onDismissRequest = { isProjectDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            projects.forEach { proj ->
                                DropdownMenuItem(
                                    text = { Text(proj.title, maxLines = 1) },
                                    onClick = {
                                        selectedProjectId = proj.id
                                        projectError = ""
                                        isProjectDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (projectError.isNotBlank()) {
                        Text(
                            text = projectError,
                            color = BrandDanger,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 6.dp, top = 2.dp)
                        )
                    }
                }

                // Team Leader Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isLeader = !isLeader }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isLeader,
                        onCheckedChange = { isLeader = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BrandSecondary
                        ),
                        modifier = Modifier.testTag("student_is_leader_checkbox")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "تعيين كقائد للفريق",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "(يلغي تلقائياً القائد السابق للمشروع لضمان قائد واحد)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { validateAndSubmit() },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.testTag("save_student_dialog_btn")
            ) {
                Text(if (isEdit) "حفظ التعديل" else "إضافة الطالب")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
