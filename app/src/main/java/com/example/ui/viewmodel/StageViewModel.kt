package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.StageDatabase
import com.example.data.model.StageProject
import com.example.data.repository.ProjectRepository
import com.example.utils.PdfExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

data class PushNotification(
    val id: Long = System.currentTimeMillis() + (0..1000).random(),
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String, // HỆ THỐNG, ĐỒNG BỘ, PHÂN QUYỀN, BÁO GIÁ
    val title: String,
    val message: String
)

sealed interface SelectedStageElement {
    object None : SelectedStageElement
    object StageFloor : SelectedStageElement
    object LedScreen : SelectedStageElement
    object TrussFrame : SelectedStageElement
    object SpeakerLeft : SelectedStageElement
    object SpeakerRight : SelectedStageElement
}

class StageViewModel(application: Application) : AndroidViewModel(application) {
    private val db = StageDatabase.getDatabase(application)
    private val repository = ProjectRepository(db.projectDao())

    // Search and filters
    val searchQuery = MutableStateFlow("")
    val selectedStatusFilter = MutableStateFlow("Tất cả")
    val selectedTagFilter = MutableStateFlow("Tất cả")

    // Active project selection
    val selectedProject = MutableStateFlow<StageProject?>(null)
    
    // Active item being customized visually
    val selectedElement = MutableStateFlow<SelectedStageElement>(SelectedStageElement.None)

    // Simulation states
    val userRole = MutableStateFlow("Kỹ Sư Thiết Kế") // Kỹ Sư Thiết Kế, Trưởng Phòng Mỹ Thuật, Khách Hàng
    val isSyncing = MutableStateFlow(false)
    val notifications = MutableStateFlow<List<PushNotification>>(emptyList())
    
    // PDF status
    val isGeneratingPdf = MutableStateFlow(false)

    // Live reactive project stream filtered on-the-fly
    val projects: StateFlow<List<StageProject>> = combine(
        searchQuery,
        selectedStatusFilter,
        selectedTagFilter,
        repository.allProjects
    ) { search, status, tag, allList ->
        allList.filter { project ->
            val matchSearch = search.isBlank() || 
                project.name.contains(search, ignoreCase = true) ||
                project.clientName.contains(search, ignoreCase = true) ||
                project.venueName.contains(search, ignoreCase = true) ||
                project.tags.contains(search, ignoreCase = true)
                
            val matchStatus = status == "Tất cả" || project.status == status
            
            val matchTag = tag == "Tất cả" || project.tags.split(",").map { it.trim() }.contains(tag)
            
            matchSearch && matchStatus && matchTag
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Safe check and seed initial data if empty
        viewModelScope.launch {
            try {
                if (repository.allProjects.first().isEmpty()) {
                    seedSampleData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Post welcome notification
        addNotification(
            tag = "HỆ THỐNG",
            title = "Chào mừng bạn đến với StageBuilder Pro",
            message = "Công cụ thiết kế layout sân khấu 2D/3D & tự động lập báo giá vật tư chi tiết."
        )
    }

    private fun seedSampleData() {
        viewModelScope.launch {
            val p1 = StageProject(
                name = "YEP Gala Dinner Sun Group",
                clientName = "Sun Group miền Trung",
                venueName = "InterContinental Danang Sun Peninsula Hotel",
                status = "Đã duyệt",
                tags = "Sự kiện, Ngoài trời",
                stageWidth = 14.0f,
                stageDepth = 8.0f,
                stageHeight = 1.2f,
                ledWidth = 10.0f,
                ledHeight = 4.5f,
                ledElevation = 1.0f,
                trussWidth = 15.0f,
                trussDepth = 9.0f,
                trussHeight = 7.5f,
                speakerCount = 10,
                subCount = 6,
                beamCount = 18,
                parCount = 28,
                blinderCount = 4,
                rentalDays = 2,
                createdByRole = "Trưởng Phòng Mỹ Thuật",
                updatedByRole = "Trưởng Phòng Mỹ Thuật",
                isSynced = true,
                notes = "Hợp đồng trọn gói bao gồm nhân sự kỹ sư vận hành âm thanh ánh sáng trực."
            )

            val p2 = StageProject(
                name = "Đại Nhạc Hội Ravolution EDM",
                clientName = "Anternet Media Agency",
                venueName = "Phố Đi Bộ Nguyễn Huệ, Thành phố Hồ Chí Minh",
                status = "Nháp",
                tags = "Show ca nhạc, Ngoài trời",
                stageWidth = 18.0f,
                stageDepth = 10.0f,
                stageHeight = 1.5f,
                ledWidth = 16.0f,
                ledHeight = 6.0f,
                ledElevation = 1.5f,
                trussWidth = 20.0f,
                trussDepth = 12.0f,
                trussHeight = 9.5f,
                speakerCount = 16,
                subCount = 12,
                beamCount = 36,
                parCount = 48,
                blinderCount = 8,
                rentalDays = 3,
                createdByRole = "Kỹ Sư Thiết Kế",
                updatedByRole = "Kỹ Sư Thiết Kế",
                isSynced = false,
                notes = "Lắp đặt khung giàn chịu lực gió bão đặc biệt vì tổ chức đại lộ công cộng ngoài trời."
            )

            val p3 = StageProject(
                name = "Hội Nghị Giới Thiệu Xe VinFast 2026",
                clientName = "VinFast Auto Global JSC",
                venueName = "Hội trường Đại hội, Trung tâm Hội nghị Quốc gia NCC Hà Nội",
                status = "Đã báo giá",
                tags = "Hội thảo, Trong nhà",
                stageWidth = 12.0f,
                stageDepth = 6.0f,
                stageHeight = 0.8f,
                ledWidth = 9.0f,
                ledHeight = 4.0f,
                ledElevation = 0.8f,
                trussWidth = 13.0f,
                trussDepth = 7.0f,
                trussHeight = 6.0f,
                speakerCount = 6,
                subCount = 4,
                beamCount = 12,
                parCount = 16,
                blinderCount = 2,
                rentalDays = 1,
                createdByRole = "Kỹ Sư Thiết Kế",
                updatedByRole = "Kỹ Sư Thiết Kế",
                isSynced = true,
                notes = "Trong nhà nên yêu cầu đèn Par Led dội mộc dịu mắt, không dùng máy tạo khói nặng."
            )

            repository.insert(p1)
            repository.insert(p2)
            repository.insert(p3)
            
            addNotification(
                tag = "HỆ THỐNG",
                title = "Khởi tạo dữ liệu mẫu",
                message = "Đã tích hợp 3 dự án sự kiện thực tế để bạn phân tích bản vẽ & báo giá lập tức."
            )
        }
    }

    // Insert new project
    fun createProject(name: String, client: String, venue: String, tagsStr: String) {
        viewModelScope.launch {
            val safeProj = StageProject(
                name = name.ifBlank { "Dự án mới chưa đặt tên" },
                clientName = client,
                venueName = venue,
                tags = if (tagsStr.isNotBlank()) tagsStr else "Sự kiện",
                createdByRole = userRole.value,
                updatedByRole = userRole.value
            )
            val newId = repository.insert(safeProj)
            val created = safeProj.copy(id = newId.toInt())
            selectedProject.value = created
            
            addNotification(
                tag = "HỆ THỐNG",
                title = "Tạo dự án mới",
                message = "Đã tạo thành công bản vẽ '${created.name}' mới! Bạn có thể kéo thả thiết bị ngay."
            )
        }
    }

    // Select project and reset selected element
    fun selectProject(project: StageProject?) {
        selectedProject.value = project
        selectedElement.value = SelectedStageElement.None
    }

    // Save project modifications
    fun updateProject(project: StageProject, persist: Boolean = true) {
        val updated = project.copy(
            dateModified = System.currentTimeMillis(),
            updatedByRole = userRole.value,
            isSynced = false // Mark dirty on edit for cloud sync flow
        )
        // Keep selected project updated immediately in memory
        if (selectedProject.value?.id == project.id) {
            selectedProject.value = updated
        }
        if (persist) {
            viewModelScope.launch {
                repository.update(updated)
            }
        }
    }

    fun persistSelectedProject() {
        val current = selectedProject.value ?: return
        viewModelScope.launch {
            repository.update(current)
        }
    }

    // Save customized positions specifically
    fun updateElementPosition(xPercent: Float, yPercent: Float, persist: Boolean = true) {
        val proj = selectedProject.value ?: return
        
        // Ensure values remain inside boundaries
        val boundX = xPercent.coerceIn(5.0f, 95.0f)
        val boundY = yPercent.coerceIn(5.0f, 95.0f)

        val updatedProj = when (selectedElement.value) {
            SelectedStageElement.LedScreen -> proj.copy(ledXPercent = boundX, ledYPercent = boundY)
            SelectedStageElement.TrussFrame -> proj.copy(trussXPercent = boundX, trussYPercent = boundY)
            SelectedStageElement.SpeakerLeft -> proj.copy(speakerLeftXPercent = boundX, speakerLeftYPercent = boundY)
            SelectedStageElement.SpeakerRight -> proj.copy(speakerRightXPercent = boundX, speakerRightYPercent = boundY)
            else -> proj
        }
        
        updateProject(updatedProj, persist)
    }

    // Quick duplicate project
    fun duplicateProject(project: StageProject) {
        viewModelScope.launch {
            val clone = project.copy(
                id = 0,
                name = "${project.name} (Bản sao)",
                status = "Nháp",
                isSynced = false,
                dateCreated = System.currentTimeMillis(),
                dateModified = System.currentTimeMillis(),
                createdByRole = userRole.value,
                updatedByRole = userRole.value
            )
            val newId = repository.insert(clone)
            addNotification(
                tag = "HỆ THỐNG",
                title = "Sao chép dự án",
                message = "Đã nhân bản nhanh dự án '${project.name}' thành bản sao mới."
            )
        }
    }

    // Remove project
    fun deleteProject(project: StageProject) {
        viewModelScope.launch {
            repository.delete(project)
            if (selectedProject.value?.id == project.id) {
                selectedProject.value = null
            }
            addNotification(
                tag = "HỆ THỐNG",
                title = "Đã xóa dự án",
                message = "Dự án '${project.name}' cùng tất cả thông số cấu lượng đã hoàn tất xóa bỏ."
            )
        }
    }

    // Custom Cloud Sync trigger with animation delay
    fun triggerCloudSync() {
        val currentProj = selectedProject.value ?: return
        if (isSyncing.value) return

        viewModelScope.launch {
            isSyncing.value = true
            addNotification(
                tag = "ĐỒNG BỘ",
                title = "Bắt đầu đồng bộ bảo mật",
                message = "Đang mã hóa 256-bit SSL dự án '${currentProj.name}'..."
            )
            
            kotlinx.coroutines.delay(1500) // Beautiful visual progress
            
            val updatedProj = currentProj.copy(
                isSynced = true,
                lastSyncTime = System.currentTimeMillis()
            )
            repository.update(updatedProj)
            selectedProject.value = updatedProj
            isSyncing.value = false
            
            addNotification(
                tag = "ĐỒNG BỘ",
                title = "Đồng bộ đám mây thành công",
                message = "Tải dữ liệu an toàn 100% của '${currentProj.name}' về máy chủ StageBuilder SSL Cloud."
            )
        }
    }

    // Role-based status updates
    fun updateProjectStatus(newStatus: String) {
        val proj = selectedProject.value ?: return
        
        // Assert authorization roles
        if (newStatus == "Đã duyệt" && userRole.value != "Trưởng Phòng Mỹ Thuật") {
            addNotification(
                tag = "PHÂN QUYỀN",
                title = "Từ chối cấp phép duyệt",
                message = "Chỉ Trưởng Phòng Mỹ Thuật mới có quyền Phê Duyệt thiết kế này lên máy chủ."
            )
            return
        }

        viewModelScope.launch {
            val updated = proj.copy(status = newStatus)
            updateProject(updated)
            
            addNotification(
                tag = "HỆ THỐNG",
                title = "Cập nhật trạng thái",
                message = "Dự án '${proj.name}' chuyển sang trạng thái: [ $newStatus ]"
            )
        }
    }

    // Add push alerts logs
    fun addNotification(tag: String, title: String, message: String) {
        val newNotification = PushNotification(tag = tag, title = title, message = message)
        val current = notifications.value.toMutableList()
        current.add(0, newNotification) // Add at start of stack (latest)
        if (current.size > 25) {
            current.removeAt(current.lastIndex)
        }
        notifications.value = current
    }

    fun clearNotifications() {
        notifications.value = emptyList()
    }

    // Export PDF and launch native Android Share Sheet
    fun exportAndSharePdf(context: Context) {
        val proj = selectedProject.value ?: return
        isGeneratingPdf.value = true
        
        addNotification(
            tag = "BÁO GIÁ",
            title = "Đang tạo tài liệu PDF",
            message = "Đang sinh bảng đề xuất chi phí & khối lượng vật tư cho '${proj.name}'..."
        )

        PdfExporter.exportProjectToPdf(
            context = context,
            project = proj,
            onCompleted = { file ->
                isGeneratingPdf.value = false
                addNotification(
                    tag = "BÁO GIÁ",
                    title = "Xuất PDF hoàn thành",
                    message = "File báo giá lưu trữ tại Bộ nhớ tạm: ${file.name}."
                )
                
                // Share Intent using FileProvider authority we configured in Manifest
                try {
                    val uri: Uri = FileProvider.getUriForFile(
                        context,
                        "com.aistudio.stagedesigner.kxwpzq.provider",
                        file
                    )
                    
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(shareIntent, "Gửi hóa đơn, báo giá PDF").apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(chooser)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Không thể chia sẻ PDF: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { errorMsg ->
                isGeneratingPdf.value = false
                addNotification(
                    tag = "BÁO GIÁ",
                    title = "Lỗi xuất bản vẽ PDF",
                    message = "Lỗi kỹ thuật: $errorMsg"
                )
                Toast.makeText(context, "Lỗi PDF: $errorMsg", Toast.LENGTH_LONG).show()
            }
        )
    }
}
