package com.example.data.repository

import com.example.data.database.ProjectDao
import com.example.data.model.StageProject
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<StageProject>> = projectDao.getAllProjects()

    fun searchProjects(query: String): Flow<List<StageProject>> {
        return if (query.isBlank()) {
            projectDao.getAllProjects()
        } else {
            projectDao.searchProjects("%$query%")
        }
    }

    fun getProjectById(id: Int): Flow<StageProject?> {
        return projectDao.getProjectById(id)
    }

    suspend fun insert(project: StageProject): Long {
        return projectDao.insertProject(project)
    }

    suspend fun update(project: StageProject) {
        projectDao.updateProject(project)
    }

    suspend fun delete(project: StageProject) {
        projectDao.deleteProject(project)
    }

    suspend fun deleteById(id: Int) {
        projectDao.deleteProjectById(id)
    }
}
