package com.fit2081.Luke_34080481.nutritrack


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Save
import java.util.Calendar
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.Alignment

class Questionnaire : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar() }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                        Questions()
                    }
                }
            }
        }
    }

    data class FoodOption(val label: String, var isChecked: MutableState<Boolean> = mutableStateOf(false))
    data class Persona(val name: String, var desc: String, var image: Int, var isSelected: MutableState<Boolean> = mutableStateOf(false))

    @Preview
    @Composable
    fun Questions() {
        val context = LocalContext.current
        val appHome = Intent(context, AppHome::class.java)
        val user_id = intent.getStringExtra("USER_ID")
        val user_phone = intent.getStringExtra("USER_PHONE")
        val sharedPref = context.getSharedPreferences(user_id, Context.MODE_PRIVATE)
        var foodTypes = remember { mutableListOf(
            FoodOption("Fruits"),
            FoodOption("Vegetables"),
            FoodOption("Grains"),
            FoodOption("Red Meat"),
            FoodOption("Seafood"),
            FoodOption("Poultry"),
            FoodOption("Fish"),
            FoodOption("Eggs"),
            FoodOption("Nuts/Seeds")
        ) }

        // Loading up previous questionnaire data for food categories
        val previousFood = sharedPref.getString("likedFood", null)
        foodTypes.forEach { option ->
            if (previousFood?.contains(option.label) == true) {
                option.isChecked.value = true
            }
        }

        val personas = remember { mutableListOf<Persona>(
            Persona("Health Devotee", "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.", R.drawable.persona_1),
            Persona("Mindful Eater", "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.", R.drawable.persona_2),
            Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.", R.drawable.persona_3),
            Persona("Balance Seeker", "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.", R.drawable.persona_4),
            Persona("Health Procrastinator", "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.", R.drawable.persona_5),
            Persona("Food Carefree", "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.", R.drawable.persona_6)
        ) }

        // Loading up previous questionnaire data for persona
        val previousPersonaString = sharedPref.getString("chosenPersona", personas[0].name)
        val previousPersona = remember { mutableStateOf(personas[0]) }

        personas.forEach { persona ->
            if (persona.name == previousPersonaString) {
                previousPersona.value = persona
            }
        }


        var selectedPersona by remember { mutableStateOf<Persona>(previousPersona.value) }

        // Loading up previous questionnaire data for timings
        val previousBiggestMealTime = sharedPref.getString("biggestMealTime", "")
        val previousSleepTime = sharedPref.getString("sleepTime", "")
        val previousWakeUpTime = sharedPref.getString("wakeUpTime", "")

        var biggestMealTime by remember { mutableStateOf(previousBiggestMealTime) }
        var sleepTime by remember { mutableStateOf(previousSleepTime) }
        var wakeUpTime by remember { mutableStateOf(previousWakeUpTime) }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
            FoodCategories(foodTypes)
            HorizontalDivider(thickness = 1.dp)
            PersonaPicker(personas, previousPersona.value) { persona ->
                selectedPersona = persona
            }
            HorizontalDivider(thickness = 1.dp)
            Timings(
                biggestMealTime = biggestMealTime.toString(),
                onBiggestMealTimeSelected = { biggestMealTime = it },
                sleepTime = sleepTime.toString(),
                onSleepTimeSelected = { sleepTime = it },
                wakeUpTime = wakeUpTime.toString(),
                onWakeUpTimeSelected = { wakeUpTime = it }
            )
            HorizontalDivider(thickness = 1.dp)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    val likedFood = foodTypes.filter { it.isChecked.value }.joinToString { it.label }
                    val sharedPref = context.getSharedPreferences(user_id, Context.MODE_PRIVATE).edit()
                    if (biggestMealTime == "" || sleepTime == "" || wakeUpTime == "") {
                        Toast.makeText(context, "Error: timing field is empty", Toast.LENGTH_SHORT).show()
                    } else {
                        sharedPref.putString("likedFood", likedFood)
                        sharedPref.putString("chosenPersona", selectedPersona.name)
                        sharedPref.putString("biggestMealTime", biggestMealTime)
                        sharedPref.putString("sleepTime", sleepTime)
                        sharedPref.putString("wakeUpTime", wakeUpTime)
                        sharedPref.apply()

                        // Add input validation

                        appHome.putExtra("USER_ID", user_id)
                        appHome.putExtra("USER_PHONE", user_phone)
                        context.startActivity(appHome)
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text(text = "Save")
                }
            }
            // Display selected values
            /*Text(
                text = "Selected: ${foodTypes.filter { it.isChecked.value }.joinToString { it.label }}",
                modifier = Modifier.padding(top = 16.dp)
            )*/
        }
    }

    data class Time(var hour: Int = 0, var minute: Int = 0)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Timings(
                biggestMealTime: String,
                onBiggestMealTimeSelected: (String) -> Unit,
                sleepTime: String,
                onSleepTimeSelected: (String) -> Unit,
                wakeUpTime: String,
                onWakeUpTimeSelected: (String) -> Unit) {
        Text(
            text = "Timings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 10.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "What time of day approx. do you normally eat your biggest meal?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(horizontal = 15.dp))
            timePickerButton(selectedTime = biggestMealTime, onTimeSelected = onBiggestMealTimeSelected)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "What time of day approx. do you sleep?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(horizontal = 15.dp))
            timePickerButton(selectedTime = sleepTime, onTimeSelected = onSleepTimeSelected)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "What time of day approx. do you wake up in the morning?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(horizontal = 15.dp))
            timePickerButton(selectedTime = wakeUpTime, onTimeSelected = onWakeUpTimeSelected)
        }
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
    }



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun timePickerButton(selectedTime: String, onTimeSelected: (String) -> Unit) {
        var showDialog by remember { mutableStateOf(false) }
        var selectedTime by remember { mutableStateOf(selectedTime) }

        OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.size(width = 120.dp, height = 35.dp).fillMaxHeight()) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = "Clock Button",
                modifier = Modifier.padding(end = 2.dp)
            )
            Text(selectedTime)
        }

        if (showDialog) {
            DialWithDialog(
                onConfirm = { timePickerState ->
                    selectedTime = "${timePickerState.hour}:${timePickerState.minute}"
                    onTimeSelected(selectedTime)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DialWithDialog(
        onConfirm: (TimePickerState) -> Unit,
        onDismiss: () -> Unit,
    ) {
        val currentTime = Calendar.getInstance()

        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = false,
        )

        TimePickerDialog(
            onDismiss = { onDismiss() },
            onConfirm = { onConfirm(timePickerState) }
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    }

    @Composable
    fun TimePickerDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        content: @Composable () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text("OK")
                }
            },
            text = { content() }
        )
    }


    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun FoodCategories(foodTypes: MutableList<FoodOption>) {
        Text(
            text = "Tick all the food categories you can eat",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        FlowRow(
            maxItemsInEachRow = 3,
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            foodTypes.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(0.dp).size(width = 115.dp, height = 40.dp)
                ) {
                    Checkbox(
                        checked = item.isChecked.value,
                        onCheckedChange = { isChecked -> item.isChecked.value = isChecked }
                    )
                    Text(text = item.label, modifier = Modifier.padding(start = 0.dp), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun PersonaPicker(personas: MutableList<Persona>, oldPersona: Persona, onPersonaSelected: (Persona) -> Unit) {
        Text(
            text = "Your Persona",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = "People can be broadly classified into 6 different types based on their eating preference. " +
                    "Click on each button below to find out the different types, and select the type that best fits you!",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.padding(5.dp))

        FlowRow(
            maxItemsInEachRow = 3,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            personas.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(0.dp)
                ) {
                    PersonaModelButton(item)
                }
            }
        }

        Spacer(modifier = Modifier.padding(5.dp))

        var expanded by remember { mutableStateOf(false) }
        val textFieldState = rememberTextFieldState(oldPersona.name)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.padding(top = 0.dp, start = 20.dp, end = 20.dp, bottom = 20.dp).fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                state = textFieldState,
                readOnly = true,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text("Which persona best fits you?") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona.name, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd(persona.name)
                            onPersonaSelected(persona)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }

    @Composable
    fun PersonaModelButton(persona: Persona) {
        var showDialog by remember { mutableStateOf(false) }
        Button(onClick = { showDialog = true }, modifier = Modifier.size(width = 170.dp, height = 60.dp).padding(vertical = 5.dp), contentPadding = PaddingValues()) {
            Text(text = persona.name, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Column {
                        Image(
                            painter = painterResource(id = persona.image),
                            contentDescription = "",
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        HorizontalDivider(thickness = 2.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = persona.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        HorizontalDivider(thickness = 2.dp)
                    }
                },
                text = {
                    Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)) {
                        Text(
                            text = persona.desc,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        TopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            title = {
                Text(
                    "Food Intake Questionnaire",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Button"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}
