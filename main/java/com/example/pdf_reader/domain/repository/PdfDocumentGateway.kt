package com.example.pdf_reader.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.pdf_reader.domain.model.PdfDocumentInfo
import com.example.pdf_reader.domain.model.PdfPageInfo

interface PdfDocumentGateway {
    suspend fun open(uri: Uri): PdfDocumentInfo
    suspend fun getPageInfo(pageIndex: Int): PdfPageInfo
    suspend fun renderPage(pageIndex: Int, targetWidthPx: Int): Bitmap
    fun close()
}
