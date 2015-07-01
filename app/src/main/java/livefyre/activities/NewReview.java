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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filepicker.sdk.FilePicker;
import com.filepicker.sdk.FilePickerAPI;
import com.livefyre.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;

import livefyre.BaseActivity;
import livefyre.ImagesCache.ImagesCache;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.WriteClient;


public class NewReview extends BaseActivity {
	EditText newReviewTitleEt;
	EditText newReviewBodyEt;
	EditText newReviewProsEt;
	EditText newReviewConsEt;

	TextView newReviewTitleTv;
	TextView newReviewBodyTv;
	TextView newReviewProsTv;
	TextView newReviewConsTv;
	TextView addPhoto, reviewText;

	RatingBar newReviewRatingBar;

	Button backtoReviewActivity, postReview;

	ImageView camBtn, capturedImage;
	RelativeLayout deleteCapturedImage;
	ImagesCache cache = ImagesCache.getInstance();
	JSONObject imgObj;
	String imgUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_review);
		init();
		newReviewTitleEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (newReviewTitleEt.getText().toString().length() > 0) {
					newReviewTitleTv.setVisibility(View.VISIBLE);
					newReviewTitleEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewTitleTv.setVisibility(View.INVISIBLE);
					newReviewTitleEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (newReviewTitleEt.getText().toString().length() > 0) {
					newReviewTitleTv.setVisibility(View.VISIBLE);
					newReviewTitleEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewTitleTv.setVisibility(View.INVISIBLE);
					newReviewTitleEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
		});

		newReviewBodyEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (newReviewBodyEt.getText().toString().length() > 0) {
					newReviewBodyTv.setVisibility(View.VISIBLE);
					newReviewBodyEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewBodyTv.setVisibility(View.INVISIBLE);
					newReviewBodyEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (newReviewBodyEt.getText().toString().length() > 0) {
					newReviewBodyTv.setVisibility(View.VISIBLE);
					newReviewBodyEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewBodyTv.setVisibility(View.INVISIBLE);
					newReviewBodyEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
		});
		newReviewProsEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (newReviewProsEt.getText().toString().length() > 0) {
					newReviewProsTv.setVisibility(View.VISIBLE);
					newReviewProsEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewProsTv.setVisibility(View.INVISIBLE);
					newReviewProsEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (newReviewProsEt.getText().toString().length() > 0) {
					newReviewProsTv.setVisibility(View.VISIBLE);
					newReviewProsEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewProsTv.setVisibility(View.INVISIBLE);
					newReviewProsEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
		});
		newReviewConsEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (newReviewConsEt.getText().toString().length() > 0) {
					newReviewConsTv.setVisibility(View.VISIBLE);
					newReviewConsEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewConsTv.setVisibility(View.INVISIBLE);
					newReviewConsEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (newReviewConsEt.getText().toString().length() > 0) {
					newReviewConsTv.setVisibility(View.VISIBLE);
					newReviewConsEt.setHintTextColor(Color
							.parseColor("#ffffff"));
				} else {
					newReviewConsTv.setVisibility(View.INVISIBLE);
					newReviewConsEt.setHintTextColor(Color
							.parseColor("#cdcdcd"));
				}
			}
		});
	}

	void init() {
		newReviewTitleEt = (EditText) findViewById(R.id.newReviewTitleEt);
		newReviewBodyEt = (EditText) findViewById(R.id.newReviewBodyEt);
		newReviewProsEt = (EditText) findViewById(R.id.newReviewProsEt);
		newReviewConsEt = (EditText) findViewById(R.id.newReviewConsEt);

		newReviewTitleTv = (TextView) findViewById(R.id.newReviewTitleTv);
		newReviewBodyTv = (TextView) findViewById(R.id.newReviewBodyTv);
		newReviewProsTv = (TextView) findViewById(R.id.newReviewProsTv);
		newReviewConsTv = (TextView) findViewById(R.id.newReviewConsTv);

		postReview = (Button) findViewById(R.id.postReview);
		postReview.setOnClickListener(postReviewListener);

		backtoReviewActivity = (Button) findViewById(R.id.backtoReviewActivity);
		backtoReviewActivity.setOnClickListener(backtoReviewActivityListener);

		reviewText = (TextView) findViewById(R.id.reviewText);
		reviewText.setOnClickListener(backtoReviewActivityListener);

		camBtn = (ImageView) findViewById(R.id.camBtn);
		addPhoto = (TextView) findViewById(R.id.addPhoto);

		addPhoto.setOnClickListener(captureImageListener);
		camBtn.setOnClickListener(captureImageListener);

		capturedImage = (ImageView) findViewById(R.id.capturedImage);

		deleteCapturedImage = (RelativeLayout) findViewById(R.id.deleteCapturedImage);
		deleteCapturedImage.setOnClickListener(deleteCapturedImageListener);

		newReviewRatingBar = (RatingBar) findViewById(R.id.newReviewRatingBar);
		if (!isNetworkAvailable()) {
			showToast("Network Not Available");
			return;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE) {
			if (resultCode != RESULT_OK) {
				// Result was cancelled by the user or there was an error
				// showAlert("Something Went Wrong.", LFSAppConstants.DISMISS);
				camBtn.setVisibility(View.VISIBLE);
				addPhoto.setVisibility(View.VISIBLE);
				capturedImage.setVisibility(View.GONE);
				deleteCapturedImage.setVisibility(View.GONE);
				return;
			}
			camBtn.setVisibility(View.GONE);
			addPhoto.setVisibility(View.GONE);
			capturedImage.setVisibility(View.VISIBLE);
			deleteCapturedImage.setVisibility(View.VISIBLE);
			String imgUrl = data.getExtras().getString("fpurl");
			Log.d("url", imgUrl + "");
			try {
				imgObj = new JSONObject();
				imgObj.put("link", imgUrl);
				imgObj.put("provider_name", "LivefyreFilePicker");
				// imgObj.put("thumbnail_height",320);
				imgObj.put("thumbnail_url", imgUrl);
				imgObj.put("type", "photo");
				imgObj.put("url", imgUrl);
				Picasso.with(getBaseContext()).load(imgUrl).fit()
						.into(capturedImage);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	OnClickListener deleteCapturedImageListener = new OnClickListener() {

		public void onClick(View v) {
			camBtn.setVisibility(View.VISIBLE);
			addPhoto.setVisibility(View.VISIBLE);
			capturedImage.setVisibility(View.GONE);
			deleteCapturedImage.setVisibility(View.GONE);
			imgUrl = "";
			imgObj = null;
		}
	};

	OnClickListener captureImageListener = new OnClickListener() {

		public void onClick(View v) {

			Intent intent = new Intent(NewReview.this, FilePicker.class);
			FilePickerAPI.setKey(LFSConfig.FILEPICKER_API_KEY);
			startActivityForResult(intent, FilePickerAPI.REQUEST_CODE_GETFILE);
		}
	};

	OnClickListener postReviewListener = new OnClickListener() {

		public void onClick(View v) {
			String title = newReviewTitleEt.getText().toString();
			String description = newReviewBodyEt.getText().toString();
			String pros = newReviewProsEt.getText().toString();
			String cons = newReviewConsEt.getText().toString();
			int reviewRating = (int) (newReviewRatingBar.getRating() * 20);
			if (title.length() == 0) {
				showAlert("Please Enter Title.", LFSAppConstants.DISMISS);
				return;
			}

			if (reviewRating == 0) {
				showAlert("Please give Rating.", LFSAppConstants.DISMISS);
				return;
			}
			if (pros.length() == 0) {
				showAlert("Please Enter Pros.", LFSAppConstants.DISMISS);
				return;
			}

			if (cons.length() == 0) {
				showAlert("Please Enter Cons.", LFSAppConstants.DISMISS);
				return;
			}

			if (description.length() == 0) {
				showAlert("Please Enter Description.", LFSAppConstants.DISMISS);
				return;
			}
			String descriptionHTML = Html.toHtml(newReviewBodyEt.getText());
			if (pros.length() > 0 || cons.length() > 0) {
				descriptionHTML = "<p><b>Pro</b><p>"
						+ Html.toHtml(newReviewProsEt.getText()) + "</p></p>"
						+ "<p><b>Cons</b><p>"
						+ Html.toHtml(newReviewConsEt.getText()) + "</p></p>"
						+ " <p><b>Description</b><p>" + descriptionHTML
						+ "</p></p>";
			}
			postNewReview(newReviewTitleEt.getText().toString(),
					descriptionHTML, reviewRating);

		}
	};

	void postNewReview(String title, String body, int reviewRating) {
		if (!isNetworkAvailable()) {
			showToast("Network Not Available");
			return;
		}
		showProgress();
		HashMap<String, Object> parameters = new HashMap();
		parameters.put(LFSConstants.LFSPostBodyKey, body);
		parameters.put(LFSConstants.LFSPostTitleKey, title);
		parameters.put(LFSConstants.LFSPostTypeReview, reviewRating);
		parameters
				.put(LFSConstants.LFSPostType, LFSConstants.LFSPostTypeReview);
		parameters.put(LFSConstants.LFSPostUserTokenKey, LFSConfig.USER_TOKEN);
		if (imgObj != null)
			parameters.put(LFSConstants.LFSPostAttachment,
					(new JSONArray().put(imgObj)).toString());
		try {
			WriteClient.postContent(
					LFSConfig.COLLECTION_ID, null, LFSConfig.USER_TOKEN,
					parameters, new writeclientCallback());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public class writeclientCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject data) {
			dismissProgress();
			showAlert("Review Posted Successfully.", LFSAppConstants.FINISH);

		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			dismissProgress();
			Log.d("data error", "" + content);

			try {
				JSONObject errorJson = new JSONObject(content);
				if (!errorJson.isNull("msg")) {
					showAlert(errorJson.getString("msg"),
							LFSAppConstants.DISMISS);

				} else {
					showAlert("Something went wrong.", LFSAppConstants.DISMISS);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				showAlert("Something went wrong.", LFSAppConstants.DISMISS);

			}

		}
	}

	OnClickListener backtoReviewActivityListener = new OnClickListener() {

		public void onClick(View v) {
			finish();
		}
	};
}
