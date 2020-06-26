package com.example.bpsocial

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.View.MeasureSpec
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_summerize.*
import java.io.*
import java.util.*


//SSend; ; ;"+ Calendar.getInstance().time.toString()
//"Schecked;" + t + ";" + sec + ";"+ Calendar.getInstance().time.toString(
var list=mutableListOf("");
var adapter: ArrayAdapter<String>?=null;

class Summerize : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summerize)
        adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, list)

        ListSummerize.adapter = adapter
        SaveB.setOnClickListener {
            val ed: EditText? = EditText(this);
            val builder = AlertDialog.Builder(this)
            builder.setTitle("שמור ושתף את התוצאות ")
            builder.setMessage("הגדר שם לקובץ ")
            builder.setView(ed);
            builder.setPositiveButton("OK") { dialog, which ->
                savefile(ed?.text.toString());
            }
            builder.show();
        }
        showr()
    }

    fun showr() {

        list.add("תוצאות:")
        list.add("סיימת את תרגיל ב -  " + sumAlltimedCheked() + " שניות ")
        list.add("לחצת  " + TotalCheked()+" פעמים ")
        list.add("לחצת  " + TotalChekedBlue()+" פעמים בצבע כחול ")
        list.add(" התוצאה הטובה ביותר " + bestcore())
        list.add("")
        list.add(" כל הלחיצות ")
        for (element in TimersObjectlist.listoftofResult.filter { s -> s.contains("checked")}) {

            list.add(" לחצת " + element.split(";")[1].toString() + " ב " +  element.split(";")[2].toString() + " שניות ")
        }

        adapter?.notifyDataSetChanged();
    }

    fun sumAlltimedCheked(): String {

        return TimersDataVal.totaltime.toString();
    }

    fun TotalCheked(): String {

        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        return CheckedList.size.toString();
    }

    fun TotalChekedBlue(): String {

        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("Color Blue") }
        return CheckedList.size.toString();
    }

    fun bestcore(): String {

        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        var best : Double ?=100.0;
        for (element in CheckedList) {
            if (element.split(";")[2].toDouble()<best!!)  best = element.split(";")[2].toDouble();
        }
        return best.toString();
    }

    fun savefile(fname: String) {
        var myExternalFile: File =
            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS.toString()), fname + ".html")
        myExternalFile.bufferedWriter().use { out ->
            out.write(" <!DOCTYPE html>")
            out.write("<meta charset=\"utf-8\">");
            out.write("<html>")
            out.write("<body>")
            for (element in list) {
                out.write("<p dir=\"rtl\"> <br>" + element.toString() + "<p/>")
                out.write(" <br>")
            }
            out.write("</body>")
            out.write("</html>")

        }


        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_STREAM,  FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",myExternalFile))
        sendIntent.type = "text/csv"
        startActivity(Intent.createChooser(sendIntent, "SHARE"))
    }
}