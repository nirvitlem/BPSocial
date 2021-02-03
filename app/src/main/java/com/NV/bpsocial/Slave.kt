package com.NV.bpsocial

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.NV.bpsocial.Objectlist.planN
import com.NV.bpsocial.SVal.vibpnum
import com.NV.bpsocial.SlaveObjectlist.cb
import com.NV.bpsocial.SlaveVal.bool
import com.NV.bpsocial.SlaveVal.eVib
import com.NV.bpsocial.SlaveVal.index
import com.NV.bpsocial.SlaveVal.mistakecount
import java.lang.Exception
import java.lang.Math.sqrt
import java.util.*
import java.util.concurrent.TimeUnit

object SlaveVal {
    @JvmField var index : Int=0;
    @JvmField var bool : Boolean=false;
    @JvmField var mistakecount : Int ?=0;
    @JvmField var eVib : Boolean ?=true;

    //...
}

object SlaveObjectlist {
    @JvmStatic public var cb: Button?=null;
    //...
}
private var sensorManager: SensorManager? = null;
private var acceleration = 0f;
private var currentAcceleration = 0f;
private var lastAcceleration = 0f;

class Slave : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        if (ConstVal.logEnable) Log.e("vibpnum", vibpnum.toString());
        if (!eVib!!) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            Objects.requireNonNull(sensorManager)!!.registerListener(
                sensorListener, sensorManager!!
                    .getDefaultSensor(Sensor.TYPE_ALL), SensorManager.SENSOR_DELAY_FASTEST
            )
            acceleration = 10f
            currentAcceleration = SensorManager.GRAVITY_EARTH
            lastAcceleration = SensorManager.GRAVITY_EARTH
        }
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

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
           // if (ConstVal.logEnable) Log.e("sensorListener", acceleration.toString());
            if (acceleration > vibpnum!!) {
                if (ConstVal.logEnable) Log.e("sensorListener>0.1", acceleration.toString());
                try {
                    if (bool) {
                        bool = false;
                        when (Objectlist?.planN) {
                            0 -> cb?.setBackgroundColor(Color.WHITE)
                            1 -> cb?.setBackgroundColor(Color.BLUE)
                            2 -> cb?.setBackgroundColor(Color.WHITE)
                            else -> cb?.setBackgroundColor(Color.WHITE)
                        }
                        Thread {
                            Objectlist.mbs?.setconextintent(this@Slave);
                            Objectlist.mbs?.write(
                                ("StartM+" + ConstVal.ScheckedP + index.toString() + "+" + cb?.tag + "+" + TimeUnit.MILLISECONDS.toMicros(
                                    System.currentTimeMillis()
                                ).toString() + "+ENDM").toByteArray()
                            );
                        }.start();

                    } else {
                        if (planN != 2) {
                            Thread {
                                Objectlist.mbs?.setconextintent(this@Slave);
                                Objectlist.mbs?.write(("StartM+" + ConstVal.SmistakeP + index.toString() + "+" + cb?.tag + "+ENDM").toByteArray());
                            }.start();
                        } else {
                            mistakecount = mistakecount?.plus(1)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Slave ", e.message.toString())
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
    override fun onResume() {
        if (!eVib!!) {
            sensorManager?.registerListener(
                sensorListener, sensorManager!!.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
                ), SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        super.onResume()
    }

    override fun onPause() {
        if (!eVib!!) {
            sensorManager!!.unregisterListener(sensorListener)
        }
        super.onPause()
    }

}