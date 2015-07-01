package livefyre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.livefyre.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;

import livefyre.BaseActivity;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.LFUtils;
import livefyre.models.ContentBean;
import livefyre.parsers.ContentParser;
import livefyre.streamhub.LFSActions;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.WriteClient;

public class Reply extends BaseActivity {

	Button postReply, backtoReviewInDetailActivity;
	EditText newReplyEt;
	String id, replytext;
	Boolean isEdit;
	ContentBean selectedReview;
	TextView replyText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reply);
		init();
		newReplyEt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newReplyEt.setCursorVisible(true);
			}
		});
		getDataFromIntent();
		if (isEdit)
			setData();
	}

	private void setData() {
		selectedReview = ContentParser.ContentCollection.get(id);
		newReplyEt.setText(LFUtils.trimTrailingWhitespace(Html
                        .fromHtml(selectedReview.getBodyHtml())),
				TextView.BufferType.SPANNABLE);
	}

	void init() {
		newReplyEt = (EditText) findViewById(R.id.newReplyEt);
		postReply = (Button) findViewById(R.id.postReply);
		postReply.setOnClickListener(postReplyListener);
		backtoReviewInDetailActivity = (Button) findViewById(R.id.backtoReviewInDetailActivity);
		backtoReviewInDetailActivity
				.setOnClickListener(backtoReviewInDetailActivityListener);

		replyText = (TextView) findViewById(R.id.replyText);
		replyText.setOnClickListener(backtoReviewInDetailActivityListener);
		if (!isNetworkAvailable()) {
			showToast("Network Not Available");
			return;
		}
	}

	void getDataFromIntent() {
		Intent fromInDetailAdapter = getIntent();
		id = fromInDetailAdapter.getStringExtra("id");
		isEdit = fromInDetailAdapter.getBooleanExtra("isEdit", false);
	}

	OnClickListener backtoReviewInDetailActivityListener = new OnClickListener() {

		public void onClick(View v) {
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish(); 
		}
	};

	OnClickListener postReplyListener = new OnClickListener() {

		public void onClick(View v) {
			if (!isNetworkAvailable()) {
				showToast("Network Not Available");
				return;
			}
			replytext = newReplyEt.getText().toString();
			if (replytext.length() != 0) {
				String htmlReplytext = Html.toHtml(newReplyEt.getText());
				Log.d("htmlReplytext", htmlReplytext);
				postNewReply(htmlReplytext);
			} else {
				showAlert("Please enter text before post.",
						LFSAppConstants.DISMISS);
			}
		}
	};

	void postNewReply(String body) {
		showProgress();

		if (!isEdit) {
			Log.d("REPLY", "IN NEW REPLY");
			HashMap<String, Object> parameters = new HashMap();
			parameters.put(LFSConstants.LFSPostBodyKey, body);
			parameters.put(LFSConstants.LFSPostType,
					LFSConstants.LFSPostTypeReply);
			parameters.put(LFSConstants.LFSPostUserTokenKey,
					LFSConfig.USER_TOKEN);
			try {
				WriteClient.postContent(
						LFSConfig.COLLECTION_ID, id, LFSConfig.USER_TOKEN,
						parameters, new newReplyCallback());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			Log.d("EDIT", "IN EDIT REPLY");

			RequestParams parameters = new RequestParams();
			parameters.put(LFSConstants.LFSPostBodyKey, body);
			parameters.put(LFSConstants.LFSPostUserTokenKey,
					LFSConfig.USER_TOKEN);
			WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
					LFSConfig.USER_TOKEN, LFSActions.EDIT, parameters,
					new editCallback());
		}
	}

	private class editCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject data) {
			dismissProgress();
			showAlert("Reply Edited Successfully.", LFSAppConstants.FINISH);
			// Log.d("Log", "" + data);
			// showToast("Reply Edited Successfully.");
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			dismissProgress();
			try {
				JSONObject errorJson=new JSONObject(content);
				if(!errorJson.isNull("msg")){
					showAlert(errorJson.getString("msg"), LFSAppConstants.DISMISS);
				}else{
					showAlert("Something went wrong.", LFSAppConstants.DISMISS);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showAlert("Something went wrong.", LFSAppConstants.DISMISS);
			}
		}
	}

	public class newReplyCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject data) {
			dismissProgress();
			showAlert("Reply Posted Successfully.", LFSAppConstants.FINISH);
			Intent returnIntent = new Intent();
			returnIntent.putExtra("result",1); 
			setResult(RESULT_OK,returnIntent);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			dismissProgress();
			try {
				JSONObject errorJson=new JSONObject(content);
				if(!errorJson.isNull("msg")){
					showAlert(errorJson.getString("msg"), LFSAppConstants.DISMISS);

				}else{
					showAlert("Something went wrong.", LFSAppConstants.DISMISS);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showAlert("Something went wrong.", LFSAppConstants.DISMISS);

			}
		}

	}

}
