package com.NV.bpsocial

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.NV.bpsocial.Objectlist.planN
import com.NV.bpsocial.SlaveObjectlist.cb
import com.NV.bpsocial.SlaveVal.bool
import com.NV.bpsocial.SlaveVal.index
import com.NV.bpsocial.SlaveVal.mistakecount
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

object SlaveVal {
    @JvmField var index : Int=0;
    @JvmField var bool : Boolean=false;
    @JvmField var mistakecount : Int ?=0;
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
            try{
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
                    Objectlist.mbs?.write(("StartM+" +ConstVal.ScheckedP + index.toString() + "+" +cb?.tag + "+" + TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()).toString()+ "+ENDM").toByteArray());
                }.start();

            }
            else
            {
                if (planN != 2 ) {
                      Thread {
                           Objectlist.mbs?.setconextintent(this);
                          Objectlist.mbs?.write(("StartM+" +ConstVal.SmistakeP + index.toString() + "+" +cb?.tag+ "+ENDM").toByteArray());
                      }.start();
                }
                else
                {
                    mistakecount= mistakecount?.plus(1)
                }
            }
        }
            catch (e: Exception)
            {
                Log.e("Slave " , e.message.toString())
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }


}