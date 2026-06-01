package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage_projects")
data class StageProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val clientName: String = "",
    val venueName: String = "",
    val status: String = "Nháp", // Nháp, Đã báo giá, Đã duyệt, Đã hoàn thành
    val tags: String = "Sự kiện", // Comma-separated tags
    
    // Stage Floor dimensions (meters)
    val stageWidth: Float = 12.0f,
    val stageDepth: Float = 8.0f,
    val stageHeight: Float = 1.2f,
    
    // LED Screen dimensions (meters)
    val ledWidth: Float = 10.0f,
    val ledHeight: Float = 4.0f,
    val ledElevation: Float = 1.0f, // Height from stage floor
    val ledXPercent: Float = 50.0f, // Center X (0-100%)
    val ledYPercent: Float = 20.0f, // Center Y (0-100%)
    
    // Truss Frame dimensions (meters)
    val trussWidth: Float = 14.0f,
    val trussDepth: Float = 10.0f,
    val trussHeight: Float = 7.0f,
    val trussXPercent: Float = 50.0f,
    val trussYPercent: Float = 40.0f,
    
    // Sound & Lights Counters
    val speakerCount: Int = 8,  // Line array modules
    val subCount: Int = 4,      // Subwoofers
    val beamCount: Int = 16,     // Moving head beams
    val parCount: Int = 24,      // Par LED wash lights
    val blinderCount: Int = 4,   // Audience blinders
    
    // Speaker Positions
    val speakerLeftXPercent: Float = 15.0f,
    val speakerLeftYPercent: Float = 50.0f,
    val speakerRightXPercent: Float = 85.0f,
    val speakerRightYPercent: Float = 50.0f,
    
    // Metadata for sync, history, multi-user simulation
    val costPerSquareMeterStage: Double = 150000.0, // VND / m2 / day
    val costPerSquareMeterLed: Double = 350000.0,   // VND / m2 / day
    val costPerTrussMeter: Double = 120000.0,       // VND / m
    val costPerSpeaker: Double = 800000.0,          // VND / unit
    val costPerSub: Double = 1000000.0,              // VND / unit
    val costPerBeam: Double = 400000.0,             // VND / unit
    val costPerPar: Double = 150000.0,              // VND / unit
    val costPerBlinder: Double = 250000.0,          // VND / unit
    
    // Timeline
    val rentalDays: Int = 2,
    
    // Roles & Sync
    val createdByRole: String = "Designer",
    val updatedByRole: String = "Designer",
    val isSynced: Boolean = false,
    val lastSyncTime: Long = 0L,
    
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    // Computed property for Area & Length calculations
    val stageArea: Float
        get() = stageWidth * stageDepth
        
    val ledArea: Float
        get() = ledWidth * ledHeight
        
    val totalTrussLength: Float
        get() = (trussWidth * 2) + (trussDepth * 3) // Front, back, and support bars
        
    // Costs Calculation
    val stageCost: Double
        get() = stageArea.toDouble() * costPerSquareMeterStage * rentalDays
        
    val ledCost: Double
        get() = ledArea.toDouble() * costPerSquareMeterLed * rentalDays
        
    val trussCost: Double
        get() = totalTrussLength.toDouble() * costPerTrussMeter * rentalDays
        
    val soundCost: Double
        get() = ((speakerCount * costPerSpeaker) + (subCount * costPerSub)) * rentalDays
        
    val lightCost: Double
        get() = ((beamCount * costPerBeam) + (parCount * costPerPar) + (blinderCount * costPerBlinder)) * rentalDays

    val totalEstimateCost: Double
        get() = stageCost + ledCost + trussCost + soundCost + lightCost
}
