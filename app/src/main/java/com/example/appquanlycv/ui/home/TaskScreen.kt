package com.example.appquanlycv.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appquanlycv.R
import com.example.appquanlycv.data.Task
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun TaskScreen(
    uiState: TaskUiState,
    onAddTask: (String, String, Long, Boolean) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onToggleCompletion: (Task, Boolean) -> Unit,
    onToggleReminder: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val gradientBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color(0xFFFFF0F5),
                Color(0xFFFDE7F3),
                Color(0xFFF5F9FF)
            )
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFFA6C9),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Thêm việc")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CatGreetingCard(
                    selectedDate = uiState.selectedDate,
                    taskCount = uiState.tasksForSelectedDate.size
                )
                SummaryRow(
                    weekCount = uiState.daysWithTasksInWeek,
                    monthCount = uiState.daysWithTasksInMonth
                )
                WeekDateSelector(
                    selectedDate = uiState.selectedDate,
                    onSelectDate = onSelectDate
                )
                TaskList(
                    tasks = uiState.tasksForSelectedDate,
                    onToggleCompletion = onToggleCompletion,
                    onToggleReminder = onToggleReminder,
                    onDeleteTask = onDeleteTask
                )
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, description, dateTime, reminder ->
                onAddTask(title, description, dateTime, reminder)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CatGreetingCard(selectedDate: LocalDate, taskCount: Int) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEEE, dd MMM", Locale.getDefault()) }
    val friendlyDate = remember(selectedDate) {
        selectedDate.format(formatter)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFC1E3))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = Color.White.copy(alpha = 0.6f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pets,
                        contentDescription = null,
                        tint = Color(0xFF6C4A93),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text(
                        text = "Xin chào, sen!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF6C4A93)
                    )
                    Text(
                        text = friendlyDate.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6C4A93)
                    )
                }
            }
            Text(
                text = if (taskCount > 0) {
                    "Mèo nhắc: hôm nay còn $taskCount việc cần được vuốt!"
                } else {
                    "Không có việc nào hôm nay, mèo rất hài lòng!"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF5B3C88)
            )
        }
    }
}

@Composable
private fun SummaryRow(weekCount: Int, monthCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Ngày bận trong tuần",
            value = weekCount,
            gradient = Brush.verticalGradient(
                listOf(Color(0xFFFFE5F1), Color(0xFFFFF2FA))
            ),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Ngày bận trong tháng",
            value = monthCount,
            gradient = Brush.verticalGradient(
                listOf(Color(0xFFE5F1FF), Color(0xFFF2F8FF))
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: Int,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = MaterialTheme.shapes.medium) {
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF6C4A93)
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black
                ),
                color = Color(0xFF3A1D5D)
            )
        }
    }
}

@Composable
private fun WeekDateSelector(
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit
) {
    val startOfWeek = remember(selectedDate) {
        selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
    val days = remember(startOfWeek) {
        (0 until 7).map { startOfWeek.plusDays(it.toLong()) }
    }
    val dayFormatter = remember { DateTimeFormatter.ofPattern("EEE", Locale.getDefault()) }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(days) { date ->
            val isSelected = date == selectedDate
            val dayLabel = remember(date) {
                dayFormatter.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
            AssistChip(
                onClick = { onSelectDate(date) },
                label = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else Color(0xFF6C4A93)
                        )
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) Color.White else Color(0xFF3A1D5D)
                        )
                    }
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) Color(0xFF6C4A93)
                    else Color.White.copy(alpha = 0.8f)
                )
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onToggleCompletion: (Task, Boolean) -> Unit,
    onToggleReminder: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    if (tasks.isEmpty()) {
        EmptyState()
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onToggleCompletion = onToggleCompletion,
                onToggleReminder = onToggleReminder,
                onDeleteTask = onDeleteTask
            )
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "=^.^=",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF6C4A93)
        )
        Text(
            text = "Chưa có việc nào cho ngày này",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF6C4A93),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Thêm việc mới để mèo nhắc nhé!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8B5FBF)
        )
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onToggleCompletion: (Task, Boolean) -> Unit,
    onToggleReminder: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) }
    val timeText = remember(task.dueDateTime) {
        Instant.ofEpochMilli(task.dueDateTime)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(formatter)
    }

    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.9f))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleCompletion(task, it) }
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (task.isCompleted) Color(0xFF8B5FBF) else Color(0xFF3A1D5D)
                    )
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6C4A93)
                        )
                    }
                    Text(
                        text = "Nhắc vào $timeText",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF8B5FBF)
                    )
                }
                IconButton(onClick = { onDeleteTask(task) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Xóa việc",
                        tint = Color(0xFFB3426F)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ReminderToggle(
                task = task,
                onToggleReminder = onToggleReminder
            )
        }
    }
}

@Composable
private fun ReminderToggle(task: Task, onToggleReminder: (Task, Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (task.reminderEnabled)
                    Icons.Filled.Notifications
                else
                    Icons.Outlined.NotificationsOff,
                contentDescription = null,
                tint = Color(0xFF6C4A93)
            )
            Text(
                text = if (task.reminderEnabled) "Mèo sẽ nhắc bạn" else "Nhắc nhở đang tắt",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6C4A93)
            )
        }
        Text(
            text = if (task.reminderEnabled) "Tắt" else "Bật",
            modifier = Modifier
                .clickable { onToggleReminder(task, !task.reminderEnabled) }
                .background(Color(0xFFFFE5F1), shape = MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color(0xFFB3426F),
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Long, Boolean) -> Unit
) {
    val zoneId = remember { ZoneId.systemDefault() }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val initialTime = remember { LocalTime.now().truncatedTo(ChronoUnit.MINUTES) }
    var timeInput by remember { mutableStateOf(initialTime.format(timeFormatter)) }
    var timeInputError by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateLabel = remember(selectedDate) {
        selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
    }

    if (showDatePicker) {
        val dateState =
            rememberDatePickerState(initialSelectedDateMillis = selectedDate.toEpochMilli(zoneId))
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = dateState.selectedDateMillis
                        if (millis != null) {
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(zoneId)
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Chọn")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedTime =
                        runCatching { LocalTime.parse(timeInput, timeFormatter) }.getOrNull()
                    if (parsedTime == null) {
                        timeInputError = true
                    } else {
                        val dateTime = LocalDateTime.of(selectedDate, parsedTime)
                        onSave(
                            title,
                            description,
                            dateTime.atZone(zoneId).toInstant().toEpochMilli(),
                            reminderEnabled
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Lưu việc")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Việc mới cho mèo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên việc") },
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Ghi chú (tùy chọn)") },
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        color = Color(0xFFFFE5F1),
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { showDatePicker = true }
                                .padding(12.dp)
                        ) {
                            Text(
                                "Ngày",
                                color = Color(0xFF6C4A93),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(dateLabel, color = Color(0xFF3A1D5D))
                        }
                    }
                    OutlinedTextField(
                        value = timeInput,
                        onValueChange = { value ->
                            val digitsOnly = value.filter { it.isDigit() }.take(4)
                            timeInput = when {
                                digitsOnly.length <= 2 -> digitsOnly
                                else -> digitsOnly.substring(0, 2) +
                                        ":" +
                                        digitsOnly.substring(2)
                            }
                            timeInputError = false
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Giờ (HH:mm)") },
                        placeholder = { Text("Ví dụ: 08:30") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        isError = timeInputError,
                        supportingText = {
                            if (timeInputError) {
                                Text("Vui lòng nhập giờ hợp lệ theo định dạng HH:mm")
                            }
                        }
                    )
                }
                ReminderSelector(
                    enabled = reminderEnabled,
                    onToggle = { reminderEnabled = !reminderEnabled }
                )
            }
        }
    )
}

@Composable
private fun ReminderSelector(enabled: Boolean, onToggle: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (enabled) Color(0xFFFEF6FF) else Color(0xFFF0F0F0),
        modifier = Modifier.clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (enabled)
                    Icons.Filled.Notifications
                else
                    Icons.Outlined.NotificationsOff,
                contentDescription = null,
                tint = Color(0xFF6C4A93)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nhắc nhở",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A1D5D)
                )
                Text(
                    text = if (enabled) "Mèo sẽ nhắc nhở bạn đúng giờ."
                    else "Mèo sẽ im lặng.",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF6C4A93)
                )
            }
            Text(
                text = if (enabled) "Bật" else "Tắt",
                color = Color(0xFFB3426F),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun LocalDate.toEpochMilli(zoneId: ZoneId): Long =
    this.atStartOfDay(zoneId).toInstant().toEpochMilli()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    uiState: TaskUiState,
    onAddTask: (String, String, Long, Boolean) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onToggleCompletion: (Task, Boolean) -> Unit,
    onToggleReminder: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onLogout: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showConfirmLogout by remember { mutableStateOf(false) }

    val gradientBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color(0xFFFFF0F5),
                Color(0xFFFDE7F3),
                Color(0xFFF5F9FF)
            )
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Mèo Planner") },
                actions = {
                    IconButton(onClick = { showConfirmLogout = true }) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Đăng xuất"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFFA6C9),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Thêm việc")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CatGreetingCard(
                    selectedDate = uiState.selectedDate,
                    taskCount = uiState.tasksForSelectedDate.size
                )
                SummaryRow(
                    weekCount = uiState.daysWithTasksInWeek,
                    monthCount = uiState.daysWithTasksInMonth
                )
                WeekDateSelector(
                    selectedDate = uiState.selectedDate,
                    onSelectDate = onSelectDate
                )
                TaskList(
                    tasks = uiState.tasksForSelectedDate,
                    onToggleCompletion = onToggleCompletion,
                    onToggleReminder = onToggleReminder,
                    onDeleteTask = onDeleteTask
                )
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, desc, time, reminder ->
                onAddTask(title, desc, time, reminder)
                showAddDialog = false
            }
        )
    }

    if (showConfirmLogout) {
        AlertDialog(
            onDismissRequest = { showConfirmLogout = false },
            title = { Text("Đăng xuất?") },
            text = { Text("Bạn có chắc muốn đăng xuất không?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmLogout = false
                    onLogout()
                }) {
                    Text("Đăng xuất")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmLogout = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

