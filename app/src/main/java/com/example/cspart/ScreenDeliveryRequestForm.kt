package com.example.cspart

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zebra.adc.decoder.Barcode2DWithSoft
import com.zebra.adc.decoder.Barcode2DWithSoft.ScanCallback
import java.io.UnsupportedEncodingException


class ScreenDeliveryRequestForm : AppCompatActivity() {

    var barCode = "";
    var barcode2DWithSoft: Barcode2DWithSoft? = null
    var seldata = "ASCII"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_delivery_request_form)
        barcode2DWithSoft = Barcode2DWithSoft.getInstance()

    }

    var scanBack = ScanCallback { i, length, bytes ->
        fun onScanComplete(){
            if (length < 1) {

            } else {
                barCode = ""
                //  String res = new String(dd,"gb2312");
                try {
                    barCode = String(bytes)
                } catch (ex: UnsupportedEncodingException) {
                }
            }
        }
    }



    private fun ScanBarcode() {
        if (barcode2DWithSoft != null) {
            barcode2DWithSoft!!.scan()
            barcode2DWithSoft!!.setScanCallback(scanBack)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 66 || keyCode == 293) {
            if (event.repeatCount == 0) {
                ScanBarcode()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 293) {
            if (event.repeatCount == 0) {
                barcode2DWithSoft!!.stopScan()
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }


}