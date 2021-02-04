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
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.NV.bpsocial.SVal.vibpnum
import com.NV.bpsocial.Slist.adapter
import com.NV.bpsocial.Slist.list
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_start.*
import java.util.*


public var bluetoothAdapter : BluetoothAdapter? = null;
public var ct : ConnectThread?=null;
public var at : AcceptThread? = null;
public var mbs: MyBluetoothService? = null;
public var BTGObject : BlueToothGeneralClass?=null;
//public var adapter:ArrayAdapter<String>?=null;
//public var list=mutableListOf("");
lateinit var locationManager: LocationManager;


private val PERMISSION_REQUEST_CODE = 101;
private val TAG = "Permission";
object SVal {
    @JvmField val M_UUID : UUID ?=  UUID.fromString("8ce255c0-1508-74e0-ac64-0800200c9a66");
    @JvmField var vibpnum : Int ?=15;
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
private var btPBar : ProgressBar ?=null;

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
        val SwitchV = findViewById(R.id.switch2) as Switch
        val SButton = findViewById(R.id.SButton) as Button
        val GPSButton = findViewById(R.id.sendGPS) as Button
        val ConnectButton = findViewById(R.id.connectb) as Button
        val sendM = findViewById(R.id.SendM) as Button
        btPBar = findViewById<ProgressBar>(R.id.progressBar2)
        btPBar?.isEnabled=false;
        btPBar?.visibility = View.GONE;


        //getLocation();//call GPS
        SwitchB.isChecked=true;
        SwitchV.isChecked=false;
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
            android.R.layout.simple_dropdown_item_1line, list
        )

        // attach the array adapter with list view
        ListBItems.adapter = adapter
        // list view item click listener
        ListBItems.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (!SwitchB.isChecked) {
                    btPBar?.visibility = View.GONE;
                    btPBar?.isEnabled = false;
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
                    while (listofbluetoothdevices[position].bondState != BluetoothDevice.BOND_BONDED) {
                        Thread.sleep(1000);
                    }
                    //alertm("התאמה", "בוצעה התאמה בהצלחה");
                    var t: Int? = 0;
                    Thread {
                        if (ConstVal.logEnable) Log.e(
                            "ConnectThread",
                            listofbluetoothPaireddevices[0].name
                        );
                        // BTGObject?.setCAobject(ct!!);
                        Objectlist.ct = ConnectThread(listofbluetoothPaireddevices[0]);
                        Objectlist.ct?.setname(listofbluetoothPaireddevices[0]?.name);
                        Objectlist.ct?.run();

                    }.start();
                    while (Objectlist.ct?.getsocket() == null) {
                        Thread.sleep(1000);
                        t = t?.plus(1);
                        if (t == 15) {
                            break
                        };
                        //list.clear();
                        //adapter?.notifyDataSetChanged();
                    }
                    if (t!! < 15) {
                        Thread {
                            // BTGObject?.setMBSobject(mbs!!);
                            Objectlist.mbs =
                                MyBluetoothService(Objectlist.ct?.getsocket() as BluetoothSocket);
                            Objectlist.mbs?.setconextintent(this!!);
                            Objectlist.mbs?.run();
                        }.start();
                        sendM.isEnabled = true;
                        alertm("הצלחה", "הצלחה בחיבור למנהל");
                        bluetoothAdapter?.cancelDiscovery();
                    } else {
                        alertm("שגיאת חיבור", "לא מצליח להתחבר למנהל, נסה שנית");
                    }
                } else {
                    itemckickllistposition = (position - 1);
                }

            }

        sendM.isEnabled=true;
        SwitchB?.isChecked=true;
        if ( SwitchB?.isChecked) ConnectButton.text="התחל";
        SwitchB?.text="מנהל";
        SButton?.text="הפעל לגילוי";
        SButton.setOnClickListener {
            if (ConstVal.logEnable) Log.e(" SButton.setOnClickListener", "click");
            if (ConstVal.logEnable) Log.e(" SwitchB ", SwitchB.isChecked.toString());
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
                    if (ConstVal.logEnable) Log.e("GetBAdapter", "GetBAdapter");
                    btPBar?.isEnabled= true;
                    btPBar?.visibility = View.VISIBLE;

                       val ed : EditText ?= EditText(this);
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("שם")
                        builder.setMessage("הגדר שם למכשיר לזיהוי ")
                        builder.setView(ed);
                        builder.setPositiveButton("OK"){ dialog, which ->
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
                    if (ConstVal.logEnable) Log.e("bluetoothAdapter", "startDiscovery");
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
                            mbs?.write(("StartM+" + "ready" + "+ENDM").toByteArray());
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
                        Objectlist.mbs?.write(
                            ("StartM+" + "150874" + (0..100).random()
                                .toString() + "+ENDM").toByteArray()
                        );
                    }.start();
                    if (ConstVal.logEnable) Log.e("BPSocial Client send message", "150874");
                }
            }
            else {
                if (!Objectlist.at?.getlistsocket().isNullOrEmpty()) {
                    for (element in Objectlist.at?.getlistsocket() as ArrayList<BluetoothSocket>) {

                        Thread {
                            (Objectlist.MBSArray?.get(element as BluetoothSocket))?.write(("StartM+" + "150874" + (0..100).random() + "+ENDM").toByteArray());
                        }.start();
                        if (ConstVal.logEnable) Log.e("BPSocial Server send message", "150874");

                    }
                }
            }
        }

        GPSButton.setOnClickListener{
            getLocation();
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
        SwitchV.setOnClickListener {
            if (SwitchV.isChecked) {
                // The switch is enabled/checked
                if (ConstVal.logEnable) Log.e("SwitchV.isChecked", "SwitchV.isChecked");
                SwitchV.setText("חיישן פנימי");
                SlaveVal.eVib=false;

            } else {
                if (ConstVal.logEnable) Log.e("SwitchV.isnotChecked", "SwitchV.isnotChecked");
                SwitchV.setText("חיישן חיצוני");
                SlaveVal.eVib=true;

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
        alertm("התאמה", "התאמה בוצעה בהצלחה");
        return returnValue;
    }

    private fun alertm(t: String, m: String)
    {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(t)
        builder.setMessage(m)

        builder.setPositiveButton("OK"){ dialog, which ->
        }
        builder.show();
    }
    private fun getBluetoothMacAddress(bluetoothAdapter: BluetoothAdapter): String? {
       return "";

    }


    private fun  GetBAdapter():Boolean
    {
        // Get the default adapter
        if (ConstVal.logEnable) Log.e("bluetooth GetBAdapter", "GetBAdapter");
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
                builder.setPositiveButton("OK"){ dialog, which -> finishAffinity();
                }
                builder.show();
            }
        }
        return false;
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action;
            if (ConstVal.logEnable) Log.e("bluetooth Receiverr", "in");
            when(action) {

                BluetoothDevice.ACTION_FOUND -> {
                    if (ConstVal.logEnable) Log.e("bluetooth Receiverr", "ACTION_FOUND");
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device.name != null) {
                        if (ConstVal.logEnable)
                            Log.e("BluetoothDevice", device.name);
                    } else {
                        if (ConstVal.logEnable) Log.e("BluetoothDevice", "null'");
                    }
                    if (device.name != null && device.name.contains("BPS")) {
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
                    if (ConstVal.logEnable) Log.e(
                        "bluetooth Receiverr",
                        "ACTION_DISCOVERY_STARTED"
                    );


                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    // alertm("סיום חיפש ","סיום חיפוש יחידות קצה ")
                    //  bluetoothAdapter?.cancelDiscovery();
                    btPBar?.visibility  = View.GONE;
                    btPBar?.isEnabled= false;
                    if (ConstVal.logEnable) Log.e(
                        "bluetooth Receiverr",
                        "ACTION_DISCOVERY_FINISHED"
                    );

                }
            }
        }
    }

    private fun makeRequest() {
     /*   ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.BLUETOOTH_PRIVILEGED,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE
            ,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )*/
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH_PRIVILEGED,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onDestroy() {
        super.onDestroy()


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
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

    private fun getLocation() {
/*
        // alertm("GPS","latitude " + latitude.toString() + " longitude " + longitude.toString())
        locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (hasGps) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            var criteria : Criteria? =  Criteria();
            criteria!!.accuracy = Criteria.ACCURACY_COARSE ;
            criteria!!.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria!!.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            criteria!!.setBearingAccuracy(Criteria.ACCURACY_HIGH);

            locationManager.getBestProvider(criteria, true)
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                object : LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            latitude = p0!!.latitude;
                            longitude = p0!!.longitude;
                            runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                alertm(
                                    "GPS",
                                    "latitude " + p0!!.latitude.toString() + " longitude " + p0!!.longitude.toString()
                                );
                            })
                            locationManager.removeUpdates(this);
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                        TODO("Not yet implemented")
                    }

                    override fun onProviderEnabled(p0: String?) {

                    }

                    override fun onProviderDisabled(p0: String?) {

                    }

                })

        }
        else
        {
            runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
                alertm(
                    "GPS",
                    "GPS Disabled , Enabled GPS "
                );
            })
        }

*/
    }


}

