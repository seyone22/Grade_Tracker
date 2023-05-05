package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Room

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowLandingPage(database : AppDatabase) {
    var showDialog by remember { mutableStateOf(false) }

    //Texts for the new entry dialog
    var textField1 by remember { mutableStateOf("") }
    var textField2 by remember { mutableStateOf("") }
    var textField3 by remember { mutableStateOf("") }

    //Use the Room DB to populate subjects
    val subjectDao = database.moduleDao()
    var subjects by remember { mutableStateOf(subjectDao.getAll().toMutableStateList()) }

    // 1. Use mutableStateListOf for managing subjects list
    //val subjects by remember { mutableStateOf(SampleData.allSubjectSample.toMutableStateList()) }

    //variables for bottom bar
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Modules", "Courses", "You")


    // 2. Implement the onSubjectUpdated callback
    fun handleSubjectUpdated(updatedSubject: Module) {
        val index = subjects.indexOfFirst { it.id == updatedSubject.id }
        if (index >= 0) {
            subjects[index] = updatedSubject
            subjects = subjects.toMutableList().toMutableStateList() // Trigger a recomposition by reassigning the subjects list

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Modules") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(modifier = Modifier.fillMaxHeight().padding(8.dp, 64.dp, 8.dp, 77.dp).fillMaxWidth()) {
                when (selectedItem) {
                    0 -> {
                        // Code for displaying all Module content
                        AllSubjects(subjectDao, subjects, onSubjectUpdated = ::handleSubjectUpdated)
                    }
                    1 -> {
                        // Code for displaying Artists content
                        Text("Artists Content")
                    }
                    2 -> {
                        // Code for displaying Playlists content
                        Text("Playlists Content")
                    }
                }
            }
        }
    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = textField1,
                        onValueChange = { textField1 = it },
                        label = { Text("Module ID") }
                    )
                    OutlinedTextField(
                        value = textField2,
                        onValueChange = { textField2 = it },
                        label = { Text("Module Name") }
                    )
                    OutlinedTextField(
                        value = textField3,
                        onValueChange = { textField3 = it },
                        label = { Text("Grade") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    // 4. Add the new subject to the subjects list
                    var modul = Module(textField1, textField2, textField3)
                    subjectDao.updateModule(modul)
                    subjects.add(modul)
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}