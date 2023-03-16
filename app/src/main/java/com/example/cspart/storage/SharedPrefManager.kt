package com.example.cspart.storage

import android.content.Context
import com.example.cspart.models.User

class SharedPrefManager private constructor(private val mCtx: Context) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt("id", -1) != -1
        }

    val user: User
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return User(
                sharedPreferences.getInt("id", -1),
                sharedPreferences.getString("code",""),
                sharedPreferences.getString("fullName", ""),
                sharedPreferences.getInt("roleRequestImport", 0),
                sharedPreferences.getInt("roleRequestExport", 0),
            )
        }


    fun saveUser(user: User) {

        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("id", user.id)
        editor.putString("code", user.code)
        editor.putString("fullName", user.fullName)
        editor.putInt("roleRequestImport", user.roleRequestImport!!)
        editor.putInt("roleRequestExport", user.roleRequestExport!!)
        editor.apply()
    }

    fun getUserName(): String? {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val userNameCode = sharedPreferences.getString("code","");
        return userNameCode
    }


    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private val SHARED_PREF_NAME = "my_shared_preff"
        private var mInstance: SharedPrefManager? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

}