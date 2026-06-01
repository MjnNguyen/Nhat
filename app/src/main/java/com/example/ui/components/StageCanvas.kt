package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StageProject
import com.example.ui.viewmodel.SelectedStageElement
import com.example.ui.viewmodel.StageViewModel

@Composable
fun StageCanvas(
    project: StageProject,
    viewModel: StageViewModel,
    modifier: Modifier = Modifier,
    selectedElement: SelectedStageElement,
    canEdit: Boolean
) {
    // Pulsing light beam animations for live stage feel
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_beams")
    val beamPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val beamAngleSweep by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beam_sweep"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111418))
            .border(1.dp, Color(0xFF232B35), RoundedCornerShape(12.dp))
            .padding(12.dp)
            .testTag("stage_canvas_container")
    ) {
        // Stage Canvas Header and Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BẢN VẼ TỰ ĐỘNG REAL-TIME 2D/3D (Z-PROJECTION)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF16A085)
                )
                Text(
                    text = "Duyệt qua các thiết bị & tinh chỉnh vị trí/kích thước bên dưới:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
            
            // Neon Status Light
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (project.isSynced) Color(0xFF1ABC9C) else Color(0xFFE74C3C))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (project.isSynced) "SYNCED" else "INTERNAL",
                    fontSize = 10.sp,
                    color = if (project.isSynced) Color(0xFF1ABC9C) else Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Selector Chips using standard SuggestionChip Material 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Chọn thiết bị:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            SuggestionChip(
                onClick = { viewModel.selectedElement.value = SelectedStageElement.StageFloor },
                label = { Text("Sàn Sân Khấu", fontSize = 11.sp) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (selectedElement == SelectedStageElement.StageFloor) Color(0xFF16A085).copy(alpha = 0.2f) else Color.Transparent,
                    labelColor = if (selectedElement == SelectedStageElement.StageFloor) Color(0xFF1ABC9C) else Color.Gray
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = if (selectedElement == SelectedStageElement.StageFloor) Color(0xFF1ABC9C) else Color(0xFF2C3E50)
                )
            )

            SuggestionChip(
                onClick = { viewModel.selectedElement.value = SelectedStageElement.LedScreen },
                label = { Text("Màn LED", fontSize = 11.sp) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (selectedElement == SelectedStageElement.LedScreen) Color(0xFF16A085).copy(alpha = 0.2f) else Color.Transparent,
                    labelColor = if (selectedElement == SelectedStageElement.LedScreen) Color(0xFF1ABC9C) else Color.Gray
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = if (selectedElement == SelectedStageElement.LedScreen) Color(0xFF1ABC9C) else Color(0xFF2C3E50)
                )
            )

            SuggestionChip(
                onClick = { viewModel.selectedElement.value = SelectedStageElement.TrussFrame },
                label = { Text("Giàn Truss", fontSize = 11.sp) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (selectedElement == SelectedStageElement.TrussFrame) Color(0xFF16A085).copy(alpha = 0.2f) else Color.Transparent,
                    labelColor = if (selectedElement == SelectedStageElement.TrussFrame) Color(0xFF1ABC9C) else Color.Gray
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = if (selectedElement == SelectedStageElement.TrussFrame) Color(0xFF1ABC9C) else Color(0xFF2C3E50)
                )
            )

            SuggestionChip(
                onClick = { viewModel.selectedElement.value = SelectedStageElement.SpeakerLeft },
                label = { Text("Loa Trái/Phải", fontSize = 11.sp) },
                colors = SuggestionChipColorsUnified(selectedElement),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = if (selectedElement is SelectedStageElement.SpeakerLeft || selectedElement is SelectedStageElement.SpeakerRight) Color(0xFF1ABC9C) else Color(0xFF2C3E50)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Visual Canvas Workspace
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF080A0D))
                .border(1.dp, Color(0xFF1C242F), RoundedCornerShape(8.dp))
                .clipToBounds()
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("interactive_stage_canvas")
            ) {
                // 1. Draw grid background & boundaries (chế độ tối giảm mỏi mắt)
                drawStageGrid(widthPx, heightPx)

                // 2. Draw metallic overhead truss support pillars and beams
                val trussX = (project.trussXPercent / 100.0f) * widthPx
                val trussY = (project.trussYPercent / 100.0f) * heightPx
                val trussW = (project.trussWidth / 20.0f) * widthPx
                val trussH = (project.trussHeight / 12.0f) * heightPx
                drawOverheadTruss(
                    x = trussX, y = trussY, w = trussW, h = trussH,
                    isSelected = selectedElement == SelectedStageElement.TrussFrame
                )

                // 3. Draw LED Back wall
                val ledX = (project.ledXPercent / 100.0f) * widthPx
                val ledY = (project.ledYPercent / 100.0f) * heightPx
                val ledW = (project.ledWidth / 20.0f) * widthPx
                val ledH = (project.ledHeight / 10.0f) * heightPx
                drawLedWall(
                    x = ledX, y = ledY, w = ledW, h = ledH,
                    isSelected = selectedElement == SelectedStageElement.LedScreen,
                    beamAlpha = beamPulseAlpha
                )

                // 4. Draw Stage Floor platform
                val stageW = (project.stageWidth / 22.0f) * widthPx
                val stageH = (project.stageDepth / 14.0f) * heightPx
                val stageY = heightPx * 0.70f // Standard deck base
                drawStageFloor(
                    widthPx = widthPx, heightPx = heightPx,
                    w = stageW, h = stageH, baseHeight = stageY, thickness = project.stageHeight * 12f,
                    isSelected = selectedElement == SelectedStageElement.StageFloor
                )

                // 5. Draw Subs underneath the stage floor
                drawSubwoofers(stageW = stageW, stageBaseY = stageY, numSubs = project.subCount)

                // 6. Draw Hanging Line-Array Left and Right speakers
                val spLX = (project.speakerLeftXPercent / 100.0f) * widthPx
                val spLY = (project.speakerLeftYPercent / 100.0f) * heightPx
                val spRX = (project.speakerRightXPercent / 100.0f) * widthPx
                val spRY = (project.speakerRightYPercent / 100.0f) * heightPx
                
                drawSpeakerArray(
                    x = spLX, y = spLY, count = project.speakerCount / 2, isLeft = true,
                    isSelected = selectedElement == SelectedStageElement.SpeakerLeft
                )
                drawSpeakerArray(
                    x = spRX, y = spRY, count = project.speakerCount / 2, isLeft = false,
                    isSelected = selectedElement == SelectedStageElement.SpeakerRight
                )

                // 7. Draw Moving Beam Lights and dynamic lighting rays!
                drawBeamLightsAndShow(
                    widthPx = widthPx, stageBaseY = stageY, stageW = stageW,
                    beamCount = project.beamCount, parCount = project.parCount, blinderCount = project.blinderCount,
                    pulseAlpha = beamPulseAlpha, angleSweep = beamAngleSweep
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Customization Sliders
        if (selectedElement != SelectedStageElement.None) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E384D))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    val titleLabel = when (selectedElement) {
                        SelectedStageElement.StageFloor -> "Sàn Sân Khấu (Kích thước hiện tại: ${project.stageWidth}m × ${project.stageDepth}m × ${project.stageHeight}m)"
                        SelectedStageElement.LedScreen -> "Màn LED (Kích thước: ${project.ledWidth}m × ${project.ledHeight}m)"
                        SelectedStageElement.TrussFrame -> "Giàn Truss Treo Nhôm (Kích thước: ${project.trussWidth}m × ${project.trussDepth}m)"
                        SelectedStageElement.SpeakerLeft -> "Loa Trái (Vị trí treo: ${project.speakerLeftXPercent.toInt()}% X)"
                        SelectedStageElement.SpeakerRight -> "Loa Phải (Vị trí treo: ${project.speakerRightXPercent.toInt()}% X)"
                        SelectedStageElement.None -> ""
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = titleLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1ABC9C),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        )
                        if (canEdit && selectedElement != SelectedStageElement.StageFloor) {
                            Text(
                                text = "Kéo sliders để căn chỉnh vị trí thiết bị",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!canEdit) {
                        Text(
                            text = "💡 Lưu ý: Trạng thái của dự án là '${project.status}' hoặc tài khoản của bạn là 'Khách Hàng' (Read-only), nên tính năng chỉnh sửa thanh trượt chỉnh vị trí này đã tạm ẩn.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.DarkGray
                        )
                    } else {
                        when (selectedElement) {
                            SelectedStageElement.StageFloor -> {
                                // Width & Depth customization
                                Text("Chiều Rộng sàn sân khấu: ${project.stageWidth}m", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.stageWidth,
                                    onValueChange = { viewModel.updateProject(project.copy(stageWidth = Math.round(it * 10f) / 10f), persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 6.0f..24.0f,
                                    colors = customizedSliderColors()
                                )
                                Text("Chiều Sâu sàn sân khấu: ${project.stageDepth}m", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.stageDepth,
                                    onValueChange = { viewModel.updateProject(project.copy(stageDepth = Math.round(it * 10f) / 10f), persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 4.0f..16.0f,
                                    colors = customizedSliderColors()
                                )
                            }
                            SelectedStageElement.LedScreen -> {
                                // LED position coordinates and height
                                Text("Vị trí trục ngang X: ${project.ledXPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.ledXPercent,
                                    onValueChange = { viewModel.updateElementPosition(it, project.ledYPercent, persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 20f..80f,
                                    colors = customizedSliderColors()
                                )
                                Text("Vị trí trục dọc Y: ${project.ledYPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.ledYPercent,
                                    onValueChange = { viewModel.updateElementPosition(project.ledXPercent, it, persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 10f..60f,
                                    colors = customizedSliderColors()
                                )
                                // Width
                                Text("Chiều Rộng màn LED: ${project.ledWidth}m", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.ledWidth,
                                    onValueChange = { viewModel.updateProject(project.copy(ledWidth = Math.round(it * 10f) / 10f), persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 4.0f..20.0f,
                                    colors = customizedSliderColors()
                                )
                            }
                            SelectedStageElement.TrussFrame -> {
                                Text("Vị trí trục ngang X: ${project.trussXPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.trussXPercent,
                                    onValueChange = { viewModel.updateElementPosition(it, project.trussYPercent, persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 20f..80f,
                                    colors = customizedSliderColors()
                                )
                                Text("Vị trí đặt giàn dọc Y: ${project.trussYPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.trussYPercent,
                                    onValueChange = { viewModel.updateElementPosition(project.trussXPercent, it, persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 10f..70f,
                                    colors = customizedSliderColors()
                                )
                            }
                            SelectedStageElement.SpeakerLeft -> {
                                Text("Điều chỉnh vị trí loa bên Trái X: ${project.speakerLeftXPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.speakerLeftXPercent,
                                    onValueChange = { viewModel.updateElementPosition(it, project.speakerLeftYPercent, persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 5f..40f,
                                    colors = customizedSliderColors()
                                )
                                Text("Chiều cao treo móc Y: ${project.speakerLeftYPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.speakerLeftYPercent,
                                    onValueChange = { viewModel.updateProject(project.copy(speakerLeftYPercent = it, speakerRightYPercent = it), persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 10f..80f,
                                    colors = customizedSliderColors()
                                )
                            }
                            SelectedStageElement.SpeakerRight -> {
                                Text("Điều chỉnh vị trí loa bên Phải X: ${project.speakerRightXPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.speakerRightXPercent,
                                    onValueChange = { viewModel.updateElementPosition(it, project.speakerRightYPercent, persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 60f..95f,
                                    colors = customizedSliderColors()
                                )
                                Text("Chiều cao treo móc Y: ${project.speakerRightYPercent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Slider(
                                    value = project.speakerRightYPercent,
                                    onValueChange = { viewModel.updateProject(project.copy(speakerRightYPercent = it, speakerLeftYPercent = it), persist = false) },
                                    onValueChangeFinished = { viewModel.persistSelectedProject() },
                                    valueRange = 10f..80f,
                                    colors = customizedSliderColors()
                                )
                            }
                            SelectedStageElement.None -> {}
                        }
                        
                        // Joypad Manual controls using core arrow icons
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Căn chỉnh nhanh: ", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            IconButton(
                                onClick = { 
                                    when(selectedElement) {
                                        SelectedStageElement.LedScreen -> viewModel.updateElementPosition(project.ledXPercent - 4f, project.ledYPercent)
                                        SelectedStageElement.TrussFrame -> viewModel.updateElementPosition(project.trussXPercent - 4f, project.trussYPercent)
                                        SelectedStageElement.SpeakerLeft -> viewModel.updateElementPosition(project.speakerLeftXPercent - 4f, project.speakerLeftYPercent)
                                        SelectedStageElement.SpeakerRight -> viewModel.updateElementPosition(project.speakerRightXPercent - 4f, project.speakerRightYPercent)
                                        else -> {}
                                    }
                                },
                                modifier = Modifier.size(24.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF141A22))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Left", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { 
                                    when(selectedElement) {
                                        SelectedStageElement.LedScreen -> viewModel.updateElementPosition(project.ledXPercent + 4f, project.ledYPercent)
                                        SelectedStageElement.TrussFrame -> viewModel.updateElementPosition(project.trussXPercent + 4f, project.trussYPercent)
                                        SelectedStageElement.SpeakerLeft -> viewModel.updateElementPosition(project.speakerLeftXPercent + 4f, project.speakerLeftYPercent)
                                        SelectedStageElement.SpeakerRight -> viewModel.updateElementPosition(project.speakerRightXPercent + 4f, project.speakerRightYPercent)
                                        else -> {}
                                    }
                                },
                                modifier = Modifier.size(24.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF141A22))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Right", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        } else {
            // Friendly tips banner using core Star icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF15191E))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        "Tip",
                        tint = Color(0xFF16A085),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "💡 Mẹo: Nhấn chọn Sàn Sân Khấu, Màn hình LED, Giàn Truss hoặc Dàn Loa để mở bộ điều hướng vị trí chi tiết.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun customizedSliderColors() = SliderDefaults.colors(
    thumbColor = Color(0xFF1ABC9C),
    activeTrackColor = Color(0xFF16A085),
    inactiveTrackColor = Color(0xFF2C3E50)
)

@Composable
fun SuggestionChipColorsUnified(selectedElement: SelectedStageElement) = SuggestionChipDefaults.suggestionChipColors(
    containerColor = if (selectedElement is SelectedStageElement.SpeakerLeft || selectedElement is SelectedStageElement.SpeakerRight) Color(0xFF16A085).copy(alpha = 0.2f) else Color.Transparent,
    labelColor = if (selectedElement is SelectedStageElement.SpeakerLeft || selectedElement is SelectedStageElement.SpeakerRight) Color(0xFF1ABC9C) else Color.Gray
)

// Canvas Drawing Helper Extensions
fun DrawScope.drawStageGrid(w: Float, h: Float) {
    val gridColor = Color(0xFF11171E)
    val spacing = 30f
    
    // Horizontal gridlines
    for (y in 0..(h / spacing).toInt()) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y * spacing),
            end = Offset(w, y * spacing),
            strokeWidth = 1f
        )
    }
    // Vertical gridlines
    for (x in 0..(w / spacing).toInt()) {
        drawLine(
            color = gridColor,
            start = Offset(x * spacing, 0f),
            end = Offset(x * spacing, h),
            strokeWidth = 1f
        )
    }
}

fun DrawScope.drawOverheadTruss(x: Float, y: Float, w: Float, h: Float, isSelected: Boolean) {
    val trussW = w.coerceIn(120f, 600f)
    val trussH = h.coerceIn(80f, 400f)
    val startX = x - (trussW / 2)
    val startY = y - (trussH / 2)
    
    val trussOutline = Color(0xFF7F8C8D)
    val trussInner = Color(0xFFBDC3C7)

    // Left pillar
    drawRect(
        color = trussOutline,
        topLeft = Offset(startX, startY),
        size = Size(10f, trussH)
    )
    // Left diagonal bracing lines
    for (step in 0..10) {
        val nextP = step * (trussH / 10)
        drawLine(
            color = trussInner,
            start = Offset(startX, startY + nextP),
            end = Offset(startX + 10f, startY + nextP + 10f),
            strokeWidth = 1f
        )
    }

    // Right pillar
    drawRect(
        color = trussOutline,
        topLeft = Offset(startX + trussW - 10f, startY),
        size = Size(10f, trussH)
    )
    // Right diagonal bracings
    for (step in 0..10) {
        val nextP = step * (trussH / 10)
        drawLine(
            color = trussInner,
            start = Offset(startX + trussW - 10f, startY + nextP),
            end = Offset(startX + trussW, startY + nextP + 10f),
            strokeWidth = 1f
        )
    }

    // Top horizontal crossbar truss span
    drawRect(
        color = trussOutline,
        topLeft = Offset(startX, startY),
        size = Size(trussW, 10f)
    )
    for (step in 0..(trussW / 20).toInt()) {
        drawLine(
            color = trussInner,
            start = Offset(startX + (step * 20), startY),
            end = Offset(startX + (step * 20) + 10f, startY + 10f),
            strokeWidth = 1.5f
        )
    }

    // Selection highlight glow
    if (isSelected) {
        drawRoundRect(
            color = Color(0xFF00FFCC),
            topLeft = Offset(startX - 6f, startY - 6f),
            size = Size(trussW + 12f, trussH + 12f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            ),
            cornerRadius = CornerRadius(4f)
        )
    }
}

fun DrawScope.drawLedWall(x: Float, y: Float, w: Float, h: Float, isSelected: Boolean, beamAlpha: Float) {
    val ledW = w.coerceIn(100f, 450f)
    val ledH = h.coerceIn(80f, 240f)
    val startX = x - (ledW / 2)
    val startY = y - (ledH / 2)

    // Base wall background (shadow depth)
    drawRoundRect(
        color = Color(0xFF1C2833),
        topLeft = Offset(startX, startY),
        size = Size(ledW, ledH),
        cornerRadius = CornerRadius(6f)
    )

    // Beautiful Gradient screen colors simulating visual projection
    val screenGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF34495E), Color(0xFF1ABC9C), Color(0xFF8E44AD), Color(0xFF2C3E50)),
        start = Offset(startX, startY),
        end = Offset(startX + ledW, startY + ledH)
    )
    drawRoundRect(
        brush = screenGradient,
        topLeft = Offset(startX + 4f, startY + 4f),
        size = Size(ledW - 8f, ledH - 8f),
        cornerRadius = CornerRadius(4f)
    )

    // Draw tech grid matrix lines on LED
    val meshColor = Color(0x33000000)
    for (stepX in 1..8) {
        val linesX = startX + (stepX * (ledW / 9))
        drawLine(meshColor, Offset(linesX, startY), Offset(linesX, startY + ledH))
    }
    for (stepY in 1..4) {
        val linesY = startY + (stepY * (ledH / 5))
        drawLine(meshColor, Offset(startX, linesY), Offset(startX + ledW, linesY))
    }

    // Visual glowing neon frame
    drawRoundRect(
        color = Color(0xFF2E4053),
        topLeft = Offset(startX, startY),
        size = Size(ledW, ledH),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
        cornerRadius = CornerRadius(6f)
    )

    if (isSelected) {
        drawRoundRect(
            color = Color(0xFF2DFA9D),
            topLeft = Offset(startX - 6f, startY - 6f),
            size = Size(ledW + 12f, ledH + 12f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            ),
            cornerRadius = CornerRadius(8f)
        )
    }
}

fun DrawScope.drawStageFloor(
    widthPx: Float, heightPx: Float,
    w: Float, h: Float, baseHeight: Float, thickness: Float,
    isSelected: Boolean
) {
    val stageW = w.coerceIn(180f, 550f)
    val stageH = h.coerceIn(100f, 250f)
    
    val leftX = (widthPx - stageW) / 2
    val rightX = (widthPx + stageW) / 2
    val topY = baseHeight
    val bottomY = baseHeight + stageH

    // Isometric coordinates for beautiful depth visual
    // Stage face (Top surface)
    val stagePath = Path().apply {
        moveTo(leftX, topY)                        // Top-Left corner
        lineTo(rightX, topY)                       // Top-Right corner
        lineTo(rightX + 40f, bottomY)              // Bottom-Right perspective
        lineTo(leftX - 40f, bottomY)               // Bottom-Left perspective
        close()
    }
    
    // Draw top floor with deep slate gray wood deck color
    drawPath(
        path = stagePath,
        color = Color(0xFF212F3D)
    )

    // Red carpet outline inside stage top
    val carpetPath = Path().apply {
        moveTo(leftX + 20f, topY + 10f)
        lineTo(rightX - 20f, topY + 10f)
        lineTo(rightX + 15f, bottomY - 10f)
        lineTo(leftX - 15f, bottomY - 10f)
        close()
    }
    drawPath(
        path = carpetPath,
        color = Color(0xFF7B241C) // Professional deep red carpet
    )

    // Stage Front Facade (Front thickness wood skirting)
    val facadeHeight = thickness.coerceIn(10f, 50f)
    val facadePath = Path().apply {
        moveTo(leftX - 40f, bottomY)
        lineTo(rightX + 40f, bottomY)
        lineTo(rightX + 40f, bottomY + facadeHeight)
        lineTo(leftX - 40f, bottomY + facadeHeight)
        close()
    }
    drawPath(
        path = facadePath,
        color = Color(0xFF1A1A1A)
    )

    // Stage steps lines
    for (stepX in 0..6) {
        val stepPos = (leftX - 40) + stepX * ((stageW + 80) / 6f)
        drawLine(
            color = Color(0x66FFFFFF),
            start = Offset(stepPos, bottomY),
            end = Offset(stepPos, bottomY + facadeHeight),
            strokeWidth = 1f
        )
    }

    if (isSelected) {
        // Outline entire perspective stage
        val highlightPath = Path().apply {
            moveTo(leftX - 3f, topY - 3f)
            lineTo(rightX + 3f, topY - 3f)
            lineTo(rightX + 43f, bottomY + facadeHeight + 3f)
            lineTo(leftX - 43f, bottomY + facadeHeight + 3f)
            close()
        }
        drawPath(
            path = highlightPath,
            color = Color(0xFF1ABC9C),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )
        )
    }
}

fun DrawScope.drawSubwoofers(stageW: Float, stageBaseY: Float, numSubs: Int) {
    val subW = 20f
    val subH = 14f
    val spacing = 25f
    val totalSubsWidth = (numSubs * subW) + ((numSubs - 1) * spacing)
    val startX = (size.width - totalSubsWidth) / 2
    
    for (i in 0 until numSubs) {
        val sX = startX + (i * (subW + spacing))
        val sY = stageBaseY + 12f // Below top surface perspective
        
        // Sub cabinet
        drawRoundRect(
            color = Color(0xFF17202A),
            topLeft = Offset(sX, sY),
            size = Size(subW, subH),
            cornerRadius = CornerRadius(2f)
        )
        // Speaker sub cones detail circles
        drawCircle(
            color = Color(0xFF11171D),
            radius = subH / 3f,
            center = Offset(sX + subW / 2, sY + subH / 2)
        )
    }
}

fun DrawScope.drawSpeakerArray(x: Float, y: Float, count: Int, isLeft: Boolean, isSelected: Boolean) {
    val arrCount = count.coerceIn(2, 8)
    val width = 16f
    val height = 11f
    val space = 2f

    // Draw flying truss rigging steel wire holding the arrays
    drawLine(
        color = Color(0xFFBDC3C7),
        start = Offset(x, 0f),
        end = Offset(x, y),
        strokeWidth = 1.5f
    )

    // Draw stacked array modules
    for (i in 0 until arrCount) {
        val modY = y + (i * (height + space))
        val modAngleXOffset = if (isLeft) i * 0.8f else -i * 0.8f // curved arrays
        
        // Single Speaker block
        drawRoundRect(
            color = Color(0xFF1E2833),
            topLeft = Offset(x - (width / 2) + modAngleXOffset, modY),
            size = Size(width, height),
            cornerRadius = CornerRadius(1f)
        )
        // Grill color mesh line
        drawLine(
            color = Color(0xFF0F161E),
            start = Offset(x - (width / 2) + 2f + modAngleXOffset, modY + 2f),
            end = Offset(x + (width / 2) - 2f + modAngleXOffset, modY + height - 2f),
            strokeWidth = 1.5f
        )
    }

    // Target Selection highlight
    if (isSelected) {
        val totalH = arrCount * (height + space)
        drawRoundRect(
            color = Color(0xFF00E6FF),
            topLeft = Offset(x - (width / 2) - 4f, y - 4f),
            size = Size(width + 12f, totalH + 8f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            ),
            cornerRadius = CornerRadius(3f)
        )
    }
}

fun DrawScope.drawBeamLightsAndShow(
    widthPx: Float, stageBaseY: Float, stageW: Float,
    beamCount: Int, parCount: Int, blinderCount: Int,
    pulseAlpha: Float, angleSweep: Float
) {
    // We draw beam moving assemblies on stage rear top level
    val bCount = beamCount.coerceIn(4, 24)
    val lampW = 10f
    val startX = (widthPx - stageW) / 2 + 30f
    val lampSpacing = (stageW - 60f) / (bCount - 1).coerceAtLeast(1)

    // Beam cone glow drawing colors
    val beamColors = listOf(
        Color(0xFF00FFCC).copy(alpha = pulseAlpha),
        Color(0xFF9A12B3).copy(alpha = pulseAlpha),
        Color(0xFF29B6F6).copy(alpha = pulseAlpha),
        Color(0xFFFFEE58).copy(alpha = pulseAlpha)
    )

    for (i in 0 until bCount) {
        val lampX = startX + (i * lampSpacing)
        val lampY = stageBaseY - 5f
        
        // Lamp body base
        drawRect(
            color = Color(0xFF2C3E50),
            topLeft = Offset(lampX - (lampW / 2), lampY),
            size = Size(lampW, 5f)
        )
        // Lamp head circular pivot assembly
        drawCircle(
            color = Color(0xFF34495E),
            radius = lampW / 2f,
            center = Offset(lampX, lampY)
        )

        // Draw glowing spotlight projections (Cone paths pointing skywards)
        // Use a deterministic fan-out spread based on the index 'i' to eliminate runtime GC allocations and chaotic flickering in draw loops
        val beamSpreadOffset = ((i * 3) % 7 - 3) * 1.5f
        val sweepRad = Math.toRadians((angleSweep + beamSpreadOffset).toDouble())
        val coneHeight = stageBaseY - 60f
        val xShift = (coneHeight * Math.sin(sweepRad)).toFloat()

        // Path representing spotlight cone
        val beamPath = Path().apply {
            moveTo(lampX, lampY)
            lineTo(lampX + xShift - 60f, 15f) // Flared top left
            lineTo(lampX + xShift + 60f, 15f) // Flared top right
            close()
        }
        
        // Color indexes shift
        val col = beamColors[i % beamColors.size]
        drawPath(
            path = beamPath,
            color = col
        )
    }

    // Par Leds row details as bottom backlight glows
    val rgbColors = listOf(Color.Red, Color.Green, Color.Blue, Color.Cyan, Color.Magenta, Color.Yellow)
    val parC = parCount.coerceIn(6, 40)
    for (p in 0 until parC) {
        val pX = (widthPx - stageW) / 2 + p * (stageW / parC)
        drawCircle(
            color = rgbColors[p % rgbColors.size].copy(alpha = 0.5f),
            radius = 3f,
            center = Offset(pX, stageBaseY)
        )
    }
}
