package window

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.*
import common.LocalAppResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.FileDialog
import util.YesNoCancelDialog


@Composable
fun SCCUIWindow(state: SCCUIWindowState) {
    val scope = rememberCoroutineScope()



    fun exit() = scope.launch { state.exit() }

    Window(
        state = state.window,
        title = titleOf(state),
        resizable = true,
        icon = LocalAppResources.current.icon,
        onCloseRequest = { exit() }
    ) {
        LaunchedEffect(Unit) { state.run() }

        WindowNotifications(state)
        WindowMenuBar(state)
        //Column {
            Box (modifier = Modifier.background(Color.LightGray).padding(10.dp).border(2.dp, Color.Black).fillMaxWidth().fillMaxHeight()) {
                Column (modifier = Modifier.padding(10.dp)) {

                    //UI element for selecting keyboard (layout)
                    Row {
                        keyboardDropDown(state)
                    }
                    if (state.keyboard != 0) {
                        //UI elements for switching layers and selecting layer keys
                        Box(modifier = Modifier.border(2.dp, Color.Black).padding(10.dp)) {
                            Column {

                                Row {
                                    for (i in 0..8) {
                                        layerButton(state, i)
                                    }
                                }
                                Row {
                                    if (state.layer != 0) {
                                        for (i in 0..2) { //must be 0-2 not 1-3 !
                                            layerKeyDropDown(state, i + 1, i)
                                        }
                                        applyLayerKeyButton(state)
                                    }
                                }
                            }
                        }

                        // draw keyboard and keys
                        //state.initKeyboard(0)
                        Row(modifier = Modifier.padding(5.dp)) {}
                        Box(modifier = Modifier.border(2.dp, Color.Black).padding(10.dp)) {

                            Column(modifier = Modifier.padding(10.dp)) {

                                Box(modifier = Modifier.background(Color.White).border(2.dp, Color.Black)) {
                                    keyboard(state)
                                }
                                //UI elements for remapping
                                Row(
                                    modifier = Modifier
                                        .padding(0.dp, 10.dp)

                                ) {

                                    selectedKey(state)
                                    //Text(" --> ")
                                    mapToDropDown(state)

                                    applyMapToButton(state)
                                }
                            }


                        }

                        // Output Text Field
                        /*
                    BasicTextField(
                        value = state.remapblockOutput[state.layer],
                        onValueChange = { state.remapblockOutput[state.layer] = it },
                        modifier = Modifier
                            .height(100.dp)
                            .requiredWidth(500.dp)
                            .border(BorderStroke(2.dp, Color.LightGray))
                            .padding(20.dp)
                            .verticalScroll(ScrollState(1))

                    )*/


                        //button to flash the converter
                        flashButton(state, scope)
                    }




                }
            }
            Box (modifier = Modifier
                .background(Color(0xFF00AAAA))
                .fillMaxWidth()
                .padding(10.dp)
                //.align(Alignment.Bottom)
            ) {
                Text(text = state.statusText, color = state.activeButtonTextColor, fontFamily = state.oldFont)
            }
        //}


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
private fun layerButton (state: SCCUIWindowState, layer: Int) {
    Button(
        modifier = Modifier
            .padding(0.dp, 0.dp),
            //.border(0.dp, Color.LightGray, RectangleShape),
        onClick = {
            for (i in 0..8) {
                if (i != layer) {
                    state.layerButtonColor[i] = state.inactiveButtonColor
                    state.layerButtonTextColor[i] = state.inactiveButtonTextColor
                } else {
                    state.layerButtonColor[i] = state.activeButtonColor
                    state.layerButtonTextColor[i] = state.activeButtonTextColor
                }

            }
            state.layer = layer
            for (i in 0..2) {
                if (state.layerKey[layer][i] != 0) {
                    state.layerKeyNameTemp[i] = state.fnKey[state.layerKey[layer][i]]
                }
                if (state.fnKey[state.layerKey[layer][i]] != "" && state.layerKey[layer][i] != 0 && state.mappingKeys.indexOfFirst { it.name == state.fnKey[state.layerKey[layer][i]] } != -1) {
                    state.layerKeyDescriptionTemp[i] = state.mappingKeys[state.mappingKeys.indexOfFirst { it.name == state.fnKey[state.layerKey[layer][i]] }].description
                } else { //this is only done to update the variable and therefore the UI
                    state.layerKeyDescriptionTemp[i] = ""
                    state.label = ""
                    state.mapTo = ""
                    state.mapToDescription = ""
                }
            }

            state.updateRemapblockOutput(layer)
            state.updateLayerblockOutput()
        },
        shape = RectangleShape,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        //border = ButtonDefaults.outlinedBorder(RectangleShape, 2.dp, Color.Black, Size(5F, 5F)),
        colors = ButtonDefaults.buttonColors(state.layerButtonColor[layer])


    ) { Text("Layer "+layer, fontFamily = state.oldFont, color = state.layerButtonTextColor[layer] ) }


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

                it.forEach {
                    it.row = t
                    it.column = c

                    c++
                    if (it.label != "") {
                        OutlinedButton(
                            onClick = {
                                //set row and column number of each key
                                state.row = it.row!!
                                state.column = it.column!!
                                //update label if it was changed in th UI
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
                            Text(
                                text = if (it.label != "  ") {
                                    it.label + if (it.mapTo[state.layer] != null) { "\r\n" + "> " + it.mapTo[state.layer] } else {""}
                                } else {
                                    it.name
                                },
                                fontSize = if (it.label != "  ") {
                                    9.sp
                                } else {
                                    7.sp
                                },
                                modifier = Modifier.padding(vertical = 0.dp),
                                color = if (it.label != "  ") {
                                    Color.Black
                                } else {
                                    Color.Gray
                                },

                                )

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
            value = state.label,
            onValueChange = { state.label = it },
            textStyle = TextStyle(fontFamily = state.oldFont, textAlign = TextAlign.Center),
            modifier = Modifier.padding(0.dp, 10.dp).border(2.dp, Color.Black, RectangleShape),
            //singleline = true
            //label = { Text("Selected Key", fontFamily = FontFamily.Monospace) }
        )
        Box (modifier = Modifier.padding(75.dp,4.dp)) {
            Text("Selected Key",  modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }
    }
}
@Composable
private fun mapToDropDown(state: SCCUIWindowState){

    val no = 0
    // Up Icon when expanded and down icon when collapsed
    val icon = if (state.mExpanded[no])
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

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
                var i = 0
                state.mappingKeysDropDown.forEach() { mappingKey ->
                    if (mappingKey.description.contains(it, ignoreCase = true) && mappingKey.name != "NOMAPPING") {
                        //println(mappingKey.description)
                        state.foundMappingKeys.add(mappingKey)
                    }
                    state.mappingKeysDropDown = state.foundMappingKeys
                }
                i++
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
                .border(2.dp, Color.Black, RectangleShape)
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    state.mTextFieldSize[no] = coordinates.size.toSize()
                },
            //label = {Text("Select key to map to")},
            textStyle = TextStyle(color = Color.Black, fontFamily = state.oldFont, textAlign = TextAlign.Center),
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
                .height(200.dp)
        ) {
            var i = 0
            state.mappingKeysDropDown.forEach {
                DropdownMenuItem(onClick = {
                    state.mapToDescription = it.description
                    state.mapTo = it.name
                    state.mExpanded[no] = false
                }
                ) {
                    Text(
                        text = it.description, fontFamily = state.oldFont
                    )
                }
            }
            i++
        }
    }
        Box (modifier = Modifier.padding(55.dp,4.dp)) {
            Text("Select key to map to",  modifier = Modifier.background(state.inactiveButtonColor).padding(10.dp, 0.dp), fontFamily = state.oldFont)
        }

    }
}



@Composable
private fun layerKeyDropDown(state: SCCUIWindowState, index: Int, layerKeyNo: Int) {


    // Up Icon when expanded and down icon when collapsed
    val icon = if (state.mExpanded[index])
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
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
                    var i = 0
                    state.mappingKeysDropDown.forEach() { mappingKey ->
                        if (mappingKey.description.contains(it, ignoreCase = true) && mappingKey.name != "NOMAPPING") {
                            //println(mappingKey.description)
                            state.foundMappingKeys.add(mappingKey)
                        }
                        state.mappingKeysDropDown = state.foundMappingKeys
                    }
                    i++
                    state.mExpanded[index] = true
                },
                modifier = Modifier
                    .onFocusChanged {
                        state.mExpanded[index] = it.hasFocus
                        state.mappingKeysDropDown = state.mappingKeys
                    }
                    .border(2.dp, Color.Black, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSize[index] = coordinates.size.toSize()
                    },
                //label = {Text("Select key(s) to access layer")},
                textStyle = TextStyle(color = Color.Black, fontFamily = state.oldFont, textAlign = TextAlign.Center),
            )

            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                focusable = false,
                expanded = state.mExpanded[index],
                onDismissRequest = { state.mExpanded[index] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSize[index].width.toDp() })
                    .height(200.dp)
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
                onClick = {

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
                onClick = {

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
                onClick = {                },
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
                onClick = { state.writeTempFile(scope) },
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
    private fun keyboardDropDown(state: SCCUIWindowState) {

        val no = 4
        // Up Icon when expanded and down icon when collapsed
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
                    .border(2.dp, Color.Black, RectangleShape)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        state.mTextFieldSize[no] = coordinates.size.toSize()
                    },
                //label = {Text("Select keyboard", fontFamily = FontFamily.Monospace, color = Color.Black, textAlign = TextAlign.Center)},
                textStyle = TextStyle(color = Color.Black, fontFamily = state.oldFont),
                trailingIcon = {
                    Icon(icon, "contentDescription",
                        Modifier.clickable { state.mExpanded[no] = !state.mExpanded[no] })
                }
            )

            // Create a drop-down menu with list of keys,
            // when clicked, set the Text Field text as the key selected
            DropdownMenu(
                expanded = state.mExpanded[no],
                onDismissRequest = { state.mExpanded[no] = false },
                modifier = Modifier.width(with(LocalDensity.current) { state.mTextFieldSize[no].width.toDp() })
            ) {
                state.keyboards.forEach {
                    DropdownMenuItem(onClick = {
                        state.keyboard = it.index
                        state.initKeyboard(state.keyboard)
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
        fun read() = scope.launch { state.read() }

        Menu("File") {
            Item("New window", onClick = state::newWindow)
            Item("Open file...", onClick = { open() })
            Item("Read config from converter", onClick = { read() })
            Item("Save", onClick = { save() }, enabled = state.isChanged || state.path == null)
            Separator()
            Item("Exit", onClick = { exit() })
        }

        Menu("Settings") {
            Item(
                if (state.settings.isTrayEnabled) "Hide tray" else "Show tray",
                onClick = state.settings::toggleTray
            )
            Item(
                if (state.window.placement == WindowPlacement.Fullscreen) "Exit fullscreen" else "Enter fullscreen",
                onClick = state::toggleFullscreen
            )
        }
    }
