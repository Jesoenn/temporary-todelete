package com.example.pdf_reader.data.repository

import com.example.pdf_reader.data.local.db.DocumentDao
import com.example.pdf_reader.data.local.db.DocumentEntity
import com.example.pdf_reader.domain.model.PdfDocumentInfo
import com.example.pdf_reader.domain.model.RecentDocument
import com.example.pdf_reader.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
) : DocumentRepository {

    override fun observeRecentDocuments(): Flow<List<RecentDocument>> {
        return documentDao.observeRecent().map { entities ->
            entities.map { entity ->
                RecentDocument(
                    uri = entity.uri,
                    displayName = entity.displayName,
                    pageCount = entity.pageCount,
                    lastOpenedEpochMs = entity.lastOpenedEpochMs,
                )
            }
        }
    }

    override suspend fun saveOpenedDocument(info: PdfDocumentInfo) {
        documentDao.upsert(
            DocumentEntity(
                uri = info.uri,
                displayName = info.displayName,
                pageCount = info.pageCount,
                lastOpenedEpochMs = System.currentTimeMillis(),
            ),
        )
    }
}
