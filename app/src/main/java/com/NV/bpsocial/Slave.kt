package com.NV.bpsocial

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.NV.bpsocial.Objectlist.planN
import com.NV.bpsocial.SVal.vibpnum
import com.NV.bpsocial.SlaveObjectlist.cb
import com.NV.bpsocial.SlaveVal.bool
import com.NV.bpsocial.SlaveVal.cnx
import com.NV.bpsocial.SlaveVal.eVib
import com.NV.bpsocial.SlaveVal.iVib
import com.NV.bpsocial.SlaveVal.index
import com.NV.bpsocial.SlaveVal.mistakecount
import java.lang.Exception
import java.lang.Math.sqrt
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object SlaveVal {
    @JvmField var index : Int=0;
    @JvmField var bool : Boolean=false;
    @JvmField var mistakecount : Int ?=0;
    @JvmField var eVib : Boolean ?=null;
    @JvmField var iVib : Boolean ?=null;
    @JvmField var device : UsbDevice? = null;
    @JvmField var manager : UsbManager? = null;
    @JvmField var cnx : UsbDeviceConnection?=null;


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
const val REQ_OUT = UsbConstants.USB_TYPE_CLASS + UsbConstants.USB_DIR_OUT
const val REQ_IN = UsbConstants.USB_TYPE_CLASS + UsbConstants.USB_DIR_IN
private var mMin = 0
private var mMax = 0
private var isStop=false;
val DIGISPARK_VID = 0x16C0
val DIGISPARK_PID = 0x05DF

class Slave : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        mMin = 1023
        mMax = 0
        if (ConstVal.logEnable) Log.e("vibpnum", vibpnum.toString());
        if (iVib!!) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            Objects.requireNonNull(sensorManager)!!.registerListener(
                sensorListener, sensorManager!!
                    .getDefaultSensor(Sensor.TYPE_ALL), SensorManager.SENSOR_DELAY_FASTEST
            )
            acceleration = 10f
            currentAcceleration = SensorManager.GRAVITY_EARTH
            lastAcceleration = SensorManager.GRAVITY_EARTH
        }
        if (eVib!!) {
            thread {
                startlisten();
            }
        }
        setContentView(R.layout.activity_slave)
        cb = findViewById<Button>(R.id.CheckB)
        cb?.setBackgroundColor(Color.WHITE);
        cb?.setOnClickListener {
            if (ConstVal.logEnable) Log.e("setOnClickListener", "clicked");
            GetClick();
        }
    }

    private fun doBlink(cnx: UsbDeviceConnection) {
        cnx.controlTransfer(REQ_OUT, 9, 0, 'b'.toInt(), null, 0, 1000)
        cnx.controlTransfer(REQ_OUT, 9, 0, '\n'.toInt(), null, 0, 1000)
    }

    private fun stoplisten(cnx: UsbDeviceConnection) {
        cnx.controlTransfer(REQ_OUT, 9, 0, 's'.toInt(), null, 0, 1000)
        cnx.controlTransfer(REQ_OUT, 9, 0, '\n'.toInt(), null, 0, 1000)
    }

    private fun startlisten() {

        cnx?.let { doBlink(it) };
        // send t + \n

        while (!isStop) {
            try {
                cnx?.controlTransfer(REQ_OUT, 9, 0, 't'.toInt(), null, 0, 1000)
                cnx?.controlTransfer(REQ_OUT, 9, 0, '\n'.toInt(), null, 0, 1000)
                Thread.sleep(100 /*ms*/)

                val v = cnx?.let { it1 -> readValueSync(it1) }
                if (v != null) {
                    if (v >= 0 && v <= 1023) {
                        if (v < mMin) mMin = v
                        if (v > mMax) mMax = v
                    }
                }

            } catch (e: InterruptedException) {

            }
        }

    }

    @Throws(InterruptedException::class)
    private fun readValueSync(cnx: UsbDeviceConnection): Int {
        // read numbers back till we get \n
        val buffer = ByteArray(16)
        var value = 0
        var res: Int
        while (true) {
            buffer[0] = 4
            res = cnx.controlTransfer(REQ_IN, 1, 0, 0, buffer, 1, 1000)
            val c = buffer[0].toChar()
            if (res < 0 || c.toInt() == 4) {
                return -1
            }
            if (c == '\n') break
            if (Character.isDigit(c)) {
                value = value * 10 + (c - '0')
            }
        }
        //if (ConstVal.logEnable) Log.e("readValueSync", value.toString())
        if (value>10)
        {
            if (ConstVal.logEnable) Log.e("readValueSync>10", value.toString());
            GetClick();
        }
        return value
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
                GetClick();
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private fun GetClick()
    {
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

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    override fun onDestroy() {
        isStop = true;
        stoplisten(cnx!!);
        super.onDestroy()
    }
    override fun onResume() {
        if (iVib!!) {
            sensorManager?.registerListener(
                sensorListener, sensorManager!!.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
                ), SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        super.onResume()
    }

    override fun onPause() {
        if (iVib!!) {
            sensorManager!!.unregisterListener(sensorListener)
        }
        super.onPause()
    }

}