package livefyre.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.livefyre.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import livefyre.AppSingleton;
import livefyre.BaseActivity;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.LFUtils;
import livefyre.LivefyreApplication;
import livefyre.models.ContentBean;
import livefyre.parsers.ContentParser;
import livefyre.streamhub.LFSActions;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.WriteClient;


public class Edit extends BaseActivity {

	EditText editReviewTitleEt, editReviewBodyEt;
	Button editPostReview, backtoReviewInDetailFromEditActivity;
	RatingBar editReviewRatingBar;
	TextView editReviewTitleTv, editReviewBodyTv, editReviewText;
	String id, title, body;
	ContentBean selectedReview;
	int rating;
	private LivefyreApplication application;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit);

		init();
		editReviewTitleEt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editReviewTitleEt.setCursorVisible(true);
			}
		});
		getDataFromIntent();
		setData();

		editReviewTitleEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

				if (editReviewTitleEt.getText().toString().length() > 0) {
					editReviewTitleTv.setVisibility(View.VISIBLE);
					editReviewTitleEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					editReviewTitleTv.setVisibility(View.INVISIBLE);
					editReviewTitleEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (editReviewTitleEt.getText().toString().length() > 0) {
					editReviewTitleTv.setVisibility(View.VISIBLE);
					editReviewTitleEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					editReviewTitleTv.setVisibility(View.INVISIBLE);
					editReviewTitleEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
		});

		editReviewBodyEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

				if (editReviewBodyEt.getText().toString().length() > 0) {
					editReviewBodyTv.setVisibility(View.VISIBLE);
					editReviewBodyEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					editReviewBodyTv.setVisibility(View.INVISIBLE);
					editReviewBodyEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (editReviewBodyEt.getText().toString().length() > 0) {
					editReviewBodyTv.setVisibility(View.VISIBLE);
					editReviewBodyEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					editReviewBodyTv.setVisibility(View.INVISIBLE);
					editReviewBodyEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
		});

	}

	void init() {

		application = AppSingleton.getInstance().getApplication();

		editReviewTitleTv = (TextView) findViewById(R.id.editReviewTitleTv);
		editReviewBodyTv = (TextView) findViewById(R.id.editReviewBodyTv);
		editReviewText = (TextView) findViewById(R.id.editReviewText);
		editReviewText
				.setOnClickListener(backtoReviewInDetailFromEditActivityListener);

		editReviewTitleEt = (EditText) findViewById(R.id.editReviewTitleEt);
		editReviewBodyEt = (EditText) findViewById(R.id.editReviewBodyEt);

		editPostReview = (Button) findViewById(R.id.editPostReview);
		editPostReview.setOnClickListener(editPostReplyListener);

		editReviewRatingBar = (RatingBar) findViewById(R.id.editReviewRatingBar);

		backtoReviewInDetailFromEditActivity = (Button) findViewById(R.id.backtoReviewInDetailFromEditActivity);
		backtoReviewInDetailFromEditActivity
				.setOnClickListener(backtoReviewInDetailFromEditActivityListener);
		if (!isNetworkAvailable()) {
			showToast("Network Not Available");
			return;
		}
	}

	void getDataFromIntent() {
		Intent fromInDetailAdapter = getIntent();
		id = fromInDetailAdapter.getStringExtra("id");
	}

	void setData() {
		selectedReview = ContentParser.ContentCollection.get(id);
		editReviewTitleEt.setText(selectedReview.getTitle());
		editReviewBodyEt.setText(LFUtils.trimTrailingWhitespace(Html
                        .fromHtml(selectedReview.getBodyHtml())),
				TextView.BufferType.SPANNABLE);

		editReviewRatingBar.setRating(Float.parseFloat(selectedReview
				.getRating()) / 20);
		editReviewTitleTv.setVisibility(View.VISIBLE);
		editReviewBodyTv.setVisibility(View.VISIBLE);
		if (selectedReview.getAuthorId().equals(
				application.getDataFromSharedPreferences(LFSAppConstants.ID))) {
			editReviewRatingBar.setIsIndicator(false);
		} else {
			editReviewRatingBar.setIsIndicator(true);
		}

	}

	OnClickListener backtoReviewInDetailFromEditActivityListener = new OnClickListener() {

		public void onClick(View v) {
			finish();
		}
	};

	OnClickListener editPostReplyListener = new OnClickListener() {

		public void onClick(View v) {
			if (!isNetworkAvailable()) {
				showToast("Network Not Available");
				return;
			}
			showProgress();
			title = editReviewTitleEt.getText().toString();
			body = editReviewBodyEt.getText().toString();
			rating = (int) (editReviewRatingBar.getRating() * 20);

			if (title.length() == 0) {
				showAlert("Please enter title before post.",
						LFSAppConstants.DISMISS);
				return;
			}
			if (body.length() == 0) {
				showAlert("Please enter description before post.",
						LFSAppConstants.DISMISS);
				return;
			}
			if (rating == 0) {
				showAlert("Please give rating before post.",
						LFSAppConstants.DISMISS);
				return;
			}
			String htmlBody = Html.toHtml(editReviewBodyEt.getText());
			RequestParams parameters = new RequestParams();
			parameters.put(LFSConstants.LFSPostBodyKey, htmlBody);
			parameters.put(LFSConstants.LFSPostTitleKey, editReviewTitleEt
					.getText().toString());

			parameters.put(LFSConstants.LFSPostType,
					LFSConstants.LFSPostTypeReview);
			JSONObject ratingJson = new JSONObject();
			try {
				ratingJson.put("default", rating + "");
				parameters.put(LFSConstants.LFSPostRatingKey,
						ratingJson.toString());

			} catch (JSONException e) {
				e.printStackTrace();
			}

			parameters.put(LFSConstants.LFSPostUserTokenKey,
					LFSConfig.USER_TOKEN);
			WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
                    LFSConfig.USER_TOKEN, LFSActions.EDIT, parameters,
                    new editCallback());

		}
	};

	private class editCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject data) {
			Log.d("Log", "" + data);
			dismissProgress();

			showAlert("Review Edited Successfully.", LFSAppConstants.FINISH);
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			dismissProgress();

			showAlert("Something went wrong.", LFSAppConstants.DISMISS);
		}

	}

}
