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
                when (Objectlist?.planN)                {
                    0 -> cb?.setBackgroundColor(Color.WHITE)
                    1 ->  cb?.setBackgroundColor(Color.BLUE)
                    2 -> cb?.setBackgroundColor(Color.WHITE)
                    else ->  cb?.setBackgroundColor(Color.WHITE)
                }
                Thread {
                    Objectlist.mbs?.setconextintent(this);
                    Objectlist.mbs?.write((ConstVal.ScheckedP + index.toString() + "+" +cb?.tag).toByteArray());
                }.start();

            }
            else
            {
                Thread {
                    Objectlist.mbs?.setconextintent(this);
                    Objectlist.mbs?.write((ConstVal.SmistakeP + index.toString() + "+" +cb?.tag).toByteArray());
                }.start();
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }


}