package com.fit2081.Luke_34080481.nutritrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.area.WindowAreaInfo
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextDecoration

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(
            ) {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding).fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {

                        Image(
                            painter = painterResource(R.drawable.nutritrack_logo_transparent),
                            contentDescription = "Nutritrack Logo",
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

                        ElevatedCard(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(
                                    text = "DISCLAIMER",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onError
                            )
                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                DisclaimerText()
                            }
                        }

                        LoginButton()

                        Text(
                            text = "Made with ❤\uFE0F by Luke Won (34080481)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraLight,
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun LoginButton() {
        val context = LocalContext.current
        ElevatedButton(
            onClick = {
                context.startActivity(Intent(context, LoginScreen::class.java))
            },
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
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
                text = "Login",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
fun DisclaimerText() {
    val annotatedLinkString: AnnotatedString = remember {
        buildAnnotatedString {
            val style = SpanStyle(
                color = Color(0xFF93000A),
                fontSize = 10.sp,
            )
            val styleCenter = SpanStyle(
                color = Color(0xff64B5F6),
                fontSize = 10.sp,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.SemiBold
            )
            withStyle(
                style = style
            ) {
                append("This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen. Use this app at your own risk. If you’d like to an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students): ")
            }
            withLink(LinkAnnotation.Url(url = "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition")) {
                withStyle(
                    style = styleCenter
                ) {
                    append("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition")
                }
            }
        }
    }
    Column(
    ) {
        Text(annotatedLinkString)
    }
}

