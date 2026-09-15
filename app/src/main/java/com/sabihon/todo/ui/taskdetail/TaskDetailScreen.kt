package com.sabihon.todo.ui.taskdetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.sabihon.todo.domain.model.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
                },
                actions = {
                    // 3. remove bg of edit icon – per request, no blue background, just icon
                    IconButton(
                        onClick = { onEdit(taskId) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = AccentBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.task?.let { task ->
                val totalSubs = task.subTasks.size
                val doneSubs = task.subTasks.count { it.isDone }
                val rawProgress = if (totalSubs > 0) doneSubs.toFloat() / totalSubs else if (task.isCompleted) 1f else 0f
                var targetProgress by remember { mutableFloatStateOf(0f) }
                LaunchedEffect(rawProgress) { targetProgress = rawProgress }
                val progress by animateFloatAsState(
                    targetValue = targetProgress,
                    animationSpec = tween(durationMillis = 800),
                    label = "progressAnim"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header – bg #e3f5ff, title top, description white box, percentage overlapping top-right
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F5FF)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Description box with percentage overlapping top-right per request – more overlap like screenshot
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // White description container with light purple border like screenshot
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 20.dp, end = 16.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.9f))
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White)
                                        .padding(14.dp)
                                ) {
                                    var editableDesc by remember(task.id, task.description) { mutableStateOf(task.description) }
                                    LaunchedEffect(task.description) {
                                        if (editableDesc != task.description) editableDesc = task.description
                                    }
                                    OutlinedTextField(
                                        value = editableDesc,
                                        onValueChange = { editableDesc = it },
                                        placeholder = {
                                            Text(
                                                "Add description...",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                                color = Color(0xFF9CA3AF)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        minLines = 2,
                                        maxLines = 4,
                                        textStyle = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 13.sp,
                                            color = Color(0xFF374151)
                                        ),
                                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedBorderColor = AccentBlue.copy(alpha = 0.3f),
                                            unfocusedBorderColor = Color.Transparent,
                                            cursorColor = AccentBlue
                                        )
                                    )
                                    LaunchedEffect(editableDesc) {
                                        if (editableDesc != task.description) {
                                            kotlinx.coroutines.delay(600)
                                            viewModel.updateDescription(editableDesc)
                                        }
                                    }
                                }

                                // Percentage overlapping description – more overlap per request
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 12.dp, y = (-4).dp)
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .padding(3.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE3F5FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (totalSubs > 0) {
                                        Canvas(modifier = Modifier.size(52.dp)) {
                                            val stroke = 3.5.dp.toPx()
                                            drawCircle(
                                                color = Color.Gray.copy(alpha = 0.15f),
                                                style = Stroke(width = stroke)
                                            )
                                            if (progress > 0f) {
                                                drawArc(
                                                    color = AccentBlue,
                                                    startAngle = -90f,
                                                    sweepAngle = 360f * progress,
                                                    useCenter = false,
                                                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                                                )
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${(progress * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                ),
                                                color = Color(0xFF101114)
                                            )
                                            Text(
                                                text = "$doneSubs/$totalSubs",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = Color(0xFF6B7280)
                                            )
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "0%",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                color = Color(0xFF101114)
                                            )
                                            Text(
                                                text = "0/0",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                                color = Color(0xFF6B7280)
                                            )
                                        }
                                    }
                                }
                            }

                            // Date and Priority below the image – 1. remove category per request
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E7EB))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = task.dueAt?.let { formatDate(it) } ?: "No date",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Filled.CalendarToday, null, modifier = Modifier.size(14.dp))
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color.White.copy(alpha = 0.8f),
                                            labelColor = Color(0xFF374151)
                                        ),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                    val priorityColor = when (task.priority) {
                                        Priority.LOW -> Color(0xFF6B7280)
                                        Priority.MEDIUM -> Color(0xFF3B82F6)
                                        Priority.HIGH -> Color(0xFFF59E0B)
                                        Priority.URGENT -> Color(0xFFEF4444)
                                    }
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Filled.Flag, null, modifier = Modifier.size(14.dp), tint = priorityColor)
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = priorityColor.copy(alpha = 0.12f),
                                            labelColor = priorityColor
                                        ),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Sub-tasks with dividers + edit capability per request
                    var editingSubId by remember { mutableStateOf<String?>(null) }
                    var editingSubText by remember { mutableStateOf("") }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EBFF)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column {
                            task.subTasks.forEach { sub ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                        .combinedClickable(onClick = { viewModel.toggleSubTask(sub.id) })
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                ) {
                                    SubTaskRoundedCheckbox(
                                        checked = sub.isDone,
                                        onCheckedChange = { viewModel.toggleSubTask(sub.id) }
                                    )
                                    if (editingSubId == sub.id) {
                                        OutlinedTextField(
                                            value = editingSubText,
                                            onValueChange = { editingSubText = it },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        IconButton(onClick = {
                                            viewModel.editSubTask(sub.id, editingSubText)
                                            editingSubId = null
                                        }) {
                                            Text("✓", color = AccentBlue, fontWeight = FontWeight.Bold)
                                        }
                                        IconButton(onClick = { editingSubId = null }) {
                                            Text("×")
                                        }
                                    } else {
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = sub.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    textDecoration = if (sub.isDone) TextDecoration.LineThrough else null
                                                ),
                                                color = if (sub.isDone) Color.Gray.copy(alpha = 0.5f) else Color(0xFF101114),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "from: ${task.title}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray.copy(alpha = 0.7f),
                                                maxLines = 1
                                            )
                                        }
                                        IconButton(onClick = {
                                            editingSubId = sub.id
                                            editingSubText = sub.title
                                        }) {
                                            Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                        }
                                        IconButton(onClick = { viewModel.deleteSubTask(sub.id) }) {
                                            Text("×", color = Color.Gray)
                                        }
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 1.dp,
                                    color = Color(0xFFE5E7EB)
                                )
                            }

                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                                    .background(AccentBlue)
                                    .clickable { showAddSheet = true }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "Add task",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(100.dp))
                }

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
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp), contentAlignment = Alignment.Center) {
                                Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(100.dp)))
                            }
                            Text("Add new task", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text("to ${task.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = newSubTaskTitle,
                                onValueChange = { newSubTaskTitle = it },
                                label = { Text("Task name") },
                                placeholder = { Text("e.g. Make notes") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                TextButton(onClick = { showAddSheet = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp)) {
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = {
                                        if (newSubTaskTitle.isNotBlank()) viewModel.addSubTask(newSubTaskTitle.trim())
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
                Box(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("Task not found")
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    return try {
        val fmt = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        fmt.format(Date(millis))
    } catch (e: Exception) {
        "Invalid date"
    }
}
