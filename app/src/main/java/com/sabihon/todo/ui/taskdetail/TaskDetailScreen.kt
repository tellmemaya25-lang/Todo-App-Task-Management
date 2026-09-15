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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
                },
                actions = {
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
                    // Header – Webinar style: fix spacing per image-2.png – title + percentage top row, white description box below, date/priority chips below with spacing
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F5FF)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Top row: title left, percentage right – 44% 4/9 like image-2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(56.dp)) {
                                        val stroke = 3.5.dp.toPx()
                                        drawCircle(
                                            color = Color(0xFFE5E7EB),
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
                                }
                            }

                            // White description box – non-editable, white bg, with spacing like image-2
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = task.description.takeIf { it.isNotBlank() } ?: "Cybersecurity is What type of thing this do what Other things should we able to do here. asdasdf asdfas asdf asd asdfa",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        color = Color(0xFF374151)
                                    )
                                )
                            }

                            // Date and Priority below with spacing – white bg chips like image-2
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E7EB))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = task.dueAt?.let { formatDate(it) } ?: "Sep 16, 2026 • 1:51 AM",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Filled.CalendarToday, null, modifier = Modifier.size(14.dp))
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = Color.White,
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
                                            containerColor = Color.White,
                                            labelColor = priorityColor
                                        ),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Sub-tasks with edit – scrollable within container per request – corner radius 24.dp per request – consistent like All Tasks
                    var editingSubId by remember { mutableStateOf<String?>(null) }
                    var editingSubText by remember { mutableStateOf("") }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F5FF)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column {
                            // Scrollable container for sub-tasks – max height 320dp – consistent with All Tasks cards #E3F5FF
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 320.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                            task.subTasks.forEach { sub ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                        .combinedClickable(onClick = { viewModel.toggleSubTask(sub.id) })
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    // Checkbox – round, same as All Tasks percentage style but checkbox
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
                                        }, modifier = Modifier.size(32.dp)) {
                                            Text("✓", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        IconButton(onClick = { editingSubId = null }, modifier = Modifier.size(32.dp)) {
                                            Text("×", fontSize = 14.sp)
                                        }
                                    } else {
                                        // Consistent like All Tasks: title bold 16sp + short desc 13sp + status dot 6dp
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = sub.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    lineHeight = 18.sp,
                                                    textDecoration = if (sub.isDone) TextDecoration.LineThrough else null
                                                ),
                                                color = if (sub.isDone) Color(0xFF6B7280).copy(alpha = 0.6f) else Color(0xFF101114),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "from: ${task.title}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 12.sp,
                                                    lineHeight = 14.sp
                                                ),
                                                color = Color(0xFF6B7280).copy(alpha = 0.9f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(if (sub.isDone) Color(0xFF4CAF50) else Color(0xFF9CA3AF))
                                                )
                                                Text(
                                                    text = if (sub.isDone) "Completed" else "Pending",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = if (sub.isDone) Color(0xFF4CAF50) else Color(0xFF6B7280)
                                                )
                                            }
                                        }
                                        IconButton(onClick = {
                                            editingSubId = sub.id
                                            editingSubText = sub.title
                                        }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.Filled.Edit, null, modifier = Modifier.size(14.dp), tint = Color(0xFF6B7280))
                                        }
                                        IconButton(onClick = { viewModel.deleteSubTask(sub.id) }, modifier = Modifier.size(28.dp)) {
                                            Text("×", color = Color(0xFF6B7280), fontSize = 14.sp)
                                        }
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = Color.Gray.copy(alpha = 0.12f)
                                )
                            }
                            }

                            // CTA with overlapping arrow down btn per request – corner radius 24.dp
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(top = 12.dp)
                                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
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
                                // Arrow down btn overlapping CTA
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = (-12).dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { /* could collapse */ }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Expand",
                                        tint = AccentBlue,
                                        modifier = Modifier.size(20.dp)
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
