package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.data.model.Student
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProjectCard
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess

@Composable
fun HomeScreen(
    projects: List<Project>,
    allStudents: List<Student>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProjectClick: (Long) -> Unit,
    onAddProjectClick: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = projects.size
    val inProgressCount = projects.count { it.status == "in_progress" }
    val completedCount = projects.count { it.status == "completed" }

    // Map project ID to student count
    val studentsCountMap = remember(allStudents) {
        allStudents.groupBy { it.projectId }.mapValues { it.value.size }
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
                                modifier = Modifier.testTag("home_drawer_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "فتح القائمة الجانبية",
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "مشاريعي",
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
                                text = "$totalCount مشاريع",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatChip(
                            title = "الإجمالي",
                            count = totalCount,
                            color = BrandPrimary,
                            icon = Icons.Outlined.Assignment,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            title = "قيد التنفيذ",
                            count = inProgressCount,
                            color = BrandSecondary,
                            icon = Icons.Outlined.PendingActions,
                            modifier = Modifier.weight(1f)
                        )
                        StatChip(
                            title = "مكتمل",
                            count = completedCount,
                            color = BrandSuccess,
                            icon = Icons.Outlined.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("ابحث عن مشروع بالعنوان أو الوصف...") },
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
                            .testTag("home_search_input")
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProjectClick,
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("home_add_project_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة مشروع جديد",
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
            if (projects.isEmpty()) {
                EmptyStateView(
                    title = if (searchQuery.isNotBlank()) "لا توجد نتائج بحث" else "لا توجد مشاريع تخرج حالياً",
                    subtitle = if (searchQuery.isNotBlank()) "جرب البحث بكلمات أخرى" else "ابدأ بإضافة مشاريع التخرج التي تشرف عليها لإدارتها وتوزيع الطلاب عليها",
                    icon = Icons.Outlined.FolderSpecial,
                    actionButtonText = if (searchQuery.isBlank()) "إضافة مشروع جديد" else null,
                    onActionClick = if (searchQuery.isBlank()) onAddProjectClick else null,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(projects, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            studentCount = studentsCountMap[project.id] ?: 0,
                            onClick = { onProjectClick(project.id) }
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
private fun StatChip(
    title: String,
    count: Int,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = count.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = color
                )
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
