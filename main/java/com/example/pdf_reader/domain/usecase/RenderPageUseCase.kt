package com.example.pdf_reader.domain.usecase

import android.graphics.Bitmap
import com.example.pdf_reader.domain.repository.PdfDocumentGateway
import javax.inject.Inject

class RenderPageUseCase @Inject constructor(
    private val pdfDocumentGateway: PdfDocumentGateway,
) {
    suspend operator fun invoke(pageIndex: Int, targetWidthPx: Int): Bitmap {
        return pdfDocumentGateway.renderPage(pageIndex, targetWidthPx)
    }
}
