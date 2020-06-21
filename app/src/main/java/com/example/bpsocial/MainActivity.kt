package com.example.bpsocial

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.random.Random

public var bluetoothAdapter : BluetoothAdapter? = null;
public var adapter:ArrayAdapter<String>?=null;
public var list=mutableListOf("");
private var RemText : TextView ?=null;
private val PERMISSION_REQUEST_CODE = 101;
private val TAG = "Permission";
object SVal {
    @JvmField val M_UUID : UUID ?=  UUID.randomUUID();
    //...
}

private var listofbluetoothdevices : ArrayList<BluetoothDevice> = ArrayList();

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        makeRequest();

        var filter :IntentFilter  =  IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);
        RemText = findViewById(com.example.bpsocial.R.id.RemText) as android.widget.TextView;
        val ListBItems = findViewById(R.id.ListItems) as ListView;
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,list
        )
        // attach the array adapter with list view
        ListBItems.adapter = adapter
        // list view item click listener
        ListBItems.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position)
               // textViewResult.text = "Selected : $selectedItem"
                bluetoothAdapter?.cancelDiscovery();
                var ConnetServer : ConnectThread ?= ConnectThread(listofbluetoothdevices[position]);
                ConnetServer?.run();
            }


        val SwitchB = findViewById(R.id.switch1) as Switch
        val SButton = findViewById(R.id.SButton) as Button
        SwitchB?.isChecked=true;
        SwitchB?.text="Master";
        SButton?.text="הפעל לגילוי";
        SButton.setOnClickListener {
            if (SwitchB.isChecked) {
                if (GetBAdapter()) {
                    bluetoothAdapter?.name = "BPS Master " ;
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 1);
                    var AT : AcceptThread ?= AcceptThread();
                    AT?.run();
                }
            }
            else
            {
                list.clear();
                listofbluetoothdevices.clear();
                if (GetBAdapter()) {
                    if (bluetoothAdapter?.isDiscovering() == true) {
                        bluetoothAdapter?.cancelDiscovery();
                    }
                    RemText?.text ="מתחיל חיפוש מנהל";
                    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                    pairedDevices?.forEach { device ->

                        val deviceName = device.name
                        val deviceHardwareAddress = device.address // MAC address
                        if (device.name == null) list?.add("UnKown Device " + device.address);
                        else  list?.add(device.name + " " + device.address);
                        adapter?.notifyDataSetChanged()
                        listofbluetoothdevices.add(device);

                    }
                    bluetoothAdapter?.startDiscovery();
                }
            }
        }

        SwitchB.setOnClickListener {
            if (SwitchB.isChecked) {
                // The switch is enabled/checked
                SwitchB.setText("Master");
                SButton.setText("הפעל לגילוי");

            } else {
                SwitchB.setText("End Point");
                SButton.setText("חפש מנהל");
            }
        }
    }


    public fun  GetBAdapter():Boolean
    {
        // Get the default adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter?.isEnabled == true) {
                // val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                //  startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)


                return true;
            }
            else
            {
                val builder = AlertDialog.Builder(this@MainActivity)
                // Set the alert dialog title
                builder.setTitle("Bluetooth Error")
                // Display a message on alert dialog
                builder.setMessage("Please Enabled you Bluetooth device ")
                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OK"){dialog, which ->
                    // Do something when user press the positive button
                   // Toast.makeText(applicationContext,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()
                    finishAffinity();
                }
                builder.show();
            }
        }
        return false;
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    public val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action;
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    if (device.name == null) list?.add("UnKown Device " + device.address);
                    else  list?.add(device.name + " " + device.address);

                    listofbluetoothdevices.add(device);
                    adapter?.notifyDataSetChanged()

                    val deviceHardwareAddress = device.address // MAC address
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.

                    RemText?.text="התחלת חיפוש מנהל";

                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    bluetoothAdapter?.cancelDiscovery();
                    RemText?.text = "סיום חיפוש מנהל";
                }
            }
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.BLUETOOTH_PRIVILEGED,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE)
    }

    override fun onDestroy() {
        super.onDestroy()


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


}