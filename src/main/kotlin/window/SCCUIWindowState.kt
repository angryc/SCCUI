package window

import SCCUIApplicationState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import common.Settings
import common.getKeyboard
import common.getKeyboards
import evalBash
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import util.AlertDialogResult
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readLines


data class Key(
    var name: String,
    var label: String,
    //val HID: String,
    val width: Double,
    val height: Double,
    //val color: String,
    var backgroundColor: Color,
    //var mapTo: String? = null,
    var mapTo: MutableList<String?>,
    var row: Int? = null,
    var column: Int? = null
    //val description: String
)
data class MappingKey(
    var name: String,
    val HID: String,
    val description: String
)

data class Macro(
    var name: String,
    var trigger: String,
    var metaTriggers: MutableList<MacroMetaTrigger>,
    var index: Int,
    val actions: MutableList<MacroAction>
)
data class MacroMetaTrigger(
    var pressed: Boolean,
    var leftRight: String,
    var keyName: String
)

data class MacroAction(
    var keyName: String,
    var action: String,
    var index: Int
)

data class Actions(
    var action: String,
    val description: String
)


class SCCUIWindowState(
    private val application: SCCUIApplicationState,
    path: Path?,
    private val exit: (SCCUIWindowState) -> Unit
) {

    // ########## GENERAL #############

    val settings: Settings get() = application.settings
    val resourcesDir = File(System.getProperty("compose.application.resources.dir")).toString()

    private var _notifications = Channel<NotepadWindowNotification>(0)
    val notifications: Flow<NotepadWindowNotification> get() = _notifications.receiveAsFlow()

    val window = WindowState(height = 1000.dp, width = 1700.dp)
    val defaultWidth = 40.dp
    val defaultHeight = 40.dp

    var mExpanded = mutableStateListOf(false, false, false, false, false, false, false)
    var mTextFieldSize = mutableStateListOf(Size.Zero, Size.Zero, Size.Zero, Size.Zero, Size.Zero, Size.Zero, Size.Zero)

    var statusText by mutableStateOf("")

    var checkedConfigState by mutableStateOf(false)
    var checkedLayerState by mutableStateOf(false)
    var checkedMacroState by mutableStateOf(false)

    val activeButtonTextColor = Color.Black //Color.White
    val inactiveButtonTextColor = Color.LightGray
    val activeButtonColor = Color.LightGray //Color(0xFF0000AA)
    val inactiveButtonColor = Color(0xFF0000AA) //Color.LightGray
    val backgroundColor = Color(0xFF0000AA) //Color.LightGray
    val borderColor = Color.LightGray
    val textColor = Color.LightGray

    val oldFont = FontFamily.Monospace
    /*var oldFont = FontFamily(
        Font(
            //resource = File(System.getProperty("compose.application.resources.dir")).toString()+ "/VT323-Regular.ttf",
            //resource = "fonts/ModernDOS8x14.ttf",
            resource = "VT323-Regular.ttf",
            //resource = "fonts/Perfect DOS VGA 437.ttf",
            weight = FontWeight.W400,
            style = FontStyle.Normal
        )
    )*/

    fun readSettings() {
        val filePath: String = if (System.getProperty("os.name").lowercase().contains("win")) {
            resourcesDir + "\\settings.txt"
        } else {
            resourcesDir + "/settings.txt"
        }
        try {
            _input = Paths.get(filePath).readLines(Charset.defaultCharset())
            isInit = true
            _input.forEach {
                val s = it.trim().split(":")
                if (s[0].contains("showConfig")) {
                    checkedConfigState = s[1].contains("true")
                } else if (s[0].contains("showLayers")) {
                    checkedLayerState = s[1].contains("true")
                } else if (s[0].contains("showMacros")) {
                    checkedMacroState = s[1].contains("true")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            output = "Cannot read $filePath"
        }

    }






    // ############  INPUT  #############


    private lateinit var _input: List<String>

    // read config from Soarer's Converter
    fun read() {
        val filePath: String
        val commandRdWin = ProcessBuilder(resourcesDir+"\\scrd", resourcesDir+"\\temp.bin")
        val commandDisWin = ProcessBuilder(resourcesDir+"\\scdis", resourcesDir+"\\temp.bin", resourcesDir+"\\temp.txt")

        val commandRd = ProcessBuilder(resourcesDir+"/scrd", resourcesDir+"/temp.bin")
        val commandDis = ProcessBuilder(resourcesDir+"/scdis", resourcesDir+"/temp.bin", resourcesDir+"/temp.txt")

        if (System.getProperty("os.name").lowercase().contains("win")) {
            commandRdWin.start()
            Thread.sleep(1000)
            commandDisWin.start()
            Thread.sleep(1000)
            filePath = resourcesDir + "\\temp.txt"
        } else {
            commandRd.start()
            Thread.sleep(1000)
            commandDis.start()
            Thread.sleep(1000)
            filePath = resourcesDir + "/temp.txt"
        }
        //println(filePath)
        statusText = "Config was read."
        open(Paths.get(filePath))

    }


    // read config from file
    private fun open(path: Path) {
        isInit = false
        isChanged = false
        this.path = path
        var remapBlockFound = false
        var layerBlockFound = false
        var macroBlockFound = false
        var macroIndex = 0
        var macroActionIndex = 0
        var firstLine = false
        try {
            _input = path.readLines(Charset.defaultCharset())
            isInit = true

            _input.forEach {
                if (it.contains("endblock", ignoreCase = true)) {
                    remapBlockFound = false
                    layerBlockFound = false
                    macroBlockFound = false
                } else if (remapBlockFound) {
                    val s = it.trim().split(" ")
                    if (firstLine) { //first line after "remapblock" can be a layer information, but has to default to layer 0 if omitted
                        if (s[0] == "layer") {
                            layer = s[1].toInt()
                        } else {
                            layer = 0
                            setMapTo(s, layer)
                            if (s[1].startsWith("FN")) {
                                fnKey[fnKey.indexOfFirst { it == s[1] }] = s[0]
                            }
                        }
                        firstLine = false
                    } else {
                        setMapTo(s, layer)
                        if (s[1].startsWith("FN")) {
                            fnKey[fnKey.indexOfFirst { it == s[1] }] = s[0]
                        }
                    }

                } else if (layerBlockFound) {
                    val s = it.trim().split(" ")
                    val inputLayer = s.last().toInt()
                    val end = s.size - 2
                    for (i in 0 .. end) {
                        if (fnKey.indexOfFirst { it == s[i] } != -1 ) {
                            layerKey[inputLayer][i] = fnKey.indexOfFirst { it == s[i] }
                        } else {
                            layerKey[inputLayer][i] = fnKey.indexOfFirst { it == "" }
                            fnKey[fnKey.indexOfFirst { it == "" }] = s[i]
                        }

                    }
                } else if (macroBlockFound) {
                    val s = it.trim().split(" ")
                    println(s)
                    if (s[0] == "endmacro") {
                        macroIndex++
                        macroActionIndex = 0
                    } else if (s[0] == "macro") {
                        var trigger = s[1]
                        var name = "No Name"
                        /*if (s[2] != "") {
                            name = s[2].replace("#", "")
                        }*/
                        var metaTriggers = mutableListOf(MacroMetaTrigger(false, "", ""))
                        metaTriggers.clear()
                        for (i in 2 until s.size) {
                            var triggerString = s[i]
                            var pressed = true
                            var leftRight = ""
                            var keyName = ""
                            if (triggerString != "") {
                                if (triggerString.substring(0, 1) == "-") {
                                    pressed = false
                                    val i = triggerString.length
                                    triggerString = triggerString.substring(1, i)
                                }
                                if (triggerString.substring(0, 1).uppercase() == "R" || triggerString.substring(0, 1)
                                        .uppercase() == "L"
                                ) {
                                    leftRight = triggerString.substring(0, 1).uppercase()
                                    val i = triggerString.length
                                    triggerString = triggerString.substring(1, i)
                                }
                                if (triggerString == "control") {
                                    triggerString = "CTRL"
                                }
                                keyName = triggerString
                                if (keyName == "ALT" || keyName == "SHIFT" || keyName == "GUI" || keyName == "CTRL") {
                                    metaTriggers.add(MacroMetaTrigger(pressed, leftRight, keyName))
                                    //println(pressed.toString() + leftRight + keyName)
                                }
                            }
                        }
                        macros.add(Macro(trigger, trigger, metaTriggers, macroIndex, mutableListOf(MacroAction("", "", 0))))
                        macros[macroIndex].actions.clear()
                    } else if (s.size == 2 && (s[0] == "PRESS" || s[0] == "MAKE" || s[0] == "BREAK" || s[0] == "SET_META" || s[0] == "CLEAR_META")) {
                        var keyName = s[1]
                        var action = s[0]
                        if (action == "SET_META") { action = "MAKE" }
                        else if (action == "CLEAR_META") { action = "BREAK" }
                        if (keyName == "lcontrol") {
                            keyName = "LCTRL"
                        } else if (keyName == "rcontrol") {
                            keyName = "RCTRL"
                        }
                        macros[macroIndex].actions.add(MacroAction(keyName, action, macroActionIndex))
                        macroActionIndex++
                    }
                } else if (it.contains("remapblock", ignoreCase = true)) {
                    remapBlockFound = true
                    firstLine = true
                } else if (it.contains("layerblock", ignoreCase = true)) {
                    layerBlockFound = true
                } else if (it.contains("macroblock", ignoreCase = true)) {
                    macroBlockFound = true
                    if (macros[0].trigger == "") {
                        macros.clear()
                    }
                }
            }
            layer = 0 //set layer to default layer
            updateRemapblockOutput(0)

        } catch (e: Exception) {
            e.printStackTrace()
            output = "Cannot read $path"
        }
    }









    // ###########   LAYERS   ###########



    var layerButtonColor = mutableStateListOf(activeButtonColor, inactiveButtonColor, inactiveButtonColor, inactiveButtonColor, inactiveButtonColor, inactiveButtonColor, inactiveButtonColor, inactiveButtonColor, inactiveButtonColor)
    var layerButtonTextColor = mutableStateListOf(activeButtonTextColor, inactiveButtonTextColor, inactiveButtonTextColor, inactiveButtonTextColor, inactiveButtonTextColor, inactiveButtonTextColor, inactiveButtonTextColor, inactiveButtonTextColor, inactiveButtonTextColor)

    private var _layer by mutableStateOf(0)
    var layer: Int
        get() = _layer
        set(value) {
            check(isInit)
            _layer = value
            isChanged = true
        }


    //these are for temp storage until "apply" is pressed
    var layerKeyNameTemp = mutableStateListOf("", "", "", "")
    var layerKeyDescriptionTemp = mutableStateListOf("", "", "", "")

    //these are used to create the output
    var fnKey = mutableStateListOf("not used but needed", "", "", "", "", "", "", "", "")
    var layerKey = mutableStateListOf(mutableListOf(0,0,0,0), mutableListOf(0,0,0,0), mutableListOf(0,0,0,0), mutableListOf(0,0,0,0), mutableListOf(0,0,0,0), mutableListOf(0,0,0,0), mutableListOf(0,0,0,0), mutableListOf(0,0,0,0), mutableListOf(0,0,0,0))


    fun applyLayerKeyButtonPressed() {

        for (x in 0..2) {
            var found = false
            var i = 1
            while (i < 9) {
                if (fnKey[i] == layerKeyNameTemp[x] && layerKeyNameTemp[x] != "") { //look for already existing mappings
                    layerKey[layer][x] = i
                    setMapTo(listOf(layerKeyNameTemp[x], "FN" + i), 0)
                    found = true
                    break
                }
                i++
            }
            if (!found) {
                i = 1
                while (i < 9) {
                    if (fnKey[i] == "" && layerKeyNameTemp[x] != "NOMAPPING" && layerKeyNameTemp[x] != "") { //create new mapping in empty slot
                        //println(state.layerKeyName[x])
                        fnKey[i] = layerKeyNameTemp[x]
                        //println(state.fnKey[i])
                        layerKey[layer][x] = i
                        setMapTo(listOf(layerKeyNameTemp[x], "FN"+i), 0)
                        break

                    } else if (layerKeyNameTemp[x] == "NOMAPPING"){
                        layerKey[layer][x] = 0
                        //setMapTo(listOf(layerKeyNameTemp[x], null), 0)
                    }
                    i++
                }
            }
            clearUnusedFnKeys()

        }
        statusText = "Layer Keys saved."
        /*
        for (x in 0..2) {
            println(layerKey[layer][x])
        } */
        updateLayerblockOutput()
        updateRemapblockOutput(0)

    }

    fun updateLayerblockOutput() {
        var i = 0
        layerblockOutput = "layerblock \r\n  "
        layerKey.forEach {
            var o = 0
            layerKey[i].forEach {
                if (it != 0) {
                    layerblockOutput += " FN" + it.toString()
                    o++
                }
            }
            if (o > 0) {
                layerblockOutput += " " + i.toString() + "\r\n  "
            }
            i++
        }
        layerblockOutput += "endblock" + "\r\n\r\n"
        //println (layerblockOutput)
        updateOutput()
    }

    private fun clearUnusedFnKeys() {

        for (f in 1..8) {
            var exists = false
            for (l in 0..7) {
                if (exists) {break}
                for (i in 0..3) {
                    if (layerKey[l][i] == f) {
                        exists = true
                        break
                    }
                }
            }
            if (!exists) {
                setMapTo(listOf(fnKey[f], null), 0)
                fnKey[f] = ""

            }
        }
    }





    // ########### Keyboard #############

    var keyboard by mutableStateOf(0)
    var keyboards = getKeyboards()











    // ########### MACROS #############


    var macroMode by mutableStateOf(false)
    var macros = mutableStateListOf(Macro("New", "", mutableListOf(MacroMetaTrigger(false, "", "")), 0, mutableListOf(MacroAction("", "", 0))))
    var metaTriggerChoices = listOf("Left pressed", "Left pressed, Right not pressed", "Left and Right pressed", "Right pressed", "Right pressed and Left not pressed", "Left and Right not pressed", "Left not pressed", "Right not pressed", "Any pressed", "Any not pressed", "")
    val actions = listOf(Actions("PRESS", "Press down & let go"), Actions("MAKE", "Press down & hold"), Actions("BREAK", "Let go"))

    //macro <hid> [[-][{l|r}]shift] [[-][{l|r}]ctrl] [[-][{l|r}]alt] [[-][{l|r}]gui] ...

    //temp
    var macroButtonColor by mutableStateOf(inactiveButtonColor)
    var macroButtonTextColor by mutableStateOf(inactiveButtonTextColor)
    var selectedMacro by mutableStateOf(0)
    var macroName by mutableStateOf("")
    var triggerKey by mutableStateOf("")
    var triggerKeyDescription by mutableStateOf("")
    var metaTriggers = mutableStateListOf("", "", "", "")
    /*var ctrl = mutableStateListOf("")
    var shift = mutableStateListOf("")
    var alt = mutableStateListOf("")
    var gui = mutableStateListOf("")*/
    var action = mutableStateListOf("")
    var actionDescription = mutableStateListOf("")
    var actionKey = mutableStateListOf("")
    var actionKeyDescription = mutableStateListOf("")

    var mExpandedMetaTrigger = mutableStateListOf(false, false, false, false)
    var mTextFieldSizeMetaTrigger = mutableStateListOf(Size.Zero, Size.Zero, Size.Zero, Size.Zero)
    var mExpandedAction = mutableStateListOf(false)
    var mTextFieldSizeAction = mutableStateListOf(Size.Zero)
    var mExpandedActionKey = mutableStateListOf(false)
    var mTextFieldSizeActionKey = mutableStateListOf(Size.Zero)


    fun saveMacroButtonPressed() {
        if (triggerKey != "" && triggerKey != "NOMAPPING") {
            macros[selectedMacro].name = macroName
            macros[selectedMacro].trigger = triggerKey
            macros[selectedMacro].metaTriggers.clear()
            for (i in 0 until metaTriggers.size) {
                var keyName = ""
                when (i) {
                    0 -> { keyName = "CTRL" }
                    1 -> { keyName = "SHIFT" }
                    2 -> { keyName = "ALT" }
                    3 -> { keyName = "GUI"}
                }
                when (metaTriggers[i]) {
                    "Left pressed" -> { macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(true, "L", keyName)) }
                    "Left pressed, Right not pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(true, "L", keyName))
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "R", keyName))
                    }
                    "Left and Right pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(true, "L", keyName))
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(true, "R", keyName))
                    }
                    "Right pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(true, "R", keyName))
                    }
                    "Right pressed and Left not pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "L", keyName))
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(true, "R", keyName))
                    }
                    "Left and Right not pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "L", keyName))
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "R", keyName))
                    }
                    "Any pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(true, "", keyName))
                    }
                    "Left not pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "L", keyName))
                    }
                    "Right not pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "R", keyName))
                    }
                    "Any not pressed" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "", keyName))
                    }
                    "" -> {
                        macros[selectedMacro].metaTriggers.add(MacroMetaTrigger(false, "", ""))
                    }

                }

            }
            macros[selectedMacro].actions.clear()
            for (i in 0 until action.size) {
                macros[selectedMacro].actions.add(MacroAction(actionKey[i], action[i], i))
            }
            statusText = "Macro saved."
        } else {
            statusText = "Please fill all fields before you save."
        }
        if (macros[macros.size-1].trigger != "") {
            macros.add(Macro("New", "", mutableListOf(MacroMetaTrigger(false, "", "")), macros.size, mutableListOf(MacroAction("", "", 0))))
        }
        updateMacroblockOutput()
    }


    private fun updateMacroblockOutput() {

        macroblockOutput = "macroblock\r\n"
        macros.forEach() {
            if (it.trigger != "") {
                macroblockOutput += "macro " + it.trigger
                for (i in 0 until it.metaTriggers.size) {
                    if (it.metaTriggers[i].keyName != "") {
                        macroblockOutput += " "
                        if (!it.metaTriggers[i].pressed) {
                            macroblockOutput += "-"
                        }
                        macroblockOutput += it.metaTriggers[i].leftRight + it.metaTriggers[i].keyName
                    }
                }
                macroblockOutput += " #" + it.name + "\r\n  CLEAR_META all \r\n"
                it.actions.forEach() {
                    if (it.action != "" && it.keyName != "") {
                        macroblockOutput += "  " + it.action + " " + it.keyName + "\r\n"
                    }
                }
                macroblockOutput += "endmacro\r\n"
            }
        }
        macroblockOutput += "endblock"
        //println(macroblockOutput)
        updateOutput()
    }








    // ########### MAPPINGS #############



    val mappingKeys = mutableListOf(MappingKey("NOMAPPING", "-1", "No Mapping"), MappingKey("UNASSIGNED", "00", "No Event"), MappingKey("OVERRUN_ERROR", "01", "Overrun Error"), MappingKey("POST_FAIL", "02", "POST Fail"), MappingKey("ERROR_UNDEFINED", "03", "ErrorUndefined"), MappingKey("A", "04", "a A"), MappingKey("B", "05", "b B"), MappingKey("C", "06", "c C"), MappingKey("D", "07", "d D"), MappingKey("E", "08", "e E"), MappingKey("F", "09", "f F"), MappingKey("G", "0A", "g G"), MappingKey("H", "0B", "h H"), MappingKey("I", "0C", "i I"), MappingKey("J", "0D", "j J"), MappingKey("K", "0E", "k K"), MappingKey("L", "0F", "l L"), MappingKey("M", "10", "m M"), MappingKey("N", "11", "n N"), MappingKey("O", "12", "o O"), MappingKey("P", "13", "p P"), MappingKey("Q", "14", "q Q"), MappingKey("R", "15", "r R"), MappingKey("S", "16", "s S"), MappingKey("T", "17", "t T"), MappingKey("U", "18", "u U"), MappingKey("V", "19", "v V"), MappingKey("W", "1A", "w W"), MappingKey("X", "1B", "x X"), MappingKey("Y", "1C", "y Y (de: z Z )"), MappingKey("Z", "1D", "z Z (de: y Y )"), MappingKey("1", "1E", "1 !"), MappingKey("2", "1F", "2 @ (de: \" )"), MappingKey("3", "20", "3 # (de: § )"), MappingKey("4", "21", "4 $"), MappingKey("5", "22", "5 %"), MappingKey("6", "23", "6 ^ (de: & )"), MappingKey("7", "24", "7 & (de: / {)"), MappingKey("8", "25", "8 * (de: ( [ )"), MappingKey("9", "26", "9 ( (de: ) ] )"), MappingKey("0", "27", "0 ) (de: = } )"), MappingKey("ENTER", "28", "Return"), MappingKey("ESC", "29", "Escape"), MappingKey("BACKSPACE", "2A", "Backspace"), MappingKey("TAB", "2B", "Tab"), MappingKey("SPACE", "2C", "Space"), MappingKey("MINUS", "2D", "- _ (de: ß ? \\ )"), MappingKey("EQUAL", "2E", "= + (de: ´ `)"), MappingKey("LEFT_BRACE", "2F", "[ { (de: ü Ü)"), MappingKey("RIGHT_BRACE", "30", "] } (de: + * ~ )"), MappingKey("BACKSLASH", "31", "\\ | (de: # ' )"), MappingKey("EUROPE_1", "32", "Europe 1 (use BACKSLASH instead)"), MappingKey("SEMICOLON", "33", "; : (de: ö Ö )"), MappingKey("QUOTE", "34", "' \" (de: # ' )"), MappingKey("BACK_QUOTE", "35", "` ~ (de: < > )"), MappingKey("COMMA", "36", ", < (de: ; )"), MappingKey("PERIOD", "37", ". > (de: : )"), MappingKey("SLASH", "38", "/ ? (de: - _ )"), MappingKey("CAPS_LOCK", "39", "Caps Lock"), MappingKey("F1", "3A", "F1"), MappingKey("F2", "3B", "F2"), MappingKey("F3", "3C", "F3"), MappingKey("F4", "3D", "F4"), MappingKey("F5", "3E", "F5"), MappingKey("F6", "3F", "F6"), MappingKey("F7", "40", "F7"), MappingKey("F8", "41", "F8"), MappingKey("F9", "42", "F9"), MappingKey("F10", "43", "F10"), MappingKey("F11", "44", "F11"), MappingKey("F12", "45", "F12"), MappingKey("PRINTSCREEN", "46", "Print Screen"), MappingKey("SCROLL_LOCK", "47", "Scroll Lock"), MappingKey("PAUSE", "48", "Pause"), MappingKey("INSERT", "49", "Insert"), MappingKey("HOME", "4A", "Home"), MappingKey("PAGE_UP", "4B", "Page Up"), MappingKey("DELETE", "4C", "Delete"), MappingKey("END", "4D", "End"), MappingKey("PAGE_DOWN", "4E", "Page Down"), MappingKey("RIGHT", "4F", "Right Arrow"), MappingKey("LEFT", "50", "Left Arrow"), MappingKey("DOWN", "51", "Down Arrow"), MappingKey("UP", "52", "Up Arrow"), MappingKey("NUM_LOCK", "53", "Num Lock"), MappingKey("PAD_SLASH", "54", "Keypad /"), MappingKey("PAD_ASTERIX", "55", "Keypad *"), MappingKey("PAD_MINUS", "56", "Keypad -"), MappingKey("PAD_PLUS", "57", "Keypad +"), MappingKey("PAD_ENTER", "58", "Keypad Enter"), MappingKey("PAD_1", "59", "Keypad 1 End"), MappingKey("PAD_2", "5A", "Keypad 2 Down"), MappingKey("PAD_3", "5B", "Keypad 3 PageDn"), MappingKey("PAD_4", "5C", "Keypad 4 Left"), MappingKey("PAD_5", "5D", "Keypad 5"), MappingKey("PAD_6", "5E", "Keypad 6 Right"), MappingKey("PAD_7", "5F", "Keypad 7 Home"), MappingKey("PAD_8", "60", "Keypad 8 Up"), MappingKey("PAD_9", "61", "Keypad 9 PageUp"), MappingKey("PAD_0", "62", "Keypad 0 Insert"), MappingKey("PAD_PERIOD", "63", "Keypad . Delete"), MappingKey("EUROPE_2", "64", "Europe 2"), MappingKey("APP", "65", "App (Windows Menu)"), MappingKey("POWER", "66", "Keyboard Power"), MappingKey("PAD_EQUALS", "67", "Keypad ="), MappingKey("F13", "68", "F13"), MappingKey("F14", "69", "F14"), MappingKey("F15", "6A", "F15"), MappingKey("F16", "6B", "F16"), MappingKey("F17", "6C", "F17"), MappingKey("F18", "6D", "F18"), MappingKey("F19", "6E", "F19"), MappingKey("F20", "6F", "F20"), MappingKey("F21", "70", "F21"), MappingKey("F22", "71", "F22"), MappingKey("F23", "72", "F23"), MappingKey("F24", "73", "F24"), MappingKey("EXECUTE", "74", "Keyboard Execute"), MappingKey("HELP", "75", "Keyboard Help"), MappingKey("MENU", "76", "Keyboard Menu"), MappingKey("SELECT", "77", "Keyboard Select"), MappingKey("STOP", "78", "Keyboard Stop"), MappingKey("AGAIN", "79", "Keyboard Again"), MappingKey("UNDO", "7A", "Keyboard Undo"), MappingKey("CUT", "7B", "Keyboard Cut"), MappingKey("COPY", "7C", "Keyboard Copy"), MappingKey("PASTE", "7D", "Keyboard Paste"), MappingKey("FIND", "7E", "Keyboard Find"), MappingKey("MUTE", "7F", "Keyboard Mute"), MappingKey("VOLUME_UP", "80", "Keyboard Volume Up"), MappingKey("VOLUME_DOWN", "81", "Keyboard Volume Dn"), MappingKey("LOCKING_CAPS_LOCK", "82", "Keyboard Locking Caps Lock"), MappingKey("LOCKING_NUM_LOCK", "83", "Keyboard Locking Num Lock"), MappingKey("LOCKING_SCROLL_LOCK", "84", "Keyboard Locking Scroll Lock"), MappingKey("PAD_COMMA", "85", "Keypad comma (Brazilian Keypad .)"), MappingKey("EQUAL_SIGN", "86", "Keyboard Equal Sign"), MappingKey("INTERNATIONAL_1", "87", "Keyboard Int'l 1 (Ro)"), MappingKey("INTERNATIONAL_2", "88", "Keyboard Intl'2 (Katakana/Hiragana)"), MappingKey("INTERNATIONAL_3", "89", "Keyboard Int'l 2 (Yen)"), MappingKey("INTERNATIONAL_4", "8A", "Keyboard Int'l 4 (Henkan)"), MappingKey("INTERNATIONAL_5", "8B", "Keyboard Int'l 5 (Muhenkan)"), MappingKey("INTERNATIONAL_6", "8C", "Keyboard Int'l 6 (PC9800 Keypad comma)"), MappingKey("INTERNATIONAL_7", "8D", "Keyboard Int'l 7"), MappingKey("INTERNATIONAL_8", "8E", "Keyboard Int'l 8"), MappingKey("INTERNATIONAL_9", "8F", "Keyboard Int'l 9"), MappingKey("LANG_1", "90", "Keyboard Lang 1 (Hanguel/English)"), MappingKey("LANG_2", "91", "Keyboard Lang 2 (Hanja)"), MappingKey("LANG_3", "92", "Keyboard Lang 3 (Katakana)"), MappingKey("LANG_4", "93", "Keyboard Lang 4 (Hiragana)"), MappingKey("LANG_5", "94", "Keyboard Lang 5 (Zenkaku/Hankaku)"), MappingKey("LANG_6", "95", "Keyboard Lang 6"), MappingKey("LANG_7", "96", "Keyboard Lang 7"), MappingKey("LANG_8", "97", "Keyboard Lang 8"), MappingKey("LANG_9", "98", "Keyboard Lang 9"), MappingKey("ALTERNATE_ERASE", "99", "Keyboard Alternate Erase"), MappingKey("SYSREQ_ATTN", "9A", "Keyboard SysReq/Attention"), MappingKey("CANCEL", "9B", "Keyboard Cancel"), MappingKey("CLEAR", "9C", "Keyboard Clear (use DELETE instead)"), MappingKey("PRIOR", "9D", "Keyboard Prior"), MappingKey("RETURN", "9E", "Keyboard Return"), MappingKey("SEPARATOR", "9F", "Keyboard Separator"), MappingKey("OUT", "A0", "Keyboard Out"), MappingKey("OPER", "A1", "Keyboard Oper"), MappingKey("CLEAR_AGAIN", "A2", "Keyboard Clear/Again"), MappingKey("CRSEL_PROPS", "A3", "Keyboard CrSel/Props"), MappingKey("EXSEL", "A4", "Keyboard ExSel"), MappingKey("SYSTEM_POWER", "A8", "System Power"), MappingKey("SYSTEM_SLEEP", "A9", "System Sleep"), MappingKey("SYSTEM_WAKE", "AA", "System Wake"), MappingKey("AUX1", "AB", "Auxiliary key 1"), MappingKey("AUX2", "AC", "Auxiliary key 2"), MappingKey("AUX3", "AD", "Auxiliary key 3"), MappingKey("AUX4", "AE", "Auxiliary key 4"), MappingKey("AUX5", "AF", "Auxiliary key 5"), MappingKey("EXTRA_LALT", "B1", "AT-F extra pad lhs of space"), MappingKey("EXTRA_PAD_PLUS", "B2", "Term extra pad bottom of keypad +"), MappingKey("EXTRA_RALT", "B3", "AT-F extra pad rhs of space"), MappingKey("EXTRA_EUROPE_2", "B4", "AT-F extra pad lhs of enter"), MappingKey("EXTRA_BACKSLASH", "B5", "AT-F extra pad top of enter"), MappingKey("EXTRA_INSERT", "B6", "AT-F extra pad lhs of Insert"), MappingKey("EXTRA_F1", "B7", "122-key Terminal lhs F1"), MappingKey("EXTRA_F2", "B8", "122-key Terminal lhs F2"), MappingKey("EXTRA_F3", "B9", "122-key Terminal lhs F3"), MappingKey("EXTRA_F4", "BA", "122-key Terminal lhs F4"), MappingKey("EXTRA_F5", "BB", "122-key Terminal lhs F5"), MappingKey("EXTRA_F6", "BC", "122-key Terminal lhs F6"), MappingKey("EXTRA_F7", "BD", "122-key Terminal lhs F7"), MappingKey("EXTRA_F8", "BE", "122-key Terminal lhs F8"), MappingKey("EXTRA_F9", "BF", "122-key Terminal lhs F9"), MappingKey("EXTRA_F10", "C0", "122-key Terminal lhs F10"), MappingKey("EXTRA_SYSRQ", "C2", "Sys Req (AT 84-key)"), MappingKey("FN1", "D0", "Function layer key 1"), MappingKey("FN2", "D1", "Function layer key 2"), MappingKey("FN3", "D2", "Function layer key 3"), MappingKey("FN4", "D3", "Function layer key 4"), MappingKey("FN5", "D4", "Function layer key 5"), MappingKey("FN6", "D5", "Function layer key 6"), MappingKey("FN7", "D6", "Function layer key 7"), MappingKey("FN8", "D7", "Function layer key 8"), MappingKey("SELECT_0", "D8", "Select reset"), MappingKey("SELECT_1", "D9", "Select 1 toggle"), MappingKey("SELECT_2", "DA", "Select 2 toggle"), MappingKey("SELECT_3", "DB", "Select 3 toggle"), MappingKey("SELECT_4", "DC", "Select 4 toggle"), MappingKey("SELECT_5", "DD", "Select 5 toggle"), MappingKey("SELECT_6", "DE", "Select 6 toggle"), MappingKey("SELECT_7", "DF", "Select 7 toggle"), MappingKey("LCTRL", "E0", "Left Control"), MappingKey("LSHIFT", "E1", "Left Shift"), MappingKey("LALT", "E2", "Left Alt"), MappingKey("LGUI", "E3", "Left GUI (Left Windows)"), MappingKey("RCTRL", "E4", "Right Control"), MappingKey("RSHIFT", "E5", "Right Shift"), MappingKey("RALT", "E6", "Right Alt"), MappingKey("RGUI", "E7", "Right GUI (Right Windows)"), MappingKey("MEDIA_NEXT_TRACK", "E8", "Scan Next Track"), MappingKey("MEDIA_PREV_TRACK", "E9", "Scan Previous Track"), MappingKey("MEDIA_STOP", "EA", "Stop"), MappingKey("MEDIA_PLAY_PAUSE", "EB", "Play/ Pause"), MappingKey("MEDIA_MUTE", "EC", "Mute"), MappingKey("MEDIA_BASS_BOOST", "ED", "Bass Boost"), MappingKey("MEDIA_LOUDNESS", "EE", "Loudness"), MappingKey("MEDIA_VOLUME_UP", "EF", "Volume Up"), MappingKey("MEDIA_VOLUME_DOWN", "F0", "Volume Down"), MappingKey("MEDIA_BASS_UP", "F1", "Bass Up"), MappingKey("MEDIA_BASS_DOWN", "F2", "Bass Down"), MappingKey("MEDIA_TREBLE_UP", "F3", "Treble Up"), MappingKey("MEDIA_TREBLE_DOWN", "F4", "Treble Down"), MappingKey("MEDIA_MEDIA_SELECT", "F5", "Media Select"), MappingKey("MEDIA_MAIL", "F6", "Mail"), MappingKey("MEDIA_CALCULATOR", "F7", "Calculator"), MappingKey("MEDIA_MY_COMPUTER", "F8", "My Computer"), MappingKey("MEDIA_WWW_SEARCH", "F9", "WWW Search"), MappingKey("MEDIA_WWW_HOME", "FA", "WWW Home"), MappingKey("MEDIA_WWW_BACK", "FB", "WWW Back"), MappingKey("MEDIA_WWW_FORWARD", "FC", "WWW Forward"), MappingKey("MEDIA_WWW_STOP", "FD", "WWW Stop"), MappingKey("MEDIA_WWW_REFRESH", "FE", "WWW Refresh"), MappingKey("MEDIA_WWW_FAVORITES", "FF", "WWW Favorites"))
    var mappingKeysDropDown = mappingKeys
    var foundMappingKeys = mutableStateListOf(mappingKeys[0])

    //variables used to create the output

    var rows = mutableStateListOf(mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))))
    fun initKeyboard(index: Int) {
        rows = mutableStateListOf(mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))), mutableListOf(Key("","",0.0,1.0, Color.LightGray, mutableListOf(null, null, null, null, null, null, null, null, null))))
        val keyboard = getKeyboard(index)
        var r = 0
        keyboard.rows.forEach() {
            var k = 0
            keyboard.rows[r].forEach() {
                rows[r].add(Key(keyboard.rows[r][k].name, keyboard.rows[r][k].label, keyboard.rows[r][k].width, keyboard.rows[r][k].height, keyboard.rows[r][k].backgroundColor, mutableListOf(null, null, null, null, null, null, null, null, null)))
                k++
            }
            r++

        }
        updateRemapblockOutput(0)

    }

    fun applyMapToButtonPressed() {
        rows[row][column].label = label
        if (rows[row][column].mapTo[layer] != null) {
            if (!rows[row][column].mapTo[layer]!!.startsWith("FN")) { //FN keys cannot be overwritten!
                if (mapTo == "NOMAPPING") { //delete mapping
                    rows[row][column].mapTo[layer] = null
                    mapTo = ""
                    mapToDescription = ""
                } else {
                    rows[row][column].mapTo[layer] = mapTo
                    statusText = "Mapping saved."
                }
            } else {
                statusText = "Function keys cannot be overwritten. Select relevant layer and deselect key there."
            }
        } else {
            if (mapTo == "") {
                statusText = "Mapping NOT saved, because no key to map to was selected."
            } else {
                rows[row][column].mapTo[layer] = mapTo
                statusText = "Mapping saved."
            }
        }
        updateRemapblockOutput(layer)
    }



    // temp variables

    //label on the selected key
    private var _label by mutableStateOf("")
    var label: String
        get() = _label
        set(value) {
            check(isInit)
            _label = value
            isChanged = true
        }

    //key to which the selected key should be mapped to
    private var _mapTo by mutableStateOf("")
    var mapTo: String
        get() = _mapTo
        set(value) {
            check(isInit)
            _mapTo = value
            isChanged = true
        }
    private var _mapToDescription by mutableStateOf("")
    var mapToDescription: String
        get() = _mapToDescription
        set(value) {
            check(isInit)
            _mapToDescription = value
            isChanged = true
        }

    private var _row: Int by mutableStateOf(1)
    var row: Int
        get() = _row
        set(value) {
            check(isInit)
            _row = value
            isChanged = true
        }
    private var _column: Int by mutableStateOf(1)
    var column: Int
        get() = _column
        set(value) {
            check(isInit)
            _column = value
            isChanged = true
        }


    private fun setMapTo(s: List<String?>, layer: Int) {
        var r = 0
        rows.forEach {
            rows[r].forEach {
                if (s[0] == it.name) {
                    it.mapTo[layer] = s[1]
                    it.label += " " // only done to update UI ...
                    it.label.trim()
                }
            }
            r++
        }
    }


    fun updateRemapblockOutput(layer: Int) {
        var i = 0
        remapblockOutput[layer] = "remapblock \r\n"+"layer "+layer.toString()+"\r\n"
        rows.forEach {
            rows[i].forEach {
                if (it.mapTo[layer] != null) {
                    remapblockOutput[layer] += "  " + it.name + " " + it.mapTo[layer] + "\r\n"
                }
            }
            i++
        }
        remapblockOutput[layer] += "endblock"
        updateOutput()
    }







    // ########### OUTPUT ##############



    private var layerblockOutput by mutableStateOf("")
    private var macroblockOutput by mutableStateOf("")
    var remapblockOutput = mutableStateListOf("","","","","","","","","")
    private var _output by mutableStateOf("")
    var output: String
        get() = _output
        set(value) {
            check(isInit)
            _output = value
            isChanged = true
        }

    var path by mutableStateOf(path)
        private set

    var isChanged by mutableStateOf(false)
        private set

    val openDialog = DialogState<Path?>()
    val saveDialog = DialogState<Path?>()
    val exitDialog = DialogState<AlertDialogResult>()
    val flashDialog = DialogState<AlertDialogResult>()
    

    private var _commandLine by mutableStateOf("")
    var commandLine: String
        get() = _commandLine
        set(value) {
            check(isInit)
            _commandLine = value
            isChanged = true
        }




    fun updateOutput() {
        var i = 0
        output = layerblockOutput
        remapblockOutput.forEach {
            if (remapblockOutput[i] != "") {
                output += remapblockOutput[i] + "\r\n\r\n"
            }
            i++
        }
        output += "\r\n\r\n" + macroblockOutput
        //println(output)
    }

    fun writeSettings() = runBlocking {
        var settingsOutput = "showConfig:" + checkedConfigState.toString() + "\r\n"
        settingsOutput += "showLayers:" + checkedLayerState.toString() + "\r\n"
        settingsOutput += "showMacros:" + checkedMacroState.toString() + "\r\n"

        if (System.getProperty("os.name").lowercase().contains("win")) {
            Paths.get(resourcesDir+"\\settings.txt").writeTextAsync(settingsOutput)

        } else {
            Paths.get(resourcesDir+"/settings.txt").writeTextAsync(settingsOutput)
        }
    }


    //assemble and write file to Soarer's Converter / ask user before flashing
    fun writeTempFile(scope: CoroutineScope) = runBlocking {

        val commandAsWin = ProcessBuilder(resourcesDir+"\\scas", resourcesDir+"\\temp.txt", resourcesDir+"\\temp.bin")
        val commandWrWin = ProcessBuilder(resourcesDir+"\\scwr", resourcesDir+"\\temp.bin")

        val commandAs = ProcessBuilder(resourcesDir+"/scas", resourcesDir+"/temp.txt", resourcesDir+"/temp.bin")
        val commandWr = ProcessBuilder(resourcesDir+"/scwr", resourcesDir+"/temp.bin")

        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
        }
        if (System.getProperty("os.name").lowercase().contains("win")) {
            Paths.get(resourcesDir+"\\temp.txt").writeTextAsync(output)
            commandAsWin.start()
            statusText = "Created binary file for flashing."
            scope.launch {
                if (askToFlash()) {
                    commandWrWin.start()
                    statusText = "Soarer's Converter flashed."
                }
            }

        } else {
            Paths.get(resourcesDir+"/temp.txt").writeTextAsync(output)
            commandAs.start()
            statusText = "Created binary file for flashing."
            scope.launch {
                if (askToFlash()) {
                    commandWr.start()
                    statusText = "Soarer's Converter flashed."
                }

            }
        }
    }











    var isInit by mutableStateOf(false)
        private set

    fun toggleFullscreen() {
        window.placement = if (window.placement == WindowPlacement.Fullscreen) {
            WindowPlacement.Floating
        } else {
            WindowPlacement.Fullscreen
        }
    }

    fun run() {
        if (path != null) {
            open(path!!)
        } else {
            initNew()
        }
    }



    private fun initNew() {
        _output = ""
        isInit = true
        isChanged = false
    }

    fun newWindow() {
        application.newWindow()
    }

    suspend fun open() {
        if (askToSave()) {
            val path = openDialog.awaitResult()
            if (path != null) {
                open(path)
            }
        }
    }

    suspend fun save(): Boolean {
        check(isInit)
        if (path == null) {
            val path = saveDialog.awaitResult()
            if (path != null) {
                save(path)
                return true
            }
        } else {
            save(path!!)
            return true
        }
        return false
    }


    private var saveJob: Job? = null

    private suspend fun save(path: Path) {
        isChanged = false
        this.path = path

        saveJob?.cancel()
        saveJob = path.launchSaving(output)

        try {
            saveJob?.join()
            _notifications.trySend(NotepadWindowNotification.SaveSuccess(path))
        } catch (e: Exception) {
            isChanged = true
            e.printStackTrace()
            _notifications.trySend(NotepadWindowNotification.SaveError(path))
        }
    }



    suspend fun exit(): Boolean {
        return if (askToSave()) {
            exit(this)
            true
        } else {
            false
        }
    }

    private suspend fun askToSave(): Boolean {
        if (isChanged) {
            when (exitDialog.awaitResult()) {
                AlertDialogResult.Yes -> {
                    if (save()) {
                        return true
                    }
                }
                AlertDialogResult.No -> {
                    return true
                }
                AlertDialogResult.Cancel -> return false
            }
        } else {
            return true
        }

        return false
    }

    suspend fun askToFlash(): Boolean {
            when (flashDialog.awaitResult()) {
                AlertDialogResult.Yes -> return true
                AlertDialogResult.No -> return false
                AlertDialogResult.Cancel -> return false
            }
    }


    fun sendNotification(notification: Notification) {
        application.sendNotification(notification)
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun Path.launchSaving(text: String) = GlobalScope.launch {
    writeTextAsync(text)
}

private suspend fun Path.writeTextAsync(text: String) = withContext(Dispatchers.IO) {
    toFile().writeText(text)
}

/*
private suspend fun Path.readTextAsync() = withContext(Dispatchers.IO) {
    toFile().readText()
}*/

sealed class NotepadWindowNotification {
    class SaveSuccess(val path: Path) : NotepadWindowNotification()
    class SaveError(val path: Path) : NotepadWindowNotification()
}

class DialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)

    val isAwaiting get() = onResult != null

    suspend fun awaitResult(): T {
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        return result
    }

    fun onResult(result: T) = onResult!!.complete(result)
}