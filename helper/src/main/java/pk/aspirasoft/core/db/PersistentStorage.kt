package pk.aspirasoft.core.db

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson

import java.lang.reflect.Type

/**
 * PersistentStorage defines and provides a local storage space for saving application's persistent
 * data which is preserved across application runs.
 *
 * @author saifkhichi96
 */
object PersistentStorage {

    private var preferences: SharedPreferences? = null

    fun init(context: Context, key: String) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
    }

    operator fun <T> get(key: String, type: Class<T>): T? {
        val gson = Gson()
        val json = preferences!!.getString(key, "")
        return type.cast(gson.fromJson(json, type))
    }

    fun <T> getGeneric(key: String, type: Type): T {
        val gson = Gson()
        val json = preferences!!.getString(key, "")
        return gson.fromJson<Any>(json, type) as T
    }

    fun put(key: String, value: Any) {
        val editor = preferences!!.edit()
        val gson = Gson()
        editor.putString(key, gson.toJson(value))
        editor.apply()
    }

}