package com.example.cspart

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.cspart.api.RetrofitClient


class ScreenSetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_setting)
        initAction()
        var edtIP: EditText = findViewById<EditText>(R.id.edtIPConfig)
        val PREFS_NAME = "Setting"
        val settings = applicationContext.getSharedPreferences(PREFS_NAME, 0)
        val urlBase = settings.getString("url", "")
        edtIP.setText(urlBase).toString()
    }

    private fun initAction() {
        val btnSaveSetting = findViewById<Button>(R.id.btnSaveSetting)
        btnSaveSetting.setOnClickListener { saveSetting() }
    }

    private fun saveSetting() {
        val PREFS_NAME = "Setting"
        val settings = applicationContext.getSharedPreferences(PREFS_NAME, 0)
        val editor = settings.edit()
        var edtIP: EditText = findViewById<EditText>(R.id.edtIPConfig)
        var url = edtIP.text.toString()
        if (url == null) {
            url = "";
        }
        editor.putString("url", url);
        editor.apply()

        RetrofitClient.changeApiBaseUrl(url)

        val urlBase = settings.getString("url", "")
        println(urlBase)
        onBackPressed()
    }
}

