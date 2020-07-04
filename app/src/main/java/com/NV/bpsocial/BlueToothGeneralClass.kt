package com.NV.bpsocial

import android.bluetooth.BluetoothSocket
import com.NV.bpsocial.Objectlist.mbs
import com.NV.bpsocial.Objectlist.ct


object Objectlist {
    @JvmStatic public var ct:ConnectThread?=null;
    @JvmStatic public var at: AcceptThread?=null;
    @JvmStatic public var mbs: MyBluetoothService?=null;
    @JvmField public var planN : Int? =0;
    @JvmField public var M: HashMap<String, Long>? = HashMap<String, Long>();
    @JvmField public var MBSArray: HashMap<BluetoothSocket, MyBluetoothService>? = HashMap<BluetoothSocket, MyBluetoothService>();

    //...
}

class BlueToothGeneralClass {


    public fun setCAobject(CT : ConnectThread)
    {
        ct=CT;
    }

    public fun setaATobject(AT : AcceptThread)
    {
        at=AT;
    }

    public fun setMBSobject(MBS : MyBluetoothService)
    {
        mbs=MBS;
    }

}