package org.tahoe.lafs.extension

import android.content.SharedPreferences

inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T {
    return when (T::class) {
        Boolean::class -> getBoolean(key, defaultValue as? Boolean? ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float? ?: 0.0f) as T
        Int::class -> getInt(key, defaultValue as? Int? ?: 0) as T
        Long::class -> getLong(key, defaultValue as? Long? ?: 0L) as T
        String::class -> getString(key, defaultValue as? String? ?: "") as T
        else -> throw IllegalArgumentException("Unable to get shared preference with value type")
    }
}

inline fun <reified T> SharedPreferences.set(key: String, value: T) {
    val editor = edit()
    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> throw IllegalArgumentException("This type can't be stored in shared preferences")
    }
    editor.apply()
}

fun SharedPreferences.addWithSeparator(key: String, value: String) {
    var prefValues = getString(key, "")
    prefValues = if (!prefValues.isNullOrEmpty()) {
        "$prefValues,$value"
    } else {
        value
    }
    val editor = edit()
    editor.putString(key, prefValues)
    editor.apply()
}

fun SharedPreferences.commit() = edit().commit()

fun SharedPreferences.remove(key: String) = edit().remove(key).apply()

fun SharedPreferences.clear() = edit().clear().apply()
