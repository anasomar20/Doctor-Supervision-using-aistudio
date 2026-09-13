package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomTextField
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProjectScreen(
    editingProject: Project?,
    onSave: (
        title: String,
        description: String,
        status: String,
        notes: String,
        githubLink: String,
        proposalLink: String,
        srsLink: String
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(editingProject?.title ?: "") }
    var description by remember { mutableStateOf(editingProject?.description ?: "") }
    var status by remember { mutableStateOf(editingProject?.status ?: "pending") }
    var notes by remember { mutableStateOf(editingProject?.notes ?: "") }
    var githubLink by remember { mutableStateOf(editingProject?.githubLink ?: "") }
    var proposalLink by remember { mutableStateOf(editingProject?.proposalLink ?: "") }
    var srsLink by remember { mutableStateOf(editingProject?.srsLink ?: "") }

    var titleError by remember { mutableStateOf("") }

    val isEditMode = editingProject != null
    val scrollState = rememberScrollState()

    fun validateAndSave() {
        if (title.isBlank()) {
            titleError = "يرجى إدخال عنوان المشروع"
            return
        }
        onSave(title, description, status, notes, githubLink, proposalLink, srsLink)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "تعديل بيانات المشروع" else "إضافة مشروع تخرج جديد",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("add_edit_project_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackground)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "البيانات الأساسية",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CustomTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = ""
                        },
                        label = "عنوان المشروع *",
                        placeholder = "مثال: نظام إدارة العيادات الذكي",
                        leadingIcon = Icons.Default.Title,
                        isError = titleError.isNotBlank(),
                        errorMessage = titleError,
                        testTag = "project_title_input"
                    )

                    CustomTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "وصف المشروع",
                        placeholder = "نبذة موجزة عن فكرة المشروع وأهدافه التقنية...",
                        singleLine = false,
                        maxLines = 4,
                        testTag = "project_description_input"
                    )

                    // Project Status Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "حالة المشروع:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusSelectChip(
                                title = "لم يبدأ",
                                isSelected = status == "pending",
                                selectedColor = Color(0xFF64748B),
                                onClick = { status = "pending" },
                                modifier = Modifier.weight(1f),
                                testTag = "status_chip_pending"
                            )
                            StatusSelectChip(
                                title = "قيد التنفيذ",
                                isSelected = status == "in_progress",
                                selectedColor = BrandSecondary,
                                onClick = { status = "in_progress" },
                                modifier = Modifier.weight(1f),
                                testTag = "status_chip_in_progress"
                            )
                            StatusSelectChip(
                                title = "مكتمل",
                                isSelected = status == "completed",
                                selectedColor = BrandSuccess,
                                onClick = { status = "completed" },
                                modifier = Modifier.weight(1f),
                                testTag = "status_chip_completed"
                            )
                        }
                    }

                    CustomTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "ملاحظات وتوجيهات المشرف",
                        placeholder = "ملاحظات حول التقدم، مواعيد التسليم، التوجيهات...",
                        leadingIcon = Icons.Default.Notes,
                        singleLine = false,
                        maxLines = 3,
                        testTag = "project_notes_input"
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "روابط المشروع والوثائق (اختياري)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CustomTextField(
                        value = githubLink,
                        onValueChange = { githubLink = it },
                        label = "رابط مستودع GitHub",
                        placeholder = "https://github.com/...",
                        leadingIcon = Icons.Default.Code,
                        testTag = "project_github_input"
                    )

                    CustomTextField(
                        value = proposalLink,
                        onValueChange = { proposalLink = it },
                        label = "رابط مقترح المشروع (Proposal)",
                        placeholder = "https://example.com/proposal.pdf",
                        leadingIcon = Icons.Default.Description,
                        testTag = "project_proposal_input"
                    )

                    CustomTextField(
                        value = srsLink,
                        onValueChange = { srsLink = it },
                        label = "رابط وثيقة المتطلبات (SRS)",
                        placeholder = "https://example.com/srs.pdf",
                        leadingIcon = Icons.Default.Description,
                        testTag = "project_srs_input"
                    )
                }
            }

            CustomButton(
                text = if (isEditMode) "حفظ التعديلات" else "إضافة المشروع",
                onClick = { validateAndSave() },
                testTag = "save_project_btn"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatusSelectChip(
    title: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Surface(
        color = if (isSelected) selectedColor.copy(alpha = 0.15f) else BrandBackground,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) selectedColor else Color(0xFFE2E8F0)
        ),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = selectedColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
            }
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
