package livefyre.parsers;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import livefyre.AppSingleton;
import livefyre.LFSAppConstants;
import livefyre.LivefyreApplication;


public class AdminClintParser {

	private JSONObject jsonResponseObject;
	private LivefyreApplication application;
	public static String adminId="No";

	public AdminClintParser(JSONObject jsonResponseObject) {
		this.jsonResponseObject = jsonResponseObject;

		application = AppSingleton.getInstance().getApplication();
	}

	public void storeDataInSpFromResponce() throws JSONException {

		if (jsonResponseObject.get("status").equals("ok")) {

			JSONObject data = jsonResponseObject.getJSONObject("data");
//			if (!data.isNull("isModAnywhere"))
//				Log.d("isModAnywhere", data.getString("isModAnywhere"));
//			if (!data.isNull("collection_id"))
//				Log.d("collection_id", data.getString("collection_id"));

			if (!data.isNull("profile")) {
				JSONObject profile = data.getJSONObject("profile");
//				if (!profile.isNull("collection_id"))
//					Log.d("profileUrl", profile.getString("profileUrl"));
//				if (!profile.isNull("settingsUrl"))
//					Log.d("settingsUrl", profile.getString("settingsUrl"));
//				if (!profile.isNull("displayName"))
//					Log.d("displayName", profile.getString("displayName"));
//				if (!profile.isNull("avatar"))
//					Log.d("avatar", profile.getString("avatar"));
//				
				
				
				if (!profile.isNull("id")) {
					application.saveDataInSharedPreferences(LFSAppConstants.ID,
							profile.getString("id"));
					adminId=profile.getString("id");
					Log.d("id", profile.getString("id"));
				}
				
				
				

			}
//			if (!data.isNull("profile")) {
//				JSONObject authToken = data.getJSONObject("auth_token");
//				if (!authToken.isNull("value"))
//					Log.d("authTokenValue", authToken.getString("value"));
//				if (!authToken.isNull("ttl"))
//					Log.d("authTokenTtl", authToken.getString("ttl"));
//
//				JSONObject token = data.getJSONObject("token");
//				if (!token.isNull("value"))
//					Log.d("tokenValue", token.getString("value"));
//				if (!authToken.isNull("ttl"))
//					Log.d("tokenTtl", token.getString("ttl"));
//			}
			// JSONObject modScopes = data.getJSONObject("modScopes");

		}

	}

}
