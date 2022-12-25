package window

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.*
import common.LocalAppResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import util.FileDialog
import util.YesNoCancelDialog
import java.util.*


@Composable
fun SCCUIWindow(state: SCCUIWindowState) {
    val scope = rememberCoroutineScope()

    fun exit() = scope.launch { state.exit() }

    state.readSettings()

    Window(
        state = state.window,
        title = titleOf(state),
        resizable = false,
        icon = LocalAppResources.current.icon,
        onCloseRequest = {
            state.writeSettings()
            exit()
        }
    ) {
        LaunchedEffect(Unit) { state.run() }
        WindowNotifications(state)
        WindowMenuBar(state)
        Column {
            Box (modifier = Modifier.background(state.backgroundColor).padding(10.dp).border(2.dp, state.borderColor).fillMaxWidth().fillMaxHeight(0.965F)) {
                Column (modifier = Modifier.padding(10.dp)) {
                    //UI element for selecting keyboard (layout)
                    Row {
                        keyboardDropDown(state)
                        if (state.keyboard != 0) {
                            readButton(state)
                            settingsCheckboxes(state)
                        }
                    }
                    if (state.keyboard != 0) {
                        navigationMenu(state)
                        Row(modifier = Modifier.padding(5.dp)) {}
                        if (state.macroMode) { // draw macro screen or draw keyboard
                            drawMacroScreen(state)
                        } else {
                            drawKeyboard(state)
                        }
                        Row {
                            flashRadioButton(state) //radio selection to choose source of config to flash
                            flashButton(state, scope) //button to flash the converter
                        }
                    }
                }
            }
            drawStatusbar(state)
        }


        if (state.openDialog.isAwaiting) {
            FileDialog(
                title = "Soarer's Converter Config UI",
                isLoad = true,
                onResult = {
                    state.openDialog.onResult(it)
                }
            )
        }

        if (state.saveDialog.isAwaiting) {
            FileDialog(
                title = "Soarer's Converter Config UI",
                isLoad = false,
                onResult = { state.saveDialog.onResult(it) }
            )
        }

        if (state.selectDialog.isAwaiting) {
            FileDialog(
                title = "Soarer's Converter Config UI",
                isLoad = true,
                onResult = {
                    state.selectDialog.onResult(it)
                }
            )
        }

        if (state.exitDialog.isAwaiting) {
            YesNoCancelDialog(
                title = "Soarer's Converter Config UI",
                message = "Save changes?",
                onResult = { state.exitDialog.onResult(it) }
            )
        }
        if (state.flashDialog.isAwaiting) {
            YesNoCancelDialog(
                title = "Soarer's Converter Config UI",
                message = "Flash?",
                onResult = { state.flashDialog.onResult(it) }
            )
        }
    }
}
@Composable
fun drawStatusbar(state: SCCUIWindowState) {
    Box (modifier = Modifier
        .background(Color(0xFF00AAAA))
        .fillMaxWidth()
        .padding(10.dp)
        //.align(Alignment.Bottom)
    ) {
        Text(text = state.statusText, color = state.textColor, fontFamily = state.oldFont)
    }
}
@Composable
fun drawKeyboard(state: SCCUIWindowState) {
// draw keyboard and keys

    Box(
        modifier = Modifier
            .border(2.dp, state.borderColor)
            .padding(10.dp)
            .fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(10.dp)) {

            Box(
                modifier = Modifier.background(state.backgroundColor)
                //.border(2.dp, state.borderColor)
            ) {
                keyboard(state)
            }
            //UI elements for remapping
            Row(
                modifier = Modifier.padding(0.dp, 10.dp)

            ) {
                selectedKey(state)
                mapToDropDown(state)
                applyMapToButton(state)
            }
        }

    }
}
@Composable
fun drawMacroScreen(state: SCCUIWindowState) {
    state.layerButtonColor.clear()
    state.layerButtonTextColor.clear()
    for (i in 0..8) {
        state.layerButtonColor.add(state.inactiveButtonColor)
        state.layerButtonTextColor.add(state.inactiveButtonTextColor)
    }

    Box(
        modifier = Modifier.border(2.dp, state.borderColor).padding(10.dp).fillMaxWidth()
            .height(550.dp)
    ) {
        //draw macro UI
        Row {
            //List of all macros
            Column(modifier = Modifier.verticalScroll(ScrollState(1), true)) {
                macroList(state)
            }
            if (state.macroNameTemp != "") {
                editMacro(state)
            }
        }
    }
}
@Composable
fun navigationMenu(state: SCCUIWindowState) {
//UI elements for switching layers and selecting layer keys
    if (state.checkedLayerState || state.checkedMacroState) {
        Box(modifier = Modifier.border(2.dp, state.borderColor).padding(10.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    layerButton(state, 0)
                    if (state.checkedLayerState) {
                        for (i in 1..8) {
                            layerButton(state, i)
                        }
                    }
                    if (state.checkedMacroState) {
                        macroButton(state)
                    }
                }
                Row {
                    if (state.layer != 0 && !state.macroMode) {
                        for (i in 0..2) { //must be 0-2 not 1-3 !
                            layerKeyDropDown(state, i + 1, i)
                        }
                        applyLayerKeyButton(state)
                    }
                }
            }

        }
    }
}


@Composable
private fun settingsCheckboxes(state: SCCUIWindowState) {
    Checkbox(
        checked = state.checkedConfigState,
        onCheckedChange = { state.checkedConfigState = it },
        //modifier = Modifier.padding(0.dp, 0.dp),
        colors = CheckboxDefaults.colors(
            checkedColor = state.backgroundColor,
            uncheckedColor = state.borderColor,
        )
    )
    Text(text = "Show Alternative Configs (not yet implemented)", color = state.textColor, fontFamily = state.oldFont, modifier = Modifier.padding(0.dp, 18.dp))

    Checkbox(
        checked = state.checkedLayerState,
        onCheckedChange = { state.checkedLayerState = it },
        colors = CheckboxDefaults.colors(
            checkedColor = state.backgroundColor,
            uncheckedColor = state.borderColor
        )
    )
    Text(text = "Show Layers", color = state.textColor, fontFamily = state.oldFont, modifier = Modifier.padding(0.dp, 18.dp))

    Checkbox(
        checked = state.checkedMacroState,
        onCheckedChange = { state.checkedMacroState = it },
        //modifier = Modifier.border(2.dp, state.textColor, RectangleShape),
        colors = CheckboxDefaults.colors(
            checkedColor = state.backgroundColor,
            uncheckedColor = state.borderColor
        )
    )
    Text(text = "Show Macros", color = state.textColor, fontFamily = state.oldFont, modifier = Modifier.padding(0.dp, 18.dp))

}

@Composable
private fun flashRadioButton(state: SCCUIWindowState) {

    val label1 = "Flash from config above"
    val label2 = "Flash from file"
    Column (
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.flashSource == label1,
                colors = RadioButtonDefaults.colors(selectedColor = state.borderColor, unselectedColor = state.borderColor),
                onClick = { state.flashSource = label1 }
            )
            Text(
                text = label1, fontFamily = state.oldFont, color = state.inactiveButtonTextColor

            )
        }
        Row (verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.flashSource == label2,
                colors = RadioButtonDefaults.colors(selectedColor = state.borderColor, unselectedColor = state.borderColor),
                onClick = { state.flashSource = label2 }
            )
            Text(
                text = label2, fontFamily = state.oldFont, color = state.inactiveButtonTextColor
            )
        }
    }
}


@Composable
private fun macroList(state: SCCUIWindowState) {
    state.macros.forEach() {
        Button(
            modifier = Modifier
                .padding(0.dp, 0.dp),
            //.border(0.dp, Color.LightGray, RectangleShape),
            onClick = {
                state.macrolistClicked(it)
            },
            shape = RectangleShape,
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            //border = ButtonDefaults.outlinedBorder(RectangleShape, 2.dp, Color.Black, Size(5F, 5F)),
            colors = if (it.index == state.selectedMacro) { ButtonDefaults.buttonColors(state.activeButtonColor) } else { ButtonDefaults.buttonColors(state.inactiveButtonColor) }


        ) { Text(it.name, fontFamily = state.oldFont, color =  if (it.index == state.selectedMacro) { state.activeButtonTextColor } else { state.inactiveButtonTextColor }) }

    }

}


@Composable
private fun macroName(state: SCCUIWindowState) {
    Box {

        OutlinedTextField(
            value = state.macroNameTemp,
            onValueChange = {
                if (it.length <= 20) {
                    state.macroNameTemp = it
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontFamily = state.oldFont, textAlign = TextAlign.Center, color = state.textColor),
            modifier = Modifier.padding(10.dp).border(2.dp, state.borderColor, RectangleShape),
            //singleline = true
            //label = { Text("Selected Key", fontFamily = FontFamily.Monospace) }
        )
        Box (modifier = Modifier.padding(105.dp,4.dp)) {
            Text("Macro Name", color = state.textColor, modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }
    }
}

@Composable
private fun addActionButton(state: SCCUIWindowState) {

    Box (modifier = Modifier.padding(20.dp, 10.dp)) {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {

            },
            shape = RectangleShape,
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text(
                text = "+", fontFamily = state.oldFont, color = Color.Black, fontSize = 30.sp
            )
        }
        Button(
            modifier = Modifier
                .padding(0.dp, 0.dp)
                .background(state.activeButtonColor),
            //.border(0.dp, Color.LightGray, RectangleShape),
            onClick = {
                state.addActionButtonClicked()
            },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(state.activeButtonColor)
            //border = ButtonDefaults.outlinedBorder(RectangleShape, 2.dp, Color.Black, Size(5F, 5F)),
            //colors = ButtonDefaults.buttonColors(state.layerButtonColor[layer])


        ) { Text("+", fontFamily = state.oldFont, fontSize = 30.sp, color = state.activeButtonTextColor) }
    }


}

@Composable
private fun layerButton (state: SCCUIWindowState, layer: Int) {
    Button(
        modifier = Modifier
            .padding(0.dp, 0.dp),
            //.border(0.dp, Color.LightGray, RectangleShape),
        onClick = {
            state.macroButtonColor = state.inactiveButtonColor
            state.macroButtonTextColor = state.inactiveButtonTextColor
            for (i in 0..8) {
                if (i != layer) {
                    state.layerButtonColor[i] = state.inactiveButtonColor
                    state.layerButtonTextColor[i] = state.inactiveButtonTextColor
                } else {
                    state.layerButtonColor[i] = state.activeButtonColor
                    state.layerButtonTextColor[i] = state.activeButtonTextColor
                }

            }
            state.layerButtonClicked(layer)
        },
        shape = RectangleShape,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        //border = ButtonDefaults.outlinedBorder(RectangleShape, 2.dp, Color.Black, Size(5F, 5F)),
        colors = ButtonDefaults.buttonColors(state.layerButtonColor[layer])


    ) { Text("Layer "+layer, fontFamily = state.oldFont, color = state.layerButtonTextColor[layer] ) }


}

@Composable
private fun macroButton (state: SCCUIWindowState) {
    Button(
        modifier = Modifier
            .padding(0.dp, 0.dp),
        //.border(0.dp, Color.LightGray, RectangleShape),
        onClick = {
            state.statusText = "Click on a macro on the left to edit it."
            state.macroMode = true
            state.macroButtonColor = state.activeButtonColor
            state.macroButtonTextColor = state.activeButtonTextColor

        },
        shape = RectangleShape,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        //border = ButtonDefaults.outlinedBorder(RectangleShape, 2.dp, Color.Black, Size(5F, 5F)),
        colors = ButtonDefaults.buttonColors(state.macroButtonColor)


    ) { Text("Macros", fontFamily = state.oldFont, color = state.macroButtonTextColor ) }


}

@Composable
private fun keyboard(state: SCCUIWindowState) {

    //Variables
    var t = 0
    var c = 0

    Box(
        modifier = Modifier.padding(10.dp)
        //.border(2.dp, Color.LightGray, RectangleShape)
    ) {
        state.rows.forEach {
            Row(
                modifier = Modifier.absolutePadding(top = state.defaultHeight.times(t)) //.paddingFromBaseline(top = defaultHeight.times(t))
            ) {

                it.forEach { it ->
                    it.row = t
                    it.column = c

                    c++
                    if (it.label != "") {
                        OutlinedButton(
                            onClick = {
                                //set row and column number of each key
                                state.row = it.row!!
                                state.column = it.column!!
                                //update name & label if it was changed in the UI
                                state.name = it.name
                                state.label = if (it.label != "  ") {
                                    it.label
                                } else {
                                    it.name
                                }
                                if (it.mapTo[state.layer] != null) {
                                    //get already mapped keys and update description/label of keys
                                    state.mapTo = it.mapTo[state.layer]!!
                                    state.mapToDescription = state.mappingKeys[state.mappingKeys.indexOfFirst { it.name == state.rows[state.row][state.column].mapTo[state.layer] }].description
                                } else {
                                    state.mapToDescription = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = it.backgroundColor),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(
                                width = state.defaultWidth.times(it.width.toFloat()),
                                height = state.defaultHeight.times(it.height.toFloat())
                            ).padding(all = 0.dp)
                        ) {
                            (if (it.label != "  " && it.mapTo[state.layer] == null) {
                                it.label
                            } else if (it.mapTo[state.layer] != null) {
                                it.mapTo[state.layer]
                            } else {
                                it.name
                            })?.let { it1 ->
                                Text(
                                    text = it1,
                                    fontSize = if (it.label != "  ") {
                                            9.sp
                                        } else {
                                            7.sp
                                        },
                                    modifier = Modifier.padding(vertical = 0.dp),
                                    color = if (it.label != "  " && it.mapTo[state.layer] == null) {
                                            Color.Black
                                        } else if (it.mapTo[state.layer] != null) {
                                            Color.Magenta
                                        } else {
                                            Color.Gray
                                        },
                                    )
                            }
                        }
                    } else {
                        Spacer(
                            Modifier.size(
                                width = state.defaultWidth.times(it.width.toFloat()),
                                height = state.defaultHeight.times(it.height.toFloat())
                            )
                        )
                    }
                }
                t++
            }
            c = 0
        }
        t = 0
    }
}


@Composable
private fun selectedKey(state: SCCUIWindowState) {
    Box {

        OutlinedTextField(
            value = if (state.label == "") { "" } else { state.label + " (" + state.name +")" },
            onValueChange = {
                state.label = it
            },
            textStyle = TextStyle(fontFamily = state.oldFont, textAlign = TextAlign.Center, color = state.textColor),
            modifier = Modifier.padding(0.dp, 10.dp).border(2.dp, state.borderColor, RectangleShape),
            //singleline = true
            //label = { Text("Selected Key", fontFamily = FontFamily.Monospace) }
        )
        Box (modifier = Modifier.padding(75.dp,4.dp)) {
            Text("Selected Key", color = state.textColor, modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }
    }
}
@Composable
private fun mapToDropDown(state: SCCUIWindowState){

    val no = 0
    //val focusRequester = remember { FocusRequester() }
    Box {
    Column (modifier = Modifier.padding(20.dp, 10.dp)) {

        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = state.mapToDescription,
            onValueChange = {

                state.mappingKeysDropDown = state.mappingKeys
                state.foundMappingKeys = mutableStateListOf(state.mappingKeys[0])
                state.mapToDescription = it
                state.mappingKeysDropDown.forEach() { mappingKey ->
                    if (mappingKey.description.contains(it, ignoreCase = true) && mappingKey.name != "NOMAPPING") {
                        //println(mappingKey.description)
                        state.foundMappingKeys.add(mappingKey)
                    }
                    state.mappingKeysDropDown = state.foundMappingKeys
                }
                state.mExpanded[no] = true
                //delay(100)
                //focusRequester.requestFocus()
            },
            modifier = Modifier
                //.fillMaxWidth()
                //.focusRequester(focusRequester)
                .onFocusChanged {
                    state.mExpanded[no] = it.hasFocus
                    state.mappingKeysDropDown = state.mappingKeys
                }
                .border(2.dp, state.borderColor, RectangleShape)
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    state.mTextFieldSize[no] = coordinates.size.toSize()
                },
            //label = {Text("Select key to map to")},
            textStyle = TextStyle(color = state.textColor, fontFamily = state.oldFont, textAlign = TextAlign.Center),
            /*trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { state.mExpanded[no] = !state.mExpanded[no] })
            }*/
        )

        // Create a drop-down menu with list of keys,
        // when clicked, set the Text Field text as the key selected
        DropdownMenu(
            focusable = false,
            expanded = state.mExpanded[no],
            onDismissRequest = { state.mExpanded[no] = false },
            modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSize[no].width.toDp() })
                .height(400.dp)
        ) {
            state.mappingKeysDropDown.forEach {
                DropdownMenuItem(onClick = {
                    state.mapToDescription = it.description
                    state.mapTo = it.name
                    state.mExpanded[no] = false
                }
                ) {
                    Text(
                        text = it.description /* + " (" + it.name + ")" */, fontFamily = state.oldFont//, fontSize = 12.sp
                    )
                }
            }
        }
    }
        Box (modifier = Modifier.padding(55.dp,4.dp)) {
            Text("Select key to map to", color = state.textColor, modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }

    }
}


@Composable
private fun editMacro(state: SCCUIWindowState) {
    Column (modifier = Modifier.verticalScroll(ScrollState(1), true)) {
        Row {
            macroName(state)
            saveMacroButton(state)
        }
        Row {
            triggerKeyDropdown(state)
            metaTriggerDropDown(state, "CTRL")
            metaTriggerDropDown(state, "SHIFT")
            metaTriggerDropDown(state, "ALT")
            metaTriggerDropDown(state, "WIN/COMMAND")
        }
        //macro actions (action, key)
        state.macros[state.selectedMacro].actions.forEach() {
            Row {
                actionDropdown(state, it.index)
                actionKeyDropdown(state, it.index)
                actionOrderButtons(state, it.index)
            }
        }

        //add new action
        addActionButton(state)
    }

}

@Composable
private fun actionOrderButtons(state: SCCUIWindowState, index: Int) {
    if (index > 0 && state.actionTemp.size > 1) {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                Collections.swap(state.actionTemp, index, index - 1)
                Collections.swap(state.actionDescriptionTemp, index, index - 1)
                Collections.swap(state.actionKeyTemp, index, index - 1)
                Collections.swap(state.actionKeyDescriptionTemp, index, index - 1)
            },
            shape = RectangleShape,
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(state.activeButtonColor)
        ) {
            Text(
                text = "^", fontFamily = FontFamily.Default, color = state.activeButtonTextColor, fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
        }
    }
    if (index+1 < state.actionTemp.size) {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                Collections.swap(state.actionTemp, index, index + 1)
                Collections.swap(state.actionDescriptionTemp, index, index + 1)
                Collections.swap(state.actionKeyTemp, index, index + 1)
                Collections.swap(state.actionKeyDescriptionTemp, index, index + 1)
            },
            shape = RectangleShape,
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(state.activeButtonColor)
        ) {
            Text(
                text = "v", fontFamily = FontFamily.Default, color = state.activeButtonTextColor, fontSize = 20.sp
            )
        }
    }
}
@Composable
private fun saveMacroButton(state: SCCUIWindowState) {
    Box (modifier = Modifier.padding(0.dp, 10.dp)) {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {

            },
            shape = RectangleShape,
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text(
                text = "Save", fontFamily = state.oldFont, color = Color.Black
            )
        }
        Button(
            modifier = Modifier.padding(0.dp),
            onClick = { state.saveMacroButtonPressed() },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(state.activeButtonColor)
        ) { Text("Save", fontFamily = state.oldFont, color = state.activeButtonTextColor) }
    }
}


@Composable
private fun triggerKeyDropdown(state: SCCUIWindowState){

    val no = 5

    //val focusRequester = remember { FocusRequester() }
    Box {
        Column (modifier = Modifier.padding(10.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = state.triggerKeyDescriptionTemp,
                onValueChange = {
                    state.mappingKeysDropDown = state.mappingKeys
                    state.foundMappingKeys = mutableStateListOf(state.mappingKeys[0])
                    state.triggerKeyDescriptionTemp = it
                    state.mappingKeysDropDown.forEach() { mappingKey ->
                        if (mappingKey.description.contains(it, ignoreCase = true) && mappingKey.name != "NOMAPPING") {
                            //println(mappingKey.description)
                            state.foundMappingKeys.add(mappingKey)
                        }
                        state.mappingKeysDropDown = state.foundMappingKeys
                    }
                    state.mExpanded[no] = true
                    //delay(100)
                    //focusRequester.requestFocus()
                },
                modifier = Modifier
                    //.fillMaxWidth()
                    //.focusRequester(focusRequester)
                    .onFocusChanged {
                        state.mExpanded[no] = it.hasFocus
                        state.mappingKeysDropDown = state.mappingKeys
                    }
                    .border(2.dp, state.borderColor, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSize[no] = coordinates.size.toSize()
                    },
                //label = {Text("Select key to map to")},
                textStyle = TextStyle(color = state.textColor, fontFamily = state.oldFont, textAlign = TextAlign.Center),
                /*trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { state.mExpanded[no] = !state.mExpanded[no] })
                }*/
            )

            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                focusable = false,
                expanded = state.mExpanded[no],
                onDismissRequest = { state.mExpanded[no] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSize[no].width.toDp() })
                    .height(400.dp)
            ) {
                state.mappingKeysDropDown.forEach {
                    DropdownMenuItem(onClick = {
                        state.triggerKeyDescriptionTemp = it.description
                        state.triggerKeyTemp = it.name
                        state.mExpanded[no] = false
                    }
                    ) {
                        Text(
                            text = it.description, fontFamily = state.oldFont//, fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Box (modifier = Modifier.padding(25.dp,4.dp)) {
            Text("Select key to trigger macro", color = state.textColor, modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }

    }
}

@Composable
private fun metaTriggerDropDown(state: SCCUIWindowState, keyName: String) {

    var index = 0 //CTRL
    when (keyName) {
        "SHIFT" -> { index = 1 }
        "ALT" -> { index = 2 }
        "WIN/COMMAND" -> { index = 3 }
    }
    Box {
        Column(modifier = Modifier.padding(10.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = state.metaTriggersTemp[index],
                onValueChange = {
                    state.metaTriggersTemp[index] = it
                    state.mExpandedMetaTrigger[index] = true
                },
                modifier = Modifier
                    .onFocusChanged {
                        state.mExpandedMetaTrigger[index] = it.hasFocus
                    }
                    .border(2.dp, state.borderColor, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSizeMetaTrigger[index] = coordinates.size.toSize()
                    },
                //label = {Text("Select key(s) to access layer")},
                textStyle = TextStyle(color = state.textColor, fontFamily = state.oldFont, textAlign = TextAlign.Center),
            )

            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                focusable = false,
                expanded = state.mExpandedMetaTrigger[index],
                onDismissRequest = { state.mExpandedMetaTrigger[index] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSizeMetaTrigger[index].width.toDp() })
                    .height(400.dp)
            ) {
                state.metaTriggerChoices.forEach {
                    DropdownMenuItem(onClick = {
                        state.metaTriggersTemp[index] = it
                        state.mExpandedMetaTrigger[index] = false
                    }) {
                        Text(text = it, fontFamily = state.oldFont)
                    }
                }
            }
        }
        Box(modifier = Modifier.padding(25.dp, 4.dp)) {
            Text(
                keyName + " should be:",
                color = state.textColor,
                modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp),
                fontFamily = state.oldFont
            )
        }
    }
}


@Composable
private fun actionDropdown(state: SCCUIWindowState, macroActionNumber: Int) {

    //val focusRequester = remember { FocusRequester() }
    Box {
        Column (modifier = Modifier.padding(10.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = state.actionDescriptionTemp[macroActionNumber],
                onValueChange = {
                    state.actionDescriptionTemp[macroActionNumber] = it
                },
                modifier = Modifier
                    //.fillMaxWidth()
                    //.focusRequester(focusRequester)
                    .onFocusChanged {
                        state.mExpandedAction[macroActionNumber] = it.hasFocus
                        //state.mappingKeysDropDown = state.mappingKeys
                    }
                    .border(2.dp, state.borderColor, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSizeAction[macroActionNumber] = coordinates.size.toSize()
                    },
                //label = {Text("Select key to map to")},
                textStyle = TextStyle(color = state.textColor, fontFamily = state.oldFont, textAlign = TextAlign.Center),
                /*trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { state.mExpanded[no] = !state.mExpanded[no] })
                }*/
            )

            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                focusable = false,
                expanded = state.mExpandedAction[macroActionNumber],
                onDismissRequest = { state.mExpandedAction[macroActionNumber] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSizeAction[macroActionNumber].width.toDp() })
                    .height(400.dp)
            ) {
                state.actions.forEach {
                    DropdownMenuItem(onClick = {
                        state.actionTemp[macroActionNumber] = it.action
                        state.actionDescriptionTemp[macroActionNumber] = it.description
                        state.mExpandedAction[macroActionNumber] = false
                    }
                    ) {
                        Text(
                            text = it.description, fontFamily = state.oldFont//, fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Box (modifier = Modifier.padding(85.dp,4.dp)) {
            Text("Select action", color = state.textColor, modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }

    }
}


@Composable
private fun actionKeyDropdown(state: SCCUIWindowState, macroActionNumber: Int){

    //val focusRequester = remember { FocusRequester() }
    Box {
        Column (modifier = Modifier.padding(10.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = state.actionKeyDescriptionTemp[macroActionNumber],
                onValueChange = {

                    state.mappingKeysDropDown = state.mappingKeys
                    state.foundMappingKeys = mutableStateListOf(state.mappingKeys[0])
                    state.actionKeyDescriptionTemp[macroActionNumber] = it
                    state.mappingKeysDropDown.forEach() { mappingKey ->
                        if (mappingKey.description.contains(it, ignoreCase = true) && mappingKey.name != "NOMAPPING") {
                            //println(mappingKey.description)
                            state.foundMappingKeys.add(mappingKey)
                        }
                        state.mappingKeysDropDown = state.foundMappingKeys
                    }
                    state.mExpandedActionKey[macroActionNumber] = true
                    //delay(100)
                    //focusRequester.requestFocus()
                },
                modifier = Modifier
                    //.fillMaxWidth()
                    //.focusRequester(focusRequester)
                    .onFocusChanged {
                        state.mExpandedActionKey[macroActionNumber] = it.hasFocus
                        state.mappingKeysDropDown = state.mappingKeys
                    }
                    .border(2.dp, state.borderColor, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSizeActionKey[macroActionNumber] = coordinates.size.toSize()
                    },
                //label = {Text("Select key to map to")},
                textStyle = TextStyle(color = state.textColor, fontFamily = state.oldFont, textAlign = TextAlign.Center),
                /*trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable { state.mExpanded[no] = !state.mExpanded[no] })
                }*/
            )

            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                focusable = false,
                expanded = state.mExpandedActionKey[macroActionNumber],
                onDismissRequest = { state.mExpandedActionKey[macroActionNumber] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSizeActionKey[macroActionNumber].width.toDp() })
                    .height(400.dp)
            ) {
                state.mappingKeysDropDown.forEach {
                    DropdownMenuItem(onClick = {
                        state.actionKeyDescriptionTemp[macroActionNumber] = it.description
                        state.actionKeyTemp[macroActionNumber] = it.name
                        state.mExpandedActionKey[macroActionNumber] = false
                    }
                    ) {
                        Text(
                            text = it.description, fontFamily = state.oldFont//, fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Box (modifier = Modifier.padding(95.dp,4.dp)) {
            Text("Select key", color = state.textColor, modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }

    }
}





@Composable
private fun layerKeyDropDown(state: SCCUIWindowState, index: Int, layerKeyNo: Int) {

    Box {
        Column(modifier = Modifier.padding(10.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = state.layerKeyDescriptionTemp[layerKeyNo],
                onValueChange = {
                    state.layerKeyDescriptionTemp[layerKeyNo] = it
                    state.mappingKeysDropDown = state.mappingKeys
                    state.foundMappingKeys = mutableStateListOf(state.mappingKeys[0])
                    state.mappingKeysDropDown.forEach() { mappingKey ->
                        if (mappingKey.description.contains(it, ignoreCase = true) && mappingKey.name != "NOMAPPING") {
                            //println(mappingKey.description)
                            state.foundMappingKeys.add(mappingKey)
                        }
                        state.mappingKeysDropDown = state.foundMappingKeys
                    }
                    state.mExpanded[index] = true
                },
                modifier = Modifier
                    .onFocusChanged {
                        state.mExpanded[index] = it.hasFocus
                        state.mappingKeysDropDown = state.mappingKeys
                    }
                    .border(2.dp, state.borderColor, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSize[index] = coordinates.size.toSize()
                    },
                //label = {Text("Select key(s) to access layer")},
                textStyle = TextStyle(color = state.textColor, fontFamily = state.oldFont, textAlign = TextAlign.Center),
            )

            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                focusable = false,
                expanded = state.mExpanded[index],
                onDismissRequest = { state.mExpanded[index] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSize[index].width.toDp() })
                    .height(400.dp)
            ) {
                state.mappingKeysDropDown.forEach {
                    DropdownMenuItem(onClick = {
                        state.layerKeyDescriptionTemp[layerKeyNo] = it.description
                        state.layerKeyNameTemp[layerKeyNo] = it.name
                        state.mExpanded[index] = false
                    }) {
                        Text(text = it.description, fontFamily = state.oldFont)
                    }
                }
            }
        }
        Box(modifier = Modifier.padding(25.dp, 4.dp)) {
            Text(
                "Select key to access layer",
                color = state.textColor,
                modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp),
                fontFamily = state.oldFont
            )
        }
    }
}

    @Composable
    private fun applyMapToButton(state: SCCUIWindowState) {
        Box (modifier = Modifier.padding(0.dp, 10.dp)) {
            Button(
                modifier = Modifier.padding(10.dp),
                onClick = { //see below
                    },
                shape = RectangleShape,
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text(
                    text = "Apply", fontFamily = state.oldFont, color = Color.Black
                )
            }
            Button(
                modifier = Modifier.padding(0.dp),
                onClick = { state.applyMapToButtonPressed() },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(state.activeButtonColor)
            ) { Text("Apply", fontFamily = state.oldFont, color = state.activeButtonTextColor) }
        }
    }

    @Composable
    private fun applyLayerKeyButton(state: SCCUIWindowState) {
        Box {
            Button(
                modifier = Modifier.padding(18.dp),
                onClick = { //see below
                },
                shape = RectangleShape,
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text(
                    text = "Apply", fontFamily = state.oldFont, color = Color.Black
                )
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { state.applyLayerKeyButtonPressed() },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(state.activeButtonColor)
            ) { Text("Apply", fontFamily = state.oldFont, color = state.activeButtonTextColor) }
        }
    }

    @Composable
    private fun flashButton(state: SCCUIWindowState, scope: CoroutineScope) {
        Box {
            Button(
                modifier = Modifier.padding(30.dp),
                onClick = {   //see below
                },
                shape = RectangleShape,
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text(
                    text = "Flash Soarer's Converter", fontFamily = state.oldFont, color = Color.Black
                )
            }
            Button(
                modifier = Modifier.padding(20.dp),
                onClick = { state.prepareFlash(scope) },
                shape = RectangleShape,
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(state.activeButtonColor)
            ) {
                Text(
                    "Flash Soarer's Converter",
                    fontFamily = state.oldFont,
                    color = state.activeButtonTextColor
                )
            }

        }

    }

@Composable
private fun readButton(state: SCCUIWindowState) {
    Box {
        Button(
            modifier = Modifier.padding(30.dp, 10.dp),
            onClick = {   //see below
            },
            shape = RectangleShape,
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text(
                text = "Read Soarer's Converter", fontFamily = state.oldFont, color = Color.Black
            )
        }
        Button(
            modifier = Modifier.padding(20.dp, 0.dp),
            onClick = { state.read() },
            shape = RectangleShape,
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(state.activeButtonColor)
        ) {
            Text(
                "Read Soarer's Converter",
                fontFamily = state.oldFont,
                color = state.activeButtonTextColor
            )
        }

    }

}

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun keyboardDropDown(state: SCCUIWindowState) {

        val no = 4
        val icon = if (state.mExpanded[no])
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown
        Column(modifier = Modifier.padding(0.dp)) {

            // Create an Outlined Text Field
            // with icon and not expanded
            OutlinedTextField(
                value = state.keyboards[state.keyboard].name,
                onValueChange = {
                    state.keyboard = it.toInt()
                },
                modifier = Modifier
                    //.fillMaxWidth()
                    .onFocusChanged {
                        state.mExpanded[no] = it.hasFocus
                        //state.mappingKeysDropDown = state.mappingKeys
                    }.border(2.dp, state.borderColor, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSize[no] = coordinates.size.toSize()
                    },
                //label = {Text("Select keyboard", fontFamily = FontFamily.Monospace, color = Color.Black, textAlign = TextAlign.Center)},
                textStyle = TextStyle(color = state.textColor, fontFamily = state.oldFont),
                trailingIcon = {
                    Icon(icon, "contentDescription",
                        Modifier.clickable { state.mExpanded[no] = !state.mExpanded[no] })
                }
            )


            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                focusable = false,
                expanded = state.mExpanded[no],
                onDismissRequest = { state.mExpanded[no] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSize[no].width.toDp() })
            ) {
                state.keyboards.forEach {
                    DropdownMenuItem(onClick = {
                        state.keyboard = it.index
                        state.initKeyboard(state.keyboard)
                        state.statusText = "Click on a key to map it to another."
                        state.mExpanded[no] = false
                    }) {
                        Text(
                            text = it.name, fontFamily = state.oldFont
                        )
                    }
                }
            }
            Row(modifier = Modifier.padding(5.dp)) {}
        }



    }


    private fun titleOf(state: SCCUIWindowState): String {
        val changeMark = if (state.isChanged) "*" else ""
        val filePath = state.path ?: "Untitled"
        return "$changeMark$filePath - Soarer's Converter Config UI"
    }

    @Composable
    private fun WindowNotifications(state: SCCUIWindowState) {
        // Usually we take into account something like LocalLocale.current here
        fun NotepadWindowNotification.format() = when (this) {
            is NotepadWindowNotification.SaveSuccess -> Notification(
                "File is saved", path.toString(), Notification.Type.Info
            )

            is NotepadWindowNotification.SaveError -> Notification(
                "File isn't saved", path.toString(), Notification.Type.Error
            )
        }

        LaunchedEffect(Unit) {
            state.notifications.collect {
                state.sendNotification(it.format())
            }
        }
    }

    @Composable
    private fun FrameWindowScope.WindowMenuBar(state: SCCUIWindowState) = MenuBar {
        val scope = rememberCoroutineScope()

        fun save() = scope.launch { state.save() }
        fun open() = scope.launch { state.open() }
        fun exit() = scope.launch { state.exit() }
        //fun read() = scope.launch { state.read() }

        Menu("File") {
            //Item("New window", onClick = state::newWindow)
            Item("Open file...", onClick = { open() })
            //Item("Read config from converter", onClick = { read() })
            Item("Save", onClick = { save() }, enabled = state.isChanged || state.path == null)
            Separator()
            Item("Exit", onClick = { exit() })
        }
/*
        Menu("Settings") {
            Item(
                if (state.settings.isTrayEnabled) "Hide tray" else "Show tray",
                onClick = state.settings::toggleTray
            )
            Item(
                if (state.window.placement == WindowPlacement.Fullscreen) "Exit fullscreen" else "Enter fullscreen",
                onClick = state::toggleFullscreen
            )
        }*/
    }
