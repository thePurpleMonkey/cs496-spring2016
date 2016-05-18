package com.best_slopes.bestslopes;

import android.view.ViewDebug;

import com.best_slopes.bestslopes.Trail;
import com.best_slopes.bestslopes.Constants;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.best_slopes.bestslopes.http.*;

public class Uploader {
	private static final String BASE_URL = Constants.BASE_URL;
	private static final String TRACKER_URL = BASE_URL + "/track_trails";
	private static final String AUTH_URL = BASE_URL + "/authentication";
	private static final String CHARSET = "UTF-8";

	public static boolean upload(Trail trail) throws Exception {
//		JSONObject regJson = registerUser(pet.getUsername(), pet.getPassword());
//		boolean regSuccess = regJson.getBoolean("success");
//		String token = regJson.getString("info");
		JSONObject trailJson = uploadTrail(trail);

/*
		if (regSuccess && token != null && token.length() > 0) {
			boolean regConfirmed = validateToken(token);

			if (regConfirmed) {
				JSONObject petJson = uploadPet(pet, token);
				if (petJson.getBoolean("success"))
					return true;
			}
		}
*/

		return false;
	}

	private static JSONObject uploadTrail(Trail trail)
			throws IOException, JSONException {
		HttpUpload createTrail = new HttpUpload(TRACKER_URL, CHARSET);
		//TODO: 10 should be the unique id of trail
		createTrail.addFormField("id", Long.toString((long)trail.getId()));
		createTrail.addFormField("owner_id", "10");
		createTrail.addFormField("title", trail.getName());
		createTrail.addFormField("rating", Float.toString(trail.getRating()));
		createTrail.addFormField("comment", "Test comment");
		String trailResult = createTrail.finish();
		JSONObject trailJson = new JSONObject(trailResult);
		return trailJson;
	}

	private static boolean validateToken(String token) throws Exception,
			JSONException, InterruptedException {
		int retries = 10;
		boolean regConfirmed = false;
		while (retries > 0 && !regConfirmed) {
			// new registrations might not immediately become valid; so wait
			HttpGet validate = new HttpGet(AUTH_URL, CHARSET);
			validate.addFormField("op", "validate");
			validate.addFormField("token", token);
			String valResult = validate.finish();
			JSONObject valJson = new JSONObject(valResult);

			if (valJson.getBoolean("success"))
				regConfirmed = true;
			else
				Thread.sleep(5000);
		}
		return regConfirmed;
	}

/*
	private static JSONObject registerUser(String username, String password)
			throws Exception, JSONException {
		HttpPost register = new HttpPost(USER_URL, CHARSET);
		register.addFormField("op", "create");
		register.addFormField("username", username);
		register.addFormField("password", password);
		String result = register.finish();
		String regResult = result;
		// System.err.println(registrationResult);

		JSONObject regJson = new JSONObject(regResult);
		return regJson;
	}
*/
}
