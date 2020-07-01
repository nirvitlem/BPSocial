package com.NV.bpsocial

import com.NV.bpsocial.Objectlist.mbs
import com.NV.bpsocial.Objectlist.ct


object Objectlist {
    @JvmStatic public var ct:ConnectThread?=null;
    @JvmStatic public var at: AcceptThread?=null;
    @JvmStatic public var mbs: MyBluetoothService?=null;
    @JvmField public var planN : Int? =0;

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