package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StageProject
import com.example.ui.viewmodel.StageViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MaterialReport(
    project: StageProject,
    viewModel: StageViewModel,
    modifier: Modifier = Modifier,
    canEdit: Boolean
) {
    val context = LocalContext.current
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val scrollState = rememberScrollState()

    val vnFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
            .testTag("material_report_panel")
    ) {
        // Equipment Customizer Panel
        Text(
            text = "I. CẤU HÌNH THIẾT BỊ SẢN XUẤT",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Thay đổi số lượng thiết bị vật tư trực tiếp tại đây:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (!canEdit) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2024), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "⚠️ Vai trò hiện tại của bạn là Khách Hàng (hoặc dự án đã được Duyệt/Mã hóa), nên tính năng chỉnh sửa số lượng loa đèn tạm thời bị khóa chế độ chỉ xem.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111418)),
                border = BorderStroke(1.dp, Color(0xFF232B35)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Speakers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Số loa Line Array Treo: ${project.speakerCount} cái", fontSize = 12.sp, color = Color.White)
                        Text("(${vnFormat.format(project.costPerSpeaker).replace("₫", "").trim()}đ/loa)", fontSize = 10.sp, color = Color.Gray)
                    }
                    Slider(
                        value = project.speakerCount.toFloat(),
                        onValueChange = { viewModel.updateProject(project.copy(speakerCount = it.toInt() / 2 * 2), persist = false) },
                        onValueChangeFinished = { viewModel.persistSelectedProject() },
                        valueRange = 4.0f..16.0f,
                        steps = 5,
                        colors = customizedSliderColors()
                    )

                    // Subwoofers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Số loa siêu trầm Subwoofer: ${project.subCount} cái", fontSize = 12.sp, color = Color.White)
                        Text("(${vnFormat.format(project.costPerSub).replace("₫", "").trim()}đ/loa)", fontSize = 10.sp, color = Color.Gray)
                    }
                    Slider(
                        value = project.subCount.toFloat(),
                        onValueChange = { viewModel.updateProject(project.copy(subCount = it.toInt() / 2 * 2), persist = false) },
                        onValueChangeFinished = { viewModel.persistSelectedProject() },
                        valueRange = 2.0f..12.0f,
                        steps = 4,
                        colors = customizedSliderColors()
                    )

                    // Moving Head Beams
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Đèn Spotlight Moving Beam: ${project.beamCount} cái", fontSize = 12.sp, color = Color.White)
                        Text("(${vnFormat.format(project.costPerBeam).replace("₫", "").trim()}đ/cái)", fontSize = 10.sp, color = Color.Gray)
                    }
                    Slider(
                        value = project.beamCount.toFloat(),
                        onValueChange = { viewModel.updateProject(project.copy(beamCount = it.toInt() / 2 * 2), persist = false) },
                        onValueChangeFinished = { viewModel.persistSelectedProject() },
                        valueRange = 4.0f..24.0f,
                        steps = 9,
                        colors = customizedSliderColors()
                    )

                    // Par Leds
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Đèn màu Par LED Wash: ${project.parCount} cái", fontSize = 12.sp, color = Color.White)
                        Text("(${vnFormat.format(project.costPerPar).replace("₫", "").trim()}đ/cái)", fontSize = 10.sp, color = Color.Gray)
                    }
                    Slider(
                        value = project.parCount.toFloat(),
                        onValueChange = { viewModel.updateProject(project.copy(parCount = it.toInt() / 4 * 4), persist = false) },
                        onValueChangeFinished = { viewModel.persistSelectedProject() },
                        valueRange = 8.0f..40.0f,
                        steps = 7,
                        colors = customizedSliderColors()
                    )

                    // Rental days
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.List, "Days", tint = Color(0xFF1ABC9C), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thời gian thuê sự kiện:", fontSize = 12.sp, color = Color.White)
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(1, 2, 3, 5).forEach { day ->
                                val acts = project.rentalDays == day
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (acts) Color(0xFF16A085) else Color(0xFF232B35))
                                        .clickable { viewModel.updateProject(project.copy(rentalDays = day)) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$day ngày",
                                        color = if (acts) Color.White else Color.LightGray,
                                        fontSize = 11.sp,
                                        fontWeight = if (acts) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Auto Generated Bill of Quantities Sheet
        Text(
            text = "II. BÁO CÁO KHỐI LƯỢNG VÀ BAO GIÁ VẬT TƯ",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Duyệt tính toán bảng đơn giá chi tiết theo ngày dựa trên layout kỹ thuật:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1216)),
            border = BorderStroke(1.dp, Color(0xFF1B232D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Table Headers
                TableHeaderRow()

                Spacer(modifier = Modifier.height(6.dp))

                // Line items
                TableItemRow(name = "Sàn Sân Khấu", detail = "${project.stageWidth}m × ${project.stageDepth}m", qty = "${project.stageArea} m²", cost = project.stageCost, vnFormat = vnFormat)
                TableItemRow(name = "Màn Hình LED", detail = "${project.ledWidth}m × ${project.ledHeight}m", qty = "${project.ledArea} m²", cost = project.ledCost, vnFormat = vnFormat)
                TableItemRow(name = "Khung Giàn Truss", detail = "${project.trussWidth}m × ${project.trussDepth}m", qty = "${project.totalTrussLength.toInt()} m", cost = project.trussCost, vnFormat = vnFormat)
                TableItemRow(name = "Loa Line Array", detail = "Bao gồm dàn treo", qty = "${project.speakerCount} cái", cost = project.speakerCount * project.costPerSpeaker * project.rentalDays, vnFormat = vnFormat)
                TableItemRow(name = "Loa Subwoofer", detail = "Treo hoặc đặt bục", qty = "${project.subCount} cái", cost = project.subCount * project.costPerSub * project.rentalDays, vnFormat = vnFormat)
                TableItemRow(name = "Đèn rọi Beam 350W", detail = "Dòng quay moving head", qty = "${project.beamCount} cái", cost = project.beamCount * project.costPerBeam * project.rentalDays, vnFormat = vnFormat)
                TableItemRow(name = "Đèn Par LED Wash", detail = "Led pha màu lền nền", qty = "${project.parCount} cái", cost = project.parCount * project.costPerPar * project.rentalDays, vnFormat = vnFormat)
                TableItemRow(name = "Đèn Blinder", detail = "Rọi sáng khán phòng", qty = "${project.blinderCount} cái", cost = project.blinderCount * project.costPerBlinder * project.rentalDays, vnFormat = vnFormat)

                Spacer(modifier = Modifier.height(10.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF2C3E50))
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Sum row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TỔNG CỘNG HỢP ĐỒNG:",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        vnFormat.format(project.totalEstimateCost).replace("₫", "VND"),
                        color = Color(0xFFE74C3C),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Export PDF and Share Action Section
        Text(
            text = "III. XUẤT FILE BÁO GIÁ & CHIA SẺ",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Duyệt báo cáo và in trực tiếp thư đề xuất hoặc gửi qua mạng xã hội:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        // PDF Generation Trigger Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16A085).copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, Color(0xFF1ABC9C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Share,
                        "PDF",
                        tint = Color(0xFF1ABC9C),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sinh văn bản báo cáo đính kèm PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Tự động xuất bảng báo giá và chi tiết khối lượng vật liệu theo Layout sân khấu.", fontSize = 11.sp, color = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.exportAndSharePdf(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1ABC9C)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_export_pdf"),
                    enabled = !isGeneratingPdf
                ) {
                    if (isGeneratingPdf) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đang tạo PDF...", color = Color.White, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.Share, "Share", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.6.dp))
                        Text("Xuất & Chia Sẻ Thư Báo Giá (PDF)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C242F), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Hạng mục vật tư", style = MaterialTheme.typography.labelSmall, color = Color.LightGray, modifier = Modifier.weight(2f))
        Text("Thông số", style = MaterialTheme.typography.labelSmall, color = Color.LightGray, modifier = Modifier.weight(1.5f))
        Text("K.Lượng", style = MaterialTheme.typography.labelSmall, color = Color.LightGray, modifier = Modifier.weight(1f))
        Text("Tổng cộng", style = MaterialTheme.typography.labelSmall, color = Color.LightGray, modifier = Modifier.weight(1.5f))
    }
}

@Composable
fun TableItemRow(
    name: String,
    detail: String,
    qty: String,
    cost: Double,
    vnFormat: NumberFormat
) {
    val showValue = vnFormat.format(cost).replace("₫", "").trim()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = Color.White, modifier = Modifier.weight(2f))
        Text(detail, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(1.5f))
        Text(qty, fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.weight(1f))
        Text("${showValue}đ", fontSize = 11.sp, color = Color(0xFF1ABC9C), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
    }
}
