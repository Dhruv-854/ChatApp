package com.dhruv.chatapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dhruv.chatapp.presentation.LCViewModel
import com.dhruv.chatapp.presentation.navigation.Login
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    vm: LCViewModel,
) {
    val userData = vm.userData.value
    var name by rememberSaveable {
        mutableStateOf(userData?.name ?: "")
    }
    var number by rememberSaveable {
        mutableStateOf(userData?.number ?: "")
    }
    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val nameFocusRequester = remember {
        FocusRequester()
    }
    val numberFocusRequester = remember {
        FocusRequester()
    }




    val userName = name
    val firstChar = userName.firstOrNull()?.uppercaseChar() ?: ' '
    val colorForChar = getColorForChar(firstChar).color

    Scaffold {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            ProfileContent(
                onBackClick = {
                    navController.popBackStack()
                },
                onNameChange = { name = it },
                onNumberChange = { number = it },
                firstChar = firstChar,
                colorForChar = colorForChar,
                onLogOutClick = {
                    vm.signOut()
                    navController.navigate(Login) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                name = name,
                number = number,
                onSaveClick = {
                    coroutineScope.launch {
                        vm.inProcess.value = true
                        vm.updateProfile(
                            name = name,
                            number = number
                        )
                        focusManager.clearFocus()
                    }
                },
                nameFocusRequester = nameFocusRequester,
                numberFocusRequester = numberFocusRequester,
            )
        }
    }
}


@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    firstChar: Char,
    colorForChar: Color,
    onLogOutClick: () -> Unit,
    name: String,
    number: String,
    onSaveClick: () -> Unit,
    numberFocusRequester: FocusRequester,
    nameFocusRequester: FocusRequester,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Back", modifier = modifier.clickable {
                    onBackClick.invoke()
                })
                Text(text = "Save", modifier = modifier.clickable {
                    onSaveClick.invoke()
                })
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        Divider(
            modifier = modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(colorForChar),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$firstChar", color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 46.sp
                )
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        Divider(
            modifier = modifier
                .fillMaxWidth()
                .height(4.dp)
        )

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp)
        ) {
            Column {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Username")
                    TextField(
                        value = name, onValueChange = onNameChange,
                        modifier = modifier
                            .padding(end = 16.dp)
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                            .height(50.dp)
                            .focusRequester(nameFocusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                }
                Spacer(modifier = modifier.height(10.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Number")
                    TextField(
                        value = number, onValueChange = onNumberChange,
                        modifier = modifier
                            .padding(end = 16.dp)
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                            .height(50.dp)
                            .focusRequester(numberFocusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                }
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        Divider(
            modifier = modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "LogOut", modifier = modifier.clickable {
                onLogOutClick.invoke()
            })
        }

    }

}


fun getColorForChar(char: Char): MyColor {
    return when (char.uppercaseChar()) {
        'A', 'B', 'C' -> MyColor.RED
        'D', 'E', 'F' -> MyColor.BLUE
        'G', 'H', 'I' -> MyColor.GREEN
        'J', 'K', 'L' -> MyColor.YELLOW
        'M', 'N', 'O' -> MyColor.CYAN
        else -> MyColor.MAGENTA
    }
}

enum class MyColor(val color: Color) {
    RED(Color.Red),
    BLUE(Color.Blue),
    GREEN(Color.Green),
    YELLOW(Color.Yellow),
    CYAN(Color.Cyan),
    MAGENTA(Color.Magenta)
}