package com.NV.bpsocial

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.NV.bpsocial.SlaveObjectlist.cb
import com.NV.bpsocial.SlaveVal.bool
import com.NV.bpsocial.SlaveVal.index

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
                Thread {
                    Objectlist.mbs?.setconextintent(this);
                    Objectlist.mbs?.write(("Schecked+" + index.toString() + "+" +cb?.tag).toByteArray());
                }.start();

            }
            else
            {
                Thread {
                    Objectlist.mbs?.setconextintent(this);
                    Objectlist.mbs?.write(("Smistake+" + index.toString() + "+" +cb?.tag).toByteArray());
                }.start();
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }


}