package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserRoleDashboard(
    project: StageProject,
    viewModel: StageViewModel,
    modifier: Modifier = Modifier
) {
    val activeRole by viewModel.userRole.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val notificationsList by viewModel.notifications.collectAsState()

    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("role_dashboard_panel"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // 1. SSL Cloud Sync Panel
        Text(
            text = "ĐỒNG BỘ ĐÁM MÂY THỜI GIAN THỰC",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1216)),
            border = BorderStroke(1.dp, Color(0xFF1B232D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (project.isSynced) Icons.Default.Check else Icons.Default.Share,
                            contentDescription = "Sync",
                            tint = if (project.isSynced) Color(0xFF1ABC9C) else Color(0xFFE67E22),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (project.isSynced) "Trạng thái: ĐÃ ĐỒNG BỘ" else "Trạng thái: CHƯA ĐỒNG BỘ BẢN MỚI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (project.isSynced) Color(0xFF1ABC9C) else Color(0xFFE67E22)
                            )
                            if (project.isSynced) {
                                val syncDate = sdf.format(Date(project.lastSyncTime))
                                Text("Lịch sử đồng bộ cuối: Lúc $syncDate", fontSize = 10.sp, color = Color.Gray)
                            } else {
                                Text("Các chỉnh sửa vị trí/loa đèn của bạn đang lưu ở máy local", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.triggerCloudSync() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50)),
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("btn_trigger_sync")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.5.dp, color = Color.White)
                        } else {
                            Icon(Icons.Default.Refresh, "Sync", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đồng bộ", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 2. Multi-user Role simulation selector
        Text(
            text = "HỆ THỐNG PHÂN QUYỀN TRUY CẬP (SIMULATORY)",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111418)),
            border = BorderStroke(1.dp, Color(0xFF232B35)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Nhấp để giả lập chuyển vai trò bảo mật của bạn:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                listOf("Kỹ Sư Thiết Kế", "Trưởng Phòng Mỹ Thuật", "Khách Hàng").forEach { roleName ->
                    val isSelected = activeRole == roleName
                    val roleIcon = when (roleName) {
                        "Trưởng Phòng Mỹ Thuật" -> Icons.Default.Lock
                        "Kỹ Sư Thiết Kế" -> Icons.Default.Star
                        else -> Icons.Default.Person
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF1ABC9C).copy(alpha = 0.1f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF1ABC9C) else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { viewModel.userRole.value = roleName }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(roleIcon, "Role", tint = if (isSelected) Color(0xFF1ABC9C) else Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    roleName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                                Text(
                                    text = when (roleName) {
                                        "Trưởng Phòng Mỹ Thuật" -> "Quyền tối cao: Có thể Sửa thông số, Di dời và Phê Duyệt trạng thái thiết kế."
                                        "Kỹ Sư Thiết Kế" -> "Quyền vận hành: Di dời layout, Thêm bớt số loa đèn và Gửi báo giá PDF."
                                        else -> "Quyền xem: Chỉ xem trực quan, xem báo giá, hoàn toàn bị khóa chỉnh sửa."
                                    },
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, "Active", tint = Color(0xFF1ABC9C), modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        // 3. Simulated Push Notifications Timeline list
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, "Alerts", tint = Color(0xFFE74C3C), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LỊCH SỬ THÔNG BÁO ĐẨY (PUSH ALERTS LOG)",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            if (notificationsList.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearNotifications() }) {
                    Text("Xóa hết", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1216)),
            border = BorderStroke(1.dp, Color(0xFF1B232D)),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (notificationsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Chưa có sự kiện thông báo đẩy nào ghi nhận.", fontSize = 11.sp, color = Color.DarkGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notificationsList, key = { it.id }) { alert ->
                        val alertTime = sdf.format(Date(alert.timestamp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF161B22), RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = when (alert.tag) {
                                            "ĐỒNG BỘ" -> Color(0xFF1ABC9C).copy(alpha = 0.2f)
                                            "PHÂN QUYỀN" -> Color(0xFFF1C40F).copy(alpha = 0.2f)
                                            "BÁO GIÁ" -> Color(0xFF2980B9).copy(alpha = 0.2f)
                                            else -> Color(0x337F8C8D)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    alert.tag,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (alert.tag) {
                                        "ĐỒNG BỘ" -> Color(0xFF1ABC9C)
                                        "PHÂN QUYỀN" -> Color(0xFFF1C40F)
                                        "BÁO GIÁ" -> Color(0xFF3498DB)
                                        else -> Color.Gray
                                    }
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(alert.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(alert.message, fontSize = 10.sp, color = Color.LightGray)
                            }
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(alertTime, fontSize = 9.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
