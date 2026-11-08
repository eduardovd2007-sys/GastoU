package com.example.gastou.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastou.ui.theme.GastoUGreen
import com.example.gastou.ui.theme.GastoULightGreen
import com.example.gastou.ui.theme.GastoUTextDark
import com.example.gastou.ui.theme.GastoUTextFieldBorder
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatYearMonth(yearMonth: YearMonth): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
    return yearMonth.format(formatter).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

fun formatMonth(yearMonth: YearMonth): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM", Locale("es", "ES"))
    return yearMonth.format(formatter).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

@Composable
fun GastoULogo(modifier: Modifier = Modifier, fontSize: Int = 36) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = GastoUTextDark, fontWeight = FontWeight.ExtraBold)) {
                append("Gasto")
            }
            withStyle(style = SpanStyle(color = GastoULightGreen, fontWeight = FontWeight.ExtraBold)) {
                append("U")
            }
        },
        fontSize = fontSize.sp,
        modifier = modifier
    )
}

@Composable
fun GastoUButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = GastoUGreen,
            disabledContainerColor = GastoUGreen.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun GastoUTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = GastoUTextFieldBorder,
            focusedBorderColor = GastoUGreen,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}
