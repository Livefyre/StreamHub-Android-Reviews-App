package livefyre;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class BaseActivity extends Activity {
	private AlertDialog alertDialog;
	private ProgressDialog dialog;

	protected void showProgress() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Please wait." + "\n"
				+ "Your request is being processed..");

		dialog.setCancelable(false);
		dialog.show();
	}

	protected void dismissProgress() {
		try {
			dialog.dismiss();
		} catch (Exception e) {

		}
	}

	public void showAlert(String alertMsg, Boolean type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("");
		builder.setMessage(alertMsg);
		builder.setCancelable(false);
		if (type) {
			builder.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
		} else {
			builder.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					alertDialog.dismiss();
				}
			});
		}

		alertDialog = builder.create();
		alertDialog.show();

	}

	public void showToast(String toastText) {
		Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT)
				.show();
	}

	protected Boolean isNetworkAvailable() {
		ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true) {
			return true;
		} else {
			return false;
		}
	}
}