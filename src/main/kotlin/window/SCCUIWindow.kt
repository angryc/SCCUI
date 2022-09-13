package window

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.*
import common.LocalAppResources
import evalBash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import util.AlertDialogResult
import util.FileDialog
import util.YesNoCancelDialog
import java.io.File


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

        Column {
            Row {
                for (i in 0..8) {
                    layerButton(state, i)
                }
            }



            keyboard(state)

            //UI elements for remapping
            Row (modifier = Modifier
                .padding(20.dp)

            ) {
                OutlinedTextField(
                    state.label,
                    state::label::set,
                    modifier = Modifier.padding(20.dp),
                    label = { Text("Selected Key")}
                )

                mapToDropDown(state)

                applyButton(state)


            }

            // Output Text Field
            BasicTextField(
                value = state.remapblock[state.layer],
                onValueChange = { state.remapblock[state.layer] = it },
                modifier = Modifier
                    .height(150.dp)
                    .requiredWidth(500.dp)
                    .border(BorderStroke(2.dp, Color.LightGray))
                    .padding(20.dp)
                    //.scrollable(ScrollableState { })
                    .verticalScroll(ScrollState(1))
                //label = { Text("Output")}
            )


            //button to flash the converter

            Button(
                modifier = Modifier.padding(20.dp),
                onClick = {
                    writeTempFile(state, scope)
                }
            ) { Text("FLASH SOARER'S CONVERTER") }


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
}


private fun writeTempFile(state: SCCUIWindowState, scope: CoroutineScope) = runBlocking {
    val resourcesDir = File(System.getProperty("compose.application.resources.dir")).toString()
    val command_scas_win = resourcesDir + "\\scas " + resourcesDir + "\\temp.txt " + resourcesDir + "\\temp.bin"
    val command_scwr_win = resourcesDir + "\\scwr " + resourcesDir + "\\temp.bin"
    val command_scas = resourcesDir + "/scas " + resourcesDir + "/temp.txt " + resourcesDir + "/temp.bin"
    val command_scwr = resourcesDir + "/scwr " + resourcesDir + "/temp.bin"

    state.saveTemp(resourcesDir)
    Thread.sleep(1000)
    if (System.getProperty("os.name").lowercase().contains("win")) {
        state.commandLine = Runtime.getRuntime().exec(command_scas_win).toString()
        scope.launch {
            if (state.askToFlash()) {
                state.commandLine = Runtime.getRuntime().exec(command_scwr_win).toString()
            }
        }

    } else {
        state.commandLine = Runtime.getRuntime().exec(command_scas).toString()
        scope.launch {
            if (state.askToFlash()) {
                state.commandLine = command_scwr.evalBash().getOrThrow()
            }

        }
    }
}

@Composable
private fun layerButton (state: SCCUIWindowState, layer: Int) {
    Button(
        onClick = {
            for (i in 0..8) {
                if (i != layer) {
                    state.buttonColor[i] = Color.DarkGray
                } else {
                    state.buttonColor[i] = Color.Green
                }

            }
            state.layer = layer
            state.updateRemapblock()
        },
        colors = ButtonDefaults.buttonColors(state.buttonColor[layer])

    ) { Text("Layer "+layer) }


}

@Composable
private fun keyboard(state: SCCUIWindowState) {

    //Variables
    var t = 1
    var c = 1

    Box(
        modifier = Modifier
            .absolutePadding(left = state.defaultWidth)
        //.border(2.dp, Color.LightGray, RectangleShape)
    ) {
        state.rows.forEach {
            Row(
                modifier = Modifier.absolutePadding(top = state.defaultHeight.times(t))//.paddingFromBaseline(top = defaultHeight.times(t))
            ) {
                t++
                it.forEach {
                    it.row = t - 2
                    it.column = c - 1

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
                                    it.label
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
            }
            c = 1
        }
        t = 1
    }
}


@Composable
private fun mapToDropDown(state: SCCUIWindowState){

    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column (modifier = Modifier.padding(20.dp)) {

        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = state.mapToDescription,
            onValueChange = { state.mapToDescription = it },
            modifier = Modifier
                 //.fillMaxWidth()
                 .onGloballyPositioned { coordinates ->
                     // This value is used to assign to
                     // the DropDown the same width
                     mTextFieldSize = coordinates.size.toSize()
                },
            label = {Text("Select key to map to")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }
        )

        // Create a drop-down menu with list of keys,
        // when clicked, set the Text Field text as the key selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier.width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
        ) {
            state.mappingKeys.forEach {
                DropdownMenuItem(onClick = {
                    state.mapToDescription = it.description
                    state.mapTo = it.name
                    mExpanded = false
                }) {
                    Text(
                        text = it.description
                    )
                }
            }
        }
    }
}

@Composable
private fun applyButton(state: SCCUIWindowState) {
    Button(
        modifier = Modifier.padding(28.dp),
        onClick = {
            state.rows[state.row][state.column].label = state.label
            state.rows[state.row][state.column].mapTo[state.layer] = state.mapTo
            state.updateRemapblock()
        }
    ) { Text("APPLY") }
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