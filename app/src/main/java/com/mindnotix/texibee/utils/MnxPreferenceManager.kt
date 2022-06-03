package com.mindnotix.texibee.utils

import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import org.json.JSONException

object MnxPreferenceManager {
    private var sharedPreferences: SharedPreferences? = null

    /**
     * Initialize preference manager.
     *
     * @param preferences the preferences
     */
    fun initializePreferenceManager(preferences: SharedPreferences?) {
        sharedPreferences = preferences
    }

    /**
     * Gets boolean.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return value store in key
     */
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(key, defaultValue)
    }

    /**
     * Sets boolean.
     *
     * @param key   the key
     * @param value the value
     */
    fun setBoolean(key: String?, value: Boolean) {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * Gets long.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return value store in key
     */
    fun getLong(key: String?, defaultValue: Long): Long {
        return sharedPreferences!!.getLong(key, defaultValue)
    }

    /**
     * Sets long.
     *
     * @param key   the key
     * @param value the value
     */
    fun setLong(key: String?, value: Long) {
        val editor = sharedPreferences!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }
    /**
     * Gets user id.
     *
     * @return user id
     */
    /**
     * Gets string.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return string string
     */
    fun getString(key: String?, defaultValue: String?): String? {
        return sharedPreferences!!.getString(key, defaultValue)
    }

    /**
     * Sets string.
     *
     * @param key   the key
     * @param value the value
     */
    fun setString(key: String?, value: String?) {
        val editor = sharedPreferences!!.edit()
        if (value == null) editor.putString(key, "").apply() else editor.putString(key, value)
            .apply()
    }

    /**
     * Gets integer.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return integer integer
     */
    fun getInteger(key: String?, defaultValue: Int): Int {
        return sharedPreferences!!.getInt(key, defaultValue)
    }

    /**
     * Sets integer.
     *
     * @param key   the key
     * @param value the value
     */
    fun setInteger(key: String?, value: Int) {
        val editor = sharedPreferences!!.edit()
        editor.putInt(key, value).apply()
    }

    /**
     * Sets json.
     *
     * @param prefKey the pref key
     * @param key     the key
     * @param value   the value
     */
    fun setJson(prefKey: String?, key: String?, value: String?) {
        val preValue = getString(prefKey, "{}")
        try {
            val jsonObject = JSONObject(preValue)
            jsonObject.put(key, value)
            setString(prefKey, jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    val accessToken: String?
        get() = null

    /**
     * Gets json.
     *
     * @param prefKey the pref key
     * @param key     the key
     * @return the json
     */
    fun getJson(prefKey: String?, key: String?): String {
        var postValue = ""
        val preValue = getString(prefKey, "{}")
        try {
            val jsonObject = JSONObject(preValue)
            if (jsonObject.has(key)) postValue = jsonObject.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return postValue
    }

    /**
     * to clear all the values stored in shared proference
     */
    fun clearShdPref() {
        Log.d("clearShdPref", "clearShdPref: ")
        sharedPreferences!!.edit().clear().apply()
    }

    fun getArrayList(prefKey: String?, key: String?): String {
        var postValue = ""
        val preValue = getString(prefKey, "{}")
        try {
            val jsonObject = JSONObject(preValue)
            if (jsonObject.has(key)) postValue = jsonObject.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return postValue
    }

    fun setArrayList(key: String?, value: String?) {
        val editor = sharedPreferences!!.edit()
        if (value == null) editor.putString(key, "").apply() else editor.putString(key, value)
            .apply()
    }

    fun clearAllPreferences() {
        val editor = sharedPreferences!!.edit()
        editor.clear().apply()
    }
}