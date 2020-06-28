package com.NV.bpsocial


import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.NV.bpsocial.StartObjectval.next
import kotlinx.android.synthetic.main.activity_start.*
import kotlin.collections.ArrayList


public var tbl : TableLayout ?=null;
public var size : Int ?=0;
public var plan : String ?="";


object StartObjectlist {
    @JvmStatic public var listoftTableRow: ArrayList<TableRow> = ArrayList();
    @JvmStatic public var list=mutableListOf("");
    @JvmStatic public var adapter:ArrayAdapter<String>?=null;
    //...
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
        l.add("תוכנית 2")
        l.add("תוכנית 3")
        l.add("תוכנית 4")
        l.add("תוכנית 5")
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
               "תוכנית 2" -> Plan2();
               "תוכנית 3" -> Plan3();
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

    fun Plan1() {
        PB(60);
        next = true;
        Startbutton.isEnabled = false;
        TimersDataVal.totaltime = 0.0;

        // Start the lengthy operation in a background thread

        val timer = object : CountDownTimer(60000, 2000) {
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

            }
        }
        timer.start();
        val r = (0..(size?.minus(1)!!)).random() as Int;
        setcolorofcell(getchildview(r), Color.RED);
        firemessage(r);
    }

    fun firemessage(r:Int) {
        Thread({
            val mbs: MyBluetoothService? =
                MyBluetoothService(Oblist.listofbluetoothsocket[r] as BluetoothSocket);
            mbs?.setconextintent(this!!);
            mbs?.write(("Cred+" + r.toString()).toByteArray());
            //StartObjectlist.list?.add(Oblist.listofbluetoothsocket[r].remoteDevice.name);
            // StartObjectlist.adapter?.notifyDataSetChanged()

        }).start();
    }

    fun Plan2()
    {

    }

    fun Plan3()
    {

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