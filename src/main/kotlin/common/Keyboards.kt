package common

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import window.Key

data class Key(
    var name: String,
    var label: String,
    val width: Double,
    val height: Double,
    var backgroundColor: Color
)

data class keyboard (
    val index: Int,
    val name: String,
    val rows: List<List<common.Key>>,
    val rowCount: Int
)




val ModelFxt_en = keyboard(2, "IBM Model F XT English Layout",
    listOf(listOf(Key("F1", "F1", 1.0, 1.0, Color.LightGray), Key("F2", "F2", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("ESC", "Esc", 1.0, 1.0, Color.LightGray), Key("1", "1", 1.0, 1.0, Color.White), Key("2", "2", 1.0, 1.0, Color.White), Key("3", "3", 1.0, 1.0, Color.White), Key("4", "4", 1.0, 1.0, Color.White), Key("5", "5", 1.0, 1.0, Color.White), Key("6", "6", 1.0, 1.0, Color.White), Key("7", "7", 1.0, 1.0, Color.White), Key("8", "8", 1.0, 1.0, Color.White), Key("9", "9", 1.0, 1.0, Color.White), Key("0", "0", 1.0, 1.0, Color.White), Key("MINUS", "-", 1.0, 1.0, Color.White), Key("EQUAL", "=", 1.0, 1.0, Color.White), Key("BACKSPACE", "<--", 1.75, 1.0, Color.LightGray), Key("NUM_LOCK", "Num Lock", 2.0, 1.0, Color.LightGray), Key("SCROLL_LOCK", "Scroll Lock", 2.0, 1.0, Color.LightGray)), listOf(Key("F3", "F3", 1.0, 1.0, Color.LightGray), Key("F4","F4", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("TAB", "-->|", 1.5, 1.0, Color.LightGray), Key("Q", "Q", 1.0, 1.0, Color.White), Key("W", "W", 1.0, 1.0, Color.White), Key("E", "E", 1.0, 1.0, Color.White), Key("R", "R", 1.0, 1.0, Color.White), Key("T", "T", 1.0, 1.0, Color.White), Key("Y", "Y", 1.0, 1.0, Color.White), Key("U", "U", 1.0, 1.0, Color.White), Key("I", "I", 1.0, 1.0, Color.White), Key("O", "O", 1.0, 1.0, Color.White), Key("P", "P", 1.0, 1.0, Color.White), Key("LEFT_BRACE", "[", 1.0, 1.0, Color.White), Key("RIGHT_BRACE", "]", 1.25, 1.0, Color.White), Key("ENTER", "<-|", 1.0, 2.0, Color.LightGray), Key("PAD_7", "7", 1.0, 1.0, Color.White), Key("PAD_8", "8", 1.0, 1.0, Color.White), Key("PAD_9", "9", 1.0, 1.0, Color.White), Key("PAD_MINUS", "-", 1.0, 1.0, Color.LightGray)), listOf(Key("F5", "F5", 1.0, 1.0, Color.LightGray), Key("F6", "F6", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("LCTRL", "Ctrl", 1.75, 1.0, Color.LightGray), Key("A", "A", 1.0, 1.0, Color.White), Key("S", "S", 1.0, 1.0, Color.White), Key("D", "D", 1.0, 1.0, Color.White), Key("F", "F", 1.0, 1.0, Color.White), Key("G", "G", 1.0, 1.0, Color.White), Key("H", "H", 1.0, 1.0, Color.White), Key("J", "J", 1.0, 1.0, Color.White), Key("K", "K", 1.0, 1.0, Color.White), Key("L", "L", 1.0, 1.0, Color.White), Key("SEMICOLON", ";", 1.0, 1.0, Color.White), Key("QUOTE", "´", 1.0, 1.0, Color.White), Key("BACK_QUOTE", "`", 1.0, 1.0, Color.White), Key("", "", 1.0, 1.0, Color.White), Key("PAD_4", "4", 1.0, 1.0, Color.White), Key("PAD_5", "5", 1.0, 1.0, Color.White), Key("PAD_6", "6", 1.0, 1.0, Color.White), Key("PAD_PLUS", "+", 1.0, 3.0, Color.LightGray)), listOf(Key("F7", "F7", 1.0, 1.0, Color.LightGray), Key("F8", "F8", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("LSHIFT", "Shift", 1.25, 1.0, Color.LightGray), Key("BACKSLASH", "\\", 1.0, 1.0, Color.White), Key("Z", "Z", 1.0, 1.0, Color.White), Key("X", "X", 1.0, 1.0, Color.White), Key("C", "C", 1.0, 1.0, Color.White), Key("V", "V", 1.0, 1.0, Color.White), Key("B", "B", 1.0, 1.0, Color.White), Key("N", "N", 1.0, 1.0, Color.White), Key("M", "M", 1.0, 1.0, Color.White), Key("COMMA", ",", 1.0, 1.0, Color.White), Key("PERIOD", ".", 1.0, 1.0, Color.White), Key("SLASH", "/", 1.0, 1.0, Color.White), Key("RSHIFT", "Shift", 1.5, 1.0, Color.LightGray), Key("PAD_ASTERIX", "*", 1.0, 1.0, Color.LightGray), Key("PAD_1", "1", 1.0, 1.0, Color.White), Key("PAD_2", "2", 1.0, 1.0, Color.White), Key("PAD_3", "3", 1.0, 1.0, Color.White)) , listOf(Key("F9", "F9", 1.0, 1.0, Color.LightGray), Key("F10", "F10", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("LALT", "Alt", 2.0, 1.0, Color.LightGray), Key("SPACE", "  ", 10.0, 1.0, Color.White), Key("CAPS_LOCK", "Caps Lock", 1.75, 1.0, Color.LightGray), Key("PAD_0", "0", 2.0, 1.0, Color.White), Key("PAD_PERIOD", ",", 2.0, 1.0, Color.White)) ), 5)


val ModelM122Type3_de = keyboard(1, "IBM Model M 122 Type3 German Layout",listOf(listOf(Key("", "", 3.25, 1.0, Color.White ), Key("F13", "F13", 1.0, 1.0, Color.LightGray), Key("F14", "F14", 1.0, 1.0, Color.LightGray), Key("F15", "F15", 1.0, 1.0, Color.LightGray), Key("F16", "F16", 1.0, 1.0, Color.LightGray), Key("F17", "F17", 1.0, 1.0, Color.White), Key("F18", "F18", 1.0, 1.0, Color.White), Key("F19", "F19", 1.0, 1.0, Color.White), Key("F20", "F20", 1.0, 1.0, Color.White), Key("F21", "F21", 1.0, 1.0, Color.LightGray), Key("F22", "F22", 1.0, 1.0, Color.LightGray), Key("F23", "F23", 1.0, 1.0, Color.LightGray), Key("F24", "F24", 1.0, 1.0, Color.LightGray)), listOf(Key("", "", 3.25, 1.0, Color.White), Key("F1", "F1", 1.0, 1.0, Color.LightGray), Key("F2", "F2", 1.0, 1.0, Color.LightGray), Key("F3", "F3", 1.0, 1.0, Color.LightGray), Key("F4", "F4", 1.0, 1.0, Color.LightGray), Key("F5", "F5", 1.0, 1.0, Color.White), Key("F6", "F6", 1.0, 1.0, Color.White), Key("F7", "F7", 1.0, 1.0, Color.White), Key("F8", "F8", 1.0, 1.0, Color.White), Key("F9", "F9", 1.0, 1.0, Color.LightGray), Key("F10", "F10", 1.0, 1.0, Color.LightGray), Key("F11", "F11", 1.0, 1.0, Color.LightGray), Key("F12", "F12", 1.0, 1.0, Color.LightGray)), listOf(Key("", "", 20.0, 1.0, Color.White)), listOf(Key("EXTRA_F1", "S-Abf", 1.0, 1.0, Color.LightGray), Key("EXTRA_F2", "  ", 1.0, 1.0, Color.LightGray),
    Key("", "", 0.25, 1.0, Color.White), Key("BACK_QUOTE", " ", 1.0, 1.0, Color.White), Key("1", "1", 1.0, 1.0, Color.White), Key("2", "2", 1.0, 1.0, Color.White), Key("3", "3", 1.0, 1.0, Color.White), Key("4", "4", 1.0, 1.0, Color.White), Key("5", "5", 1.0, 1.0, Color.White), Key("6", "6", 1.0, 1.0, Color.White), Key("7", "7", 1.0, 1.0, Color.White), Key("8", "8", 1.0, 1.0, Color.White), Key("9", "9", 1.0, 1.0, Color.White), Key("0", "0", 1.0, 1.0, Color.White), Key("MINUS", "ß", 1.0, 1.0, Color.White), Key("EQUAL", "´", 1.0, 1.0, Color.White), Key("BACKSPACE", "<--", 2.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("INSERT", "|<-", 1.0, 1.0, Color.LightGray), Key("HOME", "Dup", 1.0, 1.0, Color.LightGray), Key("PAGE_UP", "  ", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("ESC", "  ", 1.0, 1.0, Color.LightGray), Key("NUM_LOCK", "  ", 1.0, 1.0, Color.LightGray), Key("SCROLL_LOCK", ".", 1.0, 1.0, Color.White), Key("EXTRA_SYSRQ", "  ", 1.0, 1.0, Color.LightGray)), listOf(Key("EXTRA_F3", "  ", 1.0, 1.0, Color.LightGray), Key("EXTRA_F4","E-Lö", 1.0, 1.0, Color.LightGray),
    Key("", "", 0.25, 1.0, Color.White), Key("TAB", "-->|", 1.5, 1.0, Color.LightGray), Key("Q", "Q", 1.0, 1.0, Color.White), Key("W", "W", 1.0, 1.0, Color.White), Key("E", "E", 1.0, 1.0, Color.White), Key("R", "R", 1.0, 1.0, Color.White), Key("T", "T", 1.0, 1.0, Color.White), Key("Y", "Z", 1.0, 1.0, Color.White), Key("U", "U", 1.0, 1.0, Color.White), Key("I", "I", 1.0, 1.0, Color.White), Key("O", "O", 1.0, 1.0, Color.White), Key("P", "P", 1.0, 1.0, Color.White), Key("LEFT_BRACE", "Ü", 1.0, 1.0, Color.White), Key("RIGHT_BRACE", "+", 1.0, 1.0, Color.White), Key("", "", 0.25, 1.0, Color.LightGray), Key("ENTER", "Eing Feld", 1.25, 2.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("DELETE", "<-|", 1.0, 1.0, Color.LightGray), Key("END", "^a", 1.0, 1.0, Color.LightGray), Key("PAGE_DOWN", "a/", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("PAD_7", "7", 1.0, 1.0, Color.White), Key("PAD_8", "8", 1.0, 1.0, Color.White), Key("PAD_9", "9", 1.0, 1.0, Color.White), Key("PAD_ASTERIX", "Eing", 1.0, 1.0, Color.LightGray)), listOf(Key("EXTRA_F5", "Druck", 1.0, 1.0, Color.LightGray), Key("EXTRA_F6", "Hilfe", 1.0, 1.0, Color.LightGray),
    Key("", "", 0.25, 1.0, Color.White), Key("CAPS_LOCK", "Capslock", 1.75, 1.0, Color.LightGray), Key("A", "A", 1.0, 1.0, Color.White), Key("S", "S", 1.0, 1.0, Color.White), Key("D", "D", 1.0, 1.0, Color.White), Key("F", "F", 1.0, 1.0, Color.White), Key("G", "G", 1.0, 1.0, Color.White), Key("H", "H", 1.0, 1.0, Color.White), Key("J", "J", 1.0, 1.0, Color.White), Key("K", "K", 1.0, 1.0, Color.White), Key("L", "L", 1.0, 1.0, Color.White), Key("SEMICOLON", "Ö", 1.0, 1.0, Color.White), Key("QUOTE", "Ä", 1.0, 1.0, Color.White), Key("EUROPE_1", "#", 1.0, 1.0, Color.White), Key("", "", 1.25, 0.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("UP", "^", 1.0, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("PAD_4", "4", 1.0, 1.0, Color.White), Key("PAD_5", "5", 1.0, 1.0, Color.White), Key("PAD_6", "6", 1.0, 1.0, Color.White), Key("PAD_MINUS", "  ", 1.0, 1.0, Color.LightGray)), listOf(Key("EXTRA_F7", "  ", 1.0, 1.0, Color.LightGray), Key("EXTRA_F8", "Wdgab", 1.0, 1.0, Color.LightGray),
    Key("", "", 0.25, 1.0, Color.White), Key("LSHIFT", "Shift", 1.25, 1.0, Color.LightGray), Key("EUROPE_2", "<", 1.0, 1.0, Color.White), Key("Z", "Y", 1.0, 1.0, Color.White), Key("X", "X", 1.0, 1.0, Color.White), Key("C", "C", 1.0, 1.0, Color.White), Key("V", "V", 1.0, 1.0, Color.White), Key("B", "B", 1.0, 1.0, Color.White), Key("N", "N", 1.0, 1.0, Color.White), Key("M", "M", 1.0, 1.0, Color.White), Key("COMMA", ",", 1.0, 1.0, Color.White), Key("PERIOD", ".", 1.0, 1.0, Color.White), Key("SLASH", "-", 1.0, 1.0, Color.White), Key("RSHIFT", "Shift", 2.75, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("LEFT", "<", 1.0, 1.0, Color.LightGray), Key("LANG_4", "Linie", 1.0, 1.0, Color.LightGray), Key("RIGHT", ">", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("PAD_1", "1", 1.0, 1.0, Color.White), Key("PAD_2", "2", 1.0, 1.0, Color.White), Key("PAD_3", "3", 1.0, 1.0, Color.White), Key("PAD_PLUS", "Eing", 1.0, 2.0, Color.LightGray)), listOf(Key("EXTRA_F9", "Defin", 1.0, 1.0, Color.LightGray), Key("EXTRA_F10", "Aufz", 1.0, 1.0, Color.LightGray),
    Key("", "", 0.25, 1.0, Color.White), Key("LCTRL", "Grdst", 1.5, 1.0, Color.LightGray), Key("", "", 1.0, 1.0, Color.White), Key("LALT", "Alt", 1.5, 1.0, Color.LightGray), Key("SPACE", "  ", 7.0, 1.0, Color.White), Key("RALT", "Alt", 1.5, 1.0, Color.LightGray), Key("", "", 1.0, 1.0, Color.White), Key("RCTRL", "Daten Freigabe", 1.5, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("DOWN", "v", 1.0, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("PAD_0", "0", 2.0, 1.0, Color.White), Key("PAD_PERIOD", ",", 1.0, 1.0, Color.White))), 8)

val empty = keyboard(0, "SELECT KEYBOARD", listOf(listOf(Key("", "", 3.25, 1.0, Color.White))), 0)

val SCCUIkeyboards = listOf(empty, ModelM122Type3_de, ModelFxt_en)

fun getKeyboards() : List<keyboard> {
    return SCCUIkeyboards
}

fun getKeyboard(index: Int) : keyboard {
    return SCCUIkeyboards[index]
}




    var row1 = mutableStateListOf(
        Key("", "", 3.25, 1.0, Color.White ), Key("F13", "F13", 1.0, 1.0, Color.LightGray), Key("F14", "F14", 1.0, 1.0, Color.LightGray), Key("F15", "F15", 1.0, 1.0, Color.LightGray), Key("F16", "F16", 1.0, 1.0, Color.LightGray), Key("F17", "F17", 1.0, 1.0, Color.White), Key("F18", "F18", 1.0, 1.0, Color.White), Key("F19", "F19", 1.0, 1.0, Color.White), Key("F20", "F20", 1.0, 1.0, Color.White), Key("F21", "F21", 1.0, 1.0, Color.LightGray), Key("F22", "F22", 1.0, 1.0, Color.LightGray), Key("F23", "F23", 1.0, 1.0, Color.LightGray), Key("F24", "F24", 1.0, 1.0, Color.LightGray)
    )
    var row2 = mutableStateListOf(
        Key("", "", 3.25, 1.0, Color.White), Key("F1", "F1", 1.0, 1.0, Color.LightGray), Key("F2", "F2", 1.0, 1.0, Color.LightGray), Key("F3", "F3", 1.0, 1.0, Color.LightGray), Key("F4", "F4", 1.0, 1.0, Color.LightGray), Key("F5", "F5", 1.0, 1.0, Color.White), Key("F6", "F6", 1.0, 1.0, Color.White), Key("F7", "F7", 1.0, 1.0, Color.White), Key("F8", "F8", 1.0, 1.0, Color.White), Key("F9", "F9", 1.0, 1.0, Color.LightGray), Key("F10", "F10", 1.0, 1.0, Color.LightGray), Key("F11", "F11", 1.0, 1.0, Color.LightGray), Key("F12", "F12", 1.0, 1.0, Color.LightGray)
    )
    var row3 = mutableStateListOf(Key("", "", 20.0, 1.0, Color.White))
    var row4 = mutableStateListOf(
        Key("EXTRA_F1", "S-Abf", 1.0, 1.0, Color.LightGray), Key("EXTRA_F2", "  ", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("BACK_QUOTE", " ", 1.0, 1.0, Color.White), Key("1", "1", 1.0, 1.0, Color.White), Key("2", "2", 1.0, 1.0, Color.White), Key("3", "3", 1.0, 1.0, Color.White), Key("4", "4", 1.0, 1.0, Color.White), Key("5", "5", 1.0, 1.0, Color.White), Key("6", "6", 1.0, 1.0, Color.White), Key("7", "7", 1.0, 1.0, Color.White), Key("8", "8", 1.0, 1.0, Color.White), Key("9", "9", 1.0, 1.0, Color.White), Key("0", "0", 1.0, 1.0, Color.White), Key("MINUS", "ß", 1.0, 1.0, Color.White), Key("EQUAL", "´", 1.0, 1.0, Color.White), Key("BACKSPACE", "<--", 2.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("INSERT", "|<-", 1.0, 1.0, Color.LightGray), Key("HOME", "Dup", 1.0, 1.0, Color.LightGray), Key("PAGE_UP", "  ", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("ESC", "  ", 1.0, 1.0, Color.LightGray), Key("NUM_LOCK", "  ", 1.0, 1.0, Color.LightGray), Key("SCROLL_LOCK", ".", 1.0, 1.0, Color.White), Key("EXTRA_SYSRQ", "  ", 1.0, 1.0, Color.LightGray)
    )
    var row5 = mutableStateListOf(
        Key("EXTRA_F3", "  ", 1.0, 1.0, Color.LightGray), Key("EXTRA_F4","E-Lö", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("TAB", "-->|", 1.5, 1.0, Color.LightGray), Key("Q", "Q", 1.0, 1.0, Color.White), Key("W", "W", 1.0, 1.0, Color.White), Key("E", "E", 1.0, 1.0, Color.White), Key("R", "R", 1.0, 1.0, Color.White), Key("T", "T", 1.0, 1.0, Color.White), Key("Y", "Z", 1.0, 1.0, Color.White), Key("U", "U", 1.0, 1.0, Color.White), Key("I", "I", 1.0, 1.0, Color.White), Key("O", "O", 1.0, 1.0, Color.White), Key("P", "P", 1.0, 1.0, Color.White), Key("LEFT_BRACE", "Ü", 1.0, 1.0, Color.White), Key("RIGHT_BRACE", "+", 1.0, 1.0, Color.White), Key("", "", 0.25, 1.0, Color.LightGray), Key("ENTER", "Eing Feld", 1.25, 2.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("DELETE", "<-|", 1.0, 1.0, Color.LightGray), Key("END", "^a", 1.0, 1.0, Color.LightGray), Key("PAGE_DOWN", "a/", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("PAD_7", "7", 1.0, 1.0, Color.White), Key("PAD_8", "8", 1.0, 1.0, Color.White), Key("PAD_9", "9", 1.0, 1.0, Color.White), Key("PAD_ASTERIX", "Eing", 1.0, 1.0, Color.LightGray)
    )
    var row6 = mutableStateListOf(
        Key("EXTRA_F5", "Druck", 1.0, 1.0, Color.LightGray), Key("EXTRA_F6", "Hilfe", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("CAPS_LOCK", "Capslock", 1.75, 1.0, Color.LightGray), Key("A", "A", 1.0, 1.0, Color.White), Key("S", "S", 1.0, 1.0, Color.White), Key("D", "D", 1.0, 1.0, Color.White), Key("F", "F", 1.0, 1.0, Color.White), Key("G", "G", 1.0, 1.0, Color.White), Key("H", "H", 1.0, 1.0, Color.White), Key("J", "J", 1.0, 1.0, Color.White), Key("K", "K", 1.0, 1.0, Color.White), Key("L", "L", 1.0, 1.0, Color.White), Key("SEMICOLON", "Ö", 1.0, 1.0, Color.White), Key("QUOTE", "Ä", 1.0, 1.0, Color.White), Key("EUROPE_1", "#", 1.0, 1.0, Color.White), Key("", "", 1.25, 0.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("UP", "^", 1.0, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("PAD_4", "4", 1.0, 1.0, Color.White), Key("PAD_5", "5", 1.0, 1.0, Color.White), Key("PAD_6", "6", 1.0, 1.0, Color.White), Key("PAD_MINUS", "  ", 1.0, 1.0, Color.LightGray)
    )
    var row7 = mutableStateListOf(
        Key("EXTRA_F7", "  ", 1.0, 1.0, Color.LightGray), Key("EXTRA_F8", "Wdgab", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("LSHIFT", "Shift", 1.25, 1.0, Color.LightGray), Key("EUROPE_2", "<", 1.0, 1.0, Color.White), Key("Z", "Y", 1.0, 1.0, Color.White), Key("X", "X", 1.0, 1.0, Color.White), Key("C", "C", 1.0, 1.0, Color.White), Key("V", "V", 1.0, 1.0, Color.White), Key("B", "B", 1.0, 1.0, Color.White), Key("N", "N", 1.0, 1.0, Color.White), Key("M", "M", 1.0, 1.0, Color.White), Key("COMMA", ",", 1.0, 1.0, Color.White), Key("PERIOD", ".", 1.0, 1.0, Color.White), Key("SLASH", "-", 1.0, 1.0, Color.White), Key("RSHIFT", "Shift", 2.75, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("LEFT", "<", 1.0, 1.0, Color.LightGray), Key("LANG_4", "Linie", 1.0, 1.0, Color.LightGray), Key("RIGHT", ">", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("PAD_1", "1", 1.0, 1.0, Color.White), Key("PAD_2", "2", 1.0, 1.0, Color.White), Key("PAD_3", "3", 1.0, 1.0, Color.White), Key("PAD_PLUS", "Eing", 1.0, 2.0, Color.LightGray)
    )
    var row8 = mutableStateListOf(
        Key("EXTRA_F9", "Defin", 1.0, 1.0, Color.LightGray), Key("EXTRA_F10", "Aufz", 1.0, 1.0, Color.LightGray),
        Key("", "", 0.25, 1.0, Color.White), Key("LCTRL", "Grdst", 1.5, 1.0, Color.LightGray), Key("", "", 1.0, 1.0, Color.White), Key("LALT", "Alt", 1.5, 1.0, Color.LightGray), Key("SPACE", "  ", 7.0, 1.0, Color.White), Key("RALT", "Alt", 1.5, 1.0, Color.LightGray), Key("", "", 1.0, 1.0, Color.White), Key("RCTRL", "Daten Freigabe", 1.5, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("DOWN", "v", 1.0, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("PAD_0", "0", 2.0, 1.0, Color.White), Key("PAD_PERIOD", ",", 1.0, 1.0, Color.White)
    )
    val rows = mutableStateListOf(row1, row2, row3, row4, row5, row6, row7, row8)


