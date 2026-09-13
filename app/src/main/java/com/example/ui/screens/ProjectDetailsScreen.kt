package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.data.model.Student
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudentCard
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    project: Project,
    students: List<Student>,
    onBack: () -> Unit,
    onEditProject: () -> Unit,
    onDeleteProject: () -> Unit,
    onAddStudent: () -> Unit,
    onEditStudent: (Student) -> Unit,
    onDeleteStudent: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var studentToDeleteId by remember { mutableStateOf<Long?>(null) }

    fun openUrl(url: String) {
        try {
            val formatted = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "https://$url"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formatted))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(phone: String) {
        try {
            val cleanPhone = phone.filter { it.isDigit() }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح واتساب", Toast.LENGTH_SHORT).show()
        }
    }

    // Delete Project Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("تأكيد حذف المشروع") },
            text = {
                Text("هل أنت متأكد من حذف مشروع '${project.title}'؟ سيتم حذف جميع بيانات الطلاب المرتبطة به أيضاً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteProject()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDanger),
                    modifier = Modifier.testTag("confirm_delete_project_btn")
                ) {
                    Text("حذف المشروع")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Delete Student Confirmation Dialog
    if (studentToDeleteId != null) {
        AlertDialog(
            onDismissRequest = { studentToDeleteId = null },
            title = { Text("تأكيد حذف الطالب") },
            text = { Text("هل أنت متأكد من حذف هذا الطالب من المشروع؟") },
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
            TopAppBar(
                title = {
                    Text(
                        text = "تفاصيل المشروع",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("project_details_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEditProject, modifier = Modifier.testTag("project_edit_btn")) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل المشروع",
                            tint = BrandPrimary
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.testTag("project_delete_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف المشروع",
                            tint = BrandDanger
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main info card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(statusKey = project.status)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${students.size} طلاب مسجلين",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandPrimary
                                )
                            }
                        }

                        Text(
                            text = project.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (project.description.isNotBlank()) {
                            Text(
                                text = project.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                        }

                        if (project.notes.isNotBlank()) {
                            HorizontalDivider(color = BrandCardBorder)
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notes,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "ملاحظات وتوجيهات المشرف:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BrandPrimary
                                    )
                                    Text(
                                        text = project.notes,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // External Links Section
            item {
                val hasLinks = project.githubLink.isNotBlank() || project.proposalLink.isNotBlank() || project.srsLink.isNotBlank()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = BrandPrimary
                            )
                            Text(
                                text = "روابط ومستندات المشروع",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (!hasLinks) {
                            Text(
                                text = "لم تتم إضافة روابط لهذا المشروع بعد.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            if (project.githubLink.isNotBlank()) {
                                LinkRowItem(
                                    title = "مستودع الكود (GitHub)",
                                    url = project.githubLink,
                                    icon = Icons.Default.Code,
                                    onClick = { openUrl(project.githubLink) }
                                )
                            }
                            if (project.proposalLink.isNotBlank()) {
                                LinkRowItem(
                                    title = "مقترح المشروع (Proposal)",
                                    url = project.proposalLink,
                                    icon = Icons.Default.Description,
                                    onClick = { openUrl(project.proposalLink) }
                                )
                            }
                            if (project.srsLink.isNotBlank()) {
                                LinkRowItem(
                                    title = "وثيقة المتطلبات (SRS)",
                                    url = project.srsLink,
                                    icon = Icons.Default.Description,
                                    onClick = { openUrl(project.srsLink) }
                                )
                            }
                        }
                    }
                }
            }

            // Students enrolled in this project
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = BrandPrimary
                        )
                        Text(
                            text = "الطلاب المسجلين بالمشروع (${students.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    TextButton(onClick = onAddStudent, modifier = Modifier.testTag("add_student_to_project_btn")) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "إضافة طالب",
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }
                }
            }

            if (students.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "لا يوجد طلاب مسجلين بهذا المشروع بعد",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(onClick = onAddStudent) {
                                Text("إضافة طالب جديد للمشروع")
                            }
                        }
                    }
                }
            } else {
                items(students, key = { it.id }) { student ->
                    StudentCard(
                        student = student,
                        projectName = "",
                        onEdit = { onEditStudent(student) },
                        onDelete = { studentToDeleteId = student.id },
                        onWhatsApp = { openWhatsApp(student.phone) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun LinkRowItem(
    title: String,
    url: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = BrandBackground,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = url,
                        fontSize = 12.sp,
                        color = BrandPrimary,
                        maxLines = 1
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "فتح الرابط",
                tint = BrandPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
