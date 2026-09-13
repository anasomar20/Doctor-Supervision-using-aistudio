package com.example

import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.data.model.Student
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DoctorSupervisionTest {

    @Test
    fun testProjectStatusMapping() {
        assertEquals(ProjectStatus.PENDING, ProjectStatus.fromKey("pending"))
        assertEquals(ProjectStatus.IN_PROGRESS, ProjectStatus.fromKey("in_progress"))
        assertEquals(ProjectStatus.COMPLETED, ProjectStatus.fromKey("completed"))
        assertEquals(ProjectStatus.PENDING, ProjectStatus.fromKey("unknown_key"))

        assertEquals("قيد التنفيذ", ProjectStatus.IN_PROGRESS.titleAr)
        assertEquals("مكتمل", ProjectStatus.COMPLETED.titleAr)
        assertEquals("لم يبدأ", ProjectStatus.PENDING.titleAr)
    }

    @Test
    fun testStudentLeaderState() {
        val leader = Student(
            id = 1L,
            projectId = 10L,
            fullName = "خالد المحمد",
            universityId = "441100234",
            isLeader = true
        )
        val member = Student(
            id = 2L,
            projectId = 10L,
            fullName = "عمر السالم",
            universityId = "441100235",
            isLeader = false
        )

        assertTrue(leader.isLeader)
        assertFalse(member.isLeader)
        assertEquals(10L, leader.projectId)
    }

    @Test
    fun testProjectCreation() {
        val project = Project(
            id = 1L,
            title = "نظام الفرز الطبي الذكي",
            description = "تطبيق ذكي يعتمد على الرؤية الحاسوبية",
            supervisorId = "sup_1",
            status = "in_progress",
            githubLink = "https://github.com/example/repo",
            proposalLink = "https://example.com/proposal.pdf",
            srsLink = "https://example.com/srs.pdf"
        )

        assertEquals("نظام الفرز الطبي الذكي", project.title)
        assertEquals("sup_1", project.supervisorId)
        assertEquals("in_progress", project.status)
        assertTrue(project.githubLink.isNotBlank())
    }
}
