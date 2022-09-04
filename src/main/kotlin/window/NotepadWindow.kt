package window

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.*
import common.LocalAppResources
import kotlinx.coroutines.launch
import util.FileDialog
import util.YesNoCancelDialog


@Composable
fun NotepadWindow(state: NotepadWindowState) {
    val scope = rememberCoroutineScope()

    // Model - Keyboard Layout (of M122 German)
/*
    var row1 = mutableStateListOf(Key("", 3.25, 1.0, Color.White), Key("F13", 1.0, 1.0, Color.LightGray), Key("F14", 1.0, 1.0, Color.LightGray), Key("F15", 1.0, 1.0, Color.LightGray), Key("F16", 1.0, 1.0, Color.LightGray), Key("F17", 1.0, 1.0, Color.White), Key("F18", 1.0, 1.0, Color.White), Key("F19", 1.0, 1.0, Color.White), Key("F20", 1.0, 1.0, Color.White), Key("F21", 1.0, 1.0, Color.LightGray), Key("F22", 1.0, 1.0, Color.LightGray), Key("F23", 1.0, 1.0, Color.LightGray), Key("F24", 1.0, 1.0, Color.LightGray))
    var row2 = mutableStateListOf(Key("", 3.25, 1.0, Color.White), Key("F1", 1.0, 1.0, Color.LightGray), Key("F2", 1.0, 1.0, Color.LightGray), Key("F3", 1.0, 1.0, Color.LightGray), Key("F4", 1.0, 1.0, Color.LightGray), Key("F5", 1.0, 1.0, Color.White), Key("F6", 1.0, 1.0, Color.White), Key("F7", 1.0, 1.0, Color.White), Key("F8", 1.0, 1.0, Color.White), Key("F9", 1.0, 1.0, Color.LightGray), Key("F10", 1.0, 1.0, Color.LightGray), Key("F11", 1.0, 1.0, Color.LightGray), Key("F12", 1.0, 1.0, Color.LightGray))
    var row3 = mutableStateListOf(Key("", 20.0, 1.0, Color.White))
    var row4 = mutableStateListOf(Key("S-Abf", 1.0, 1.0, Color.LightGray), Key("  ", 1.0, 1.0, Color.LightGray),Key("", 0.25, 1.0, Color.White), Key("°", 1.0, 1.0, Color.White), Key("1", 1.0, 1.0, Color.White), Key("2", 1.0, 1.0, Color.White), Key("3", 1.0, 1.0, Color.White), Key("4", 1.0, 1.0, Color.White), Key("5", 1.0, 1.0, Color.White), Key("6", 1.0, 1.0, Color.White), Key("7", 1.0, 1.0, Color.White), Key("8", 1.0, 1.0, Color.White), Key("9", 1.0, 1.0, Color.White), Key("0", 1.0, 1.0, Color.White), Key("ß", 1.0, 1.0, Color.White), Key("´", 1.0, 1.0, Color.White), Key("Backspace", 2.0, 1.0, Color.LightGray), Key("", 0.25, 1.0, Color.White), Key("|<-", 1.0, 1.0, Color.LightGray), Key("Dup", 1.0, 1.0, Color.LightGray), Key("  ", 1.0, 1.0, Color.LightGray), Key("", 0.25, 1.0, Color.White), Key("  ", 1.0, 1.0, Color.LightGray), Key("  ", 1.0, 1.0, Color.LightGray), Key(".", 1.0, 1.0, Color.White), Key("  ", 1.0, 1.0, Color.LightGray))
    var row5 = mutableStateListOf(Key("  ", 1.0, 1.0, Color.LightGray), Key("E-Lö", 1.0, 1.0, Color.LightGray),Key("", 0.25, 1.0, Color.White), Key("Tab", 1.5, 1.0, Color.LightGray), Key("Q", 1.0, 1.0, Color.White), Key("W", 1.0, 1.0, Color.White), Key("E", 1.0, 1.0, Color.White), Key("R", 1.0, 1.0, Color.White), Key("T", 1.0, 1.0, Color.White), Key("Z", 1.0, 1.0, Color.White), Key("U", 1.0, 1.0, Color.White), Key("I", 1.0, 1.0, Color.White), Key("O", 1.0, 1.0, Color.White), Key("P", 1.0, 1.0, Color.White), Key("Ü", 1.0, 1.0, Color.White), Key("+", 1.0, 1.0, Color.White), Key("", 0.25, 1.0, Color.LightGray), Key("Return", 1.25, 2.0, Color.LightGray), Key("", 0.25, 1.0, Color.White), Key("<-|", 1.0, 1.0, Color.LightGray), Key("^a", 1.0, 1.0, Color.LightGray), Key("a/", 1.0, 1.0, Color.LightGray), Key("", 0.25, 1.0, Color.White), Key("7", 1.0, 1.0, Color.White), Key("8", 1.0, 1.0, Color.White), Key("9", 1.0, 1.0, Color.White), Key("Eing", 1.0, 1.0, Color.LightGray))
    var row6 = mutableStateListOf(Key("Druck", 1.0, 1.0, Color.LightGray), Key("Hilfe", 1.0, 1.0, Color.LightGray),Key("", 0.25, 1.0, Color.White), Key("Capslock", 1.75, 1.0, Color.LightGray), Key("A", 1.0, 1.0, Color.White), Key("S", 1.0, 1.0, Color.White), Key("D", 1.0, 1.0, Color.White), Key("F", 1.0, 1.0, Color.White), Key("G", 1.0, 1.0, Color.White), Key("H", 1.0, 1.0, Color.White), Key("J", 1.0, 1.0, Color.White), Key("K", 1.0, 1.0, Color.White), Key("L", 1.0, 1.0, Color.White), Key("Ö", 1.0, 1.0, Color.White), Key("Ä", 1.0, 1.0, Color.White), Key("#", 1.0, 1.0, Color.White), Key("", 1.25, 0.0, Color.LightGray), Key("", 1.25, 1.0, Color.White), Key("^", 1.0, 1.0, Color.LightGray), Key("", 1.25, 1.0, Color.White), Key("4", 1.0, 1.0, Color.White), Key("5", 1.0, 1.0, Color.White), Key("6", 1.0, 1.0, Color.White), Key("  ", 1.0, 1.0, Color.LightGray))
    var row7 = mutableStateListOf(Key("  ", 1.0, 1.0, Color.LightGray), Key("Wdgab", 1.0, 1.0, Color.LightGray),Key("", 0.25, 1.0, Color.White), Key("Shift", 1.25, 1.0, Color.LightGray), Key("<", 1.0, 1.0, Color.White), Key("Y", 1.0, 1.0, Color.White), Key("X", 1.0, 1.0, Color.White), Key("C", 1.0, 1.0, Color.White), Key("V", 1.0, 1.0, Color.White), Key("B", 1.0, 1.0, Color.White), Key("N", 1.0, 1.0, Color.White), Key("M", 1.0, 1.0, Color.White), Key(",", 1.0, 1.0, Color.White), Key(".", 1.0, 1.0, Color.White), Key("-", 1.0, 1.0, Color.White), Key("Shift", 2.75, 1.0, Color.LightGray), Key("", 0.25, 1.0, Color.White), Key("<", 1.0, 1.0, Color.LightGray), Key("Linie", 1.0, 1.0, Color.LightGray), Key(">", 1.0, 1.0, Color.LightGray), Key("", 0.25, 1.0, Color.White), Key("1", 1.0, 1.0, Color.White), Key("2", 1.0, 1.0, Color.White), Key("3", 1.0, 1.0, Color.White), Key("Eing", 1.0, 2.0, Color.LightGray))
    var row8 = mutableStateListOf(Key("Defin", 1.0, 1.0, Color.LightGray), Key("Aufz", 1.0, 1.0, Color.LightGray),Key("", 0.25, 1.0, Color.White), Key("Grdst", 1.5, 1.0, Color.LightGray), Key("", 1.0, 1.0, Color.White), Key("Alt", 1.5, 1.0, Color.LightGray), Key("Leertaste", 7.0, 1.0, Color.White), Key("Alt", 1.5, 1.0, Color.LightGray), Key("", 1.0, 1.0, Color.White), Key("Daten Freigabe", 1.5, 1.0, Color.LightGray), Key("", 1.25, 1.0, Color.White), Key("v", 1.0, 1.0, Color.LightGray), Key("", 1.25, 1.0, Color.White), Key("0", 2.0, 1.0, Color.White), Key(",", 1.0, 1.0, Color.White))
    val rows = mutableStateListOf(row1, row2, row3, row4, row5, row6, row7, row8)
    val defaultWidth = 40.dp
    val defaultHeight = 40.dp
*/

    //View

    //Variables
    var t = 1
    var c = 1
   // var text by remember { mutableStateOf("") }


    fun exit() = scope.launch { state.exit() }

    Window(
        state = state.window,
        title = titleOf(state),
        resizable = false,
        icon = LocalAppResources.current.icon,
        onCloseRequest = { exit() }
    ) {
        LaunchedEffect(Unit) { state.run() }

        WindowNotifications(state)
        WindowMenuBar(state)

        Column {

            Box(modifier = Modifier
                .absolutePadding(left = state.defaultWidth)
                //.border(2.dp, Color.LightGray, RectangleShape)
            ) {
                state.rows.forEach {
                    Row(
                        modifier = Modifier.absolutePadding(top = state.defaultHeight.times(t))//.paddingFromBaseline(top = defaultHeight.times(t))
                    ) {
                        t++
                        it.forEach {
                            it.row = t-2
                            it.column = c-1

                            c++
                            if (it.label != "") {
                                OutlinedButton(
                                    onClick = {
                                        state.label = if (it.label != "  ") { it.label } else { it.name }
                                        //text = it.name + it.row + it.column + it.mapTo
                                        state.mapTo = it.mapTo.toString()
                                        state.row = it.row!!
                                        state.column = it.column!!
                                        if (state.rows[state.row][state.column].mapTo != null) {
                                            state.mapToDescription = state.mappingKeys[state.mappingKeys.indexOfFirst { it.name == state.rows[state.row][state.column].mapTo!! }].description
                                        } else { state.mapToDescription = "" }

                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = it.backgroundColor),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(
                                        width = state.defaultWidth.times(it.width.toFloat()),
                                        height = state.defaultHeight.times(it.height.toFloat())
                                    ).padding(all = 0.dp)
                                ) {
                                    Text(
                                        text = if (it.label != "  ") { it.label } else { it.name },
                                        fontSize = if (it.label != "  ") { 9.sp } else { 7.sp },
                                        modifier = Modifier.padding(vertical = 0.dp),
                                        color = if (it.label != "  ") { Color.Black } else { Color.Gray },

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



            val currentTimezoneStrings = remember { SnapshotStateList<String>() }

        Row (modifier = Modifier
            .padding(20.dp)

        ) {
            OutlinedTextField(
                state.label,
                state::label::set,
                modifier = Modifier.padding(28.dp)
            )

            mapToDropDown(state)

            Button(
                modifier = Modifier.padding(28.dp),
                onClick = {
                    state.rows[state.row][state.column].name = state.label
                    state.rows[state.row][state.column].mapTo = state.mapTo
                }
            ) { Text("SAVE") }

        }
            // TextField isn't efficient for big text files, we use it for simplicity
            BasicTextField(
                state.output,
                state::output::set,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )

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
        }
    }
}



@Composable
fun mapToDropDown(state: NotepadWindowState){

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
                    state.output = " " + state.rows[state.row][state.column].name + " " + it.name
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

private fun titleOf(state: NotepadWindowState): String {
    val changeMark = if (state.isChanged) "*" else ""
    val filePath = state.path ?: "Untitled"
    return "$changeMark$filePath - Soarer's Converter Config UI"
}

@Composable
private fun WindowNotifications(state: NotepadWindowState) {
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
private fun FrameWindowScope.WindowMenuBar(state: NotepadWindowState) = MenuBar {
    val scope = rememberCoroutineScope()

    fun save() = scope.launch { state.save() }
    fun open() = scope.launch { state.open() }
    fun exit() = scope.launch { state.exit() }

    Menu("File") {
        Item("New window", onClick = state::newWindow)
        Item("Open...", onClick = { open() })
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