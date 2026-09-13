package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandDanger
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SupervisionViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: SupervisionViewModel,
    modifier: Modifier = Modifier
) {
    // RTL Layout Direction for the entire app as requested
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
        val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
        val authError by viewModel.authError.collectAsStateWithLifecycle()

        var isShowingRegister = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        if (currentUser == null) {
            if (isShowingRegister.value) {
                RegisterScreen(
                    onRegister = { name, email, pass ->
                        viewModel.register(name, email, pass)
                    },
                    onNavigateToLogin = { isShowingRegister.value = false },
                    isLoading = isAuthLoading,
                    errorMessage = authError
                )
            } else {
                LoginScreen(
                    onLogin = { email, pass ->
                        viewModel.login(email, pass)
                    },
                    onNavigateToRegister = { isShowingRegister.value = true },
                    isLoading = isAuthLoading,
                    errorMessage = authError
                )
            }
        } else {
            // User is authenticated
            AuthenticatedApp(viewModel = viewModel, modifier = modifier)
        }
    }
}

@Composable
private fun AuthenticatedApp(
    viewModel: SupervisionViewModel,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val viewingProjectId by viewModel.viewingProjectId.collectAsStateWithLifecycle()
    val isAddEditProjectOpen by viewModel.isAddEditProjectOpen.collectAsStateWithLifecycle()
    val editingProject by viewModel.editingProject.collectAsStateWithLifecycle()
    val isAddEditStudentOpen by viewModel.isAddEditStudentOpen.collectAsStateWithLifecycle()
    val editingStudent by viewModel.editingStudent.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()

    val projects by viewModel.filteredProjects.collectAsStateWithLifecycle()
    val allProjects by viewModel.rawProjects.collectAsStateWithLifecycle()
    val students by viewModel.filteredStudents.collectAsStateWithLifecycle()
    val allStudents by viewModel.rawStudents.collectAsStateWithLifecycle()

    val currentProject by viewModel.currentProject.collectAsStateWithLifecycle()
    val currentProjectStudents by viewModel.currentProjectStudents.collectAsStateWithLifecycle()

    val projectSearchQuery by viewModel.projectSearchQuery.collectAsStateWithLifecycle()
    val studentSearchQuery by viewModel.studentSearchQuery.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight(),
                drawerContainerColor = Color.White
            ) {
                // Custom Drawer Header: Doctor Avatar, Name, Email
                Surface(
                    color = BrandPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 28.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.fullName?.take(2) ?: "د.",
                                color = BrandPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentUser?.fullName ?: "د. خالد العتيبي",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = currentUser?.email ?: "dr.khaled@university.edu.sa",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Drawer Navigation items
                NavigationDrawerItem(
                    label = { Text("مشاريعي", fontWeight = FontWeight.SemiBold) },
                    selected = !isSettingsOpen && viewingProjectId == null && selectedTab == AppTab.HOME,
                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    onClick = {
                        viewModel.selectTab(AppTab.HOME)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawer_item_home")
                )

                NavigationDrawerItem(
                    label = { Text("قائمة الطلاب", fontWeight = FontWeight.SemiBold) },
                    selected = !isSettingsOpen && viewingProjectId == null && selectedTab == AppTab.STUDENTS,
                    icon = { Icon(Icons.Default.Group, contentDescription = null) },
                    onClick = {
                        viewModel.selectTab(AppTab.STUDENTS)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawer_item_students")
                )

                NavigationDrawerItem(
                    label = { Text("الملف الشخصي", fontWeight = FontWeight.SemiBold) },
                    selected = !isSettingsOpen && viewingProjectId == null && selectedTab == AppTab.PROFILE,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = {
                        viewModel.selectTab(AppTab.PROFILE)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawer_item_profile")
                )

                NavigationDrawerItem(
                    label = { Text("الإعدادات", fontWeight = FontWeight.SemiBold) },
                    selected = isSettingsOpen,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = {
                        viewModel.openSettings()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawer_item_settings")
                )

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(color = BrandCardBorder)

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "تسجيل الخروج",
                            color = BrandDanger,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = BrandDanger
                        )
                    },
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            viewModel.logout()
                        }
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("drawer_item_logout")
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        // Main Screen Content Stack
        if (isAddEditProjectOpen) {
            AddEditProjectScreen(
                editingProject = editingProject,
                onSave = { title, desc, status, notes, gh, prop, srs ->
                    viewModel.saveProject(title, desc, status, notes, gh, prop, srs)
                },
                onBack = { viewModel.closeAddEditProject() }
            )
        } else if (isSettingsOpen) {
            SettingsScreen(
                supervisor = currentUser,
                onBack = { viewModel.closeSettings() },
                onLogout = { viewModel.logout() },
                onUpdatePassword = { curr, newP, cb ->
                    viewModel.updatePassword(curr, newP, cb)
                }
            )
        } else if (viewingProjectId != null) {
            currentProject?.let { proj ->
                ProjectDetailsScreen(
                    project = proj,
                    students = currentProjectStudents,
                    onBack = { viewModel.closeProjectDetails() },
                    onEditProject = { viewModel.openEditProject(proj) },
                    onDeleteProject = { viewModel.deleteProject(proj.id) },
                    onAddStudent = { viewModel.openAddStudent(proj.id) },
                    onEditStudent = { viewModel.openEditStudent(it) },
                    onDeleteStudent = { viewModel.deleteStudent(it) }
                )
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("المشروع غير موجود")
            }
        } else {
            // Main Tabs Screen with Bottom Navigation
            Scaffold(
                bottomBar = {
                    CustomBottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelect = { viewModel.selectTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        AppTab.HOME -> {
                            HomeScreen(
                                projects = projects,
                                allStudents = allStudents,
                                searchQuery = projectSearchQuery,
                                onSearchQueryChange = { viewModel.projectSearchQuery.value = it },
                                onProjectClick = { viewModel.openProjectDetails(it) },
                                onAddProjectClick = { viewModel.openAddProject() },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                        AppTab.STUDENTS -> {
                            StudentsListScreen(
                                students = students,
                                projects = allProjects,
                                searchQuery = studentSearchQuery,
                                onSearchQueryChange = { viewModel.studentSearchQuery.value = it },
                                onAddStudentClick = { viewModel.openAddStudent() },
                                onEditStudentClick = { viewModel.openEditStudent(it) },
                                onDeleteStudent = { viewModel.deleteStudent(it) },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                        AppTab.PROFILE -> {
                            ProfileScreen(
                                supervisor = currentUser,
                                projects = allProjects,
                                students = allStudents,
                                onLogout = { viewModel.logout() },
                                onOpenDrawer = { scope.launch { drawerState.open() } }
                            )
                        }
                    }
                }
            }
        }

        // Add/Edit Student Dialog Modal
        if (isAddEditStudentOpen) {
            AddEditStudentDialog(
                editingStudent = editingStudent,
                projects = allProjects,
                onDismiss = { viewModel.closeAddEditStudent() },
                onSave = { name, univId, phone, projId, leader ->
                    viewModel.saveStudent(name, univId, phone, projId, leader)
                }
            )
        }
    }
}

@Composable
private fun CustomBottomNavBar(
    selectedTab: AppTab,
    onTabSelect: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                title = "مشاريعي",
                iconFilled = Icons.Default.Folder,
                iconOutline = Icons.Outlined.Folder,
                isSelected = selectedTab == AppTab.HOME,
                onClick = { onTabSelect(AppTab.HOME) },
                testTag = "tab_home"
            )

            BottomNavItem(
                title = "الطلاب",
                iconFilled = Icons.Default.Group,
                iconOutline = Icons.Outlined.Group,
                isSelected = selectedTab == AppTab.STUDENTS,
                onClick = { onTabSelect(AppTab.STUDENTS) },
                testTag = "tab_students"
            )

            BottomNavItem(
                title = "الملف الشخصي",
                iconFilled = Icons.Default.Person,
                iconOutline = Icons.Outlined.Person,
                isSelected = selectedTab == AppTab.PROFILE,
                onClick = { onTabSelect(AppTab.PROFILE) },
                testTag = "tab_profile"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    title: String,
    iconFilled: ImageVector,
    iconOutline: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) BrandPrimary.copy(alpha = 0.12f) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) iconFilled else iconOutline,
                contentDescription = title,
                tint = if (isSelected) BrandPrimary else Color(0xFF8A8A8A),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) BrandPrimary else Color(0xFF8A8A8A)
        )
    }
}
