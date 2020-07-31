package com.NV.bpsocial

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.NV.bpsocial.Slist.adapter
import com.NV.bpsocial.Slist.list
import com.google.android.gms.ads.AdRequest
import java.util.*
import com.google.android.gms.ads.MobileAds;
import kotlinx.android.synthetic.main.activity_main.*


public var bluetoothAdapter : BluetoothAdapter? = null;
public var ct : ConnectThread?=null;
public var at : AcceptThread? = null;
public var mbs: MyBluetoothService? = null;
public var BTGObject : BlueToothGeneralClass?=null;
//public var adapter:ArrayAdapter<String>?=null;
//public var list=mutableListOf("");

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        /*for Test var intent = Intent(this, Start::class.java)
        this.startActivity(intent)*/

        BTGObject = BlueToothGeneralClass();
        makeRequest();

        val SwitchB = findViewById(R.id.switch1) as Switch
        val SButton = findViewById(R.id.SButton) as Button
        val ConnectButton = findViewById(R.id.connectb) as Button
        val sendM = findViewById(R.id.SendM) as Button

        SwitchB.isChecked=true;
        ConnectButton.isEnabled=false;
        SButton.isEnabled = true;
        ConnectButton.isEnabled=true;

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
                if (!SwitchB.isChecked) {
                    bluetoothAdapter?.cancelDiscovery();
                    val selectedItem = parent.getItemAtPosition(position)
                    if (listofbluetoothdevices[position].bondState == BluetoothDevice.BOND_BONDED) {
                        listofbluetoothPaireddevices.add(listofbluetoothdevices[position]);
                    } else {
                        if (createBond(listofbluetoothdevices[position])) {
                            if (ConstVal.logEnable) Log.e("createBond ", "true");
                            listofbluetoothPaireddevices.add(listofbluetoothdevices[position]);
                        }
                    }
                    while (listofbluetoothdevices[position].bondState != BluetoothDevice.BOND_BONDED)
                    {
                        Thread.sleep(1000);
                    }
                    //alertm("התאמה", "בוצעה התאמה בהצלחה");
                    var t : Int?=0;
                    Thread {
                        if (ConstVal.logEnable) Log.e("ConnectThread", listofbluetoothPaireddevices[0].name);
                        // BTGObject?.setCAobject(ct!!);
                        Objectlist.ct = ConnectThread(listofbluetoothPaireddevices[0]);
                        Objectlist.ct?.setname(listofbluetoothPaireddevices[0]?.name);
                        Objectlist.ct?.run();

                    }.start();
                    while (Objectlist.ct?.getsocket()==null)
                    {
                        Thread.sleep(1000);
                        t=t?.plus(1);
                        if (t==15)
                        {
                            break
                        };
                        //list.clear();
                        //adapter?.notifyDataSetChanged();
                    }
                    if (t!!<15) {
                        Thread {
                            // BTGObject?.setMBSobject(mbs!!);
                            Objectlist.mbs = MyBluetoothService(Objectlist.ct?.getsocket() as BluetoothSocket);
                            Objectlist.mbs?.setconextintent(this!!);
                            Objectlist.mbs?.run();
                        }.start();
                        sendM.isEnabled=true;
                        alertm("הצלחה","הצלחה בחיבור למנהל");
                        bluetoothAdapter?.cancelDiscovery();
                    }
                    else
                    {
                        alertm("שגיאת חיבור","לא מצליח להתחבר למנהל, נסה שנית");
                    }
                }else
                {
                    itemckickllistposition = (position-1);
                }

            }

        sendM.isEnabled=true;
        SwitchB?.isChecked=true;
        if ( SwitchB?.isChecked) ConnectButton.text="התחל";
        SwitchB?.text="מנהל";
        SButton?.text="הפעל לגילוי";
        SButton.setOnClickListener {
            if (SwitchB.isChecked) {
                if (GetBAdapter()) {
                    bluetoothAdapter?.name = "BPS Master "
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 1);
                    Thread {
                        // BTGObject?.setaATobject(at!!);
                        Objectlist.at = AcceptThread(bluetoothAdapter!!);
                        Objectlist.at?.setconextintent(this);
                        Objectlist.at?.run();
                    }.start();

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
                    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                    pairedDevices?.forEach { device ->
                        if (device.name!=null && device.name.contains("BPS")) {
                            val deviceName = device.name
                            val deviceHardwareAddress = device.address // MAC address
                            if (device.name == null) list?.add("UnKown Device " + device.address);
                            else list?.add(device.name + " " + device.address);
                            adapter?.notifyDataSetChanged()
                            listofbluetoothdevices.add(device);
                        }
                    }
                    bluetoothAdapter?.startDiscovery();
                }
            }
        }

        ConnectButton.setOnClickListener {
           // bluetoothAdapter?.cancelDiscovery();
            if (!SwitchB.isChecked) {

            }
            else
            {
                if (!Objectlist.at?.getlistsocket().isNullOrEmpty()) {
                    for (element in Objectlist.at?.getlistsocket() as ArrayList<BluetoothSocket>) {

                        Thread {
                            val mbs: MyBluetoothService? =
                                MyBluetoothService(element as BluetoothSocket);
                            mbs?.setconextintent(this!!);
                            mbs?.write(("StartM+" +"ready"+"+ENDM").toByteArray());
                        }.start();
                        if (ConstVal.logEnable) Log.e("BPSocial Server send message", "ready");

                    }
                    var intent = Intent(this, Start::class.java)
                    this.startActivity(intent)
                }
            }
        }

        sendM.setOnClickListener {
            if (!SwitchB.isChecked) {
                if (Objectlist.ct?.getsocket() != null) {
                    Thread {
                        list.add("נשלחה הודעה למנהל 150874")
                        Objectlist.mbs?.write(("StartM+" +"150874" + (0..100).random().toString()+"+ENDM").toByteArray());
                    }.start();
                    if (ConstVal.logEnable) Log.e("BPSocial Client send message", "150874");
                }
            }
            else {
                if (!Objectlist.at?.getlistsocket().isNullOrEmpty()) {
                    for (element in Objectlist.at?.getlistsocket() as ArrayList<BluetoothSocket>) {

                        Thread {
                            (Objectlist.MBSArray?.get(element as BluetoothSocket))?.write(("StartM+" +"150874" + (0..100).random()+"+ENDM").toByteArray());
                        }.start();
                        if (ConstVal.logEnable) Log.e("BPSocial Server send message", "150874");

                    }
                }
            }
        }

        SwitchB.setOnClickListener {
            if (SwitchB.isChecked) {
                // The switch is enabled/checked
                ConnectButton.isEnabled=true;
                SwitchB.setText("מנהל");
                SButton.setText("הפעל לגילוי");
                ConnectButton.text="התחל";

            } else {
                ConnectButton.isEnabled=false;
                ConnectButton.text="התחבר";
                SwitchB.setText("יחידת קצה");
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
                    val device: BluetoothDevice =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device.name!=null && device.name.contains("BPS")) {
                        val deviceName = device.name
                        if (device.name == null) list?.add("UnKown Device " + device.address);
                        else list?.add(device.name + " " + device.address);

                        listofbluetoothdevices.add(device);
                        adapter?.notifyDataSetChanged()

                        val deviceHardwareAddress = device.address // MAC address
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.



                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                   // alertm("סיום חיפש ","סיום חיפוש יחידות קצה ")
                  //  bluetoothAdapter?.cancelDiscovery();
                }
            }
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.BLUETOOTH_PRIVILEGED,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE
            ,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
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