package com.example.bpsocial


import android.bluetooth.BluetoothSocket
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bpsocial.StartObjectlist.listoftTableRow

public var tbl : TableLayout ?=null;
object StartObjectlist {
    @JvmStatic public var listoftTableRow: ArrayList<TableRow> = ArrayList();
    //...
}
class Start : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Objectlist.mbs?.setconextintent(this);
        tbl= findViewById(R.id.tableLayout) as TableLayout
        addEndP();
        val sb = findViewById(R.id.Startbutton) as Button;
        val size = Oblist.listofbluetoothsocket.size as Int
        sb.setOnClickListener {
            val r = (0..(size-1)).random() as Int;
            setcolorofcell(getchildview(r),Color.BLUE);
            Thread({
                val mbs: MyBluetoothService? = MyBluetoothService( Oblist.listofbluetoothsocket[r] as BluetoothSocket);
                mbs?.setconextintent(this!!);
                mbs?.write(("blue+" + r.toString()).toByteArray());
            }).start();
        }
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


    fun addEndP() {
        val size = Oblist.listofbluetoothsocket.size as Int;
        // lay_r_capture.setOrientation(LinearLayout.HORIZONTAL)


        for (x in 0..size) {
            if (x==size) break;

            var tr = TableRow(this)
            tr = TableRow(this)
            //  tr.setBackgroundColor(Color.BLUE);
            tr.tag = Oblist.listofbluetoothsocket[x]?.remoteDevice?.address
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