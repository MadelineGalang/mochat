package com.dm.mochat.watch.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.dm.mochat.watch.presentation.theme.LightCyan
import com.dm.mochat.watch.presentation.theme.LightSkyBlue

@Composable
fun NormalTextComponent(text: String, color: Color = LightCyan, alignment: TextAlign = TextAlign.Center) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 14.dp),
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = color,
        textAlign = alignment
    )
}

@Composable
fun LargeTextComponent(text: String, color: Color = LightCyan, bold: Boolean = false, alignment: TextAlign = TextAlign.Center) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 18.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = if(bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = color,
        textAlign = alignment
    )
}

@Composable
fun ButtonComponent(text:String, onButtonClick: () -> Unit, textColor:Color, buttonColor:Color) {
    Button(
        onClick = { onButtonClick.invoke() },
        modifier = Modifier
            .fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(buttonColor)
    ) {
        LargeTextComponent(text = text, color = textColor, bold = true)
    }
}

@Composable
fun IconButtonComponent(iconVector:ImageVector, description:String, onButtonClick: () -> Unit, primaryColor:Boolean = true ) {
    Button(
        onClick = { onButtonClick.invoke() },
        colors = if (primaryColor) ButtonDefaults.primaryButtonColors() else ButtonDefaults.secondaryButtonColors()
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = description
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextFieldComponent(
    placeholder: String,
    onTextChange: (String) -> Unit,
    isPassword: Boolean = false,
    icon: ImageVector? = null
) {
    val textValue = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = TextFieldValue(textValue.value),
        onValueChange = {
            textValue.value = it.text
            onTextChange(it.text)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = LightCyan
        ),
        visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LightCyan,
                        shape = RoundedCornerShape(size = 30.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row {
                    if (textValue.value.isEmpty()) {
                        if (icon != null) {
                            Icon(icon, contentDescription = null,tint=LightSkyBlue)
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = LightSkyBlue
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextFieldWithValueComponent(
    value: String,
    placeholder: String,
    onTextChange: (String) -> Unit,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    icon: ImageVector? = null
) {
    val textValue = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = TextFieldValue(value),
        onValueChange = {
            textValue.value = it.text
            onTextChange(it.text)
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = LightCyan
        ),
        visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LightCyan,
                        shape = RoundedCornerShape(size = 30.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row {
                    if (textValue.value.isEmpty()) {
                        if (icon != null) {
                            Icon(icon, contentDescription = null,tint=LightSkyBlue)
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = LightSkyBlue
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun ChipComponent(text:String, onChipClick: () -> Unit, textColor:Color, chipColor:Color) {
    Chip(
        onClick = { onChipClick.invoke() },
        label = {
            NormalTextComponent(text = text, color = textColor, alignment = TextAlign.Left)
        },
        colors = ChipDefaults.chipColors(chipColor),
        modifier = Modifier.fillMaxSize()
    )
}