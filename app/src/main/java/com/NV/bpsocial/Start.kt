package com.NV.bpsocial


import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothSocket
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
import kotlinx.android.synthetic.main.activity_start.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.random.nextInt


//public var tbl : TableLayout ?=null;
public var size : Int ?=0;
public var plan : String ?="";
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
        A=this;
        textViewPB.text="";
        Objectlist.mbs?.setconextintent(this);
        tbl= findViewById(R.id.tableLayout) as TableLayout
        addEndP();
        val planCoohser  = findViewById<Spinner>(R.id.spinner);
        val l: ArrayList<String> = ArrayList()
        l.add("תוכנית 1")
        l.add("תוכנית 1, 2 צבעים")
        l.add("תוכנית 3")
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
                plan= parent?.getItemAtPosition(position).toString();
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        val sb = findViewById(R.id.Startbutton) as Button;
        size = Oblist.listofbluetoothsocket.size as Int
        sb.setOnClickListener {
            buttons.isEnabled=false;
            TimersObjectlist.listoftofResult.clear();
            StartObjectlist.list.clear();
            StartObjectlist.adapter?.clear();
           /*test  size=4;
            var r = (0..(size?.minus(1)!!)).random() as Int;
            StartObjectlist.list.add(r.toString());
            StartObjectlist.adapter?.notifyDataSetChanged();
            setcolorofcell(getchildview(r),Color.RED)*/
           when (plan) {
               "תוכנית 1" -> Plan1();
               "תוכנית 1, 2 צבעים" -> Plan2();
               "תוכנית 3" ->
               {
                   if (size!!<2)
                   {
                       val builder = AlertDialog.Builder(this)
                       builder.setTitle("תקלה! ")
                       builder.setMessage("צריך לפחות 2 יחידות קצה ")
                       builder.setView(ed);
                       builder.setPositiveButton("OK") { dialog, which ->

                       }
                       builder.show();
                   }
                   else  Plan3()
               };
               "תוכנית 4" -> Plan4();
               "תוכנית 5" -> Plan5();
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

       // val X = (i % 3)
       // val Y = i / 3;
        val tableRow = tbl?.getChildAt(i) as TableRow
      //  val childView: View = tableRow.getChildAt(X)
        return tableRow
    }

    public fun setcolorofcell (v : View,c:Int)
    {
        v.setBackgroundColor(c)
    }

    fun firemessage(r:Int,c:String) {
        Thread {
            var mbs: MyBluetoothService? =
                MyBluetoothService(Oblist.listofbluetoothsocket[r] as BluetoothSocket);
            mbs?.setconextintent(this!!);
            mbs?.write(("plan+" + Objectlist.planN).toByteArray());
            Log.e("Start ", "plan+" + Objectlist.planN)
        }.start()

        Thread {
            val mbs: MyBluetoothService? =
                MyBluetoothService(Oblist.listofbluetoothsocket[r] as BluetoothSocket);
            mbs?.setconextintent(this!!);
            when (c) {
                "red" ->mbs?.write(("Cred+" + r.toString()).toByteArray());
                "blue" -> mbs?.write(("Cblue+" + r.toString()).toByteArray());
                "black" -> mbs?.write(("Cblack+" + r.toString()).toByteArray());
                "white" -> mbs?.write(("Cwhite+" + r.toString()).toByteArray());
                "yellow" -> mbs?.write(("Cyellow+" + r.toString()).toByteArray());
                "green" -> mbs?.write(("Cgreen+" + r.toString()).toByteArray());
                else -> mbs?.write(("Cred+" + r.toString()).toByteArray());
            }
            Log.e("Start ", c+"+"  + r.toString())
            //StartObjectlist.list?.add(Oblist.listofbluetoothsocket[r].remoteDevice.name);
            // StartObjectlist.adapter?.notifyDataSetChanged()

        }.start();
    }

    fun fireendmessage(r:Int,c:String) {
        Thread {
            val mbs: MyBluetoothService? =
                MyBluetoothService(Oblist.listofbluetoothsocket[r] as BluetoothSocket);
            //mbs?.setconextintent(this!!);
            when (c) {
                "red" -> mbs?.write("end+r".toByteArray());
                "blue" -> mbs?.write("end+b".toByteArray());
                "white" -> mbs?.write("end+w".toByteArray());
                "yellow" -> mbs?.write("end+y".toByteArray());
                "green" -> mbs?.write("end+g".toByteArray());
                else -> mbs?.write("end+w".toByteArray());
            }
            Log.e("Start ", c+ "+"  + r.toString())
        }.start();
    }

    fun StartPlans()
    {
        for (t in 0..2)
        {
            for (i in 0..(size?.minus(1))!!.toInt()) {
                fireendmessage(i, "green");
            }
            Thread.sleep(1000);
            for (i in 0..(size?.minus(1))!!.toInt()) {
                fireendmessage(i, "white");
            }
            Thread.sleep(1000);
        }

        Thread.sleep(2000);
        PB(60);
        next = true;
        Startbutton.isEnabled = false;
        TimersDataVal.totaltime = 0.0;

        // Start the lengthy operation in a background thread

        val timer = object : CountDownTimer(timplan!!, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                //   if (StartObjectval.next!!) {
                //       val r = (0..(size?.minus(1)!!)).random() as Int;
                //       setcolorofcell(getchildview(r), Color.RED);
                //       firemessage(r);
                //  next = false;
                //  }
            }

            override fun onFinish() {
                next = false;
                A?.runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    //list.add(" סך הזמן לתרגיל " + TimersDataVal.totaltime.toString() + " שניות ");
                    //adapter?.notifyDataSetChanged();
                    Startbutton.isEnabled = true;
                    buttons.isEnabled = true;
                })
                for (t in 0..4)
                {
                    for (i in 0..(size?.minus(1))!!.toInt()) {
                        fireendmessage(i, "yellow");
                    }
                    Thread.sleep(1000);
                    for (i in 0..(size?.minus(1))!!.toInt()) {
                        fireendmessage(i, "white");
                    }
                    Thread.sleep(1000);

                }
                Thread.sleep(2000);
                if (planN==2)
                {
                    var listcolor = mutableListOf("");
                    var CheckedList: List<String> =
                        TimersObjectlist.listoftofResult.filter { s -> s.contains("checked") }
                    for (element in CheckedList) {
                        if (!listcolor.contains(element.split(";")[4].toString().split(" ")[1].toString()))
                            listcolor.add(element.split(";")[4].toString().split(" ")[1].toString())
                    }
                    var res = "0;0;0"
                    var c = "";
                    for (element in listcolor) {
                        if (element.toString() != "") {
                            var temp = bestbadbycedp(element);
                            if (temp.split(";")[2].toInt() > res.split(";")[2].toInt())
                            {
                                res = temp.toString();
                            }
                        }
                    }//  c = element.toString();
                    CheckedList = TimersObjectlist.listoftofResult.filter { s -> s.contains(res.toString().split(";")[2].toString()) }
                    for (element in CheckedList) {
                        for (t in 0..4)
                        {
                            for (i in 0..(size?.minus(1))!!.toInt()) {
                               fireendmessage(i, element.split(";")[4].toString().split(" ")[1].toString());
                            }
                            Thread.sleep(1000);
                            for (i in 0..(size?.minus(1))!!.toInt()) {
                                fireendmessage(i, "white");
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
        firemessage(r,"red");
    }

    fun Plan2() {
        Objectlist.planN=1;
        StartPlans();
        val r = (0..(size?.minus(1)!!)).random() as Int;

        for (i in 0 until size!!) {
            if (i != r) {
                setcolorofcell(getchildview(i), Color.BLUE);
                firemessage(i, "blue");
            };
        }
        setcolorofcell(getchildview(r), Color.RED);
        firemessage(r,"red");
    }

    fun Plan3() {
        Objectlist.planN = 2;
        StartPlans();
        val r  =  (0..(size?.minus(1)!!)).shuffled().take(2).toSet();
        setcolorofcell(getchildview(r.elementAt(0)), Color.RED);
        setcolorofcell(getchildview(r.elementAt(1)), Color.BLUE);
        firemessage(r.elementAt(0), "red");
        firemessage(r.elementAt(1), "blue");
    }

    fun Plan4()
    {

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