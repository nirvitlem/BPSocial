package com.NV.bpsocial

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_summerize.*
import java.io.*


//SSend; ; ;"+ Calendar.getInstance().time.toString()
//"Schecked;" + t + ";" + sec + ";"+ Calendar.getInstance().time.toString(
var list=mutableListOf("");
var adapter: ArrayAdapter<String>?=null;
public var listcolor = mutableListOf("");
public var listendp  = mutableListOf("");


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
        //list.add("לחצת  " + TotalChekedBlue()+" פעמים בצבע כחול ")
        list.add(" התוצאה הטובה ביותר " + bestcore().split(";")[0].toString() + " ליחידת קצה " +  bestcore().split(";")[1].toString())
        list.add(" התוצאה הגבוהה ביותר " + badscore().split(";")[0].toString() + " ליחידת קצה " +  badscore().split(";")[1].toString())
        list.add(" זמן ממוצע " + average().toString())
        getcountcolor();
        for (element in listcolor)
        {
            if(element.toString()!="") {
                var res = bestbadbycedp(element).toString();
                list.add(" לחצת  " + res.split(";")[2] + " על " + element.toString() + " בזמן הכי טוב " + res.split(";")[0].toString()
                            + " בזמן הכי גבוה " + res.split(";")[1].toString()
                )
            }
        }
        getcountendp();
        for (element in listendp)
        {
            if(element.toString()!="") {
                var res = bestbadbycedp(element).toString();
                list.add(
                    " לחצת  " + res.split(";")[2] + " על " + element.toString() + " בזמן הכי טוב " + res.split(";")[0].toString()
                            + " בזמן הכי גבוה " +res.split(";")[1].toString()
                )
            }
        }
for (element in listendp) {
}

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
        var endp : String ?="";
        for (element in CheckedList) {
            if (element.split(";")[2].toDouble()<best!!) {
                best = element.split(";")[2].toDouble();
                endp= element.split(";")[1].toString();
            }
        }
        return (best.toString() + ";" + endp);
    }

    fun bestbadbycedp(e : String): String {

        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        CheckedList= CheckedList.filter { s -> s.contains(e) }
        var best : Double ?=100.0;
        var bad : Double ?=0.0;
        for (element in CheckedList) {
            if (element.split(";")[2].toDouble() < best!!) {
                best = element.split(";")[2].toDouble();
            }
            if (element.split(";")[2].toDouble() > bad!!) {
                bad = element.split(";")[2].toDouble();
            }

        }
        return (best.toString() + ";" + bad.toString() + ";" +CheckedList.size );
    }

    fun badscore(): String {

        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        var bad : Double ?=0.0;
        var endp : String ?="";
        for (element in CheckedList) {
            if (element.split(";")[2].toDouble()>bad!!) {
                bad = element.split(";")[2].toDouble();
                endp= element.split(";")[1].toString();
            }
        }
        return (bad.toString() + ";" + endp );
    }


    fun average(): String {

        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        var sumall : Double ?=0.0;
        for (element in CheckedList) {

            sumall = sumall?.plus(element.split(";")[2].toDouble());

        }
        return ((sumall!!/CheckedList.size).toString() );
    }

    fun getcountendp()
    {
        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        for(element in CheckedList)
        {
            if (!listendp.contains(element.split(";")[1].toString()))
                listendp.add( element.split(";")[1].toString() )
        }

    }

    fun getcountcolor()
    {
        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        for(element in CheckedList)
        {
            if (!listcolor.contains(element.split(";")[4].toString().split(" ")[1].toString()))
                listcolor.add(element.split(";")[4].toString().split(" ")[1].toString())
        }
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