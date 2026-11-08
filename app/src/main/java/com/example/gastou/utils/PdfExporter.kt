package com.example.gastou.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.gastou.ui.components.formatYearMonth
import com.example.gastou.ui.screens.home.HomeUiState
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
object PdfExporter {

    fun generateMonthlyStatement(context: Context, state: HomeUiState): Uri? {
        val pdfDocument = PdfDocument()

        // Formato A4 estándar en puntos PostScript (595 x 842)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Pinturas para los textos
        val titlePaint = Paint().apply {
            color = Color.rgb(22, 163, 74) // GastoUGreen
            textSize = 28f
            isFakeBoldText = true
        }
        val subtitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 18f
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 14f
            isFakeBoldText = true
        }

        // --- DIBUJAR ENCANBEZADO ---
        val monthStr = formatYearMonth(state.selectedMonth)
        canvas.drawText("Estado de Cuenta - GastoU", 50f, 60f, titlePaint)
        canvas.drawText("Periodo: $monthStr", 50f, 90f, subtitlePaint)

        // --- DIBUJAR RESUMEN ---
        var currentY = 140f
        canvas.drawText("RESUMEN DEL MES", 50f, currentY, subtitlePaint)
        currentY += 30f

        if (state.monthlyBudget != null) {
            canvas.drawText("Presupuesto Mensual: $${String.format(Locale.US, "%,.2f", state.monthlyBudget)} MXN", 50f, currentY, textPaint)
            currentY += 25f
        }
        canvas.drawText("Total Gastado: $${String.format(Locale.US, "%,.2f", state.totalSpent)} MXN", 50f, currentY, textPaint)
        currentY += 25f

        if (state.monthlyBudget != null) {
            val remaining = state.monthlyBudget - state.totalSpent
            val remainingTextPaint = Paint(textPaint).apply {
                color = if (remaining < 0) Color.RED else Color.rgb(22, 163, 74)
                isFakeBoldText = true
            }
            canvas.drawText("Balance (Restante): $${String.format(Locale.US, "%,.2f", remaining)} MXN", 50f, currentY, remainingTextPaint)
        }

        currentY += 50f

        // --- DIBUJAR TABLA DE GASTOS ---
        canvas.drawText("DESGLOSE DE GASTOS", 50f, currentY, subtitlePaint)
        currentY += 20f

        // Fondo del encabezado de la tabla
        val bgPaint = Paint().apply { color = Color.rgb(22, 163, 74) }
        canvas.drawRect(50f, currentY, 545f, currentY + 30f, bgPaint)

        // Textos del encabezado
        currentY += 20f
        canvas.drawText("Fecha", 60f, currentY, headerPaint)
        canvas.drawText("Categoría", 180f, currentY, headerPaint)
        canvas.drawText("Monto (MXN)", 430f, currentY, headerPaint)

        currentY += 20f

        // Listar gastos
        for (expense in state.expenses) {
            // Si nos pasamos del límite de la página, creamos una nueva
            if (currentY > 800f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                currentY = 50f
            }

            // Para no imprimir un string larguísimo en la fecha, extraemos solo el texto antes del bullet "•"
            val shortDate = expense.date.substringBefore(" •")

            canvas.drawText(shortDate, 60f, currentY, textPaint)

            // Limitar longitud de la categoría para no desbordar
            val shortCat = if (expense.categoryId.length > 20) expense.categoryId.take(20) + "..." else expense.categoryId
            canvas.drawText(shortCat, 180f, currentY, textPaint)

            val formattedAmount = "$${String.format(Locale.US, "%,.2f", expense.amount)}"
            canvas.drawText(formattedAmount, 430f, currentY, textPaint)

            // Dibujar línea divisoria gris claro
            val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
            canvas.drawLine(50f, currentY + 10f, 545f, currentY + 10f, linePaint)

            currentY += 30f
        }
        
        currentY += 20f

        // --- DIBUJAR METAS DE AHORRO ---
        if (state.savingGoals.isNotEmpty()) {
            // Si nos pasamos del límite, nueva página
            if (currentY > 700f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                currentY = 50f
            }

            canvas.drawText("ESTADO DE METAS DE AHORRO", 50f, currentY, subtitlePaint)
            currentY += 20f

            // Encabezado Metas
            canvas.drawRect(50f, currentY, 545f, currentY + 30f, bgPaint)
            currentY += 20f
            canvas.drawText("Nombre de Meta", 60f, currentY, headerPaint)
            canvas.drawText("Progreso (MXN)", 280f, currentY, headerPaint)
            canvas.drawText("Estado", 430f, currentY, headerPaint)
            
            currentY += 20f

            for (goal in state.savingGoals) {
                if (currentY > 800f) {
                    pdfDocument.finishPage(page)
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    currentY = 50f
                }

                val shortName = if (goal.name.length > 25) goal.name.take(25) + "..." else goal.name
                canvas.drawText(shortName, 60f, currentY, textPaint)
                
                val progressTxt = "$${String.format(Locale.US, "%,.0f", goal.currentAmount)} / $${String.format(Locale.US, "%,.0f", goal.targetAmount)}"
                canvas.drawText(progressTxt, 280f, currentY, textPaint)

                val statusTxt = if (goal.isCompleted) "¡Completada!" else "En progreso"
                val statusPaint = Paint(textPaint).apply { 
                    color = if (goal.isCompleted) Color.rgb(22, 163, 74) else Color.DKGRAY 
                    isFakeBoldText = goal.isCompleted
                }
                canvas.drawText(statusTxt, 430f, currentY, statusPaint)

                val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
                canvas.drawLine(50f, currentY + 10f, 545f, currentY + 10f, linePaint)

                currentY += 30f
            }
        }

        // Finalizar página
        pdfDocument.finishPage(page)

        // --- GUARDAR ARCHIVO EN CACHÉ (Y DIRECTORIO PÚBLICO SI ES POSIBLE) ---
        return try {
            // Guardar internamente (caché para compartir de inmediato)
            val pdfsDir = File(context.cacheDir, "pdfs")
            pdfsDir.mkdirs() 

            val fileName = "Estado_Cuenta_${monthStr.replace(" ", "_")}.pdf"
            val file = File(pdfsDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            
            // Guardar también en la carpeta pública de Documentos (Descargas/Documentos) para que el usuario pueda encontrarlo siempre
            try {
                val publicDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)
                if (publicDir != null) {
                    if (!publicDir.exists()) publicDir.mkdirs()
                    
                    // Solo creamos la carpeta GastoU dentro de documentos
                    val gastoUDir = File(publicDir, "GastoU")
                    if (!gastoUDir.exists()) gastoUDir.mkdirs()
                    
                    val publicFile = File(gastoUDir, fileName)
                    file.copyTo(publicFile, overwrite = true)
                }
            } catch (e: Exception) {
                // Ignoramos errores de escritura pública por ahora (depende de permisos en Androids muy nuevos o viejos)
                e.printStackTrace()
            }

            // Devolver el Uri que FileProvider puede compartir (usando el archivo interno)
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}