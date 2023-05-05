package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database by lazy { AppDatabase.getDatabase(this) }

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowLandingPage(database)
                }
            }
        }
    }
}

//Elements of Landing Page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeCard(subjects: List<Module>, subjectDao: ModuleDao, subject: Module, onSubjectUpdated: (Module) -> Unit) {
    // 1. Create a state for holding the dialog visibility status
    val showDialog = remember { mutableStateOf(false) }

    // 2. Use mutable states for the text fields
    val id = remember { mutableStateOf(subject.id) }
    val name = remember { mutableStateOf(subject.name) }
    val grade = remember { mutableStateOf(subject.grade) }

    val showDropdownMenu = remember { mutableStateOf(false) }

    fun handleMenuItemClick(item: String) {
        when (item) {
            "Delete" -> {
                subjectDao.deleteModule(subject)
            }
            "More Details" -> {
                // Handle the action for Option 2
            }
            "Share" -> {
                // Handle the action for Option 3
            }
        }
        showDropdownMenu.value = false
    }

    // 4. Create a dialog with three text boxes, showing the subject's data
    if (showDialog.value) {
        // 1. Create a copy of the subject object
        val subjectCopy = remember { subject.copy() }
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Subject Details") },
            text = {
                Column {
                    OutlinedTextField(value = subjectCopy.id, onValueChange = { subjectCopy.id = it }, label = { Text("Subject ID") })
                    OutlinedTextField(value = subjectCopy.name, onValueChange = { subjectCopy.name = it }, label = { Text("Subject Name") })
                    OutlinedTextField(value = subjectCopy.grade, onValueChange = { subjectCopy.grade = it }, label = { Text("Subject Grade") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // 5. Update the callback function when the user confirms the changes
                    onSubjectUpdated(subjectCopy)
                    showDialog.value = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. Bind the click function to the `Card`'s `onClick` parameter
    ElevatedCard(onClick = { showDialog.value = !showDialog.value }, Modifier
        .padding(0.dp, 0.dp, 0.dp, 8.dp)
        .fillMaxWidth()) {
        Row(modifier = Modifier
            .padding(8.dp, 8.dp)
            .fillMaxWidth()) {

            fun getColorFromString(str: String): Color {
                val hash = str.hashCode()
                val r = (hash and 0xFF0000 shr 16) / 255f
                val g = (hash and 0x00FF00 shr 8) / 255f
                val b = (hash and 0x0000FF) / 255f
                return Color(r, g, b)
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                Text(
                    text = id.value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = name.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // 3. Add the IconButton with the three-dot icon and bind the click function to the `onClick` parameter
            IconButton(onClick = { showDropdownMenu.value = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More")
            }

            DropdownMenu(
                expanded = showDropdownMenu.value,
                onDismissRequest = { showDropdownMenu.value = false },
                offset = DpOffset(x = (-79).dp, y = (-30).dp)

            ) {
                listOf("Delete", "More Details", "Share").forEach { item ->
                    DropdownMenuItem(text = {Text(item)}, onClick = { handleMenuItemClick(item) })
                }
            }
        }
    }
}

@Composable
fun AllSubjects(subjectDao: ModuleDao, subjects: List<Module>, onSubjectUpdated: (Module) -> Unit) {
    // 1. Remove the unnecessary data variable

    LazyColumn {
        items(subjects) { subject ->
            // 2. Pass the onSubjectUpdated callback to GradeCard
            GradeCard(subjects, subjectDao, subject, onSubjectUpdated)
        }
    }
}