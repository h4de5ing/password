package x.x.p455w0rd

import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import x.x.p455w0rd.app.App
import java.io.*


fun now(): Long = System.currentTimeMillis()

fun csv4File(filePath: String): List<Array<String>> {
    var mOriginList: List<Array<String>> = ArrayList()
    try {
        val inputStreamReader = InputStreamReader(FileInputStream(File(filePath)))
        val csvReader = CSVReader(inputStreamReader, ',')
        mOriginList = csvReader.readAll()
        csvReader.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return mOriginList
}

fun csv2File(filename: String, mutableList: MutableList<Array<String>>) {
    val file = File(filename)
    val csvWriter = CSVWriter(FileWriter(file), ',')
    mutableList.forEach {
        csvWriter.writeNext(it)
    }
    csvWriter.close()
}

interface DialogSelection {
    fun onSelectedFilePaths(files: Array<String?>?)
}

fun select_file(context: Context, dialogSelection: DialogSelection) {
    val default_dir: String = Environment.getExternalStorageDirectory().absolutePath
    val properties = DialogProperties()
    properties.selection_mode = DialogConfigs.SINGLE_MODE
    properties.selection_type = DialogConfigs.FILE_SELECT
    properties.root = File(default_dir)
    properties.error_dir = File(default_dir)
    properties.offset = File(default_dir)
    properties.extensions = null
    val dialog = FilePickerDialog(context, properties)
    dialog.setTitle("select file path")
    dialog.show()
    dialog.setDialogSelectionListener { files -> dialogSelection.onSelectedFilePaths(files) }
}

fun confirm(context: Context,title: String, block: () -> Unit) {
    val build = AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
    build.setTitle(title)
    build.setPositiveButton(android.R.string.ok) { _, _ ->
        block()
    }
    build.setNegativeButton(android.R.string.cancel, null)
    val alert = build.create()
    alert.show()
}