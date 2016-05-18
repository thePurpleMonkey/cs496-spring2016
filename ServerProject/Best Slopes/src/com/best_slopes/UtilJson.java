package com.best_slopes;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UtilJson {

	public static String toJsonPair(String key, String vl) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put(key, vl);

		return toJsonObject(obj);
	}

	public static String toJsonObject(HashMap<String, String> obj) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.toJson(obj);
	}

}
