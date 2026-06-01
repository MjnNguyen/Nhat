package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StageProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM stage_projects ORDER BY dateModified DESC")
    fun getAllProjects(): Flow<List<StageProject>>

    @Query("SELECT * FROM stage_projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: Int): Flow<StageProject?>

    @Query("SELECT * FROM stage_projects WHERE name LIKE :searchQuery OR clientName LIKE :searchQuery OR venueName LIKE :searchQuery OR tags LIKE :searchQuery ORDER BY dateModified DESC")
    fun searchProjects(searchQuery: String): Flow<List<StageProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: StageProject): Long

    @Update
    suspend fun updateProject(project: StageProject)

    @Delete
    suspend fun deleteProject(project: StageProject)

    @Query("DELETE FROM stage_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)
}
