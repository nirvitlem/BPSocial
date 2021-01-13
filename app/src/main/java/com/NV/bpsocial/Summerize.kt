package com.NV.bpsocial

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.NV.bpsocial.Objectlist.planN
import kotlinx.android.synthetic.main.activity_summerize.*
import java.io.File
import java.util.*
import kotlin.math.*

var list=mutableListOf("");
var listforCSV = mutableListOf("");
var time="";
var cTime="";
var adapter: ArrayAdapter<String>?=null;
public var listcolor = mutableListOf("");
public var listendp  = mutableListOf("");
public var ed : EditText? = null;


class Summerize : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summerize)
        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list)

        ListSummerize.adapter = adapter
        SaveB.setOnClickListener {
            ed = EditText(this);
            val builder = AlertDialog.Builder(this)
            builder.setTitle("שמור ושתף את התוצאות ")
            builder.setMessage("הגדר שם לקובץ ")
            builder.setView(ed);
            builder.setPositiveButton("OK") { dialog, which ->
                savefile(ed?.text.toString());
            }
            builder.show(); 
        }
        SaveC.setOnClickListener {
            ed = EditText(this);
            val builder = AlertDialog.Builder(this)
            builder.setTitle("שמור ושתף את התוצאות כקובץ לניתוח ")
            builder.setMessage("הגדר שם לקובץ ")
            builder.setView(ed);
            builder.setPositiveButton("OK") { dialog, which ->
                savefileC(ed?.text.toString());
            }
            builder.show();
        }
        showr()
    }

    fun showr() {
        list.clear()
        listforCSV.clear();
        cTime=Calendar.getInstance().time.toString();
        listforCSV.add("תאריך;זמן;אובייקט;מהירות תגובה");
        time="";
        list.add("תוצאות:")
        list.add("סיימת את תרגיל ב -  " + sumAlltimedCheked() + " שניות ")
        list.add("לחצת  " + TotalCheked() + " פעמים ")
        if (planN != 2 ) list.add("לחצת  " + Totalmistake() + " פעמים בטעות ")
        list.add(
            " התוצאה הטובה ביותר " + bestcore().split(";")[0].toString() + " ליחידת קצה " + bestcore().split(
                ";"
            )[1].toString()
        )
        list.add(
            " התוצאה הגבוהה ביותר " + badscore().split(";")[0].toString() + " ליחידת קצה " + badscore().split(
                ";"
            )[1].toString()
        )
        list.add(" זמן ממוצע " + average().toString())
        getcountcolor();
        //getGPSandDis();
        for (element in listcolor) {
            if (element.toString() != "") {
                var res = bestbadbycedp(element).toString();
                list.add(
                    " לחצת  " + res.split(";")[2] + " על " + element.toString() + " בזמן הכי טוב " + res.split(
                        ";"
                    )[0].toString()
                            + " בזמן הכי גבוה " + res.split(";")[1].toString()
                )
            }
        }
        getcountendp();
        for (element in listendp) {
            if (element.toString() != "") {
                var res = bestbadbycedp(element).toString();
                list.add(
                    " לחצת  " + res.split(";")[2] + " על " + element.toString() + " בזמן הכי טוב " + res.split(
                        ";"
                    )[0].toString()
                            + " בזמן הכי גבוה " + res.split(";")[1].toString()
                )
            }
        }
        for (element in listendp) {
        }

        list.add("")
        list.add(" כל הלחיצות ")
        //***11.10.2020
        for (element in TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") || s.contains(
            "STimer"
        )}) {
            if (element.split(";")[0].contains("STimer"))
            {
                list.add(" עברו " + element.split(";")[1].toString() + " שניות ")
                time= element.split(";")[1].toString();
            }
            else {
                list.add(" לחצת " + element.split(";")[1].toString() + " ב " + element.split(";")[2].toString() + " שניות ")
                listforCSV.add( cTime + ";" + time + ";" +element.split(";")[1].toString() +";" +  element.split(";")[2].toString());
            }
        }

        adapter?.notifyDataSetChanged();
    }

    fun getGPSandDis()
    {
        var CheckedList: List<String> = TimersObjectlist.listoftofResult.filter { s -> s.contains("SGEO") }
        if (CheckedList.size>1) {
            for (index in 0 until (CheckedList.size-1)) {
                val loc1 = Location("")
                loc1.setLatitude(CheckedList[index].split(";")[3].toDouble())
                loc1.setLongitude(CheckedList[index].split(";")[5].toDouble())
                if (loc1.latitude>0 && loc1.longitude>0)
                {
                    for (index2 in 1 until CheckedList.size) {
                        if (!CheckedList[index].split(";")[1].toString().equals( CheckedList[index2].split(";")[1].toString()))
                        {
                            val loc2 = Location("")
                            loc2.setLatitude(CheckedList[index2].split(";")[3].toDouble())
                            loc2.setLongitude(CheckedList[index2].split(";")[5].toDouble())
                            if (loc2.latitude>0 && loc2.longitude>0) {
                                val distanceInMeters: Float = loc1.distanceTo(loc2)
                                //val dis = distanceInMeter(loc1,loc2);
                                val degre=  CalculateBearingAngle(loc1.latitude, loc1.longitude,loc2.latitude,loc2.longitude);
                                list.add(" המרחק בן  " + CheckedList[index].split(";")[1].toString() + " ל- " + CheckedList[index2].split(";")[1].toString() + " = " + distanceInMeters.toString() + " מטרים " + " בזווית " + degre.toString());
                            }
                        }
                    }
                }
            }
        }
    }

    fun distanceInMeter(firstLocation: Location, secondLocation: Location): Double {
        val earthRadius = 6371000.0
        val deltaLatitudeDegree = (firstLocation.latitude - secondLocation.latitude) * Math.PI / 180f
        val deltaLongitudeDegree = (firstLocation.longitude - secondLocation.longitude) * Math.PI / 180f
        val a = sin(deltaLatitudeDegree / 2).pow(2) +
                cos(firstLocation.latitude * Math.PI / 180f) * cos(secondLocation.latitude * Math.PI / 180f) *
                sin(deltaLongitudeDegree / 2).pow(2)
        val c = 2f * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun CalculateBearingAngle(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val Phi1 = Math.toRadians(startLatitude)
        val Phi2 = Math.toRadians(endLatitude)
        val DeltaLambda = Math.toRadians(endLongitude - startLongitude)
        val Theta: Double = atan2(
            sin(DeltaLambda) * cos(Phi2),
            cos(Phi1) * sin(Phi2) - sin(Phi1) * cos(Phi2) * cos(DeltaLambda)
        )
        return Math.toDegrees(Theta).toFloat()
    }

    fun sumAlltimedCheked(): String {

        return TimersDataVal.totaltime.toString();
    }

    fun TotalCheked(): String {

        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        return CheckedList.size.toString();
    }

    fun Totalmistake(): String {

        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("mistake") }
        return CheckedList.size.toString();
    }

    fun TotalChekedRed(): String {

        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("Color Red") }
        return CheckedList.size.toString();
    }

    fun bestcore(): String {

        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        var best: Double? = 100.0;
        var endp: String? = "";
        for (element in CheckedList) {
            if (element.split(";")[2].toDouble() < best!!) {
                best = element.split(";")[2].toDouble();
                endp = element.split(";")[1].toString();
            }
        }
        return (best.toString() + ";" + endp);
    }

    fun bestbadbycedp(e: String): String {

        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        CheckedList = CheckedList.filter { s -> s.contains(e) }
        var best: Double? = 100.0;
        var bad: Double? = 0.0;
        for (element in CheckedList) {
            if (element.split(";")[2].toDouble() < best!!) {
                best = element.split(";")[2].toDouble();
            }
            if (element.split(";")[2].toDouble() > bad!!) {
                bad = element.split(";")[2].toDouble();
            }

        }
        return (best.toString() + ";" + bad.toString() + ";" + CheckedList.size);
    }

    fun badscore(): String {

        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        var bad: Double? = 0.0;
        var endp: String? = "";
        for (element in CheckedList) {
            if (element.split(";")[2].toDouble() > bad!!) {
                bad = element.split(";")[2].toDouble();
                endp = element.split(";")[1].toString();
            }
        }
        return (bad.toString() + ";" + endp);
    }


    fun average(): String {

        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        var sumall: Double? = 0.0;
        for (element in CheckedList) {

            sumall = sumall?.plus(element.split(";")[2].toDouble());

        }
        return ((sumall!! / CheckedList.size).toString());
    }

    fun getcountendp() {
        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        for (element in CheckedList) {
            if (!listendp.contains(element.split(";")[1].toString()))
                listendp.add(element.split(";")[1].toString())
        }

    }

    fun getcountcolor() {
        var CheckedList: List<String> =
            TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
        for (element in CheckedList) {
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
            out.write("<p dir=\"rtl\"> <br> שם - " + ed?.text.toString() + " בתאריך " + Calendar.getInstance().time.toString() + "<p/>")
            for (element in list) {
                out.write("<p dir=\"rtl\"> <br>" + element.toString() + "<p/>")
                out.write(" <br>")
            }
            out.write("</body>")
            out.write("</html>")

        }


        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID + ".provider",
                myExternalFile
            )
        )
        sendIntent.type = "text/csv"
        startActivity(Intent.createChooser(sendIntent, "SHARE"))
    }

    fun savefileC(fname: String) {
        var myExternalFile: File =
            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS.toString()), fname + ".csv")
        myExternalFile.bufferedWriter().use { out ->
            //out.write(Calendar.getInstance().time.toString() +";;");
            for (element in listforCSV) {
                out.write( element.toString())
                out.newLine();
            }

        }


        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                this, BuildConfig.APPLICATION_ID + ".provider",
                myExternalFile
            )
        )
        sendIntent.type = "text/csv"
        startActivity(Intent.createChooser(sendIntent, "SHARE"))
    }
}