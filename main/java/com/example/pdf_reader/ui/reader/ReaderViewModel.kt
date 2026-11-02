package com.example.pdf_reader.ui.reader

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdf_reader.data.pdf.BitmapPool
import com.example.pdf_reader.domain.repository.PdfDocumentGateway
import com.example.pdf_reader.domain.usecase.GetPageMetadataUseCase
import com.example.pdf_reader.domain.usecase.OpenPdfUseCase
import com.example.pdf_reader.domain.usecase.RenderPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val openPdf: OpenPdfUseCase,
    private val getPageMetadata: GetPageMetadataUseCase,
    private val renderPage: RenderPageUseCase,
    private val pdfDocumentGateway: PdfDocumentGateway,
    private val bitmapPool: BitmapPool,
) : ViewModel() {

    private val documentUri: Uri = Uri.parse(
        checkNotNull(savedStateHandle.get<String>("uri")) { "Brak URI dokumentu" },
    )

    private val _uiState = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var heldBitmap: Bitmap? = null
    private var renderJob: Job? = null
    private var viewportWidthPx: Int = DEFAULT_WIDTH_PX
    private var pageCount: Int = 0
    private var documentName: String = ""
    private var currentPageIndex: Int = 0

    init {
        viewModelScope.launch {
            runCatching { openPdf(documentUri) }
                .onSuccess { info ->
                    documentName = info.displayName
                    pageCount = info.pageCount
                    loadPage(0)
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _uiState.value = ReaderUiState.Error(error.message ?: "Nie udało się otworzyć PDF")
                }
        }
    }

    fun onViewportWidth(widthPx: Int) {
        if (widthPx <= 0 || widthPx == viewportWidthPx) return
        viewportWidthPx = widthPx
        if (pageCount > 0) loadPage(currentPageIndex)
    }

    fun nextPage() {
        if (currentPageIndex + 1 < pageCount) loadPage(currentPageIndex + 1)
    }

    fun previousPage() {
        if (currentPageIndex > 0) loadPage(currentPageIndex - 1)
    }

    private fun loadPage(pageIndex: Int) {
        currentPageIndex = pageIndex
        renderJob?.cancel()
        renderJob = viewModelScope.launch {
            _uiState.value = ReaderUiState.Loading
            runCatching {
                val metadata = getPageMetadata(pageIndex)
                val bitmap = renderPage(pageIndex, viewportWidthPx)
                metadata to bitmap
            }.onSuccess { (metadata, bitmap) ->
                recycleHeldBitmap()
                heldBitmap = bitmap
                _uiState.value = ReaderUiState.Ready(
                    documentName = documentName,
                    pageCount = pageCount,
                    pageIndex = pageIndex,
                    pageWidthPt = metadata.widthPt,
                    pageHeightPt = metadata.heightPt,
                    pageBitmap = bitmap,
                )
            }.onFailure { error ->
                if (error is CancellationException) throw error
                _uiState.value = ReaderUiState.Error(error.message ?: "Błąd renderowania strony")
            }
        }
    }

    private fun recycleHeldBitmap() {
        heldBitmap?.let(bitmapPool::release)
        heldBitmap = null
    }

    override fun onCleared() {
        renderJob?.cancel()
        recycleHeldBitmap()
        pdfDocumentGateway.close()
        super.onCleared()
    }

    private companion object {
        const val DEFAULT_WIDTH_PX = 1080
    }
}

sealed interface ReaderUiState {
    data object Loading : ReaderUiState
    data class Ready(
        val documentName: String,
        val pageCount: Int,
        val pageIndex: Int,
        val pageWidthPt: Float,
        val pageHeightPt: Float,
        val pageBitmap: Bitmap,
    ) : ReaderUiState
    data class Error(val message: String) : ReaderUiState
}
