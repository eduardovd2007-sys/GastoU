package com.example.gastou.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastou.ui.components.GastoUButton
import com.example.gastou.ui.components.GastoULogo
import com.example.gastou.ui.components.GastoUTextField
import com.example.gastou.ui.theme.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        GastoULogo(fontSize = 24, modifier = Modifier.offset(x = (-24).dp))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder for small illustration
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GastoUBackgroundLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = GastoUGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Crea tu cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GastoUTextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Empieza a administrar tu dinero inteligente\ny alcanza tus metas universitarias",
                    fontSize = 14.sp,
                    color = GastoUTextGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Form Fields
                FormFieldLabel("Nombre completo")
                GastoUTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Tu nombre y apellido",
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = GastoULightGreen) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                FormFieldLabel("Correo institucional o personal")
                GastoUTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "ejemplo@correo.edu / personal",
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = GastoULightGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormFieldLabel("Contraseña")
                GastoUTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Crea una contraseña segura",
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = GastoULightGreen) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormFieldLabel("Confirmar contraseña")
                GastoUTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repite tu contraseña",
                    leadingIcon = { Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = GastoULightGreen) },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GastoUGreen,
                            uncheckedColor = Color.Gray
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    
                    Text(
                        text = buildAnnotatedString {
                            append("Acepto los ")
                            withStyle(style = SpanStyle(color = GastoUGreen, fontWeight = FontWeight.Bold)) {
                                append("Términos y Condiciones")
                            }
                            append(" y la ")
                            withStyle(style = SpanStyle(color = GastoUGreen, fontWeight = FontWeight.Bold)) {
                                append("Política de Privacidad")
                            }
                            append(" de GastoU.")
                        },
                        fontSize = 12.sp,
                        color = GastoUTextGray,
                        modifier = Modifier.padding(top = 14.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                GastoUButton(
                    text = "Crear cuenta →",
                    onClick = { /* Handle Register */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = GastoUTextGray)) {
                            append("¿Ya tienes una cuenta? ")
                        }
                        withStyle(style = SpanStyle(color = GastoUGreen, fontWeight = FontWeight.Bold)) {
                            append("Iniciar sesión")
                        }
                    },
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(8.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FormFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = GastoUTextDark,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GastoUTheme {
        RegisterScreen(onNavigateBack = {}, onNavigateToLogin = {})
    }
}
