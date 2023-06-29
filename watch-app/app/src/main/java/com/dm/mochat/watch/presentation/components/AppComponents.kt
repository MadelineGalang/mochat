package com.dm.mochat.watch.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.dm.mochat.watch.presentation.theme.LightCyan
import com.dm.mochat.watch.presentation.theme.LightSkyBlue

@Composable
fun NormalTextComponent(text:String, color:Color) {
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
        textAlign = TextAlign.Center
    )
}

@Composable
fun LargeTextComponent(text:String, color:Color, bold:Boolean = false) {
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
        textAlign = TextAlign.Center
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
fun IconButtonComponent(iconVector:ImageVector, description:String, onButtonClick: () -> Unit ) {
    Button(
        onClick = { onButtonClick.invoke() },
        colors = ButtonDefaults.primaryButtonColors()
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
    placeholder:String,
    onTextChange: (String) -> Unit,
    isPassword:Boolean = false
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
                if (textValue.value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = LightSkyBlue
                    )
                }
                innerTextField()
            }
        }
    )
}