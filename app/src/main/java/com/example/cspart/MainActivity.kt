package com.example.cspart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.cspart.api.RetrofitClient
import com.example.cspart.models.LoginResponse
import com.example.cspart.models.UserRequest
import com.example.cspart.storage.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionbar = supportActionBar
        actionbar!!.hide()
        val PREFS_NAME = "Setting"
        val settings = applicationContext.getSharedPreferences(PREFS_NAME, 0)
        val url = settings.getString("url", "")
        if (url != null) {
            RetrofitClient.changeApiBaseUrl(url)
        }
//        actionbar!!.setDisplayShowHomeEnabled(true)
//        actionbar!!.setIcon(R.drawable.pansonicimg)
        initAction()
    }

    private fun login() {
        try {
            val useName = findViewById<EditText>(R.id.editTextUser).text.toString().trim()
            val pass = findViewById<EditText>(R.id.editTextPass).text.toString().trim()

            if (useName.isEmpty()) {
                findViewById<EditText>(R.id.editTextUser).requestFocus()
                Toast.makeText(this, "Vui lòng nhập tài khoản", Toast.LENGTH_SHORT).show()
                return
            }

            if (pass.isEmpty()) {
                findViewById<EditText>(R.id.editTextPass).requestFocus()
                Toast.makeText(this, "Vui lòng nhập mạt khẩu", Toast.LENGTH_SHORT).show()
                return
            }

            val user = UserRequest(useName, pass)

            RetrofitClient.instance.doLogin(user)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.body()!!.statusCode == 200) {

                            SharedPrefManager.getInstance(applicationContext)
                                .saveUser(response.body()?.data!!)

                            val intent = Intent(applicationContext, HomeScreen::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext,
                                response.body()?.message,
                                Toast.LENGTH_LONG).show()
                        }

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun initAction() {
        val btnLogin = findViewById<Button>(R.id.btnLogin);
        btnLogin.setOnClickListener {
            login()
        }
        val btnSetting = findViewById<Button>(R.id.btnSetting)
        btnSetting.setOnClickListener { moveToScreenSetting() }
    }

    private fun moveToScreenSetting() {
        val intent = Intent(applicationContext, ScreenSetting::class.java)
        startActivity(intent)
    }
}