package com.NV.bpsocial

import java.util.concurrent.Semaphore

object ConstVal {
    const val Schecked="Schecked";
    const val ScheckedP="Schecked+";
    const val checked="checked";
    const val black="black"
    const val Cblack="Cblack"
    const val CblackP="Cblack+"
    const val red="red"
    const val Cred="Cred"
    const val CredP="Cred+"
    const val Sred="Sred"
    const val SredP="Sred+"
    const val blue="blue"
    const val Cblue="Cblue"
    const val CblueP="Cblue+"
    const val Sblue="Sblue"
    const val SblueP="Sblue+"
    const val green="green"
    const val Cgreen="Cgreen"
    const val CgreenP="Cgreen+"
    const val Sgreen="Sgreen"
    const val SgreenP="Sgreen+"
    const val yellow="yellow"
    const val Cyellow="Cyellow"
    const val CyellowP="Cyellow+"
    const val Syellow="Syellow"
    const val SyellowP="Syellow+"
    const val white="white"
    const val Cwhite="Cwhite"
    const val CwhiteP="Cwhite+"
    const val CwhitePC="CwhitePC+"
    const val Swhite="Swhite"
    const val SwhiteP="Swhite+"
    const val Smistake="Smistake"
    const val SmistakeP="Smistake+"
    const val end="end"
    const val plan="plan"
    const val GPS="GPS+"
    const val SGEO="SGEO"
    const val slavebool = "slavebool"

    const val bufferSize = 50;
    const val logEnable = false;
    //...
}

object GeneralVal
{
    @Volatile var timeresponse : Long ?= 0 ;
    @Volatile var cReady : Int ?=0;
    @Volatile var Plan2firstrecive :Boolean ?= true;
    @Volatile var tempMessage : String ?="";
    @Volatile var latitude :  Double ?= 0.0 ;
    @Volatile var longitude :  Double ?= 0.0 ;
}

object GeneralObj
{
    @Volatile  var sharedCounterLock = Semaphore(1)

}

class Constants {
}