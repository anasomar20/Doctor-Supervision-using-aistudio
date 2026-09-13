package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Supervisor
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomTextField
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    supervisor: Supervisor?,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onUpdatePassword: (currentPass: String, newPass: String, onResult: (Boolean, String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPasswordSectionExpanded by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    var currentPassError by remember { mutableStateOf("") }
    var newPassError by remember { mutableStateOf("") }
    var confirmPassError by remember { mutableStateOf("") }
    var isUpdatingPassword by remember { mutableStateOf(false) }

    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun validateAndChangePassword() {
        var hasError = false
        if (currentPassword.isBlank()) {
            currentPassError = "يرجى إدخال كلمة المرور الحالية"
            hasError = true
        } else {
            currentPassError = ""
        }

        if (newPassword.isBlank()) {
            newPassError = "يرجى إدخال كلمة المرور الجديدة"
            hasError = true
        } else if (newPassword.length < 6) {
            newPassError = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
            hasError = true
        } else {
            newPassError = ""
        }

        if (confirmNewPassword != newPassword) {
            confirmPassError = "كلمتا المرور غير متطابقتين"
            hasError = true
        } else {
            confirmPassError = ""
        }

        if (!hasError) {
            isUpdatingPassword = true
            onUpdatePassword(currentPassword, newPassword) { success, msg ->
                isUpdatingPassword = false
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                if (success) {
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                    isPasswordSectionExpanded = false
                }
            }
        }
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("تأكيد تسجيل الخروج") },
            text = { Text("هل تريد بالتأكيد تسجيل الخروج من التطبيق؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDanger)
                ) {
                    Text("تسجيل الخروج")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
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
                        text = "الإعدادات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_btn")) {
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
            // Account Information Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandPrimary
                        )
                        Text(
                            text = "بيانات الحساب الأكاديمي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = BrandCardBorder)

                    SettingInfoRow(label = "الاسم الكامل", value = supervisor?.fullName ?: "د. خالد العتيبي")
                    SettingInfoRow(label = "البريد الإلكتروني", value = supervisor?.email ?: "dr.khaled@university.edu.sa")
                    SettingInfoRow(label = "الرتبة الأكاديمية", value = supervisor?.academicTitle ?: "أستاذ مشارك")
                }
            }

            // Security & Collapsible Change Password Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPasswordSectionExpanded = !isPasswordSectionExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BrandPrimary
                            )
                            Text(
                                text = "تغيير كلمة المرور",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = if (isPasswordSectionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "فتح أو طي قسم كلمة المرور",
                            tint = BrandPrimary
                        )
                    }

                    AnimatedVisibility(visible = isPasswordSectionExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomTextField(
                                value = currentPassword,
                                onValueChange = {
                                    currentPassword = it
                                    currentPassError = ""
                                },
                                label = "كلمة المرور الحالية",
                                placeholder = "••••••••",
                                leadingIcon = Icons.Default.Lock,
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                },
                                isError = currentPassError.isNotBlank(),
                                errorMessage = currentPassError,
                                testTag = "settings_current_password_input"
                            )

                            CustomTextField(
                                value = newPassword,
                                onValueChange = {
                                    newPassword = it
                                    newPassError = ""
                                },
                                label = "كلمة المرور الجديدة",
                                placeholder = "••••••••",
                                leadingIcon = Icons.Default.Lock,
                                isError = newPassError.isNotBlank(),
                                errorMessage = newPassError,
                                testTag = "settings_new_password_input"
                            )

                            CustomTextField(
                                value = confirmNewPassword,
                                onValueChange = {
                                    confirmNewPassword = it
                                    confirmPassError = ""
                                },
                                label = "تأكيد كلمة المرور الجديدة",
                                placeholder = "••••••••",
                                leadingIcon = Icons.Default.Lock,
                                isError = confirmPassError.isNotBlank(),
                                errorMessage = confirmPassError,
                                testTag = "settings_confirm_password_input"
                            )

                            CustomButton(
                                text = "تحديث كلمة المرور",
                                onClick = { validateAndChangePassword() },
                                isLoading = isUpdatingPassword,
                                testTag = "settings_update_password_btn"
                            )
                        }
                    }
                }
            }

            // About App Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BrandPrimary
                        )
                        Text(
                            text = "معلومات عن التطبيق",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = BrandCardBorder)

                    SettingInfoRow(label = "اسم التطبيق", value = "إشراف على مشاريع التخرج (Doctor Supervision)")
                    SettingInfoRow(label = "رقم الإصدار", value = "1.0.0")
                    SettingInfoRow(label = "المنصة", value = "Android (Kotlin & Jetpack Compose)")
                    SettingInfoRow(label = "قاعدة البيانات والمزامنة", value = "Room Persistence & Firebase Architecture")
                    SettingInfoRow(label = "واجهة المستخدم", value = "RTL Arabic & Material 3 Design System")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            CustomButton(
                text = "تسجيل الخروج من الحساب",
                onClick = { showLogoutConfirmDialog = true },
                isDanger = true,
                icon = Icons.Default.ExitToApp,
                testTag = "settings_logout_btn"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
