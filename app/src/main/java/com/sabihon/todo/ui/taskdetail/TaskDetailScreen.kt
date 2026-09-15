package com.sabihon.todo.ui.taskdetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.core.ui.components.SubTaskRoundedCheckbox
import com.sabihon.todo.core.ui.theme.AccentBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {},
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var newSubTaskTitle by remember { mutableStateOf("") }
    val addSheetState = rememberModalBottomSheetState()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Tasks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.task?.let { task ->
                val totalSubs = task.subTasks.size
                val doneSubs = task.subTasks.count { it.isDone }
                val progress = if (totalSubs > 0) doneSubs.toFloat() / totalSubs else if (task.isCompleted) 1f else 0f

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header card – Webinar style from screenshot
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (task.isCompleted) Brush.linearGradient(
                                        colors = listOf(
                                            AccentBlue.copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                        )
                                    ) else Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Left check + title
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (task.isCompleted) AccentBlue else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (task.isCompleted) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Canvas(modifier = Modifier.size(24.dp)) {
                                            drawCircle(
                                                color = Color.Gray.copy(alpha = 0.35f),
                                                style = Stroke(width = 2.dp.toPx())
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = task.description.takeIf { it.isNotBlank() } ?: "Cybersecurity",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                // Right: edit button + percentage like screenshot
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Edit button blue
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AccentBlue)
                                            .clickable { onEdit(taskId) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            contentDescription = "Edit",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Percentage 50% 2/4 like screenshot top right
                                    if (totalSubs > 0) {
                                        Box(
                                            modifier = Modifier.size(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Canvas(modifier = Modifier.size(48.dp)) {
                                                val strokeWidth = 3.dp.toPx()
                                                drawCircle(
                                                    color = Color.Gray.copy(alpha = 0.15f),
                                                    style = Stroke(width = strokeWidth)
                                                )
                                                if (progress > 0f) {
                                                    drawArc(
                                                        color = AccentBlue,
                                                        startAngle = -90f,
                                                        sweepAngle = 360f * progress,
                                                        useCenter = false,
                                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                                    )
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "${(progress * 100).toInt()}%",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "$doneSubs/$totalSubs",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sub-tasks card with dividers – exact format from screenshot
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            task.subTasks.forEachIndexed { index, sub ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = { viewModel.toggleSubTask(sub.id) }
                                        )
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                ) {
                                    SubTaskRoundedCheckbox(
                                        checked = sub.isDone,
                                        onCheckedChange = { viewModel.toggleSubTask(sub.id) }
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = sub.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                textDecoration = if (sub.isDone) TextDecoration.LineThrough else null
                                            ),
                                            color = if (sub.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                            else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "from: ${task.title}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (index < task.subTasks.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                    )
                                }
                            }

                            // Divider before Add task
                            if (task.subTasks.isNotEmpty()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                )
                            }

                            // Add task row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showAddSheet = true }
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Canvas(modifier = Modifier.size(20.dp)) {
                                    drawCircle(
                                        color = Color.Gray.copy(alpha = 0.3f),
                                        style = Stroke(width = 1.2.dp.toPx())
                                    )
                                }
                                Text(
                                    text = "Add task",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(100.dp))
                }

                // Add sub-task bottom sheet – rename like image bottom edit
                if (showAddSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showAddSheet = false },
                        sheetState = addSheetState,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        dragHandle = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(4.dp)
                                        .background(
                                            Color.Gray.copy(alpha = 0.4f),
                                            RoundedCornerShape(100.dp)
                                        )
                                )
                            }

                            Text(
                                text = "Add new task",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "to ${task.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = newSubTaskTitle,
                                onValueChange = { newSubTaskTitle = it },
                                label = { Text("Task name") },
                                placeholder = { Text("e.g. Make notes") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TextButton(
                                    onClick = { showAddSheet = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = {
                                        if (newSubTaskTitle.isNotBlank()) {
                                            viewModel.addSubTask(newSubTaskTitle.trim())
                                        }
                                        showAddSheet = false
                                        newSubTaskTitle = ""
                                    },
                                    enabled = newSubTaskTitle.isNotBlank(),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                                ) {
                                    Text("Add")
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Task not found")
                }
            }
        }
    }
}
