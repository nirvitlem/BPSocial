package com.example.bpsocial

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bpsocial.SlaveObjectlist.cb
import com.example.bpsocial.SlaveVal.bool
import com.example.bpsocial.SlaveVal.index
import java.util.*

object SlaveVal {
    @JvmField var index : Int=0;
    @JvmField var bool : Boolean=false;

    //...
}

object SlaveObjectlist {
    @JvmStatic public var cb: Button?=null;
    //...
}

class Slave : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slave)
        cb = findViewById<Button>(R.id.CheckB)
        cb?.setBackgroundColor(Color.WHITE);
        cb?.setOnClickListener {
            if (bool) {
                bool=false;
                cb?.setBackgroundColor(Color.WHITE);
                Thread({
                    Objectlist.mbs?.setconextintent(this);
                    Objectlist.mbs?.write(("Schecked+" + index.toString() + "+" +cb?.tag).toByteArray());
                }).start();

            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }


}