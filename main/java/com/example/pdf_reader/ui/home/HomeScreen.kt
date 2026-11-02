package com.example.pdf_reader.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    onOpenDocument: (Uri) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val recents by viewModel.recentDocuments.collectAsStateWithLifecycle()
    val picker = rememberLauncherForActivityResult(
        contract = OpenPdfDocumentContract(),
    ) { uri ->
        uri?.let(onOpenDocument)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "PDF Reader", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = { picker.launch(arrayOf("application/pdf")) }) {
            Text("Otwórz PDF")
        }
        Text(text = "Ostatnie pliki", style = MaterialTheme.typography.titleMedium)
        if (recents.isEmpty()) {
            Text(text = "Brak ostatnio otwartych plików.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(recents, key = { it.uri }) { document ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDocument(Uri.parse(document.uri)) }
                            .padding(vertical = 10.dp),
                    ) {
                        Text(text = document.displayName, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "${document.pageCount} str.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

private class OpenPdfDocumentContract : ActivityResultContracts.OpenDocument() {
    override fun createIntent(context: Context, input: Array<String>): Intent {
        return super.createIntent(context, input).addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
        )
    }
}
