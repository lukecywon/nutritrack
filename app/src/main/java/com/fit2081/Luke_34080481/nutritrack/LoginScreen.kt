package com.fit2081.Luke_34080481.nutritrack

import android.R.integer
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.platform.LocalContext
import kotlin.js.ExperimentalJsFileName
import android.content.Context
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.core.content.edit

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.nutritrack_logo_transparent),
                            contentDescription = "NutriTrack Logo",
                            modifier = Modifier.padding(16.dp)
                        )
                        Row {
                            Text(
                                text = "Nutri",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Track",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Login()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Login() {
        val context = LocalContext.current
        val userData = readCSVFromAssets(context, "userData.csv")
        var allUserIds= mutableListOf<Any>()

        // Add all ID's from CSV into dropdown list
        for (row in userData.drop(1)) {
            allUserIds.add(row[1])
        }

        var expanded by remember { mutableStateOf(false) }
        val textFieldState = rememberTextFieldState(allUserIds[0].toString())
        var phoneNum by remember { mutableStateOf("") }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 10.dp).fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                state = textFieldState,
                readOnly = true,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text("My ID (Provided by your Clinician)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                allUserIds.forEach { allUserIds ->
                    DropdownMenuItem(
                        text = { Text(allUserIds.toString(), style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd(allUserIds.toString())
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        OutlinedTextField(
            value = phoneNum,
            onValueChange = { phoneNum = it },
            label = { Text("Phone Number") },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).fillMaxWidth()
        )

        ElevatedButton(
            onClick = {
                var validUser = verifyUserExistence(textFieldState.text, phoneNum, userData)
                if (validUser) {
                    context.getSharedPreferences("NutriTrack", Context.MODE_PRIVATE).edit() {
                        putString("userId", textFieldState.text.toString())
                        putString("phoneNum", phoneNum)
                    }
                    context.startActivity(Intent(context, Questionnaire::class.java))
                } else {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_LONG).show()
                }

            },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).fillMaxWidth(),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            enabled = true,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = "Continue",
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    fun readCSVFromAssets(context: Context, fileName: String): List<List<String>> {
        var assets = context.assets
        val csvList = mutableListOf<List<String>>()

        try {
            val inputStream = assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.forEach { line ->
                    csvList.add(line.split(","))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return csvList
    }

    fun verifyUserExistence(userID: CharSequence, phoneNumber: String, userData: List<List<Any>>): Boolean {
        var validUser = false

        for (row in userData.drop(1)) {
            if (row[0] == phoneNumber && row[1] == userID) {
                validUser = true
            }
        }

        return validUser
    }
}