package com.fit2081.Luke_34080481.nutritrack

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.sequences.forEach

class AppHome : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val context = LocalContext.current
                val navController: NavHostController = rememberNavController()
                var selectedItem by remember { mutableIntStateOf(0) }
                val userData = readCSVFromAssets(context, "userData.csv")
                val user_id = intent.getStringExtra("USER_ID")
                val user_phone = intent.getStringExtra("USER_PHONE")
                var currentUser by remember { mutableStateOf(listOf<String>()) }
                var score by remember { mutableStateOf(0f) }
                var currentUserStats = mutableMapOf<String, Float>()
                var gender = ""

                // Get gender of current user and retrieve total score accordingly
                for (row in userData) {
                    if (row[1] == user_id && row[0] == user_phone) { // Current user row
                        var c = 0
                        currentUser = row
                        if (row[2] == "Male") {
                            score = row[3].toFloat()/100
                            gender = "Male"
                        } else if (row[2] == "Female") {
                            score = row[4].toFloat()/100
                            gender = "Female"
                        }
                    }
                }

                // Get indexes of important scores based on gender
                var scoreIndexes = mutableListOf<Int>()
                if (gender == "Male") {
                    userData[0].mapIndexedNotNull { index, element ->
                        if (element.contains("HEIFAscoreMale")) {
                            scoreIndexes.add(index)
                        }
                    }
                } else if (gender == "Female") {
                    userData[0].mapIndexedNotNull { index, element ->
                        if (element.contains("HEIFAscoreFemale")) {
                            scoreIndexes.add(index)
                        }
                    }
                }

                Log.d("DEBUG", "Gender: " + gender)
                Log.d("DEBUG", "Indexes: " + scoreIndexes.toString())

                var headings = mutableListOf<String>()
                scoreIndexes.forEach { index ->
                    headings.add(userData[0][index])
                    currentUserStats.put(userData[0][index], currentUser[index].toFloat())
                }

                currentUserStats.put("HEIFAscore", score * 100)

                Log.d("DEBUG", "Headings: " + headings)
                Log.d("DEBUG", "All Scores: " + currentUserStats)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(navController, selectedItem) { selected ->
                            selectedItem = selected
                        }
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        AppNavHost(innerPadding, navController, score, currentUserStats) { selected ->
                            selectedItem = selected
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AppNavHost(innerPadding: PaddingValues, navController: NavHostController, score: Float, userStats: Map<String, Float>, onItemSelected: (Int) -> Unit) {
        NavHost(
            navController = navController,
            startDestination = "Home"
        ) {
            composable("Home") {
                HomeScreen(innerPadding, navController, score, onItemSelected)
            }
            composable("Insights") {
                InsightsScreen(innerPadding, navController, userStats, onItemSelected)
            }
            composable("NutriCoach") {
                NutriCoach()
            }
            composable("Settings") {
                Settings()
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun HomeScreen(innerPadding: PaddingValues, navController: NavController, score: Float, onItemSelected: (Int) -> Unit) {
        val user_id = intent.getStringExtra("USER_ID")
        val user_phone = intent.getStringExtra("USER_PHONE")
        val context = LocalContext.current

        Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
            Text(
                text = "Hello,",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraLight
            )
            Text(
                text = "User " + user_id.toString(),
                style = MaterialTheme.typography.titleLargeEmphasized,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "You've already filled in your Food Intake Questionnaire, but you can change still change the details",
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f),
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    //Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    IconButton(
                        onClick = {
                            val questionnaire = Intent(context, Questionnaire::class.java)
                            val user_id = intent.getStringExtra("USER_ID")
                            val user_phone = intent.getStringExtra("USER_PHONE")

                            questionnaire.putExtra("USER_ID", user_id)
                            questionnaire.putExtra("USER_PHONE", user_phone)
                            context.startActivity(questionnaire)
                        },
                        modifier = Modifier.padding(end = 10.dp),
                    ) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                    }
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()) {
                    Image(painter = painterResource(R.drawable.food), contentDescription = "Food")
                }
                Spacer(modifier = Modifier.padding(vertical = 5.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 3.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "My Score",
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            onItemSelected(1)
                            navController.navigate("Insights")
                        }
                    ) {
                        Text(
                            text = "See all scores",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraLight,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = "Arrow", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 5.dp))
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.padding(vertical = 5.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Your Food Quality Score",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Box(contentAlignment = Alignment.Center, modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 10.dp)) {
                        CircularProgressIndicator(
                            progress = { score },
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            color = Color.Green,
                            modifier = Modifier.size(90.dp)
                        )
                        Text(
                            text = "${(score * 100)}/100",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 5.dp))
            }
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "What is the Food Quality Score?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
                Spacer(modifier = Modifier.padding(vertical = 1.dp))
                Text(
                    text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n" +
                            "\n" +
                            "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun InsightsScreen(innerPadding: PaddingValues, navController: NavController, userStats: Map<String, Float>, onItemSelected: (Int) -> Unit) {
        var scoreMap = mapOf<String, String>()

        if (userStats.containsKey("DiscretionaryHEIFAscoreMale")) {
            scoreMap = mapOf<String, String>(
                "Vegetables" to "VegetablesHEIFAscoreMale",
                "Fruits" to "FruitHEIFAscoreMale",
                "Grains & Cereals" to "GrainsandcerealsHEIFAscoreMale",
                "Whole Grains" to "WholegrainsHEIFAscoreMale",
                "Meat & Alternatives" to "MeatandalternativesHEIFAscoreMale",
                "Diary" to "DairyandalternativesHEIFAscoreMale",
                "Sodium" to "SodiumHEIFAscoreMale",
                "Alcohol" to "AlcoholHEIFAscoreMale",
                "Water" to "WaterHEIFAscoreMale",
                "Sugar" to "SugarHEIFAscoreMale",
                "Saturated Fat" to "SaturatedFatHEIFAscoreMale",
                "Unsaturated Fat" to "UnsaturatedFatHEIFAscoreMale",
                "Discretionary Foods" to "DiscretionaryHEIFAscoreMale"
            )
        } else if (userStats.containsKey("DiscretionaryHEIFAscoreFemale")) {
            scoreMap = mapOf<String, String>(
                "Vegetables" to "VegetablesHEIFAscoreFemale",
                "Fruits" to "FruitHEIFAscoreFemale",
                "Grains & Cereals" to "GrainsandcerealsHEIFAscoreFemale",
                "Whole Grains" to "WholegrainsHEIFAscoreFemale",
                "Meat & Alternatives" to "MeatandalternativesHEIFAscoreFemale",
                "Diary" to "DairyandalternativesHEIFAscoreFemale",
                "Sodium" to "SodiumHEIFAscoreFemale",
                "Alcohol" to "AlcoholHEIFAscoreFemale",
                "Water" to "WaterHEIFAscoreFemale",
                "Sugar" to "SugarHEIFAscoreFemale",
                "Saturated Fat" to "SaturatedFatHEIFAscoreFemale",
                "Unsaturated Fat" to "UnsaturatedFatHEIFAscoreFemale",
                "Discretionary Foods" to "DiscretionaryHEIFAscoreFemale"
            )
        }



        Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)) {
            Text(
                text = "Food Score",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraLight
            )
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Insights",
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp
                )
                TextButton(
                    onClick = {
                        val shareIntent = Intent(ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, userStats.toString())
                        startActivity(Intent.createChooser(shareIntent, "Share intent via"))
                    },
                ) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Share", tint = Color.DarkGray)
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text(
                        text = "Share with someone",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color.DarkGray
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 7.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                var c = 0
                scoreMap.forEach { (key, value) ->
                    if (c != 0) {
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    FoodStatSlider(key, userStats.getValue(value))
                    c++
                }
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
            }

            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Food Quality Score",
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.ExtraBold
                )
                TextButton(
                    onClick = {
                        onItemSelected(2)
                        navController.navigate("NutriCoach")
                    },
                ) {
                    Icon(imageVector = Icons.Filled.SelfImprovement, contentDescription = "Improvement", tint = Color.DarkGray)
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text(
                        text = "Improve my diet",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = Color.DarkGray
                    )
                }
            }


            Spacer(modifier = Modifier.padding(vertical = 3.dp))
            HorizontalDivider(thickness = 1.dp)
            Spacer(modifier = Modifier.padding(vertical = 3.dp))
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = userStats.getValue("HEIFAscore"),
                    onValueChange = {},
                    enabled = false,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.LightGray,
                        disabledThumbColor = MaterialTheme.colorScheme.primary,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                        disabledInactiveTrackColor = Color.LightGray
                    ),
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(
                    text = if (userStats.getValue("HEIFAscore") % 1 == 0f) userStats.getValue("HEIFAscore").toInt().toString() + "/100" else userStats.getValue("HEIFAscore").toString() + "/100",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(width = 65.dp, height = 20.dp)
                )
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FoodStatSlider(name: String, value: Float) {
        Spacer(modifier = Modifier.padding(vertical = 2.dp))
        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.padding(horizontal = 5.dp))

            // Slider Implementation of Progress Bar (old design)
            /*Slider(
                value = value,
                onValueChange = {},
                enabled = false,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.LightGray,
                    disabledThumbColor = Color.Green,
                    disabledActiveTrackColor = Color.Green,
                    disabledInactiveTrackColor = Color.White
                ),
                valueRange = 0f..10f,
                modifier = Modifier.size(width = 150.dp, height = 10.dp)
            )*/

            if (name == "Water" || name == "Alcohol") {
                // LinearProgressIndicator of Progress Bar
                LinearProgressIndicator(
                    progress = { value/5 },
                    modifier = Modifier.size(width = 150.dp, height = 10.dp),
                    color = Color.Green,
                    trackColor = Color.White,
                    strokeCap = StrokeCap.Round,

                    )
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(
                    text = if (value % 1 == 0f) value.toInt().toString() + "/5" else value.toString() + "/5",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(width = 60.dp, height = 20.dp)
                )
            } else {
                // LinearProgressIndicator of Progress Bar
                LinearProgressIndicator(
                    progress = { value/10 },
                    modifier = Modifier.size(width = 150.dp, height = 10.dp),
                    color = Color.Green,
                    trackColor = Color.White,
                    strokeCap = StrokeCap.Round,

                    )
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Text(
                    text = if (value % 1 == 0f) value.toInt().toString() + "/10" else value.toString() + "/10",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(width = 60.dp, height = 20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 2.dp))
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun NutriCoach() {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Filled.Psychology, contentDescription = "NutriCoach", modifier = Modifier.size(200.dp))
            Text(
                text = "To be added...",
                style = MaterialTheme.typography.titleLargeEmphasized,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp
            )
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun Settings() {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings", modifier = Modifier.size(200.dp))
            Text(
                text = "To be added...",
                style = MaterialTheme.typography.titleLargeEmphasized,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp
            )
        }
    }

    @Composable
    fun BottomBar(navController: NavController, selectedItem: Int, onItemSelected: (Int) -> Unit) {
        val items = listOf("Home", "Insights", "NutriCoach", "Settings")
        val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Analytics, Icons.Filled.Psychology, Icons.Filled.Settings)
        val unselectedIcons =
            listOf(Icons.Outlined.Home, Icons.Outlined.Analytics, Icons.Outlined.Psychology, Icons.Outlined.Settings)

        NavigationBar() {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                            contentDescription = item
                        )
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = {
                        onItemSelected(index)
                        navController.navigate(item)
                    }
                )
            }
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
}