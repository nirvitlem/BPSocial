package com.example.bpsocial

import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.bpsocial.Objectlist.mbs
import com.example.bpsocial.Objectlist.ct


object Objectlist {
    @JvmStatic public var ct:ConnectThread?=null;
    @JvmStatic public var at: AcceptThread?=null;
    @JvmStatic public var mbs: MyBluetoothService?=null;

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