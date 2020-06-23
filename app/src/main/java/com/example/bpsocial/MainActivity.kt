package com.example.bpsocial

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bpsocial.Slist.adapter
import com.example.bpsocial.Slist.list
import java.net.NetworkInterface
import java.util.*
import kotlin.experimental.and
import kotlin.random.Random


public var bluetoothAdapter : BluetoothAdapter? = null;
public var ct : ConnectThread?=null;
public var at : AcceptThread? = null;
public var mbs: MyBluetoothService? = null;
//public var adapter:ArrayAdapter<String>?=null;
//public var list=mutableListOf("");
private var RemText : TextView ?=null;
private val PERMISSION_REQUEST_CODE = 101;
private val TAG = "Permission";
object SVal {
    @JvmField val M_UUID : UUID ?=  UUID.fromString("8ce255c0-1508-74e0-ac64-0800200c9a66");
    //...
}

object Slist {
    @JvmStatic public var list=mutableListOf("");
    @JvmStatic public var adapter:ArrayAdapter<String>?=null;
    //...
}

private var listofbluetoothdevices : ArrayList<BluetoothDevice> = ArrayList();
private var listofbluetoothPaireddevices : ArrayList<BluetoothDevice> = ArrayList();
private var itemckickllistposition : Int ?=-1;

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        makeRequest();


        val SwitchB = findViewById(R.id.switch1) as Switch
        val SButton = findViewById(R.id.SButton) as Button
        val ConnectButton = findViewById(R.id.connectb) as Button
        val sendM = findViewById(R.id.SendM) as Button

        SwitchB.isChecked=true;
         SButton.isEnabled = true;
        ConnectButton.isEnabled= false;

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
                if (!SwitchB.isChecked) {
                    val selectedItem = parent.getItemAtPosition(position)
                    if (listofbluetoothdevices[position].bondState == BluetoothDevice.BOND_BONDED) {
                        alertm("התאמה", "בוצעה התאמה בהצלחה");
                        listofbluetoothPaireddevices.add(listofbluetoothdevices[position]);
                    } else {
                        if (createBond(listofbluetoothdevices[position])) {
                            Log.e("createBond ", "true");
                            listofbluetoothPaireddevices.add(listofbluetoothdevices[position]);
                        }
                    }
                    // textViewResult.text = "Selected : $selectedItem"
                    // Log.e()
                    bluetoothAdapter?.cancelDiscovery();
                }else
                {
                    itemckickllistposition = (position-1);
                }

            }


        SwitchB?.isChecked=true;
        SwitchB?.text="Master";
        SButton?.text="הפעל לגילוי";
        SButton.setOnClickListener {
            if (SwitchB.isChecked) {
                if (GetBAdapter()) {
                    RemText?.text=getBluetoothMacAddress(bluetoothAdapter!!);
                    bluetoothAdapter?.name = "BPS Master " ;//+ Random.nextInt(0, 100).toString();
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 1);
                    Thread({
                        at = AcceptThread(bluetoothAdapter!!);
                        at?.setconextintent(this);
                        at?.run();
                    }).start();

                }
            }
            else
            {
                list.clear();
                listofbluetoothdevices.clear();

                if (GetBAdapter()) {
                    val ed : EditText ?= EditText(this);
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("שם")
                    builder.setMessage("הגדר שם למכשיר לזיהוי ")
                    builder.setView(ed);
                    builder.setPositiveButton("OK"){dialog, which ->
                        bluetoothAdapter?.name=ed?.text.toString()
                        //finishAffinity();
                    }
                    builder.show();
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

        ConnectButton.setOnClickListener {
            if (!SwitchB.isChecked) {
                Thread({
                    Log.e("ConnectThread", listofbluetoothPaireddevices[0].name);
                    ct = ConnectThread(listofbluetoothPaireddevices[0]);
                    ct?.setname(listofbluetoothPaireddevices[0]?.name);
                    ct?.run();

                }).start();
                while (ct?.getsocket()==null)
                {
                    Thread.sleep(1000);
                }
                Thread({
                    mbs = MyBluetoothService(ct?.getsocket() as BluetoothSocket);
                    mbs?.setconextintent(this!!);
                    mbs?.run();
                }).start();
            }
        }

        sendM.setOnClickListener {
            if (!SwitchB.isChecked) {
                if (ct?.getsocket() != null) {
                    Thread({
                            mbs?.write(("test").toByteArray());
                    }).start();
                    Log.e("BPSocial Client send message", "test");
                }
            }
            else
            {
                if(itemckickllistposition!=-1) {
                    Log.e("itemckickllistpositione", itemckickllistposition.toString());
                    if (at?.getsocket(itemckickllistposition!!) != null) {
                        Thread({
                            val mbs: MyBluetoothService? =
                                MyBluetoothService(at?.getsocket(itemckickllistposition!!) as BluetoothSocket);
                            mbs?.setconextintent(this!!);
                            mbs?.write(("test").toByteArray());
                        }).start();
                        Log.e("BPSocial Server send message", "test");
                    }
                }
           }
        }

        SwitchB.setOnClickListener {
            if (SwitchB.isChecked) {
                // The switch is enabled/checked
                ConnectButton.isEnabled= false;
                SwitchB.setText("Master");
                SButton.setText("הפעל לגילוי");

            } else {
                ConnectButton.isEnabled= true;
                SwitchB.setText("End Point");
                SButton.setText("חפש מנהל");
            }
        }
    }

    @Throws(Exception::class)
    fun removeBond(btDevice: BluetoothDevice?): Boolean {
        val btClass = Class.forName("android.bluetooth.BluetoothDevice")
        val removeBondMethod = btClass.getMethod("removeBond")
        val returnValue = removeBondMethod.invoke(btDevice) as Boolean
        return returnValue;
    }

    @Throws(Exception::class)
    fun createBond(btDevice: BluetoothDevice?): Boolean {
        val class1 = Class.forName("android.bluetooth.BluetoothDevice")
        val createBondMethod = class1.getMethod("createBond")
        val returnValue = createBondMethod.invoke(btDevice) as Boolean
        while (btDevice?.bondState != BluetoothDevice.BOND_BONDED)
        {
            Thread.sleep(1_000);
        }
        alertm("התאמה","התאמה בוצעה בהצלחה" );
        return returnValue;
    }

    private fun alertm(t : String , m :String)
    {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(t)
        builder.setMessage(m)

        builder.setPositiveButton("OK"){dialog, which ->

            //finishAffinity();
        }
        builder.show();
    }
    private fun getBluetoothMacAddress(bluetoothAdapter:BluetoothAdapter): String? {
        return "";

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
                builder.setTitle("Bluetooth Error")
                builder.setMessage("Please Enabled you Bluetooth device ")
                builder.setPositiveButton("OK"){dialog, which -> finishAffinity();
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
            arrayOf(Manifest.permission.BLUETOOTH_PRIVILEGED,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE),
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