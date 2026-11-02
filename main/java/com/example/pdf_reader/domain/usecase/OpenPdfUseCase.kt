package com.example.pdf_reader.domain.usecase

import android.net.Uri
import com.example.pdf_reader.data.local.saf.SafUriManager
import com.example.pdf_reader.domain.model.PdfDocumentInfo
import com.example.pdf_reader.domain.repository.DocumentRepository
import com.example.pdf_reader.domain.repository.PdfDocumentGateway
import javax.inject.Inject

class OpenPdfUseCase @Inject constructor(
    private val pdfDocumentGateway: PdfDocumentGateway,
    private val documentRepository: DocumentRepository,
    private val safUriManager: SafUriManager,
) {
    suspend operator fun invoke(uri: Uri): PdfDocumentInfo {
        safUriManager.takePersistableReadPermission(uri)
        val info = pdfDocumentGateway.open(uri)
        documentRepository.saveOpenedDocument(info)
        return info
    }
}
