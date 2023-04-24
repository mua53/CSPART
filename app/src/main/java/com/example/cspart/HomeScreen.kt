package com.example.cspart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        getSupportActionBar()?.setDisplayShowTitleEnabled(true);
        getSupportActionBar()?.setTitle("  CS PART")
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setIcon(R.drawable.rtcimg);
        initAction()
    }

    /*
    Khởi tạo các hành động
    Created by tupt
     */
    fun initAction() {
        val btnDeliveryRequestForm = findViewById<Button>(R.id.btnDeliveryRequestForm)
        val btnReceipt = findViewById<Button>(R.id.btnReceipt)
        val btnDeliveryBill = findViewById<Button>(R.id.btnDeliveryBill)
        val btnReport = findViewById<Button>(R.id.btnReport)
        val btnStatusDelivery = findViewById<Button>(R.id.btnStatusDelivery)
        val btnPrintQR = findViewById<Button>(R.id.btnPrintTemp)
//        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnDeliveryRequestForm.setOnClickListener { moveScreenDeliveryRequestForm() }
        btnReceipt.setOnClickListener { moveScreenReciept() }
        btnDeliveryBill.setOnClickListener { moveScreenDeliveryBill() }
        btnReport.setOnClickListener { moveScreenReport() }
        btnStatusDelivery.setOnClickListener { moveScreenStatusDelivery() }
        btnPrintQR.setOnClickListener { moveScreenPrintTemp() }
//        btnLogout.setOnClickListener { logOut() }
    }

    /*
    Chuyển sang màn hình phiếu đề nghị xuất kho
    Created by tupt
     */
    fun moveScreenDeliveryRequestForm() {
        val intent = Intent(applicationContext, DeliveryRequestForm::class.java)
        startActivity(intent)
    }
    /* Chuyển sang màn hình phiếu nhập kho
    * Created by tupt
    * */
    fun moveScreenReciept() {
        val intent = Intent(applicationContext, ScreenInput::class.java)
        startActivity(intent)
    }
    /*
    Chuyển sang màn hình phiếu xuất kho
    Created by tupt
     */
    fun moveScreenDeliveryBill(){
        val intent = Intent(applicationContext, ScreenExportWarehouse::class.java)
        startActivity(intent)
    }
    /*
    Chuyển sang màn hình báo cáo
    Created by tupt
     */
    fun moveScreenReport() {
        val intent = Intent(applicationContext, ScreenReport::class.java)
        startActivity(intent)
    }

    fun moveScreenStatusDelivery() {
        val intent = Intent(applicationContext, ScreenStatusDelivery::class.java)
        startActivity(intent)
    }

    fun moveScreenPrintTemp() {
        val intent = Intent(applicationContext, PrintTemp::class.java)
        startActivity(intent)
    }
}