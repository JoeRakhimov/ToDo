package com.joerakhimov.todo.ui.screens.task

import androidx.compose.material.icons.Icons

import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.joerakhimov.todo.R

@Composable
fun CloseIcon() {
    Icon(
        Icons.Default.Close,
        contentDescription = stringResource(R.string.close),
        tint = MaterialTheme.colorScheme.onPrimary
    )
}