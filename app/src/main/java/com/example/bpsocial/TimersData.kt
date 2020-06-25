package com.example.bpsocial

import com.example.bpsocial.TimersDataVal.time
import com.example.bpsocial.TimersDataVal.totaltime
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit

object TimersDataVal {
    @JvmField
    var time: Long ?= 0;
    @JvmField
    var totaltime: Double ?= 0.0;
    //...
}


class TimersData {
    public var T : Long?= 0;
    public var eT : Long?= 0;


    public fun startTime() {
        T = System.currentTimeMillis()
    }

    public fun StopTime() {
        eT = System.currentTimeMillis()
        time = TimeUnit.MILLISECONDS.toSeconds(eT!! -T!!);
    }

    public fun getTime(): Long? {
        return time;
    }

    public fun sumtime(t : Double)
    {
        totaltime=+t;
    }
}