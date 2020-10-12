package com.NV.bpsocial


import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothSocket
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.NV.bpsocial.Objectlist.planN
import com.NV.bpsocial.StartObjectlist.tbl
import com.NV.bpsocial.StartObjectval.next
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start.*
import java.util.*
import kotlin.collections.ArrayList


//public var tbl : TableLayout ?=null;
public var size : Int ?=0;
public var plan : Int ?=0;
public var timplan:Long ?=60000;



object StartObjectlist {
    @JvmStatic public var listoftTableRow: ArrayList<TableRow> = ArrayList();
    @JvmStatic public var list=mutableListOf("");
    @JvmStatic public var adapter:ArrayAdapter<String>?=null;
    @JvmStatic public var tbl : TableLayout ?=null;
}

object StartObjectval {
    @JvmField public var next : Boolean?=true;
    @JvmField public var A : Activity?=null;

    //...
}

class Start : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adViewsu.loadAd(adRequest)
        adViewsd.loadAd(adRequest)

        A=this;
        textViewPB.text="";
        Objectlist.mbs?.setconextintent(this);
        tbl= findViewById(R.id.tableLayout) as TableLayout
        addEndP();
        val planCoohser  = findViewById<Spinner>(R.id.spinner);
        val l: ArrayList<String> = ArrayList()
        l.add("צבע אחד")
        l.add("צבע אחד, רקע כחול")
        l.add("זוגי, תחרות")
        l.add("צבע רנדומלי, תרגילים")
       // l.add("תוכנית 4")
       // l.add("תוכנית 5")
        val dataAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, l
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planCoohser.setAdapter(dataAdapter)

        planCoohser.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                plan= position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        val sb = findViewById(R.id.Startbutton) as Button;
        size = Oblist.listofbluetoothsocket.size as Int
        sb.setOnClickListener {
            buttons.isEnabled = false;
            TimersObjectlist.listoftofResult.clear();
            StartObjectlist.list.clear();
            StartObjectlist.adapter?.clear();
            /*test  size=4;
            var r = (0..(size?.minus(1)!!)).random() as Int;
            StartObjectlist.list.add(r.toString());
            StartObjectlist.adapter?.notifyDataSetChanged();
            setcolorofcell(getchildview(r),Color.RED)*/
            when (plan) {
                0 -> Plan1();
                1 -> Plan2();
                2 -> {
                    if (size!! < 2) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("תקלה! ")
                        builder.setMessage("צריך לפחות 2 יחידות קצה ")
                        builder.setView(ed);
                        builder.setPositiveButton("OK") { dialog, which ->

                        }
                        builder.show();
                    } else Plan3()
                };
                3 -> Plan4();
                4 -> Plan5();
                else -> Plan1();
            }
        }

        StartObjectlist.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, StartObjectlist.list
        )
        // attach the array adapter with list view
        listevents.adapter = StartObjectlist.adapter
        buttons.isEnabled=false;
        buttons.setOnClickListener {
            var intent = Intent(this, Summerize::class.java)
            this.startActivity(intent)
        }
    }

    fun getchildview(i:Int):View {
        val tableRow = tbl?.getChildAt(i) as TableRow
        return tableRow
    }

    public fun setcolorofcell (v : View,c:Int)
    {
        v.setBackgroundColor(c)
    }

    fun fireplan() {
        try {
            Thread {
                for (i in 0..(size?.minus(1))!!.toInt()) {
                    (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[i] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +"plan+" + Objectlist.planN+ "+ENDM").toByteArray());
                    if (ConstVal.logEnable) Log.e("Start ", "plan+" + Objectlist.planN)
                }
            }.start()
        }catch (e: Exception)
        {
            Log.e("Server " ,"fireplan " + e.message.toString())
        }
    }

    fun getGPS()
    {
        try {
            Thread {
                for (i in 0..(size?.minus(1))!!.toInt()) {
                    (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[i] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+GPS+" + i.toString() + "+ENDM").toByteArray());
                    if (ConstVal.logEnable) Log.e("Start ", "GPS+" )
                }
            }.start()
        }catch (e: Exception)
        {
            Log.e("Server " ,"getGPS " + e.message.toString())
        }
    }

    fun firemessage(r:Int,c:String) {
        try {
            Thread {
                when (c) {
                    ConstVal.Cred ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CredP + r.toString()+ "+ENDM").toByteArray());
                    ConstVal.blue ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CblueP + r.toString()+ "+ENDM").toByteArray());
                    ConstVal.black->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CblackP + r.toString()+ "+ENDM").toByteArray());
                    ConstVal.white ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CwhiteP + r.toString()+ "+ENDM").toByteArray());
                    ConstVal.yellow ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CyellowP + r.toString()+ "+ENDM").toByteArray());
                    ConstVal.green ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CgreenP + r.toString()+ "+ENDM").toByteArray());
                    else ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CredP + r.toString()+ "+ENDM").toByteArray());
                }
                if (ConstVal.logEnable) Log.e("Start ", c + "+" + r.toString())
            }.start();
        }catch (e: Exception)
        {
            Log.e("Server " ,"firemessage " + e.message.toString())
        }
    }

    fun fireendmessage(r:Int,c:String) {
        try {
            Thread {
                when (c) {
                    ConstVal.red ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.end + "+r"+ "+ENDM").toByteArray());
                    ConstVal.blue ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.end + "+b"+ "+ENDM").toByteArray());
                    ConstVal.black ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.end + "+l"+ "+ENDM").toByteArray());
                    ConstVal.white ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.end + "+w"+ "+ENDM").toByteArray());
                    ConstVal.yellow ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.end + "+y"+"+ENDM").toByteArray());
                    ConstVal.green ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.end + "+g"+ "+ENDM").toByteArray());
                    else ->  (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.end + "+w"+ "+ENDM").toByteArray());
                }
                if (ConstVal.logEnable) Log.e("Start ", c + "+" + r.toString())
            }.start();
        }catch (e: Exception)
        {
            Log.e("Server " ,"fireendmessage " + e.message.toString())
        }
    }

    fun StartPlans() {
        fireplan();
        getGPS();
        for (t in 0..2) {
            for (i in 0..(size?.minus(1))!!.toInt()) {
                fireendmessage(i, ConstVal.green);
            }
            Thread.sleep(500);
            for (i in 0..(size?.minus(1))!!.toInt()) {
                fireendmessage(i, ConstVal.white);
            }
            Thread.sleep(500);
        }

        Thread.sleep(1500);
        PB((timplan!! / 1000).toInt());
        next = true;
        Startbutton.isEnabled = false;
        TimersDataVal.totaltime = 0.0;
        var ContDown: Long? = 2000
        // Start the lengthy operation in a background thread
        if (Objectlist.planN == 3) {
            ContDown = 30000
        }
        val timer = object : CountDownTimer(timplan!!, ContDown!!) {
            override fun onTick(millisUntilFinished: Long) {
                if (Objectlist.planN == 3) {
                    for (i in 0..(size?.minus(1))!!.toInt()) {
                        fireendmessage(i, ConstVal.white);
                    }
                    Thread.sleep(1000);
                    val r = (0..(size?.minus(1)!!)).shuffled().take(1).toSet();
                    val colors =  arrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK);
                    val randomColor = colors.random()
                    setcolorofcell(getchildview(r.elementAt(0)), randomColor);
                    when (randomColor) {
                        Color.RED -> firemessage(r.elementAt(0), ConstVal.Cred);
                        Color.BLUE -> firemessage(r.elementAt(0), ConstVal.blue);
                        Color.GREEN -> firemessage(r.elementAt(0), ConstVal.green);
                        Color.YELLOW -> firemessage(r.elementAt(0), ConstVal.yellow);
                        Color.BLACK -> firemessage(r.elementAt(0), ConstVal.black);
                    }
                }
                //***11.10.2020
                TimersObjectlist.listoftofResult.add("STimer;" + ((timplan!!/1000)-(millisUntilFinished/1000)).toString() )
            }

            override fun onFinish() {
                next = false;
                A?.runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    Startbutton.isEnabled = true;
                    buttons.isEnabled = true;
                })
                for (t in 0..4) {
                    for (i in 0..(size?.minus(1))!!.toInt()) {
                        fireendmessage(i, ConstVal.yellow);
                    }
                    Thread.sleep(500);
                    for (i in 0..(size?.minus(1))!!.toInt()) {
                        fireendmessage(i, ConstVal.white);
                    }
                    Thread.sleep(500);

                }
                Thread.sleep(1500);
                if (planN == 2) {
                    var listcolor = mutableListOf("");
                    var listcsize = mutableListOf("");
                    var CheckedList: List<String> =
                        TimersObjectlist.listoftofResult.filter { s -> s.contains(ConstVal.checked) }
                    for (element in CheckedList) {
                        if (!listcolor.contains(
                                element.split(";")[4].toString().split(" ")[1].toString()
                            )
                        )
                            listcolor.add(element.split(";")[4].toString().split(" ")[1].toString())
                    }
                    var res = "0;0;0"
                    var c = "";
                    for (element in listcolor) {
                        if (element.toString() != "") {
                            var temp = bestbadbycedp(element);
                            listcsize.add(element + ";" + temp)
                            if (temp.split(";")[2].toInt() > res.split(";")[2].toInt()) {
                                res = temp.toString();
                            }
                        }
                    }//  c = element.toString();
                    CheckedList = listcsize.filter { s ->
                        s.contains(
                            res.toString().split(";")[2].toString()
                        )
                    }
                    for (element in CheckedList) {
                        for (t in 0..4) {
                            for (i in 0..(size?.minus(1))!!.toInt()) {
                                fireendmessage(i, element.split(";")[0].toString());
                            }
                            Thread.sleep(500);
                            for (i in 0..(size?.minus(1))!!.toInt()) {
                                fireendmessage(i, ConstVal.white);
                            }
                            Thread.sleep(1000);

                        }
                    }

                }
                Thread.sleep(1000);
            }
        }
        timer.start();
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

    fun Plan1() {
        Objectlist.planN=0;
        StartPlans();
        val r = (0..(size?.minus(1)!!)).random() as Int;
        setcolorofcell(getchildview(r), Color.RED);
        firemessage(r,ConstVal.red);
    }

    fun Plan2() {

        Objectlist.planN=1;
        StartPlans();
        val r = (0..(size?.minus(1)!!)).random() as Int;

        for (i in 0 until size!!) {
            if (i != r) {
                setcolorofcell(getchildview(i), Color.BLUE);
                firemessage(i, ConstVal.blue);
            };
        }
        setcolorofcell(getchildview(r), Color.RED);
        firemessage(r,ConstVal.red);
    }

    fun Plan3() {
        timplan=90000;
        Objectlist.planN = 2;
        StartPlans();
        val r  =  (0..(size?.minus(1)!!)).shuffled().take(2).toSet();
        setcolorofcell(getchildview(r.elementAt(0)), Color.RED);
        setcolorofcell(getchildview(r.elementAt(1)), Color.BLUE);
        firemessage(r.elementAt(0), ConstVal.red);
        firemessage(r.elementAt(1), ConstVal.blue);
    }

    fun Plan4()
    {
        timplan=306000;
        Objectlist.planN = 3;
        StartPlans();
        val r  =  (0..(size?.minus(1)!!)).shuffled().take(1).toSet();
        val colors = arrayOf(Color.RED, Color.BLUE, Color.GREEN,Color.YELLOW,Color.BLACK);
        val randomColor = colors.random()
        setcolorofcell(getchildview(r.elementAt(0)), randomColor);
        when (randomColor) {
            Color.RED -> firemessage(r.elementAt(0), ConstVal.Cred);
            Color.BLUE -> firemessage(r.elementAt(0), ConstVal.blue);
            Color.GREEN -> firemessage(r.elementAt(0), ConstVal.green);
            Color.YELLOW -> firemessage(r.elementAt(0), ConstVal.yellow);
            Color.BLACK -> firemessage(r.elementAt(0), ConstVal.black);
        }

    }

    fun Plan5()
    {

    }

    fun PB(t:Int) {
        var progressStatus = 0;
        progressBar.max=t;
        // Initialize a new Handler instance
        val handler: Handler = Handler()

        // Start the lengthy operation in a background thread
        Thread(Runnable {
            while (progressStatus < t) {
                // Update the progress status
                progressStatus += 1

                // Try to sleep the thread for 1000 milliseconds
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // Update the progress bar
                handler.post(Runnable {
                    progressBar.progress = progressStatus

                    // Show the progress on text view
                    textViewPB.text = progressStatus.toString()
                })
            }
        }).start() // Start the operation


    }

    fun addEndP() {
        val size = Oblist.listofbluetoothsocket.size as Int;

        var tr = TableRow(this)
        tr = TableRow(this);
        for (x in 0..size) {
            if (x==size) break;
            tr = TableRow(this);
            tr.tag = Oblist.listofbluetoothsocket[x]?.remoteDevice?.name
            var t: TextView? = TextView(this);
            t?.setTypeface(null, Typeface.BOLD);
            t?.setTextColor(Color.GRAY)
            t?.textAlignment = TextView.TEXT_ALIGNMENT_CENTER;
            t?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25F);
            t?.text = Oblist.listofbluetoothsocket[x]?.remoteDevice?.name
           // t?.text = x.toString();

            tr?.addView(t)
            StartObjectlist?.listoftTableRow.add(tr);
           // val remainder = (x+1) % 3;
          //  if (remainder==0) {
                tbl?.addView(tr);
       //         tr = TableRow(this);
        //    }


        }
       // tbl?.addView(tr);
    }


    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}