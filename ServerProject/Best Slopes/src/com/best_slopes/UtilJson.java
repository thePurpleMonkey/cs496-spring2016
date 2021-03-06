package com.best_slopes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.common.io.BaseEncoding;;

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

	public static String hash(String salt, String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes("UTF-8"));
			md.update(message.getBytes("UTF-8"));
			byte[] digest = md.digest();
			return BaseEncoding.base64().encode(digest);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private static SecureRandom _random;

	private static SecureRandom getSecureRandom() {
		if (_random == null)
			_random = new SecureRandom();
		return _random;
	}

	public static long getRandom() {
		long rv = getSecureRandom().nextLong();
		return Math.abs(rv) / 1000; // JS has trouble with really long numbers
	}

	public static long getLong(HttpServletRequest req, String key) {
		try {
			String vl = req.getParameter(key);
			if (vl != null)
				return Long.parseLong(vl);
		} catch (NumberFormatException nfe) {

		}
		return 0;
	}

	public static String toJson(User user) {
		// new Gson().toJson(user) would include the hashpassword,
		// which we don't want; so we'd either have to customize
		// the JSON-generation using filters or metadata, or
		// (much simpler) just grab the fields we actually want to send

		HashMap<String, Object> obj = new HashMap<String, Object>();
		obj.put("username", user.getUsername());
		obj.put("session", user.getSession());
		return new Gson().toJson(obj);
	}
}
