package com.joerakhimov.todo.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joerakhimov.todo.R

@Composable
fun ErrorView(
    message: String,
    secondsBeforeRetry: Int? = null,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = CenterHorizontally) {
            Text(
                "$message ${
                    secondsBeforeRetry?.let {
                        stringResource(
                            R.string.retry_in_given_seconds,
                            it
                        )
                    } ?: ""
                }",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { onRetry() }
            ) {
                Text(stringResource(R.string.retry), color = Color.White)
            }
        }
    }
}