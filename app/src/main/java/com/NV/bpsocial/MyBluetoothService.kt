package com.NV.bpsocial

import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.graphics.Color
import android.util.Log
import com.NV.bpsocial.ConstVal.bufferSize
import com.NV.bpsocial.ConstVal.logEnable
import com.NV.bpsocial.GeneralObj.sharedCounterLock
import com.NV.bpsocial.GeneralVal.Plan2firstrecive
import com.NV.bpsocial.GeneralVal.timeresponse
import com.NV.bpsocial.StartObjectval.next
import com.NV.bpsocial.TimersObjectlist.listoftofResult
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
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
    private var M: HashMap<String, Long>? = HashMap<String, Long>();
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
                mmBuffer = ByteArray(bufferSize);
                mmInStream = mmSocket.inputStream
                mmInStream.read(mmBuffer);

                //break
            }
            if (numBytes>0) {
                if (logEnable) Log.e(TAG, String(mmBuffer));
                handlebuffer(mmBuffer)
               // addtesttolist(String(mmBuffer));
                mmBuffer = ByteArray(bufferSize);

            } else   mmBuffer = ByteArray(bufferSize);

        }

    }

    fun handlebuffer(Buffer : ByteArray)
    {
        var message : String ?= String(Buffer, StandardCharsets.UTF_8).replace(0.toChar().toString(), "");
        if (message!!.contains("150874"))
        {
            //mmBuffer = ByteArray(bufferSize);
            write("התקבלה הההודעה - 740815".toByteArray())
        };
        if (message!!.contains("740815"))
        {
            addtesttolist(mmSocket.remoteDevice.name + " בדיקה עברה בהצלחה ")
            //mmBuffer = ByteArray(bufferSize);
        }
        if (message!!.contains("ready")) {
            var intent = Intent(A, Slave::class.java)
            A?.startActivity(intent)
          //  mmBuffer = ByteArray(bufferSize);
        }

        //server
        if (message!!.contains(ConstVal.Schecked)) {

            if (next==true) {
                when (Objectlist.planN) {
                    0 ->
                    {
                        val sec: Double? =
                            ((Calendar.getInstance().timeInMillis.toDouble() - M?.get(mmSocket.remoteDevice.address.toString())
                                ?.toDouble()!!) / 1000);
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
                        var mbs: MyBluetoothService? =
                            MyBluetoothService(Oblist.listofbluetoothsocket[r] as BluetoothSocket);
                        //mbs?.setconextintent(A!!);
                        A?.runOnUiThread(Runnable {
                            StartObjectlist.tbl?.getChildAt(r)?.setBackgroundColor(Color.RED);
                        })
                        mbs?.write((ConstVal.CredP + r.toString()).toByteArray())
                        if (logEnable) Log.e(TAG, ConstVal.CredP + r.toString())
                        //mmBuffer = ByteArray(bufferSize);
                    };
                    1 ->
                    {
                        val sec: Double? =
                            ((Calendar.getInstance().timeInMillis.toDouble() - M?.get(mmSocket.remoteDevice.address.toString())
                                ?.toDouble()!!) / 1000);
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
                            var mbs: MyBluetoothService? =
                                MyBluetoothService(Oblist.listofbluetoothsocket[r] as BluetoothSocket);
                            //mbs?.setconextintent(A!!);
                            mbs?.write((ConstVal.CredP + r.toString()).toByteArray())
                            if (logEnable) Log.e(TAG, ConstVal.CredP + r.toString())
                            for (i in 0 until size!!) {
                                if (i != r) {
                                    mbs =  MyBluetoothService(Oblist.listofbluetoothsocket[i] as BluetoothSocket);
                                    //mbst?.setconextintent(A!!)
                                    mbs.write((ConstVal.CblueP + i.toString()).toByteArray())
                                    if (logEnable) Log.e(TAG, ConstVal.CblueP + i.toString())
                                }
                            }}.start();
                        //mmBuffer = ByteArray(bufferSize);
                    }
                    2-> {
                        Thread {
                            sharedCounterLock.acquire();
                            runplan2(message!!)
                        }.start()
                        mmBuffer = ByteArray(bufferSize);
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
            //mmBuffer = ByteArray(bufferSize);
        }

        if (message!!.contains(ConstVal.Sred)) {
            Plan2firstrecive = true;
            listoftofResult.add("SSendred; ; ;"+ Calendar.getInstance().time.toString()+";red")
            M?.put(
                mmSocket.remoteDevice.address.toString(),
                Calendar.getInstance().timeInMillis!!
            )
            IndexClient =message!!.split('+')[1].toInt();
            //mmBuffer = ByteArray(bufferSize);
        }

        if (message!!.contains(ConstVal.Sblue)) {
            Plan2firstrecive = true;
            listoftofResult.add("SSendblue; ; ;"+ Calendar.getInstance().time.toString()+";blue")
            M?.put(
                mmSocket.remoteDevice.address.toString(),
                Calendar.getInstance().timeInMillis!!
            )
            IndexClient =
                String(Buffer, StandardCharsets.UTF_8).replace(0.toChar().toString(), "")
                    .split('+')[1].toInt();
            //mmBuffer = ByteArray(bufferSize);
        }

        if (message!!.contains(ConstVal.Swhite)) {

            GeneralVal.cReady = GeneralVal.cReady!!.plus(1);
            val index =message!!.split('+')[1].toInt();
            //mmBuffer = ByteArray(bufferSize);
            if (logEnable) Log.e(TAG, "get "  + ConstVal.Swhite + index.toString())
        }

        //**********************************************************************************8
        //client

        if (message!!.contains(ConstVal.slavebool)) {
            SlaveVal.bool=false;
            //mmBuffer = ByteArray(bufferSize);
        }

        if (message!!.contains(ConstVal.plan)) {
            Objectlist.planN = message!!.split('+')[1].toInt();
            //mmBuffer = ByteArray(bufferSize);

        }

        if (message!!.contains(ConstVal.Cred)) {
            val index = message!!.split('+')[1].toInt();
            //time?.startTime();
            //mmBuffer = ByteArray(bufferSize);
            write((ConstVal.SredP + index.toString()).toByteArray());
            if (logEnable) Log.e(TAG, "send "  +ConstVal.SredP + index.toString())
            A?.runOnUiThread(Runnable {
                SlaveVal.bool = true;
                SlaveVal.index = index
                SlaveObjectlist.cb?.setBackgroundColor(Color.RED);
                SlaveObjectlist.cb?.tag="Color red";
            })

        }

        if (message!!.contains(ConstVal.Cblue)) {
            when (Objectlist.planN) {
                1 -> {
                    A?.runOnUiThread(Runnable {
                        SlaveObjectlist.cb?.setBackgroundColor(Color.BLUE);
                        SlaveObjectlist.cb?.tag = "Color blue";
                    })
                   // mmBuffer = ByteArray(bufferSize);
                }
                2->
                {
                    val index = message!!.split('+')[1].toInt();
                    //time?.startTime();
                   // mmBuffer = ByteArray(bufferSize);
                    write((ConstVal.SblueP + index.toString()).toByteArray());
                    if (logEnable) Log.e(TAG, "send "  + ConstVal.SblueP + index.toString())
                    A?.runOnUiThread(Runnable {
                        SlaveVal.bool = true;
                        SlaveVal.index = index
                        SlaveObjectlist.cb?.setBackgroundColor(Color.BLUE);
                        SlaveObjectlist.cb?.tag="Color blue";
                    })
                }
                else -> {

                }
            }
            mmBuffer = ByteArray(bufferSize);

        }

        if (message!!.contains(ConstVal.Cwhite)) {
            val index =message!!.split('+')[1].toInt();
            //time?.startTime();
            A?.runOnUiThread(Runnable {
                SlaveObjectlist.cb?.setBackgroundColor(Color.WHITE);
                SlaveVal.bool = false;
                //SlaveObjectlist.cb?.tag="Color white";
            })
           // write((ConstVal.SwhiteP + index.toString()).toByteArray());
          //  if (logEnable) Log.e(TAG, "send "  + ConstVal.SwhiteP + index.toString())
           
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
            //mmBuffer = ByteArray(bufferSize);
        }

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
                timeresponse = ctime.plus(800);
                // if (logEnable) Log.e(TAG, Calendar.getInstance().timeInMillis.toString());

                //if (logEnable) Log.e(TAG, String(Buffer));
                val sec: Double? =
                    ((Calendar.getInstance().timeInMillis.toDouble() - M?.get(mmSocket.remoteDevice.address.toString())
                        ?.toDouble()!!) / 1000);
                if (logEnable) Log.e(
                    TAG,
                    "in timeresponse " + c.toString() + " " + ctime.toString() + " timeresponse " + timeresponse.toString()
                );
                time?.sumtime(sec!!);
                // Thread.sleep(1000);
                A?.runOnUiThread(Runnable {
                    StartObjectlist.listoftTableRow[IndexClient!!]?.setBackgroundColor(Color.WHITE);
                    val t = StartObjectlist.listoftTableRow[IndexClient!!]?.tag.toString();
                    listoftofResult.add("Schecked;" + t + ";" + sec + ";" + Calendar.getInstance().time.toString() + ";" + c)
                    StartObjectlist.list?.add(" נגיעה ב- " + t + " אחרי " + sec.toString());
                    StartObjectlist.adapter?.notifyDataSetChanged()
                })
                Thread {
                    for (i in 0 until size!!) {
                        var mbst: MyBluetoothService? =
                            MyBluetoothService(Oblist.listofbluetoothsocket[i] as BluetoothSocket);
                        mbst?.write(
                            (ConstVal.slavebool).toByteArray()
                        );
                        if (logEnable) Log.e(TAG, "send " + ConstVal.slavebool + i)
                    }

                    for (i in 0 until size!!) {
                        A?.runOnUiThread(Runnable {
                            StartObjectlist.tbl?.getChildAt(i)
                                ?.setBackgroundColor(Color.WHITE);
                        })
                        var mbst: MyBluetoothService? =
                            MyBluetoothService(Oblist.listofbluetoothsocket[i] as BluetoothSocket);
                        //mbst?.setconextintent(A!!)
                        mbst?.write((ConstVal.CwhiteP + i.toString()).toByteArray())
                        if (logEnable) Log.e(TAG, "send " + ConstVal.CwhiteP + i.toString())
                    }

                //    while (GeneralVal.cReady!! < Oblist.listofbluetoothsocket.size) {
                        Thread.sleep(500)
                //    }
               //     GeneralVal.cReady = 0;

                    val r = (0..(size?.minus(1)!!)).shuffled().take(2).toSet();
                    A?.runOnUiThread(Runnable {
                        StartObjectlist.tbl?.getChildAt(r.elementAt(0))
                            ?.setBackgroundColor(Color.RED);
                        StartObjectlist.tbl?.getChildAt(r.elementAt(1))
                            ?.setBackgroundColor(Color.BLUE);
                    })
                    Plan2firstrecive = true;

                    var mbst: MyBluetoothService? =
                        MyBluetoothService(Oblist.listofbluetoothsocket[r.elementAt(0)] as BluetoothSocket);
                    mbst?.write((ConstVal.CredP + r.elementAt(0).toString()).toByteArray());
                    if (logEnable) Log.e(TAG, ConstVal.CredP + r.elementAt(0).toString())
                    var mbstt: MyBluetoothService? =
                        MyBluetoothService(Oblist.listofbluetoothsocket[r.elementAt(1)] as BluetoothSocket);
                    mbstt?.write((ConstVal.CblueP + r.elementAt(1).toString()).toByteArray());
                    if (logEnable) Log.e(TAG, ConstVal.CblueP + r.elementAt(1).toString())
                }.start();

            } else {
                if (logEnable) Log.e(
                    TAG,
                    "out timeresponse " + c.toString() + " " + ctime.toString() + " timeresponse " + timeresponse!!.toString()
               );
                // mmBuffer = ByteArray(bufferSize);
            }

        }
        else
        {
            if (logEnable) Log.e(
                TAG,
                "out Plan2firstrecive " + c.toString() + " " + Plan2firstrecive!!.toString())
        }
        sharedCounterLock.release();
        //Thread.sleep(500);


    }

    // Call this from the main activity to send data to the remote device.
    fun write(bytes: ByteArray) {
        try {
            if (logEnable) Log.e(TAG, "mmOutStream.write(bytes)");
            mmBuffer = ByteArray(bufferSize)
            mmOutStream.write(bytes)
        } catch (e: IOException) {
            if (logEnable) Log.e(TAG, "Error occurred when sending data", e)
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
            Slist.RText?.text = s;
        })
    }


}
//}