package com.example.bpsocial

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.IOException


private const val TAG = "BPSocial Server"
private var A : Activity ?= null;

class AcceptThread(bluetoothAdapter : BluetoothAdapter) : Thread() {

    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("BPSocial", SVal.M_UUID)
    }

    public fun getconextintent(a : Activity)
    {
        A=a;
    }

    override fun run() {
        // Keep listening until exception occurs or a socket is returned.
        Log.e("BPSocial Server SVal.M_UUID" , SVal.M_UUID.toString());
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            socket?.also {
                A?.runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    Slist.list.add(socket.remoteDevice.name);
                    Slist.adapter?.notifyDataSetChanged();
                })
                //manageMyConnectedSocket(it)
                Log.e(TAG, "Socket's accept() ");
                Thread({
                    val mbs: MyBluetoothService? = MyBluetoothService(socket);
                    mbs?.run();
                }).start();
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }
    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            mmServerSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
    }
}