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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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

    @OptIn(ExperimentalLayoutApi::class)
    @Preview
    @Composable
    fun Questions() {
        val foodTypes = remember { mutableListOf(
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

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp)) {
            Text(
                text = "Tick all the food categories you can eat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                maxItemsInEachRow = 3,
                modifier = Modifier.fillMaxWidth(),
            ) {
                foodTypes.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Checkbox(
                            checked = item.isChecked.value,
                            onCheckedChange = { isChecked -> item.isChecked.value = isChecked }
                        )
                        Text(text = item.label, modifier = Modifier.padding(start = 0.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Display selected values
            Text(
                text = "Selected: ${foodTypes.filter { it.isChecked.value }.joinToString { it.label }}",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    @Composable
    fun FoodCheckBox(food: String) {
        var checked by remember { mutableStateOf(false) }
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
        Text(
            text = food,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
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
