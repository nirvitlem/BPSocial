package com.example.bpsocial


import android.bluetooth.BluetoothSocket
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.example.bpsocial.StartObjectlist.listoftTableRow
import kotlinx.android.synthetic.main.activity_start.*


public var tbl : TableLayout ?=null;
public var size : Int ?=0;
public var plan : String ?="";
object StartObjectlist {
    @JvmStatic public var listoftTableRow: ArrayList<TableRow> = ArrayList();
    @JvmStatic public var list=mutableListOf("");
    @JvmStatic public var adapter:ArrayAdapter<String>?=null;
    //...
}
class Start : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
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

    }

    fun getchildview(i:Int):View {
        if (i<3)  return  tbl?.getChildAt(i) as View
        else {
            val X = (i % 3) +1
            val Y = i/3;
            val tableRow = tbl?.getChildAt(X) as TableRow
            val childView: View = tableRow.getChildAt(Y)
            return childView
        }
    }

    fun setcolorofcell(v : View,c:Int)
    {
        v.setBackgroundColor(c)
    }

    fun Plan1()
    {
        StartObjectlist.list.clear();
        PB(300);
        Startbutton.isEnabled=false;
        val r = (0..(size?.minus(1)!!)).random() as Int;
        setcolorofcell(getchildview(r),Color.BLUE);
        Thread({
            val mbs: MyBluetoothService? = MyBluetoothService( Oblist.listofbluetoothsocket[r] as BluetoothSocket);
            mbs?.setconextintent(this!!);
            mbs?.write(("blue+" + r.toString()).toByteArray());
            StartObjectlist.list?.add(Oblist.listofbluetoothsocket[r].remoteDevice.name);
            StartObjectlist.adapter?.notifyDataSetChanged()
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

        // Initialize a new Handler instance
        val handler: Handler = Handler()

        // Start the lengthy operation in a background thread
        Thread(Runnable {
            while (progressStatus < t) {
                // Update the progress status
                progressStatus += 2

                // Try to sleep the thread for 50 milliseconds
                try {
                    Thread.sleep(50)
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
            Startbutton.isEnabled = true;
        }).start() // Start the operation

    }

    fun addEndP() {
        val size = Oblist.listofbluetoothsocket.size as Int;
        // lay_r_capture.setOrientation(LinearLayout.HORIZONTAL)


        for (x in 0..size) {
            if (x==size) break;

            var tr = TableRow(this)
            tr = TableRow(this)
            //  tr.setBackgroundColor(Color.BLUE);
            tr.tag = Oblist.listofbluetoothsocket[x]?.remoteDevice?.name
            var t: TextView? = TextView(this);
            t?.setTypeface(null, Typeface.BOLD);
            t?.setTextColor(Color.GRAY)
            t?.textAlignment = TextView.TEXT_ALIGNMENT_CENTER;
            t?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25F);
            t?.text = Oblist.listofbluetoothsocket[x]?.remoteDevice?.name

            //        )
            //    );
            tr?.addView(t)
            listoftTableRow.add(tr);
            val remainder = size % 3;
            if (remainder==0) {
                var tr = TableRow(this)
                tr = TableRow(this)
                tbl?.addView(tr);
            }
            tbl?.addView(tr);

        }
        //tbl?.addView(tr);
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}