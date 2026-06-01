package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StageProject
import com.example.ui.viewmodel.StageViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProjectList(
    viewModel: StageViewModel,
    modifier: Modifier = Modifier,
    onProjectSelected: (StageProject) -> Unit
) {
    // Collect search query, lists, and filter states from StateFlows
    val projectsList by viewModel.projects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.selectedStatusFilter.collectAsState()
    val tagFilter by viewModel.selectedTagFilter.collectAsState()
    val selectedProj by viewModel.selectedProject.collectAsState()

    // Form inputs for creating a new project
    var showCreateDialog by remember { mutableStateOf(false) }
    var newProjName by remember { mutableStateOf("") }
    var newProjClient by remember { mutableStateOf("") }
    var newProjVenue by remember { mutableStateOf("") }
    var newProjTags by remember { mutableStateOf("") }

    // Tag list for horizontal selector
    val availableTags = listOf("Tất cả", "Sự kiện", "Ngoài trời", "Trong nhà", "Hội nghị", "Show ca nhạc", "Hội thảo")
    val availableStatuses = listOf("Tất cả", "Nháp", "Đã báo giá", "Đã duyệt")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("project_list_container")
    ) {
        // Search & Filter header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "DANH SÁCH DỰ ÁN",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            // Add New Project Action Button
            ElevatedButton(
                onClick = { 
                    showCreateDialog = true 
                    newProjName = ""
                    newProjClient = ""
                    newProjVenue = ""
                    newProjTags = ""
                },
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF1ABC9C)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_create_project")
            ) {
                Icon(Icons.Default.Add, "New", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tạo Mới", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Tìm kiếm dự án, khách hàng, nhãn...", color = Color.Gray, fontSize = 13.sp) },
            prefix = { Icon(Icons.Default.Search, "Search", tint = Color.Gray, modifier = Modifier.size(18.dp)) },
            suffix = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, "Clear", tint = Color.LightGray)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_field_project"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF161B22),
                unfocusedContainerColor = Color(0xFF111418),
                focusedBorderColor = Color(0xFF16A085),
                unfocusedBorderColor = Color(0xFF2C3E50),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.LightGray
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal status filters row
        Text("Trạng thái:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(availableStatuses) { status ->
                val isSelected = statusFilter == status
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { viewModel.selectedStatusFilter.value = status },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1ABC9C).copy(alpha = 0.2f) else Color(0xFF161B22)
                    ),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF1ABC9C) else Color(0xFF232B35))
                ) {
                    Text(
                        text = status,
                        fontSize = 11.sp,
                        color = if (isSelected) Color(0xFF1ABC9C) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Horizontal tags selector row
        Text("Thẻ nhãn:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(availableTags) { tag ->
                val isSelected = tagFilter == tag
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { viewModel.selectedTagFilter.value = tag },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF14756F).copy(alpha = 0.2f) else Color(0xFF111418)
                    ),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF16A085) else Color(0xFF232B35))
                ) {
                    Text(
                        text = tag,
                        fontSize = 11.sp,
                        color = if (isSelected) Color(0xFF1ABC9C) else Color.LightGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List Scroll
        if (projectsList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, Color(0xFF232B35), RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1317)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        "Empty",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Không tìm thấy dự án nào",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Nhấp chọn 'Tạo Mới' hoặc đổi từ khóa bộ lọc tìm kiếm",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("filtered_projects_scroller"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(projectsList, key = { it.id }) { project ->
                    val isSelected = selectedProj?.id == project.id
                    val vnFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
                    val formattedCost = vnFormat.format(project.totalEstimateCost).replace("₫", "VND")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onProjectSelected(project) }
                            .testTag("project_card_${project.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF1A222C) else Color(0xFF111418)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF1ABC9C) else Color(0xFF232B35)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // First Row: Name and Cloud representation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = project.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF1ABC9C) else Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                
                                // Cloud status indicator using core Core icons
                                Icon(
                                    imageVector = if (project.isSynced) Icons.Default.Done else Icons.Default.Warning,
                                    contentDescription = "Sync state",
                                    tint = if (project.isSynced) Color(0xFF1ABC9C) else Color.DarkGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Second Row: Client info
                            if (project.clientName.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Home, "Client", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(project.clientName, fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            // Third Row: Venue info
                            if (project.venueName.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, "Venue", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = project.venueName,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        maxLines = 1
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Technical Info summary chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                project.tags.split(",").forEach { label ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF242F3E), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(label.trim(), fontSize = 9.sp, color = Color(0xFFCBD5E1))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Money & Badges section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Tổng dự toán (${project.rentalDays} ngày):",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                    Text(
                                        formattedCost,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE74C3C)
                                    )
                                }

                                // Status indicator badge
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = when (project.status) {
                                                "Đã duyệt" -> Color(0xFF27AE60).copy(alpha = 0.2f)
                                                "Đã báo giá" -> Color(0xFF2980B9).copy(alpha = 0.2f)
                                                else -> Color(0xFFD35400).copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .border(
                                            1.dp,
                                            when (project.status) {
                                                "Đã duyệt" -> Color(0xFF2ECC71)
                                                "Đã báo giá" -> Color(0xFF3498DB)
                                                else -> Color(0xFFE67E22)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = project.status,
                                        color = when (project.status) {
                                            "Đã duyệt" -> Color(0xFF2ECC71)
                                            "Đã báo giá" -> Color(0xFF3498DB)
                                            else -> Color(0xFFE67E22)
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Show Quick Action Bar only when card is highlighted/selected
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Duplicate Button using Core Icons
                                    TextButton(
                                        onClick = { viewModel.duplicateProject(project) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color.LightGray)
                                    ) {
                                        Icon(Icons.Default.Share, "Clone", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Sao chép", fontSize = 11.sp)
                                    }
                                    
                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Delete Button
                                    TextButton(
                                        onClick = { viewModel.deleteProject(project) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE74C3C))
                                    ) {
                                        Icon(Icons.Default.Delete, "Delete", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Xóa", fontSize = 11.sp, color = Color(0xFFE74C3C))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Beautiful Material 3 Dialog for project creation
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Tạo Dự Án Sân Khấu Mới", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newProjName,
                            onValueChange = { newProjName = it },
                            label = { Text("Tên Dự Án (Bắt buộc)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )
                        OutlinedTextField(
                            value = newProjClient,
                            onValueChange = { newProjClient = it },
                            label = { Text("Tên Khách Hàng / Đối Tác") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )
                        OutlinedTextField(
                            value = newProjVenue,
                            onValueChange = { newProjVenue = it },
                            label = { Text("Địa Điểm Tổ Chức") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )
                        OutlinedTextField(
                            value = newProjTags,
                            onValueChange = { newProjTags = it },
                            label = { Text("Thẻ (Phân tách bằng dấu phẩy)") },
                            placeholder = { Text("vd: Ngoài trời, Show nhạc") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newProjName.isNotBlank()) {
                                viewModel.createProject(newProjName, newProjClient, newProjVenue, newProjTags)
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1ABC9C))
                    ) {
                        Text("Tạo Bản Vẽ", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Bỏ qua", color = Color.LightGray)
                    }
                },
                containerColor = Color(0xFF1E242B),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
