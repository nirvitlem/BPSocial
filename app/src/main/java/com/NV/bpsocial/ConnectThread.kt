package com.NV.bpsocial

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.lang.Exception

private const val TAG = "BPSocial Client"
public var sname : String ?= "";

class ConnectThread(device: BluetoothDevice) : Thread() {

    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(SVal.M_UUID);
    }

    public fun setname(Sname : String)
    {
        sname=Sname;
    }

    public override fun run() {
        try {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()
            Log.e("BPSocial Client SVal.M_UUID", SVal.M_UUID.toString());
            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                while (mmSocket!!.isConnected) {

                }
                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                // manageMyConnectedSocket(socket)
            }
        } catch (e: Exception) {

        }
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }

    fun getsocket() : BluetoothSocket?
    {
        if (mmSocket?.isConnected==true) return mmSocket!!;
       return  null;
    }
}