package livefyre;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.widget.Toast;

public class BaseActivity extends ActionBarActivity {

    private AlertDialog alertDialog;
    private ProgressDialog dialog;

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected void showProgressDialog(String message) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void showProgressDialog() {
        showProgressDialog("Please Wait..");
    }

    protected void dismissProgressDialog() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
        }
    }

    protected void showAlert(String alertMsg, String actionName, DialogInterface.OnClickListener action) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(alertMsg);
        builder.setCancelable(false);
        String CANCEL = "CANCEL";

        if (actionName.equals("OK")) {
            CANCEL = "OK";
        } else {
            builder.setPositiveButton(actionName, action);
        }
        builder.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    protected void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            return true;
        } else {
            return false;
        }
    }

//	protected void customToast(){
//		LayoutInflater li = getLayoutInflater();
//		View layout = li.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
//		Toast toast = new Toast(getApplicationContext());
//		toast.setDuration(Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//		toast.setView(layout);
//		toast.show();
//	}

}