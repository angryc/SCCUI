package window

import SCCUIApplicationState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import common.Settings
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
    val backgroundColor: Color,
    var mapTo: String? = null,
    var row: Int? = null,
    var column: Int? = null
    //val description: String
)
data class MappingKey(
    var name: String,
    val HID: String,
    val description: String
)




class SCCUIWindowState(
    private val application: SCCUIApplicationState,
    path: Path?,
    private val exit: (SCCUIWindowState) -> Unit
) {



    val settings: Settings get() = application.settings

    val window = WindowState(height = 800.dp, width = 1100.dp)

    var path by mutableStateOf(path)
        private set

    var isChanged by mutableStateOf(false)
        private set

    val openDialog = DialogState<Path?>()
    val saveDialog = DialogState<Path?>()
    val exitDialog = DialogState<AlertDialogResult>()

    private var _notifications = Channel<NotepadWindowNotification>(0)
    val notifications: Flow<NotepadWindowNotification> get() = _notifications.receiveAsFlow()

    val mappingKeys = mutableListOf(MappingKey("UNASSIGNED", "00", "No Event"), MappingKey("OVERRUN_ERROR", "01", "Overrun Error"), MappingKey("POST_FAIL", "02", "POST Fail"), MappingKey("ERROR_UNDEFINED", "03", "ErrorUndefined"), MappingKey("A", "04", "a A"), MappingKey("B", "05", "b B"), MappingKey("C", "06", "c C"), MappingKey("D", "07", "d D"), MappingKey("E", "08", "e E"), MappingKey("F", "09", "f F"), MappingKey("G", "0A", "g G"), MappingKey("H", "0B", "h H"), MappingKey("I", "0C", "i I"), MappingKey("J", "0D", "j J"), MappingKey("K", "0E", "k K"), MappingKey("L", "0F", "l L"), MappingKey("M", "10", "m M"), MappingKey("N", "11", "n N"), MappingKey("O", "12", "o O"), MappingKey("P", "13", "p P"), MappingKey("Q", "14", "q Q"), MappingKey("R", "15", "r R"), MappingKey("S", "16", "s S"), MappingKey("T", "17", "t T"), MappingKey("U", "18", "u U"), MappingKey("V", "19", "v V"), MappingKey("W", "1A", "w W"), MappingKey("X", "1B", "x X"), MappingKey("Y", "1C", "y Y"), MappingKey("Z", "1D", "z Z"), MappingKey("1", "1E", "1 !"), MappingKey("2", "1F", "2 @"), MappingKey("3", "20", "3 #"), MappingKey("4", "21", "4 $"), MappingKey("5", "22", "5 %"), MappingKey("6", "23", "6 ^"), MappingKey("7", "24", "7 &"), MappingKey("8", "25", "8 *"), MappingKey("9", "26", "9 ("), MappingKey("0", "27", "0 )"), MappingKey("ENTER", "28", "Return"), MappingKey("ESC", "29", "Escape"), MappingKey("BACKSPACE", "2A", "Backspace"), MappingKey("TAB", "2B", "Tab"), MappingKey("SPACE", "2C", "Space"), MappingKey("MINUS", "2D", "- _"), MappingKey("EQUAL", "2E", "= +"), MappingKey("LEFT_BRACE", "2F", "[ {"), MappingKey("RIGHT_BRACE", "30", "] }"), MappingKey("BACKSLASH", "31", "\\ |"), MappingKey("EUROPE_1", "32", "Europe 1 (use BACKSLASH instead)"), MappingKey("SEMICOLON", "33", "; :"), MappingKey("QUOTE", "34", "' \""), MappingKey("BACK_QUOTE", "35", "` ~"), MappingKey("COMMA", "36", ", <"), MappingKey("PERIOD", "37", ". >"), MappingKey("SLASH", "38", "/ ?"), MappingKey("CAPS_LOCK", "39", "Caps Lock"), MappingKey("F1", "3A", "F1"), MappingKey("F2", "3B", "F2"), MappingKey("F3", "3C", "F3"), MappingKey("F4", "3D", "F4"), MappingKey("F5", "3E", "F5"), MappingKey("F6", "3F", "F6"), MappingKey("F7", "40", "F7"), MappingKey("F8", "41", "F8"), MappingKey("F9", "42", "F9"), MappingKey("F10", "43", "F10"), MappingKey("F11", "44", "F11"), MappingKey("F12", "45", "F12"), MappingKey("PRINTSCREEN", "46", "Print Screen"), MappingKey("SCROLL_LOCK", "47", "Scroll Lock"), MappingKey("PAUSE", "48", "Pause"), MappingKey("INSERT", "49", "Insert"), MappingKey("HOME", "4A", "Home"), MappingKey("PAGE_UP", "4B", "Page Up"), MappingKey("DELETE", "4C", "Delete"), MappingKey("END", "4D", "End"), MappingKey("PAGE_DOWN", "4E", "Page Down"), MappingKey("RIGHT", "4F", "Right Arrow"), MappingKey("LEFT", "50", "Left Arrow"), MappingKey("DOWN", "51", "Down Arrow"), MappingKey("UP", "52", "Up Arrow"), MappingKey("NUM_LOCK", "53", "Num Lock"), MappingKey("PAD_SLASH", "54", "Keypad /"), MappingKey("PAD_ASTERIX", "55", "Keypad *"), MappingKey("PAD_MINUS", "56", "Keypad -"), MappingKey("PAD_PLUS", "57", "Keypad +"), MappingKey("PAD_ENTER", "58", "Keypad Enter"), MappingKey("PAD_1", "59", "Keypad 1 End"), MappingKey("PAD_2", "5A", "Keypad 2 Down"), MappingKey("PAD_3", "5B", "Keypad 3 PageDn"), MappingKey("PAD_4", "5C", "Keypad 4 Left"), MappingKey("PAD_5", "5D", "Keypad 5"), MappingKey("PAD_6", "5E", "Keypad 6 Right"), MappingKey("PAD_7", "5F", "Keypad 7 Home"), MappingKey("PAD_8", "60", "Keypad 8 Up"), MappingKey("PAD_9", "61", "Keypad 9 PageUp"), MappingKey("PAD_0", "62", "Keypad 0 Insert"), MappingKey("PAD_PERIOD", "63", "Keypad . Delete"), MappingKey("EUROPE_2", "64", "Europe 2"), MappingKey("APP", "65", "App (Windows Menu)"), MappingKey("POWER", "66", "Keyboard Power"), MappingKey("PAD_EQUALS", "67", "Keypad ="), MappingKey("F13", "68", "F13"), MappingKey("F14", "69", "F14"), MappingKey("F15", "6A", "F15"), MappingKey("F16", "6B", "F16"), MappingKey("F17", "6C", "F17"), MappingKey("F18", "6D", "F18"), MappingKey("F19", "6E", "F19"), MappingKey("F20", "6F", "F20"), MappingKey("F21", "70", "F21"), MappingKey("F22", "71", "F22"), MappingKey("F23", "72", "F23"), MappingKey("F24", "73", "F24"), MappingKey("EXECUTE", "74", "Keyboard Execute"), MappingKey("HELP", "75", "Keyboard Help"), MappingKey("MENU", "76", "Keyboard Menu"), MappingKey("SELECT", "77", "Keyboard Select"), MappingKey("STOP", "78", "Keyboard Stop"), MappingKey("AGAIN", "79", "Keyboard Again"), MappingKey("UNDO", "7A", "Keyboard Undo"), MappingKey("CUT", "7B", "Keyboard Cut"), MappingKey("COPY", "7C", "Keyboard Copy"), MappingKey("PASTE", "7D", "Keyboard Paste"), MappingKey("FIND", "7E", "Keyboard Find"), MappingKey("MUTE", "7F", "Keyboard Mute"), MappingKey("VOLUME_UP", "80", "Keyboard Volume Up"), MappingKey("VOLUME_DOWN", "81", "Keyboard Volume Dn"), MappingKey("LOCKING_CAPS_LOCK", "82", "Keyboard Locking Caps Lock"), MappingKey("LOCKING_NUM_LOCK", "83", "Keyboard Locking Num Lock"), MappingKey("LOCKING_SCROLL_LOCK", "84", "Keyboard Locking Scroll Lock"), MappingKey("PAD_COMMA", "85", "Keypad comma (Brazilian Keypad .)"), MappingKey("EQUAL_SIGN", "86", "Keyboard Equal Sign"), MappingKey("INTERNATIONAL_1", "87", "Keyboard Int'l 1 (Ro)"), MappingKey("INTERNATIONAL_2", "88", "Keyboard Intl'2 (Katakana/Hiragana)"), MappingKey("INTERNATIONAL_3", "89", "Keyboard Int'l 2 (Yen)"), MappingKey("INTERNATIONAL_4", "8A", "Keyboard Int'l 4 (Henkan)"), MappingKey("INTERNATIONAL_5", "8B", "Keyboard Int'l 5 (Muhenkan)"), MappingKey("INTERNATIONAL_6", "8C", "Keyboard Int'l 6 (PC9800 Keypad comma)"), MappingKey("INTERNATIONAL_7", "8D", "Keyboard Int'l 7"), MappingKey("INTERNATIONAL_8", "8E", "Keyboard Int'l 8"), MappingKey("INTERNATIONAL_9", "8F", "Keyboard Int'l 9"), MappingKey("LANG_1", "90", "Keyboard Lang 1 (Hanguel/English)"), MappingKey("LANG_2", "91", "Keyboard Lang 2 (Hanja)"), MappingKey("LANG_3", "92", "Keyboard Lang 3 (Katakana)"), MappingKey("LANG_4", "93", "Keyboard Lang 4 (Hiragana)"), MappingKey("LANG_5", "94", "Keyboard Lang 5 (Zenkaku/Hankaku)"), MappingKey("LANG_6", "95", "Keyboard Lang 6"), MappingKey("LANG_7", "96", "Keyboard Lang 7"), MappingKey("LANG_8", "97", "Keyboard Lang 8"), MappingKey("LANG_9", "98", "Keyboard Lang 9"), MappingKey("ALTERNATE_ERASE", "99", "Keyboard Alternate Erase"), MappingKey("SYSREQ_ATTN", "9A", "Keyboard SysReq/Attention"), MappingKey("CANCEL", "9B", "Keyboard Cancel"), MappingKey("CLEAR", "9C", "Keyboard Clear (use DELETE instead)"), MappingKey("PRIOR", "9D", "Keyboard Prior"), MappingKey("RETURN", "9E", "Keyboard Return"), MappingKey("SEPARATOR", "9F", "Keyboard Separator"), MappingKey("OUT", "A0", "Keyboard Out"), MappingKey("OPER", "A1", "Keyboard Oper"), MappingKey("CLEAR_AGAIN", "A2", "Keyboard Clear/Again"), MappingKey("CRSEL_PROPS", "A3", "Keyboard CrSel/Props"), MappingKey("EXSEL", "A4", "Keyboard ExSel"), MappingKey("SYSTEM_POWER", "A8", "System Power"), MappingKey("SYSTEM_SLEEP", "A9", "System Sleep"), MappingKey("SYSTEM_WAKE", "AA", "System Wake"), MappingKey("AUX1", "AB", "Auxiliary key 1"), MappingKey("AUX2", "AC", "Auxiliary key 2"), MappingKey("AUX3", "AD", "Auxiliary key 3"), MappingKey("AUX4", "AE", "Auxiliary key 4"), MappingKey("AUX5", "AF", "Auxiliary key 5"), MappingKey("EXTRA_LALT", "B1", "AT-F extra pad lhs of space"), MappingKey("EXTRA_PAD_PLUS", "B2", "Term extra pad bottom of keypad +"), MappingKey("EXTRA_RALT", "B3", "AT-F extra pad rhs of space"), MappingKey("EXTRA_EUROPE_2", "B4", "AT-F extra pad lhs of enter"), MappingKey("EXTRA_BACKSLASH", "B5", "AT-F extra pad top of enter"), MappingKey("EXTRA_INSERT", "B6", "AT-F extra pad lhs of Insert"), MappingKey("EXTRA_F1", "B7", "122-key Terminal lhs F1"), MappingKey("EXTRA_F2", "B8", "122-key Terminal lhs F2"), MappingKey("EXTRA_F3", "B9", "122-key Terminal lhs F3"), MappingKey("EXTRA_F4", "BA", "122-key Terminal lhs F4"), MappingKey("EXTRA_F5", "BB", "122-key Terminal lhs F5"), MappingKey("EXTRA_F6", "BC", "122-key Terminal lhs F6"), MappingKey("EXTRA_F7", "BD", "122-key Terminal lhs F7"), MappingKey("EXTRA_F8", "BE", "122-key Terminal lhs F8"), MappingKey("EXTRA_F9", "BF", "122-key Terminal lhs F9"), MappingKey("EXTRA_F10", "C0", "122-key Terminal lhs F10"), MappingKey("EXTRA_SYSRQ", "C2", "Sys Req (AT 84-key)"), MappingKey("FN1", "D0", "Function layer key 1"), MappingKey("FN2", "D1", "Function layer key 2"), MappingKey("FN3", "D2", "Function layer key 3"), MappingKey("FN4", "D3", "Function layer key 4"), MappingKey("FN5", "D4", "Function layer key 5"), MappingKey("FN6", "D5", "Function layer key 6"), MappingKey("FN7", "D6", "Function layer key 7"), MappingKey("FN8", "D7", "Function layer key 8"), MappingKey("SELECT_0", "D8", "Select reset"), MappingKey("SELECT_1", "D9", "Select 1 toggle"), MappingKey("SELECT_2", "DA", "Select 2 toggle"), MappingKey("SELECT_3", "DB", "Select 3 toggle"), MappingKey("SELECT_4", "DC", "Select 4 toggle"), MappingKey("SELECT_5", "DD", "Select 5 toggle"), MappingKey("SELECT_6", "DE", "Select 6 toggle"), MappingKey("SELECT_7", "DF", "Select 7 toggle"), MappingKey("LCTRL", "E0", "Left Control"), MappingKey("LSHIFT", "E1", "Left Shift"), MappingKey("LALT", "E2", "Left Alt"), MappingKey("LGUI", "E3", "Left GUI (Left Windows)"), MappingKey("RCTRL", "E4", "Right Control"), MappingKey("RSHIFT", "E5", "Right Shift"), MappingKey("RALT", "E6", "Right Alt"), MappingKey("RGUI", "E7", "Right GUI (Right Windows)"), MappingKey("MEDIA_NEXT_TRACK", "E8", "Scan Next Track"), MappingKey("MEDIA_PREV_TRACK", "E9", "Scan Previous Track"), MappingKey("MEDIA_STOP", "EA", "Stop"), MappingKey("MEDIA_PLAY_PAUSE", "EB", "Play/ Pause"), MappingKey("MEDIA_MUTE", "EC", "Mute"), MappingKey("MEDIA_BASS_BOOST", "ED", "Bass Boost"), MappingKey("MEDIA_LOUDNESS", "EE", "Loudness"), MappingKey("MEDIA_VOLUME_UP", "EF", "Volume Up"), MappingKey("MEDIA_VOLUME_DOWN", "F0", "Volume Down"), MappingKey("MEDIA_BASS_UP", "F1", "Bass Up"), MappingKey("MEDIA_BASS_DOWN", "F2", "Bass Down"), MappingKey("MEDIA_TREBLE_UP", "F3", "Treble Up"), MappingKey("MEDIA_TREBLE_DOWN", "F4", "Treble Down"), MappingKey("MEDIA_MEDIA_SELECT", "F5", "Media Select"), MappingKey("MEDIA_MAIL", "F6", "Mail"), MappingKey("MEDIA_CALCULATOR", "F7", "Calculator"), MappingKey("MEDIA_MY_COMPUTER", "F8", "My Computer"), MappingKey("MEDIA_WWW_SEARCH", "F9", "WWW Search"), MappingKey("MEDIA_WWW_HOME", "FA", "WWW Home"), MappingKey("MEDIA_WWW_BACK", "FB", "WWW Back"), MappingKey("MEDIA_WWW_FORWARD", "FC", "WWW Forward"), MappingKey("MEDIA_WWW_STOP", "FD", "WWW Stop"), MappingKey("MEDIA_WWW_REFRESH", "FE", "WWW Refresh"), MappingKey("MEDIA_WWW_FAVORITES", "FF", "WWW Favorites"))

    var row1 = mutableStateListOf(Key("", "", 3.25, 1.0, Color.White), Key("F13", "F13", 1.0, 1.0, Color.LightGray), Key("F14", "F14", 1.0, 1.0, Color.LightGray), Key("F15", "F15", 1.0, 1.0, Color.LightGray), Key("F16", "F16", 1.0, 1.0, Color.LightGray), Key("F17", "F17", 1.0, 1.0, Color.White), Key("F18", "F18", 1.0, 1.0, Color.White), Key("F19", "F19", 1.0, 1.0, Color.White), Key("F20", "F20", 1.0, 1.0, Color.White), Key("F21", "F21", 1.0, 1.0, Color.LightGray), Key("F22", "F22", 1.0, 1.0, Color.LightGray), Key("F23", "F23", 1.0, 1.0, Color.LightGray), Key("F24", "F24", 1.0, 1.0, Color.LightGray))
    var row2 = mutableStateListOf(Key("", "", 3.25, 1.0, Color.White), Key("F1", "F1", 1.0, 1.0, Color.LightGray), Key("F2", "F2", 1.0, 1.0, Color.LightGray), Key("F3", "F3", 1.0, 1.0, Color.LightGray), Key("F4", "F4", 1.0, 1.0, Color.LightGray), Key("F5", "F5", 1.0, 1.0, Color.White), Key("F6", "F6", 1.0, 1.0, Color.White), Key("F7", "F7", 1.0, 1.0, Color.White), Key("F8", "F8", 1.0, 1.0, Color.White), Key("F9", "F9", 1.0, 1.0, Color.LightGray), Key("F10", "F10", 1.0, 1.0, Color.LightGray), Key("F11", "F11", 1.0, 1.0, Color.LightGray), Key("F12", "F12", 1.0, 1.0, Color.LightGray))
    var row3 = mutableStateListOf(Key("", "", 20.0, 1.0, Color.White))
    var row4 = mutableStateListOf(Key("EXTRA_F1", "S-Abf", 1.0, 1.0, Color.LightGray), Key("EXTRA_F2", "  ", 1.0, 1.0, Color.LightGray),Key("", "", 0.25, 1.0, Color.White), Key("BACK_QUOTE", " ", 1.0, 1.0, Color.White), Key("1", "1", 1.0, 1.0, Color.White), Key("2", "2", 1.0, 1.0, Color.White), Key("3", "3", 1.0, 1.0, Color.White), Key("4", "4", 1.0, 1.0, Color.White), Key("5", "5", 1.0, 1.0, Color.White), Key("6", "6", 1.0, 1.0, Color.White), Key("7", "7", 1.0, 1.0, Color.White), Key("8", "8", 1.0, 1.0, Color.White), Key("9", "9", 1.0, 1.0, Color.White), Key("0", "0", 1.0, 1.0, Color.White), Key("MINUS", "ß", 1.0, 1.0, Color.White), Key("EQUAL", "´", 1.0, 1.0, Color.White), Key("BACKSPACE", "<--", 2.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("INSERT", "|<-", 1.0, 1.0, Color.LightGray), Key("HOME", "Dup", 1.0, 1.0, Color.LightGray), Key("PAGE_UP", "  ", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("ESC", "  ", 1.0, 1.0, Color.LightGray), Key("NUM_LOCK", "  ", 1.0, 1.0, Color.LightGray), Key("SCROLL_LOCK", ".", 1.0, 1.0, Color.White), Key("EXTRA_SYSRQ", "  ", 1.0, 1.0, Color.LightGray))
    var row5 = mutableStateListOf(Key("EXTRA_F3", "  ", 1.0, 1.0, Color.LightGray), Key("EXTRA_F4","E-Lö", 1.0, 1.0, Color.LightGray),Key("", "", 0.25, 1.0, Color.White), Key("TAB", "-->|", 1.5, 1.0, Color.LightGray), Key("Q", "Q", 1.0, 1.0, Color.White), Key("W", "W", 1.0, 1.0, Color.White), Key("E", "E", 1.0, 1.0, Color.White), Key("R", "R", 1.0, 1.0, Color.White), Key("T", "T", 1.0, 1.0, Color.White), Key("Y", "Z", 1.0, 1.0, Color.White), Key("U", "U", 1.0, 1.0, Color.White), Key("I", "I", 1.0, 1.0, Color.White), Key("O", "O", 1.0, 1.0, Color.White), Key("P", "P", 1.0, 1.0, Color.White), Key("LEFT_BRACE", "Ü", 1.0, 1.0, Color.White), Key("RIGHT_BRACE", "+", 1.0, 1.0, Color.White), Key("", "", 0.25, 1.0, Color.LightGray), Key("ENTER", "Eing Feld", 1.25, 2.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("DELETE", "<-|", 1.0, 1.0, Color.LightGray), Key("END", "^a", 1.0, 1.0, Color.LightGray), Key("PAGE_DOWN", "a/", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("PAD_7", "7", 1.0, 1.0, Color.White), Key("PAD_8", "8", 1.0, 1.0, Color.White), Key("PAD_9", "9", 1.0, 1.0, Color.White), Key("PAD_ASTERIX", "Eing", 1.0, 1.0, Color.LightGray))
    var row6 = mutableStateListOf(Key("EXTRA_F5", "Druck", 1.0, 1.0, Color.LightGray), Key("EXTRA_F6", "Hilfe", 1.0, 1.0, Color.LightGray),Key("", "", 0.25, 1.0, Color.White), Key("CAPS_LOCK", "Capslock", 1.75, 1.0, Color.LightGray), Key("A", "A", 1.0, 1.0, Color.White), Key("S", "S", 1.0, 1.0, Color.White), Key("D", "D", 1.0, 1.0, Color.White), Key("F", "F", 1.0, 1.0, Color.White), Key("G", "G", 1.0, 1.0, Color.White), Key("H", "H", 1.0, 1.0, Color.White), Key("J", "J", 1.0, 1.0, Color.White), Key("K", "K", 1.0, 1.0, Color.White), Key("L", "L", 1.0, 1.0, Color.White), Key("SEMICOLON", "Ö", 1.0, 1.0, Color.White), Key("QUOTE", "Ä", 1.0, 1.0, Color.White), Key("EUROPE_1", "#", 1.0, 1.0, Color.White), Key("", "", 1.25, 0.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("UP", "^", 1.0, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("PAD_4", "4", 1.0, 1.0, Color.White), Key("PAD_5", "5", 1.0, 1.0, Color.White), Key("PAD_6", "6", 1.0, 1.0, Color.White), Key("PAD_MINUS", "  ", 1.0, 1.0, Color.LightGray))
    var row7 = mutableStateListOf(Key("EXTRA_F7", "  ", 1.0, 1.0, Color.LightGray), Key("EXTRA_F8", "Wdgab", 1.0, 1.0, Color.LightGray),Key("", "", 0.25, 1.0, Color.White), Key("LSHIFT", "Shift", 1.25, 1.0, Color.LightGray), Key("EUROPE_2", "<", 1.0, 1.0, Color.White), Key("Z", "Y", 1.0, 1.0, Color.White), Key("X", "X", 1.0, 1.0, Color.White), Key("C", "C", 1.0, 1.0, Color.White), Key("V", "V", 1.0, 1.0, Color.White), Key("B", "B", 1.0, 1.0, Color.White), Key("N", "N", 1.0, 1.0, Color.White), Key("M", "M", 1.0, 1.0, Color.White), Key("COMMA", ",", 1.0, 1.0, Color.White), Key("PERIOD", ".", 1.0, 1.0, Color.White), Key("SLASH", "-", 1.0, 1.0, Color.White), Key("RSHIFT", "Shift", 2.75, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("LEFT", "<", 1.0, 1.0, Color.LightGray), Key("LANG_4", "Linie", 1.0, 1.0, Color.LightGray), Key("RIGHT", ">", 1.0, 1.0, Color.LightGray), Key("", "", 0.25, 1.0, Color.White), Key("PAD_1", "1", 1.0, 1.0, Color.White), Key("PAD_2", "2", 1.0, 1.0, Color.White), Key("PAD_3", "3", 1.0, 1.0, Color.White), Key("PAD_PLUS", "Eing", 1.0, 2.0, Color.LightGray))
    var row8 = mutableStateListOf(Key("EXTRA_F9", "Defin", 1.0, 1.0, Color.LightGray), Key("EXTRA_F10", "Aufz", 1.0, 1.0, Color.LightGray),Key("", "", 0.25, 1.0, Color.White), Key("LCTRL", "Grdst", 1.5, 1.0, Color.LightGray), Key("", "", 1.0, 1.0, Color.White), Key("LALT", "Alt", 1.5, 1.0, Color.LightGray), Key("SPACE", "  ", 7.0, 1.0, Color.White), Key("RALT", "Alt", 1.5, 1.0, Color.LightGray), Key("", "", 1.0, 1.0, Color.White), Key("RCTRL", "Daten Freigabe", 1.5, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("DOWN", "v", 1.0, 1.0, Color.LightGray), Key("", "", 1.25, 1.0, Color.White), Key("PAD_0", "0", 2.0, 1.0, Color.White), Key("PAD_PERIOD", ",", 1.0, 1.0, Color.White))

    private lateinit var _input: List<String>

    val rows = mutableStateListOf(row1, row2, row3, row4, row5, row6, row7, row8)
    val defaultWidth = 40.dp
    val defaultHeight = 40.dp

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

    private var _mapTo by mutableStateOf("")
    var mapTo: String
        get() = _mapTo
        set(value) {
            check(isInit)
            _mapTo = value
            isChanged = true
        }


    private var _output by mutableStateOf("")
    var output: String
        get() = _output
        set(value) {
            check(isInit)
            _output = value
            isChanged = true
        }

    private var _remapblock by mutableStateOf("")
    var remapblock: String
        get() = _remapblock
        set(value) {
            check(isInit)
            _remapblock = value
            isChanged = true
        }

    private var _macroblock by mutableStateOf("")
    var macroblock: String
        get() = _macroblock
        set(value) {
            check(isInit)
            _macroblock = value
            isChanged = true
        }

    private var _commandLine by mutableStateOf("")
    var commandLine: String
        get() = _commandLine
        set(value) {
            check(isInit)
            _commandLine = value
            isChanged = true
        }


    private var _label by mutableStateOf("")
    var label: String
        get() = _label
        set(value) {
            check(isInit)
            _label = value
            isChanged = true
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

    suspend fun run() {
        if (path != null) {
            open(path!!)
        } else {
            initNew()
        }
    }

    private suspend fun open(path: Path) {
        isInit = false
        isChanged = false
        this.path = path
        try {
            _input = path.readLines(Charset.defaultCharset())
            isInit = true
            output = _input.toString().replace(",", "\n\r").replace("[", "").replace("]", "") //TODO: make nicer
            processRemapblocks()
        } catch (e: Exception) {
            e.printStackTrace()
            output = "Cannot read $path"
        }
    }

    private fun processRemapblocks() {
        var foundBlock = false
        _input.forEach() {
            if (it.contains("endblock", ignoreCase = true)) {
                foundBlock = false
            } else if (foundBlock) {
                var s = it.trim().split(" ")
                var r = 0
                rows.forEach() {
                    r++
                    rows[r-1].forEach() {
                        if (s[0] == it.name) {
                            it.mapTo = s[1]
                        }
                    }
                }
            }
            else if (it.contains("remapblock", ignoreCase = true)) {
                foundBlock = true
            }
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


    suspend fun saveTemp(path: String) {
        Paths.get(path+"/temp.txt").writeTextAsync(output)
    }

    suspend fun read() {
        val resourcesDir = File(System.getProperty("compose.application.resources.dir")).toString()
        var filePath = ""
        val command_scdis_win = resourcesDir+"\\scdis "+resourcesDir+"\\temp.bin "+resourcesDir+"\\temp.txt"
        val command_scrd_win = resourcesDir+"\\scrd "+resourcesDir+"\\temp.bin"
        val command_scdis = resourcesDir+"/scdis "+resourcesDir+"/temp.bin "+resourcesDir+"/temp.txt"
        val command_scrd = resourcesDir+"/scrd "+resourcesDir+"/temp.bin"

        if (System.getProperty("os.name").lowercase().contains("win")) {
            commandLine = Runtime.getRuntime().exec(command_scrd_win).toString()
            Thread.sleep(1000)
            commandLine = Runtime.getRuntime().exec(command_scdis_win).toString()
            filePath = resourcesDir + "\\temp.txt"
        } else {
            commandLine = command_scrd.evalBash().getOrThrow()
            Thread.sleep(1000)
            commandLine = command_scdis.evalBash().getOrThrow()
            filePath = resourcesDir + "/temp.txt"
        }
        //println(filePath)
        open(Paths.get(filePath))

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

private suspend fun Path.readTextAsync() = withContext(Dispatchers.IO) {
    toFile().readText()
}

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