package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.StageProject
import com.example.ui.components.MaterialReport
import com.example.ui.components.ProjectList
import com.example.ui.components.StageCanvas
import com.example.ui.components.UserRoleDashboard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StageViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080B0F))
        ) { innerPadding ->
          StageDesignerApp(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

enum class MobileTab {
  PROJECTS,    // Danh mục dự án (Search, list, create)
  VISUALIZER,  // Bản vẽ 2D/3D (Canvas, slider positioning)
  BILL_OF_MT,  // Báo cáo vật tư (Quotation, quantities list, PDF share)
  ACC_SETTINGS // Phân quyền & Cloud sync (Sync, switch roles, notifications)
}

@Composable
fun StageDesignerApp(
  modifier: Modifier = Modifier,
  viewModel: StageViewModel = viewModel()
) {
  val selectedProject by viewModel.selectedProject.collectAsState()
  val activeRole by viewModel.userRole.collectAsState()
  val selectedElement by viewModel.selectedElement.collectAsState()

  // Controls mobile tab navigation state
  var currentMobileTab by remember { mutableStateOf(MobileTab.PROJECTS) }

  // Auto-redirect to canvas on list item selection on mobile
  val onProjectSelect: (StageProject) -> Unit = { proj ->
    viewModel.selectProject(proj)
    currentMobileTab = MobileTab.VISUALIZER
  }

  // Can the current user role edit features? (Khách Hàng role or approved locked design limits edits)
  val canEdit = activeRole != "Khách Hàng" && selectedProject?.status != "Đã duyệt"

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF080B0F))
  ) {
    val isTablet = maxWidth >= 860.dp

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      
      // Top elegant branding header
      StageAppHeader(activeRole = activeRole, synced = selectedProject?.isSynced == true)

      if (isTablet) {
        // --- 1. Tablet Dual/Triple-Pane Workspace ---
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(14.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Left Sidebar: Projects directory
          Card(
            modifier = Modifier
              .width(280.dp)
              .fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111418)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232B35))
          ) {
            Box(modifier = Modifier.padding(12.dp)) {
              ProjectList(
                viewModel = viewModel,
                onProjectSelected = { viewModel.selectProject(it) }
              )
            }
          }

          // Center visualizer model panel
          Column(
            modifier = Modifier
              .weight(1.3f)
              .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            val proj = selectedProject
            if (proj != null) {
              StageCanvas(
                project = proj,
                viewModel = viewModel,
                selectedElement = selectedElement,
                canEdit = canEdit,
                modifier = Modifier.weight(1f)
              )
            } else {
              VisualizerEmptyPlaceholder()
            }
          }

          // Right Sidebar: Auto BOM costing table, PDF sharing and notifications
          Card(
            modifier = Modifier
              .width(360.dp)
              .fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111418)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232B35))
          ) {
            val proj = selectedProject
            if (proj != null) {
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(12.dp)
              ) {
                // Split tabs inside Right sidebar to stay incredibly neat
                var sidebarTabState by remember { mutableStateOf(0) } // 0 = BOQ Materials, 1 = Config Settings
                
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E242B), RoundedCornerShape(6.dp))
                    .padding(3.dp),
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (sidebarTabState == 0) Color(0xFF1ABC9C) else Color.Transparent)
                      .clickable { sidebarTabState = 0 }
                      .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("BÁO GIÁ VÀ VẬT TƯ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (sidebarTabState == 0) Color.White else Color.Gray)
                  }
                  
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (sidebarTabState == 1) Color(0xFF1ABC9C) else Color.Transparent)
                      .clickable { sidebarTabState = 1 }
                      .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("ĐỒNG BỘ VÀ PHÂN QUYỀN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (sidebarTabState == 1) Color.White else Color.Gray)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (sidebarTabState == 0) {
                  MaterialReport(
                    project = proj,
                    viewModel = viewModel,
                    canEdit = canEdit,
                    modifier = Modifier.weight(1f)
                  )
                } else {
                  UserRoleDashboard(
                    project = proj,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            } else {
              RightSidebarEmptyPlaceholder()
            }
          }
        }
      } else {
        // --- 2. Mobile Bottom-Navigation Screen Layout ---
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(10.dp)
        ) {
          when (currentMobileTab) {
            MobileTab.PROJECTS -> {
              ProjectList(
                viewModel = viewModel,
                onProjectSelected = onProjectSelect,
                modifier = Modifier.fillMaxSize()
              )
            }
            MobileTab.VISUALIZER -> {
              val proj = selectedProject
              if (proj != null) {
                StageCanvas(
                  project = proj,
                  viewModel = viewModel,
                  selectedElement = selectedElement,
                  canEdit = canEdit,
                  modifier = Modifier.fillMaxSize()
                )
              } else {
                VisualizerEmptyPlaceholder()
              }
            }
            MobileTab.BILL_OF_MT -> {
              val proj = selectedProject
              if (proj != null) {
                MaterialReport(
                  project = proj,
                  viewModel = viewModel,
                  canEdit = canEdit,
                  modifier = Modifier.fillMaxSize()
                )
              } else {
                RightSidebarEmptyPlaceholder()
              }
            }
            MobileTab.ACC_SETTINGS -> {
              val proj = selectedProject
              if (proj != null) {
                UserRoleDashboard(
                  project = proj,
                  viewModel = viewModel,
                  modifier = Modifier.fillMaxSize()
                )
              } else {
                RightSidebarEmptyPlaceholder()
              }
            }
          }
        }

        // Standard M3 Bottom Navigation Bar for Mobile
        NavigationBar(
          containerColor = Color(0xFF111418),
          tonalElevation = 8.dp,
          windowInsets = WindowInsets.safeDrawing,
          modifier = Modifier.testTag("mobile_bottom_nav")
        ) {
          NavigationBarItem(
            selected = currentMobileTab == MobileTab.PROJECTS,
            onClick = { currentMobileTab = MobileTab.PROJECTS },
            icon = { Icon(Icons.Default.Home, "Dự án") },
            label = { Text("Dự Án", fontSize = 10.sp) },
            colors = customizedNavColors()
          )
          NavigationBarItem(
            selected = currentMobileTab == MobileTab.VISUALIZER,
            onClick = { currentMobileTab = MobileTab.VISUALIZER },
            icon = { Icon(Icons.Default.Menu, "Bản vẽ") },
            label = { Text("Bản Vẽ", fontSize = 10.sp) },
            colors = customizedNavColors()
          )
          NavigationBarItem(
            selected = currentMobileTab == MobileTab.BILL_OF_MT,
            onClick = { currentMobileTab = MobileTab.BILL_OF_MT },
            icon = { Icon(Icons.AutoMirrored.Filled.List, "Báo giá") },
            label = { Text("Vật Tư", fontSize = 10.sp) },
            colors = customizedNavColors()
          )
          NavigationBarItem(
            selected = currentMobileTab == MobileTab.ACC_SETTINGS,
            onClick = { currentMobileTab = MobileTab.ACC_SETTINGS },
            icon = { Icon(Icons.Default.Settings, "Bảo mật") },
            label = { Text("Đồng Bộ", fontSize = 10.sp) },
            colors = customizedNavColors()
          )
        }
      }
    }
  }
}

@Composable
fun StageAppHeader(activeRole: String, synced: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF111418))
      .border(0.dp, Color(0xFF232B35))
      .padding(horizontal = 14.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color(0xFF1ABC9C)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Build, "Enterprise logo", tint = Color.White, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          "Thiết Kế Sân Khấu Pro",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          "STAGEBUILDER AUTOMATED SYSTEMS",
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF1ABC9C),
          letterSpacing = 1.sp
        )
      }
    }

    // Role display and SSL security padlock badge
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242D)),
      shape = RoundedCornerShape(6.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Settings,
          "User",
          tint = Color(0xFF1ABC9C),
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = activeRole,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}

@Composable
fun VisualizerEmptyPlaceholder() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF111418))
      .border(1.dp, Color(0xFF232B35), RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
      Icon(Icons.Default.Menu, "No project selected", tint = Color.DarkGray, modifier = Modifier.size(56.dp))
      Spacer(modifier = Modifier.height(12.dp))
      Text("Vui lòng chọn một Dự án", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
      Text(
        text = "Nhấp chuột chọn thiết kế sân khấu sẵn có bên trái hoặc nhấp 'Tạo Mới' để vẽ layout 2.5D ngay tức thì.",
        fontSize = 11.sp,
        color = Color.Gray,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = Modifier.widthIn(max = 280.dp)
      )
    }
  }
}

@Composable
fun RightSidebarEmptyPlaceholder() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(Icons.AutoMirrored.Filled.List, "Empty data", tint = Color.DarkGray, modifier = Modifier.size(44.dp))
      Spacer(modifier = Modifier.height(10.dp))
      Text("Thông số & Dự Toán Vật Tư", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
      Text("Bảng báo giá dự án, vai trò quản lý cấp cao và nhật ký SSL sẽ cập nhật đầy đủ tại đây khi có bản vẽ được nạp.", fontSize = 10.5.sp, color = Color.DarkGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
  }
}

@Composable
fun customizedNavColors() = NavigationBarItemDefaults.colors(
  selectedIconColor = Color.White,
  selectedTextColor = Color(0xFF1ABC9C),
  indicatorColor = Color(0xFF16A085),
  unselectedIconColor = Color.Gray,
  unselectedTextColor = Color.Gray
)
