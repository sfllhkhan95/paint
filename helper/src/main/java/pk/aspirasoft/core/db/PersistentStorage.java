package pk.aspirasoft.core.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * PersistentStorage defines and provides a local storage space for saving application's persistent
 * data which is preserved across application runs.
 *
 * @author saifkhichi96
 */
public class PersistentStorage {

    private static SharedPreferences preferences;

    private PersistentStorage() {

    }

    public static void init(Context context, String key) {
        preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
    }

    static SharedPreferences getPreferences() {
        if (preferences == null) {
            throw new IllegalStateException("Storage not properly configured. Call PersistentStorage.init() before first use.");
        }

        return preferences;
    }

    public static <T> T get(String key, Class<T> type) {
        Gson gson = new Gson();
        String json = preferences.getString(key, "");
        return type.cast(gson.fromJson(json, type));
    }

    public static <T> T getGeneric(String key, Type type) {
        Gson gson = new Gson();
        String json = preferences.getString(key, "");
        return (T) gson.fromJson(json, type);
    }

    public static void put(String key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        editor.putString(key, gson.toJson(value));
        editor.apply();
    }

}