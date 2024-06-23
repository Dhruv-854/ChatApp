package com.dhruv.chatapp.presentation.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputTextFiled(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange : (String)->Unit,
    lable : String
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = {Text(lable)},
        shape = RoundedCornerShape(10.dp)
    )
}