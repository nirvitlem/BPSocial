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
import kotlin.random.Random

public var bluetoothAdapter : BluetoothAdapter? = null;
public var adapter:ArrayAdapter<String>?=null;
public var list=mutableListOf("");
private val PERMISSION_REQUEST_CODE = 101;
private val TAG = "Permission";

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
            }


        val SwitchB = findViewById(R.id.switch1) as Switch
        val SButton = findViewById(R.id.SButton) as Button
        SwitchB?.isChecked=true;
        SwitchB?.text="Master";
        SButton?.text="חפש";
        SButton.setOnClickListener {
            if (SwitchB.isChecked) {
                if (GetBAdapter()) {
                    if (bluetoothAdapter?.isDiscovering() == true) {
                        bluetoothAdapter?.cancelDiscovery();
                    }
                    list.add("Start Searcing");
                    adapter?.notifyDataSetChanged()
                    bluetoothAdapter?.startDiscovery();
                }
            }
            else
            {
                if (GetBAdapter()) {
                    bluetoothAdapter?.name = "BPS " + Random.nextInt(0, 100).toString();
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 1);
                }

            }
        }

        SwitchB.setOnClickListener {
            if (SwitchB.isChecked) {
                // The switch is enabled/checked
                SwitchB.setText("Master");
                SButton.setText("חפש");
            } else {
                SwitchB.setText("End Point");
                SButton.setText("הפעל לגילוי");

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
                    if (device.name!=null) {
                        list?.add(device.name);
                        adapter?.notifyDataSetChanged()
                    }
                    val deviceHardwareAddress = device.address // MAC address
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.

                    list.add("ACTION_DISCOVERY_STARTED");
                    adapter?.notifyDataSetChanged()

                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    bluetoothAdapter?.cancelDiscovery();
                    list.add("ACTION_DISCOVERY_FINISHED");
                    adapter?.notifyDataSetChanged()
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