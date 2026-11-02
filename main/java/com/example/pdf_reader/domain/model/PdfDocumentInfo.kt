package com.example.pdf_reader.domain.model

data class PdfDocumentInfo(
    val uri: String,
    val displayName: String,
    val pageCount: Int,
)
