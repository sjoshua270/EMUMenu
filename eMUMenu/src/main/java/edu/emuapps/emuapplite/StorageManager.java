package edu.emuapps.emuapplite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class StorageManager {

	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	@SuppressLint("CommitPrefEdits")
	public StorageManager(Context ctxt) {

		//Shared preferences cached
		prefs = ctxt.getSharedPreferences("EMU Menu App", 0);
		//Preferences editor
		editor = prefs.edit();
	}

	public String getString(String name) {
		return prefs.getString(name, "");
	}

	public boolean putString(String name, String value) {
		editor.putString(name, value);
		return editor.commit();
	}

	public Long getLong(String key) {
		return prefs.getLong(key, 0);
	}

	public boolean putLong(String key, Long value) {
		editor.putLong(key, value);
		return editor.commit();
	}

	public List<String> getArray(String arrayName) {
		int size = prefs.getInt(arrayName + "_size", 0);
		ArrayList<String> array = new ArrayList<String>();
		for (int i = 0; i < size; i++)
			array.add(prefs.getString(arrayName + "_" + i, null));
		return array;
	}

	public boolean putArray(List<String> array, String arrayName) {
		editor.putInt(arrayName + "_size", array.size());
		for (int i = 0; i < array.size(); i++)
			editor.putString(arrayName + "_" + i, array.get(i));
		return editor.commit();
	}

}
