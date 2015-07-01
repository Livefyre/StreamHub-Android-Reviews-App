package livefyre;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import livefyre.streamhub.LivefyreConfig;

public class LivefyreApplication extends Application {
	private static final int TIMEOUT_VALUE = 10000;
	private static final String LIVEFYRE = "livefyre";

	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate() {
		super.onCreate();
		LivefyreConfig.setLivefyreNetworkID(LFSConfig.NETWORK_ID);
		AppSingleton.getInstance().setApplication(this);
		init();
	}

	private void init() {
		sharedPreferences = getApplicationContext().getSharedPreferences(
				LIVEFYRE, MODE_PRIVATE);
	}

	public void saveDataInSharedPreferences(String key, String sessionId) {
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(key, sessionId);

		editor.commit();
	}

	public String getDataFromSharedPreferences(String reqString) {
		return sharedPreferences.getString(reqString, "");
	}

	public boolean isDeviceConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo ni = cm.getActiveNetworkInfo();
		return (ni != null);
	}

	public int getRequestTimeOut() {
		try {
			return TIMEOUT_VALUE;
		} catch (NumberFormatException e) {
			return 2000;
		}
	}

	public String getErrorStringFromResourceCode(int resourceCode) {
		return getResources().getText(resourceCode).toString();
	}

}
