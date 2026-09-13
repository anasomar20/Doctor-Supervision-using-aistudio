package com.example.ui.screens.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomTextField
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("dr.khaled@university.edu.sa") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        var hasError = false
        if (email.isBlank()) {
            emailError = "يرجى إدخال البريد الإلكتروني"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "صيغة البريد الإلكتروني غير صحيحة"
            hasError = true
        } else {
            emailError = ""
        }

        if (password.isBlank()) {
            passwordError = "يرجى إدخال كلمة المرور"
            hasError = true
        } else if (password.length < 6) {
            passwordError = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
            hasError = true
        } else {
            passwordError = ""
        }

        if (!hasError) {
            onLogin(email.trim(), password)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BrandBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo Icon
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "شعار التطبيق",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "بوابة الإشراف الأكاديمي",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrandPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "متابعة وإشراف مشاريع تخرج الطلاب",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "تسجيل الدخول",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Surface(
                            color = BrandDanger.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage,
                                color = BrandDanger,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    CustomTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = ""
                        },
                        label = "البريد الإلكتروني الجامعي",
                        placeholder = "doctor@university.edu.sa",
                        leadingIcon = Icons.Default.Email,
                        isError = emailError.isNotBlank(),
                        errorMessage = emailError,
                        testTag = "login_email_input"
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = ""
                        },
                        label = "كلمة المرور",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                                )
                            }
                        },
                        isError = passwordError.isNotBlank(),
                        errorMessage = passwordError,
                        testTag = "login_password_input"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    CustomButton(
                        text = "تسجيل الدخول",
                        onClick = { validateAndSubmit() },
                        isLoading = isLoading,
                        testTag = "login_submit_btn"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ليس لديك حساب مشرف؟",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.testTag("navigate_register_btn")
                ) {
                    Text(
                        text = "إنشاء حساب جديد",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RegisterScreen(
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        var hasError = false

        if (fullName.isBlank()) {
            fullNameError = "يرجى إدخال الاسم الكامل"
            hasError = true
        } else {
            fullNameError = ""
        }

        if (email.isBlank()) {
            emailError = "يرجى إدخال البريد الإلكتروني"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "صيغة البريد الإلكتروني غير صحيحة"
            hasError = true
        } else {
            emailError = ""
        }

        if (password.isBlank()) {
            passwordError = "يرجى إدخال كلمة المرور"
            hasError = true
        } else if (password.length < 6) {
            passwordError = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
            hasError = true
        } else {
            passwordError = ""
        }

        if (confirmPassword != password) {
            confirmPasswordError = "كلمتا المرور غير متطابقتين"
            hasError = true
        } else {
            confirmPasswordError = ""
        }

        if (!hasError) {
            onRegister(fullName.trim(), email.trim(), password)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BrandBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "تسجيل مشرف جديد",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BrandPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "أنشئ حسابك لإدارة ومتابعة مشاريع التخرج",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!errorMessage.isNullOrBlank()) {
                        Surface(
                            color = BrandDanger.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage,
                                color = BrandDanger,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    CustomTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            fullNameError = ""
                        },
                        label = "الاسم الكامل (مع اللقب الأكاديمي)",
                        placeholder = "مثال: د. عبد الله السعيد",
                        leadingIcon = Icons.Default.Person,
                        isError = fullNameError.isNotBlank(),
                        errorMessage = fullNameError,
                        testTag = "register_name_input"
                    )

                    CustomTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = ""
                        },
                        label = "البريد الإلكتروني الجامعي",
                        placeholder = "doctor@university.edu.sa",
                        leadingIcon = Icons.Default.Email,
                        isError = emailError.isNotBlank(),
                        errorMessage = emailError,
                        testTag = "register_email_input"
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = ""
                        },
                        label = "كلمة المرور (6 أحرف فأكثر)",
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
                        isError = passwordError.isNotBlank(),
                        errorMessage = passwordError,
                        testTag = "register_password_input"
                    )

                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = ""
                        },
                        label = "تأكيد كلمة المرور",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        isError = confirmPasswordError.isNotBlank(),
                        errorMessage = confirmPasswordError,
                        testTag = "register_confirm_password_input"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    CustomButton(
                        text = "إنشاء الحساب",
                        onClick = { validateAndSubmit() },
                        isLoading = isLoading,
                        testTag = "register_submit_btn"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "لديك حساب بالفعل؟",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.testTag("navigate_login_btn")
                ) {
                    Text(
                        text = "تسجيل الدخول",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
