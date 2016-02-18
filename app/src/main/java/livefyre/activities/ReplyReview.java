package livefyre.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;

import livefyre.BaseActivity;
import livefyre.LFSConfig;
import livefyre.LFUtils;
import livefyre.models.Content;
import livefyre.parsers.ContentParser;
import livefyre.streamhub.LFSActions;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.WriteClient;
import livefyre.R;
public class ReplyReview extends BaseActivity {

    EditText newReplyEt;
    String id, replytext;
    ImageView activityIcon;
    Boolean isEdit;
    Content selectedReview;
    TextView activityTitle, actionTv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply);

        pullViews();
        setListenersToViews();

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

    private void setListenersToViews() {
        activityIcon.setOnClickListener(backtoReviewInDetailActivityListener);
        actionTv.setOnClickListener(postReplyListener);
    }

    private void pullViews() {
        newReplyEt = (EditText) findViewById(R.id.newReplyEt);
        activityTitle = (TextView) findViewById(R.id.activityTitle);
        actionTv = (TextView) findViewById(R.id.actionTv);
        actionTv.setText("POST");
        activityIcon = (ImageView) findViewById(R.id.activityIcon);
        activityIcon.setImageResource(R.mipmap.back_arrow);
        activityTitle.setText("Reply");
    }

    private void setData() {
        selectedReview = ContentParser.ContentMap.get(id);
        newReplyEt.setText(LFUtils.trimTrailingWhitespace(Html
                        .fromHtml(selectedReview.getBodyHtml())),
                TextView.BufferType.SPANNABLE);
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
                        "TRY AGAIN", null);
            }
        }
    };
    DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
        }
    };

    void postNewReply(String body) {
        showProgressDialog();

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
            dismissProgressDialog();
            showAlert("Reply Edited Successfully.", "OK", null);
            // Log.d("Log", "" + data);
            // showToast("Reply Edited Successfully.");
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgressDialog();
            try {
                JSONObject errorJson = new JSONObject(content);
                if (!errorJson.isNull("msg")) {
                    showAlert(errorJson.getString("msg"), "TRY AGAIN", tryAgain);
                } else {
                    showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
            }
        }
    }

    public class newReplyCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject data) {
            dismissProgressDialog();
            showAlert("Reply Posted Successfully.", "OK", null);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", 1);
            setResult(RESULT_OK, returnIntent);
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgressDialog();
            try {
                JSONObject errorJson = new JSONObject(content);
                if (!errorJson.isNull("msg")) {
                    showAlert(errorJson.getString("msg"), "TRY AGAIN", tryAgain);

                } else {
                    showAlert("Something went wrong.", "TRY AGAIN", tryAgain);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", "TRY AGAIN", tryAgain);

            }
        }
    }
}
