package com.NV.bpsocial

import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import com.NV.bpsocial.ConstVal.bufferSize
import com.NV.bpsocial.ConstVal.logEnable
import com.NV.bpsocial.GeneralObj.sharedCounterLock
import com.NV.bpsocial.GeneralVal.Plan2firstrecive
import com.NV.bpsocial.GeneralVal.latitude
import com.NV.bpsocial.GeneralVal.longitude
import com.NV.bpsocial.GeneralVal.tempMessage
import com.NV.bpsocial.GeneralVal.timeresponse
import com.NV.bpsocial.Objectlist.M
import com.NV.bpsocial.StartObjectval.next
import com.NV.bpsocial.TimersObjectlist.listoftofResult
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.reflect.Array
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.HashMap

private const val TAG = "BPSocial MyBluetoothService"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)

class MyBluetoothService(private val mmSocket: BluetoothSocket) : Thread() {


    private var mmInStream: InputStream = mmSocket.inputStream
    private val mmOutStream: OutputStream = mmSocket.outputStream
    private var mmBuffer: ByteArray = ByteArray(bufferSize) // mmBuffer store for the stream
    private var A: Activity? = null;
    private val time: TimersData? = TimersData();

    private var IndexClient: Int? = 0;




    public fun setconextintent(a: Activity) {
        A = a;
    }

    override fun run() {

        var numBytes: Int // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            if (logEnable) Log.e(TAG, "mmInStream.read(mmBuffer)");
            // Read from the InputStream.

            numBytes = try {

                mmInStream.read(mmBuffer);
            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
                Objectlist.MBSArray?.remove(mmSocket);
                //Objectlist.MBSArray?.put(mmSocket,MyBluetoothService(mmSocket));
                break
            }
            if (numBytes > 0) {
                if (logEnable) Log.e(TAG, String(mmBuffer));
                storeMessages(String(mmBuffer, StandardCharsets.UTF_8).replace(0.toChar().toString(), "")!!);

            }
            //else   mmBuffer = ByteArray(bufferSize);
        }
    }

    fun storeMessages(M : String) {
        if (M!!.contains("+ENDM")&& M!!.contains("StartM+")) {
            if (logEnable) Log.e(TAG, "storeMessages " + M.toString());
            tempMessage =  M.split("StartM+")[1].split("+ENDM")[0].toString();
            if (logEnable) Log.e(TAG, "storeMessages tempMessage " + tempMessage.toString());
            var reutrnmessage = handlebuffer(tempMessage!!)
            if (logEnable) Log.e(TAG, "handlebuffer " + reutrnmessage);
            tempMessage="";
            try {
                if ( M.split("+ENDM").size>2)
                {
                    for (i in 1 until M.split("+ENDM").size)
                    {
                       var reutrnmessage = handlebuffer(M.split("+ENDM")[i]!!.split("StartM+")[1].toString())
                        if (logEnable) Log.e(TAG, "handlebuffer " + reutrnmessage);

                    }
                    tempMessage = M.split("+ENDM")[M.split("+ENDM").size].toString()
                } else  tempMessage = M.split("+ENDM")[1].toString()
            } catch(e : Exception) {
                com.NV.bpsocial.GeneralVal.tempMessage = "";
            }
        } else {
            tempMessage = tempMessage + M;
        }

    }

    fun  handlebuffer(message : String) : String
    {

        if (message!!.contains("150874"))
        {
            write(("StartM+" + "התקבלה הההודעה - 740815 "+ "+ENDM").toByteArray() )
            return  "StartM+" + "התקבלה הההודעה - 740815 "+ "+ENDM"
        };
        if (message!!.contains("740815"))
        {
            addtesttolist(mmSocket.remoteDevice.name + " בדיקה עברה בהצלחה ")
            return  mmSocket.remoteDevice.name + " בדיקה עברה בהצלחה "
        }
        if (message!!.contains("ready")) {
            var intent = Intent(A, Slave::class.java)
            A?.startActivity(intent)
            return "ready"
        }

        //server
        if (message!!.contains(ConstVal.Schecked)) {
            if (next==true) {
                when (Objectlist.planN) {
                    0 ->
                    {
                        val sec: Double? =((Calendar.getInstance().timeInMillis.toDouble() - M?.get(mmSocket.remoteDevice.address.toString())?.toDouble()!!) / 1000);
                        val c = message!!.split('+')[2].toString()
                        time?.sumtime(sec!!);
                        A?.runOnUiThread(Runnable {
                            StartObjectlist.listoftTableRow[IndexClient!!]?.setBackgroundColor(Color.WHITE);
                            val t = StartObjectlist.listoftTableRow[IndexClient!!]?.tag.toString();
                            listoftofResult.add("Schecked;" + t + ";" + sec + ";"+ Calendar.getInstance().time.toString() + ";" + c)
                            StartObjectlist.list?.add(" נגיעה ב- " + t + " אחרי " + sec.toString());
                            StartObjectlist.adapter?.notifyDataSetChanged()
                        })
                        val r = (0..(size?.minus(1)!!)).random() as Int;
                        A?.runOnUiThread(Runnable {
                            StartObjectlist.tbl?.getChildAt(r)?.setBackgroundColor(Color.RED);
                        })
                        (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CredP + r.toString()+ "+ENDM").toByteArray())
                        if (logEnable) Log.e(TAG, ConstVal.CredP + r.toString())
                        return  ConstVal.Schecked + " plane 0"
                    };
                    1 ->
                    {
                        val sec: Double? = ((Calendar.getInstance().timeInMillis.toDouble() - M?.get(mmSocket.remoteDevice.address.toString())?.toDouble()!!) / 1000);
                        val c = message!!.split('+')[2].toString()
                        time?.sumtime(sec!!);
                        A?.runOnUiThread(Runnable {
                            StartObjectlist.listoftTableRow[IndexClient!!]?.setBackgroundColor(Color.WHITE);
                            val t = StartObjectlist.listoftTableRow[IndexClient!!]?.tag.toString();
                            listoftofResult.add("Schecked;" + t + ";" + sec + ";"+ Calendar.getInstance().time.toString() + ";" + c)
                            StartObjectlist.list?.add(" נגיעה ב- " + t + " אחרי " + sec.toString());
                            StartObjectlist.adapter?.notifyDataSetChanged()
                        })
                        val r = (0..(size?.minus(1)!!)).random() as Int;
                        A?.runOnUiThread(Runnable {
                            StartObjectlist.tbl?.getChildAt(r)?.setBackgroundColor(Color.RED);
                        })
                        Thread {
                            if (logEnable) Log.e(TAG, ConstVal.CredP + r.toString())
                            for (i in 0 until size!!) {
                                    (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[i] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CblueP + i.toString()+ "+ENDM").toByteArray())
                                    if (logEnable) Log.e(TAG, ConstVal.CblueP + i.toString())
                            }
                            (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CredP + r.toString()+ "+ENDM").toByteArray())
                        }.start();
                        return  ConstVal.Schecked + " plane 1"
                    }
                    2-> {
                        Thread {
                            sharedCounterLock.acquire();
                            runplan2(message!!)
                        }.start()
                        return  ConstVal.Schecked + " plane 2"
                    }
                    else ->  SlaveObjectlist.cb?.setBackgroundColor(Color.WHITE)
                }
            }

        }

        if (message!!.contains(ConstVal.Smistake)) {
            // time?.StopTime();

            val c = message!!.split('+')[2].toString()
            A?.runOnUiThread(Runnable {
                val t = StartObjectlist.listoftTableRow[IndexClient!!]?.tag.toString();
                listoftofResult.add("Smistake;" + t + ";" + " " + ";"+ Calendar.getInstance().time.toString() + ";" + c)
                StartObjectlist.list?.add(" נגיעה בטעות ב- " + t + " אחרי " + " ");
                StartObjectlist.adapter?.notifyDataSetChanged()
            })
            return  ConstVal.Smistake
        }

        if (message!!.contains(ConstVal.Sred)) {
            Plan2firstrecive = true;
            listoftofResult.add("SSendred; ; ;"+ Calendar.getInstance().time.toString()+";red")
            M?.put(
                mmSocket.remoteDevice.address.toString(),
                Calendar.getInstance().timeInMillis!!
            )
            IndexClient =message!!.split('+')[1].toInt();
            return ConstVal.Sred
        }

        if (message!!.contains(ConstVal.SGEO)) {
            IndexClient =message!!.split('+')[1].toInt();
            val t = StartObjectlist.listoftTableRow[IndexClient!!]?.tag.toString();
            listoftofResult.add("SGEO;" + t + ";latitude;" + message!!.split("+")[2].toDouble() + ";longitude;" + + message!!.split("+")[3].toDouble())

            return ConstVal.SGEO
        }

        if (message!!.contains(ConstVal.Sblue)) {
            Plan2firstrecive = true;
            listoftofResult.add("SSendblue; ; ;"+ Calendar.getInstance().time.toString()+";blue")
            M?.put(
                mmSocket.remoteDevice.address.toString(),
                Calendar.getInstance().timeInMillis!!
            )
            IndexClient = message.split('+')[1].toInt();
            return ConstVal.Sblue;
        }

        if (message!!.contains(ConstVal.Swhite)) {

            GeneralVal.cReady = GeneralVal.cReady!!.plus(1);
            val index =message!!.split('+')[1].toInt();
            if (logEnable) Log.e(TAG, "get "  + ConstVal.Swhite + index.toString())
            return ConstVal.Swhite
        }

        //**********************************************************************************8
        //client

        if (message!!.contains(ConstVal.slavebool)) {
            SlaveVal.bool=false;
            return  ConstVal.slavebool
        }

        if (message!!.contains(ConstVal.plan)) {
            Objectlist.planN = message!!.split('+')[1].toInt();
            return  ConstVal.plan
         }

        if (message!!.contains(ConstVal.GPS)) {
            val index = message!!.split('+')[1].toInt();
            write(("StartM+SGEO+" + index.toString() + "+" + latitude.toString() + "+" + longitude.toString() + "+ENDM").toByteArray());
            return  ConstVal.GPS
        }

        if (message!!.contains(ConstVal.Cred)) {
            when (Objectlist.planN) {
                3 -> {
                    A?.runOnUiThread(Runnable {
                        SlaveObjectlist.cb?.setBackgroundColor(Color.RED);
                        SlaveObjectlist.cb?.tag = "Color Red";
                    })
                    return ConstVal.Cblue + " plan 3"
                }
                else -> {
                    val index = message!!.split('+')[1].toInt();
                    if (logEnable) Log.e(TAG, "send " + ConstVal.SredP + index.toString())
                    A?.runOnUiThread(Runnable {
                        SlaveVal.bool = true;
                        SlaveVal.index = index
                        SlaveObjectlist.cb?.setBackgroundColor(Color.RED);
                        SlaveObjectlist.cb?.tag = "Color red";
                    })
                    write(("StartM+" + ConstVal.SredP + index.toString() + "+ENDM").toByteArray());
                    return ConstVal.Cred;
                }
            }

        }

        if (message!!.contains(ConstVal.Cblue)) {
            when (Objectlist.planN) {
                1 -> {
                    A?.runOnUiThread(Runnable {
                        SlaveObjectlist.cb?.setBackgroundColor(Color.BLUE);
                        SlaveObjectlist.cb?.tag = "Color blue";
                    })
                    return ConstVal.Cblue + " plan 1"
                }
                2->
                {
                    val index = message!!.split('+')[1].toInt();
                    write(("StartM+" +ConstVal.SblueP + index.toString()+"+ENDM").toByteArray());
                    if (logEnable) Log.e(TAG, "send "  + ConstVal.SblueP + index.toString())
                    A?.runOnUiThread(Runnable {
                        SlaveVal.bool = true;
                        SlaveVal.index = index
                        SlaveObjectlist.cb?.setBackgroundColor(Color.BLUE);
                        SlaveObjectlist.cb?.tag="Color blue";
                    })
                    return ConstVal.Cblue + " plan 2"
                }
                3 -> {
                    A?.runOnUiThread(Runnable {
                        SlaveObjectlist.cb?.setBackgroundColor(Color.BLUE);
                        SlaveObjectlist.cb?.tag = "Color blue";
                    })
                    return ConstVal.Cblue + " plan 1"
                }
                else -> {

                }
            }
         }

        if (message!!.contains(ConstVal.Cyellow)) {
            when (Objectlist.planN) {
                3 -> {
                    A?.runOnUiThread(Runnable {
                        SlaveObjectlist.cb?.setBackgroundColor(Color.YELLOW);
                        SlaveObjectlist.cb?.tag = "Color Yellow";
                    })
                    return ConstVal.Cblue + " plan 3"
                }
                else -> {

                }
            }

        }

        if (message!!.contains(ConstVal.Cblack)) {
            when (Objectlist.planN) {
                3 -> {
                    A?.runOnUiThread(Runnable {
                        SlaveObjectlist.cb?.setBackgroundColor(Color.BLACK);
                        SlaveObjectlist.cb?.tag = "Color Black";
                    })
                    return ConstVal.Cblue + " plan 3"
                }
                else -> {

                }
            }

        }

        if (message!!.contains(ConstVal.Cgreen)) {
            when (Objectlist.planN) {
                3 -> {
                    A?.runOnUiThread(Runnable {
                        SlaveObjectlist.cb?.setBackgroundColor(Color.GREEN);
                        SlaveObjectlist.cb?.tag = "Color Green";
                    })
                    return ConstVal.Cblue + " plan 3"
                }
                else -> {

                }
            }

        }

        if (message!!.contains(ConstVal.CwhitePC)) {
            val index =message!!.split('+')[1].toInt();
            A?.runOnUiThread(Runnable {
                SlaveObjectlist.cb?.setBackgroundColor(Color.WHITE);
                SlaveVal.bool = false;
                //SlaveObjectlist.cb?.tag="Color white";
            })
            write(("StartM+" +ConstVal.SwhiteP + index.toString()+ "+ENDM").toByteArray());
            if (logEnable) Log.e(TAG, "send "  + ConstVal.SwhiteP + index.toString())
            return ConstVal.CwhitePC
        }

        if (message!!.contains(ConstVal.Cwhite)) {
            val index =message!!.split('+')[1].toInt();
            A?.runOnUiThread(Runnable {
                SlaveObjectlist.cb?.setBackgroundColor(Color.WHITE);
                SlaveVal.bool = false;
                //SlaveObjectlist.cb?.tag="Color white";
            })
            return ConstVal.Cwhite
        }

        if (message!!.contains(ConstVal.end)) {
            var c =message!!.split("+")[1]
            A?.runOnUiThread(Runnable {
                when (c) {
                    "r" -> SlaveObjectlist.cb?.setBackgroundColor(Color.RED);
                    "b" -> SlaveObjectlist.cb?.setBackgroundColor(Color.BLUE);
                    "w" -> SlaveObjectlist.cb?.setBackgroundColor(Color.WHITE);
                    "g" -> SlaveObjectlist.cb?.setBackgroundColor(Color.GREEN);
                    "y"-> SlaveObjectlist.cb?.setBackgroundColor(Color.YELLOW);
                    else -> SlaveObjectlist.cb?.setBackgroundColor(Color.WHITE);
                }
            })
            if (logEnable) Log.e(TAG, c)
            return  ConstVal.end
        }
        return "none "
    }

    fun runplan2(Buffer: String) {

        val c = Buffer.split('+')[2].toString()
        if (Plan2firstrecive==true) {
            Plan2firstrecive = false;
            var tSize = Buffer.split('+').size;
            var ctime = Buffer.split('+')[3].toLong()
            if (logEnable) Log.e(
                TAG,
                "ctime " + c.toString() + " " + ctime.toString() + " timeresponse " + (timeresponse!!).toString()
            );


            if (ctime > (timeresponse!!)) {
                timeresponse = ctime;
                val sec: Double? =
                    ((Calendar.getInstance().timeInMillis.toDouble() - M?.get(mmSocket.remoteDevice.address.toString())
                        ?.toDouble()!!) / 1000);
                if (logEnable) Log.e(
                    TAG,
                    "in timeresponse " + c.toString() + " " + ctime.toString() + " timeresponse " + timeresponse.toString()
                );
                time?.sumtime(sec!!);

                A?.runOnUiThread(Runnable {
                    StartObjectlist.listoftTableRow[IndexClient!!]?.setBackgroundColor(Color.WHITE);
                    val t = StartObjectlist.listoftTableRow[IndexClient!!]?.tag.toString();
                    listoftofResult.add("Schecked;" + t + ";" + sec + ";" + Calendar.getInstance().time.toString() + ";" + c)
                    StartObjectlist.list?.add(" נגיעה ב- " + t + " אחרי " + sec.toString());
                    StartObjectlist.adapter?.notifyDataSetChanged()
                })
                Thread {
                    for (i in 0 until size!!) {
                        (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[i] as BluetoothSocket) as MyBluetoothService)?.write(
                            ("StartM+" +ConstVal.slavebool+ "+ENDM").toByteArray()
                        );
                        if (logEnable) Log.e(TAG, "send " + ConstVal.slavebool + i)
                    }
                    for (i in 0 until size!!) {
                        A?.runOnUiThread(Runnable {
                            StartObjectlist.tbl?.getChildAt(i)
                                ?.setBackgroundColor(Color.WHITE);
                        })
                        (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[i] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CwhitePC + i.toString()+ "+ENDM").toByteArray())
                        if (logEnable) Log.e(TAG, "send " + ConstVal.CwhitePC + i.toString())
                    }

                    var index=0
                    loop@ while (GeneralVal.cReady!! < Oblist.listofbluetoothsocket.size) {
                        Thread.sleep(100)
                        index.plus(1);
                        if (index==30) {
                            for (i in 0 until size!!) {
                                (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[i] as BluetoothSocket) as MyBluetoothService)?.write(
                                    ("StartM+" + ConstVal.Cwhite + "+ENDM").toByteArray()
                                );
                                if (logEnable) Log.e(TAG, "send " + ConstVal.Cwhite + " " + i)

                            };
                            break@loop
                        }
                    }
                    GeneralVal.cReady = 0;
                    Thread.sleep(1500)
                    val r = (0..(size?.minus(1)!!)).shuffled().take(2).toSet();
                    A?.runOnUiThread(Runnable {
                        StartObjectlist.tbl?.getChildAt(r.elementAt(0))
                            ?.setBackgroundColor(Color.RED);
                        StartObjectlist.tbl?.getChildAt(r.elementAt(1))
                            ?.setBackgroundColor(Color.BLUE);
                    })
                    Plan2firstrecive = true;
                    (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r.elementAt(0)] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CredP + r.elementAt(0).toString()+ "+ENDM").toByteArray());
                    if (logEnable) Log.e(TAG, ConstVal.CredP + r.elementAt(0).toString())
                    (Objectlist.MBSArray?.get(Oblist.listofbluetoothsocket[r.elementAt(1)] as BluetoothSocket) as MyBluetoothService)?.write(("StartM+" +ConstVal.CblueP + r.elementAt(1).toString()+ "+ENDM").toByteArray());
                    if (logEnable) Log.e(TAG, ConstVal.CblueP + r.elementAt(1).toString())
                }.start();
            } else {
                Plan2firstrecive = true;
                if (logEnable) Log.e(
                    TAG,
                    "out timeresponse " + c.toString() + " " + ctime.toString() + " timeresponse " + timeresponse!!.toString()
               );
            }
        }
        else
        {
            Plan2firstrecive = true;
            if (logEnable) Log.e(
                TAG,
                "out Plan2firstrecive " + c.toString() + " " + Plan2firstrecive!!.toString())
        }
        sharedCounterLock.release();

    }

    // Call this from the main activity to send data to the remote device.
    fun write(bytes: ByteArray) {
        try {
            if (logEnable) Log.e(TAG, "mmOutStream.write(bytes)");
           // Thread {
                mmOutStream.write(bytes)
                mmOutStream.flush();
          //  }.start()
        } catch (e: IOException) {
            if (logEnable) Log.e(TAG, "Error occurred when sending data", e)
            Objectlist.MBSArray?.remove(mmSocket);
            return
        }
    }

    // Call this method from the main activity to shut down the connection.
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
            if (logEnable) Log.e(TAG, "Could not close the connect socket", e)
        }
    }

    fun addtesttolist(s: String) {
        A?.runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
            Slist.list.add(s);
            Slist.adapter?.notifyDataSetChanged();
        })
    }

    fun addtesttotext(s: String) {
        A?.runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.

        })
    }


}
//}