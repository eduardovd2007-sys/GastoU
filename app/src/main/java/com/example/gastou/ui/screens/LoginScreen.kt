package com.example.gastou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastou.ui.components.GastoUButton
import com.example.gastou.ui.components.GastoULogo
import com.example.gastou.ui.components.GastoUTextField
import com.example.gastou.ui.theme.GastoUBackgroundLight
import com.example.gastou.ui.theme.GastoUGreen
import com.example.gastou.ui.theme.GastoUTextGray
import com.example.gastou.ui.theme.GastoUTheme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Blobs
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-150).dp)
                .clip(CircleShape)
                .background(GastoUBackgroundLight.copy(alpha = 0.5f))
        )
        Box(
            modifier = Modifier
                .size(500.dp)
                .offset(x = 100.dp, y = 100.dp)
                .clip(CircleShape)
                .background(GastoUBackgroundLight.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            GastoULogo()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Controla hoy\ntu mejor mañana",
                color = GastoUTextGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Placeholder for the illustration
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1F2E2)),
                contentAlignment = Alignment.Center
            ) {
                // In a real app, you would use Image() here
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            GastoUTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Correo electrónico"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GastoUTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Contraseña",
                visualTransformation = PasswordVisualTransformation()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GastoUButton(
                text = "Iniciar sesión",
                onClick = onNavigateToHome
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = GastoUTextGray)) {
                        append("¿No tienes cuenta? ")
                    }
                    withStyle(style = SpanStyle(color = GastoUGreen, fontWeight = FontWeight.Bold)) {
                        append("Regístrate")
                    }
                },
                modifier = Modifier
                    .clickable { onNavigateToRegister() }
                    .padding(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    GastoUTheme {
        LoginScreen(onNavigateToRegister = {}, onNavigateToHome = {})
    }
}
