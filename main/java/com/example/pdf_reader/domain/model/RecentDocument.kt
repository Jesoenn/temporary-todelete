package com.example.pdf_reader.domain.model

data class RecentDocument(
    val uri: String,
    val displayName: String,
    val pageCount: Int,
    val lastOpenedEpochMs: Long,
)
