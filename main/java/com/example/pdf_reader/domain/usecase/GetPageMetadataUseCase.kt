package com.example.pdf_reader.domain.usecase

import com.example.pdf_reader.domain.model.PdfPageInfo
import com.example.pdf_reader.domain.repository.PdfDocumentGateway
import javax.inject.Inject

class GetPageMetadataUseCase @Inject constructor(
    private val pdfDocumentGateway: PdfDocumentGateway,
) {
    suspend operator fun invoke(pageIndex: Int): PdfPageInfo {
        return pdfDocumentGateway.getPageInfo(pageIndex)
    }
}
