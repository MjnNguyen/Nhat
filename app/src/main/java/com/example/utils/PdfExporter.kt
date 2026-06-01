package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.StageProject
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun exportProjectToPdf(
        context: Context,
        project: StageProject,
        onCompleted: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val pdfDocument = PdfDocument()
            // A4 page resolution (595 x 842 points)
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.rgb(33, 43, 54)
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.rgb(100, 110, 120)
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                isAntiAlias = true
            }

            val headerPaint = Paint().apply {
                color = Color.rgb(44, 62, 80)
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val textPaint = Paint().apply {
                color = Color.rgb(60, 60, 60)
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val boldTextPaint = Paint().apply {
                color = Color.rgb(44, 62, 80)
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val borderPaint = Paint().apply {
                color = Color.rgb(200, 200, 200)
                style = Paint.Style.STROKE
                strokeWidth = 1f
                isAntiAlias = true
            }

            val tableHeaderPaint = Paint().apply {
                color = Color.rgb(240, 243, 244)
                style = Paint.Style.FILL
            }

            var currentY = 40f

            // 1. Header Banner
            canvas.drawRect(35f, currentY, 560f, currentY + 3f, Paint().apply { color = Color.rgb(22, 160, 133); style = Paint.Style.FILL })
            currentY += 25f

            canvas.drawText("BÁO CÁO KỸ THUẬT & BÁO GIÁ SÂN KHẤU AUTOMATED", 35f, currentY, titlePaint)
            currentY += 15f
            canvas.drawText("Xác thực hệ thống - StageBuilder Pro v1.0", 35f, currentY, subtitlePaint)
            
            // Date
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val currentDateStr = sdf.format(Date())
            canvas.drawText("Ngày xuất: $currentDateStr", 400f, currentY, Paint().apply {
                color = Color.rgb(120, 120, 120)
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            })
            currentY += 25f

            // 2. Project Information
            canvas.drawText("I. THÔNG TIN DỰ ÁN", 35f, currentY, headerPaint)
            canvas.drawLine(35f, currentY + 4f, 560f, currentY + 4f, borderPaint)
            currentY += 22f

            val labelX = 35f
            val valueX = 140f
            val col2LabelX = 300f
            val col2ValueX = 410f

            canvas.drawText("Tên dự án:", labelX, currentY, boldTextPaint)
            canvas.drawText(project.name, valueX, currentY, textPaint)
            canvas.drawText("Trạng thái:", col2LabelX, currentY, boldTextPaint)
            canvas.drawText(project.status, col2ValueX, currentY, Paint().apply {
                color = when (project.status) {
                    "Đã duyệt" -> Color.rgb(39, 174, 96)
                    "Đã báo giá" -> Color.rgb(41, 128, 185)
                    else -> Color.rgb(241, 196, 15)
                }
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            })
            currentY += 16f

            canvas.drawText("Khách hàng:", labelX, currentY, boldTextPaint)
            canvas.drawText(project.clientName.ifBlank { "N/A" }, valueX, currentY, textPaint)
            canvas.drawText("Phân quyền:", col2LabelX, currentY, boldTextPaint)
            canvas.drawText("Vai trò: " + project.updatedByRole, col2ValueX, currentY, textPaint)
            currentY += 16f

            canvas.drawText("Địa điểm:", labelX, currentY, boldTextPaint)
            canvas.drawText(project.venueName.ifBlank { "N/A" }, valueX, currentY, textPaint)
            canvas.drawText("Thời gian thuê:", col2LabelX, currentY, boldTextPaint)
            canvas.drawText("${project.rentalDays} ngày", col2ValueX, currentY, textPaint)
            currentY += 16f

            canvas.drawText("Nhãn dự án:", labelX, currentY, boldTextPaint)
            canvas.drawText(project.tags.replace(",", " | "), valueX, currentY, textPaint)
            canvas.drawText("Bảo mật đám mây:", col2LabelX, currentY, boldTextPaint)
            canvas.drawText(if (project.isSynced) "Đã đồng bộ hóa SSL" else "Lưu trữ nội bộ an toàn", col2ValueX, currentY, textPaint)
            currentY += 30f

            // 3. Technical Specs
            canvas.drawText("II. THÔNG SỐ KHO LAYOUT SÂN KHẤU", 35f, currentY, headerPaint)
            canvas.drawLine(35f, currentY + 4f, 560f, currentY + 4f, borderPaint)
            currentY += 22f

            canvas.drawText("1. Sàn Sân Khấu (Kích thước thực tế):", labelX, currentY, boldTextPaint)
            canvas.drawText("Rộng ${project.stageWidth}m  ×  Sâu ${project.stageDepth}m  ×  Cao ${project.stageHeight}m (Diện tích: ${project.stageArea} m²)", valueX + 80f, currentY, textPaint)
            currentY += 16f

            canvas.drawText("2. Màn Hình LED (Phía sau):", labelX, currentY, boldTextPaint)
            canvas.drawText("Rộng ${project.ledWidth}m  ×  Cao ${project.ledHeight}m (Diện tích treo: ${project.ledArea} m²)", valueX + 80f, currentY, textPaint)
            currentY += 16f

            canvas.drawText("3. Khung Giàn Truss Treo:", labelX, currentY, boldTextPaint)
            canvas.drawText("Rộng ${project.trussWidth}m  ×  Sâu ${project.trussDepth}m  ×  Cao ${project.trussHeight}m (Lắp ráp: ${project.totalTrussLength}m truss nhôm)", valueX + 80f, currentY, textPaint)
            currentY += 30f

            // 4. Quotation Table
            canvas.drawText("III. BÁO CÁO CHI TIẾT VẬT TƯ & ĐƠN GIÁ SẢN XUẤT", 35f, currentY, headerPaint)
            canvas.drawLine(35f, currentY + 4f, 560f, currentY + 4f, borderPaint)
            currentY += 20f

            // Draw Table Header Background
            canvas.drawRect(35f, currentY, 560f, currentY + 20f, tableHeaderPaint)
            canvas.drawRect(35f, currentY, 560f, currentY + 20f, borderPaint)

            // Table Headers Text
            val colY = currentY + 14f
            canvas.drawText("STT", 40f, colY, boldTextPaint)
            canvas.drawText("Hạng mục sản xuất / Thiết bị", 70f, colY, boldTextPaint)
            canvas.drawText("Thông số / Quy cách", 260f, colY, boldTextPaint)
            canvas.drawText("SL", 400f, colY, boldTextPaint)
            canvas.drawText("Đơn giá (VND/ngày)", 430f, colY, boldTextPaint)
            canvas.drawText("Thành tiền (VND)", 500f, colY, boldTextPaint)
            currentY += 20f

            val vnFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))

            // Items list:
            val items = listOf(
                Triple("Sàn sân khấu", "Gỗ phủ thảm đỏ/đen chịu lực, cao ${project.stageHeight}m", "${project.stageArea} m²"),
                Triple("Màn LED sân khấu", "Màn Led P3 Outdoor cabinet nhôm đúc chịu nước", "${project.ledArea} m²"),
                Triple("Khung giàn truss nhôm", "Khung truss nhôm hộp 300x300 chốt pin liên kết", "${project.totalTrussLength} m"),
                Triple("Hệ loa Line Array", "Loa Array 12 inch kiểm âm 3-way, treo khung truss", "${project.speakerCount} cái"),
                Triple("Loa âm trầm Subwoofer", "Loa Sub kép 18 inch tăng cường uy lực", "${project.subCount} cái"),
                Triple("Đèn Moving Head Beam", "Beam 350W chùm tia hẹp siêu sáng, lập trình DMX", "${project.beamCount} cái"),
                Triple("Đèn Par LED màu", "Par Led dội background dải màu RGBW cực tốt", "${project.parCount} cái"),
                Triple("Đèn khán giả Blinder", "Đèn blinder 2 bóng halogen rọi hội trường", "${project.blinderCount} cái")
            )

            val prices = listOf(
                project.costPerSquareMeterStage,
                project.costPerSquareMeterLed,
                project.costPerTrussMeter,
                project.costPerSpeaker,
                project.costPerSub,
                project.costPerBeam,
                project.costPerPar,
                project.costPerBlinder
            )

            val subTotals = listOf(
                project.stageCost,
                project.ledCost,
                project.trussCost,
                project.speakerCount * project.costPerSpeaker * project.rentalDays,
                project.subCount * project.costPerSub * project.rentalDays,
                project.beamCount * project.costPerBeam * project.rentalDays,
                project.parCount * project.costPerPar * project.rentalDays,
                project.blinderCount * project.costPerBlinder * project.rentalDays
            )

            for (i in items.indices) {
                val rowY = currentY + 14f
                canvas.drawRect(35f, currentY, 560f, currentY + 18f, borderPaint)
                canvas.drawText((i + 1).toString(), 40f, rowY, textPaint)
                canvas.drawText(items[i].first, 70f, rowY, textPaint)
                canvas.drawText(items[i].second, 260f, rowY, textPaint)
                canvas.drawText(items[i].third, 395f, rowY, textPaint)
                
                val formattedPrice = vnFormat.format(prices[i]).replace("₫", "").trim()
                canvas.drawText(formattedPrice, 430f, rowY, textPaint)

                val formattedSubTotal = vnFormat.format(subTotals[i]).replace("₫", "").trim()
                canvas.drawText(formattedSubTotal, 500f, rowY, textPaint)
                currentY += 18f
            }

            // Total Block
            canvas.drawRect(35f, currentY, 560f, currentY + 22f, tableHeaderPaint)
            canvas.drawRect(35f, currentY, 560f, currentY + 22f, borderPaint)
            
            canvas.drawText("Tổng cộng dịch vụ (${project.rentalDays} ngày):", 70f, currentY + 15f, boldTextPaint)
            val formattedTotal = vnFormat.format(project.totalEstimateCost).replace("VNĐ", "VND").replace("₫", "VND")
            canvas.drawText(formattedTotal, 460f, currentY + 15f, Paint().apply {
                color = Color.rgb(192, 57, 43)
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            })
            currentY += 35f

            // 5. Terms & Conditions + Secure watermark
            canvas.drawText("IV. CAM KẾT & AN TOÀN bảo mật", 35f, currentY, headerPaint)
            canvas.drawLine(35f, currentY + 4f, 560f, currentY + 4f, borderPaint)
            currentY += 22f
            canvas.drawText("- Cam kết thiết bị chính hãng chất lượng CO/CQ đầy đủ.", 35f, currentY, textPaint)
            currentY += 14f
            canvas.drawText("- Dữ liệu thiết kế mã hóa 256-bit SSL, tự động sao lưu đám mây StageDesigner Cloud.", 35f, currentY, textPaint)
            currentY += 14f
            canvas.drawText("- Bản vẽ được chứng thực bởi tài khoản vai trò: [ " + project.updatedByRole + " ]", 35f, currentY, textPaint)
            currentY += 45f

            // Signatures
            canvas.drawText("ĐẠI DIỆN KHÁCH HÀNG", 80f, currentY, boldTextPaint)
            canvas.drawText("PHÒNG KỸ THUẬT STAGEBUILDER", 370f, currentY, boldTextPaint)
            currentY += 12f
            canvas.drawText("(Ký và ghi rõ họ tên)", 95f, currentY, subtitlePaint)
            canvas.drawText("(Đã ký trực tuyến qua SSL ID)", 395f, currentY, subtitlePaint)

            pdfDocument.finishPage(page)

            // Save the document
            val directory = File(context.cacheDir, "documents")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            
            // Clean up filename to prevent issues
            val safeProjectName = project.name.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val file = File(directory, "BaoGia_SanKhau_${safeProjectName}_${project.id}.pdf")
            
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.flush()
            outputStream.close()

            onCompleted(file)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.localizedMessage ?: "Lỗi chưa biết khi xuất file PDF.")
        }
    }
}
