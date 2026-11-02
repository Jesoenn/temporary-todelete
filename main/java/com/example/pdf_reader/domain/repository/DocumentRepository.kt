package com.example.pdf_reader.domain.repository

import com.example.pdf_reader.domain.model.PdfDocumentInfo
import com.example.pdf_reader.domain.model.RecentDocument
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun observeRecentDocuments(): Flow<List<RecentDocument>>
    suspend fun saveOpenedDocument(info: PdfDocumentInfo)
}
