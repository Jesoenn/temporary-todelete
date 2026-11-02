package com.example.pdf_reader.di

import com.example.pdf_reader.data.pdf.PdfiumEngine
import com.example.pdf_reader.domain.repository.PdfDocumentGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PdfEngineModule {

    @Binds
    @Singleton
    abstract fun bindPdfDocumentGateway(engine: PdfiumEngine): PdfDocumentGateway
}
