package livefyre.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filepicker.sdk.FilePicker;
import com.filepicker.sdk.FilePickerAPI;

import livefyre.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;

import livefyre.BaseActivity;
import livefyre.LFSConfig;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.WriteClient;

/**
 * Created by kvanadev5 on 16/06/15.
 */

public class NewReviewActivity extends BaseActivity {

    EditText newReviewTitleEt, newReviewProsEt, newReviewConsEt, newReviewBodyEt;
    TextView newReviewTitleTv, newReviewProsTv, newReviewConsTv, newReviewBodyTv,activityTitle,actionTv;
    ImageView capturedImage;
    RelativeLayout deleteCapturedImage;
    RatingBar newReviewRatingBar;
    ProgressBar progressBar;
    LinearLayout addPhotoLL;
    Toolbar toolbar;
    JSONObject imgObj;
    String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        pullViews();
        setListenersToViews();
        textChangeListeners();
        buildToolBar();

    }

    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        //disable title on toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
        homeIcon.setBackgroundResource(R.mipmap.livefyreflame);
        activityTitle.setText("New Review");
        actionTv.setText("Post");
    }

    public void textChangeListeners(){
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

    private void pullViews() {

        newReviewTitleEt = (EditText) findViewById(R.id.newReviewTitleEt);
        newReviewProsEt = (EditText) findViewById(R.id.newReviewProsEt);
        newReviewConsEt = (EditText) findViewById(R.id.newReviewConsEt);
        newReviewBodyEt = (EditText) findViewById(R.id.newReviewBodyEt);

        newReviewTitleTv = (TextView) findViewById(R.id.newReviewTitleTv);
        newReviewProsTv = (TextView) findViewById(R.id.newReviewProsTv);
        newReviewConsTv = (TextView) findViewById(R.id.newReviewConsTv);
        newReviewBodyTv = (TextView) findViewById(R.id.newReviewBodyTv);
        actionTv = (TextView) findViewById(R.id.actionTv);

        newReviewRatingBar = (RatingBar) findViewById(R.id.newReviewRatingBar);

        capturedImage = (ImageView) findViewById(R.id.capturedImage);

        deleteCapturedImage = (RelativeLayout) findViewById(R.id.deleteCapturedImage);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    private void setListenersToViews() {
        addPhotoLL.setOnClickListener(captureImageListener);
        deleteCapturedImage.setOnClickListener(deleteCapturedImageListener);
        actionTv.setOnClickListener(postReviewListener);
    }
View.OnClickListener postReviewListener = new View.OnClickListener() {

    public void onClick(View v) {
        String title = newReviewTitleEt.getText().toString();
        String description = newReviewBodyEt.getText().toString();
        String pros = newReviewProsEt.getText().toString();
        String cons = newReviewConsEt.getText().toString();
        int reviewRating = (int) (newReviewRatingBar.getRating() * 20);
        if (title.length() == 0) {
            showAlert("Please Enter Title.", "OK",tryAgain);
            return;
        }

        if (reviewRating == 0) {
            showAlert("Please give Rating.", "OK",tryAgain);
            return;
        }
        if (pros.length() == 0) {
            showAlert("Please Enter Pros.", "OK",tryAgain);
            return;
        }

        if (cons.length() == 0) {
            showAlert("Please Enter Cons.", "OK",tryAgain);
            return;
        }

        if (description.length() == 0) {
            showAlert("Please Enter Description.", "OK",tryAgain);
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
    DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
        }
    };
    void postNewReview(String title, String body, int reviewRating) {
        if (!isNetworkAvailable()) {
            showToast("Network Not Available");
            return;
        }
        showProgressDialog();
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
            dismissProgressDialog();
            showAlert("Review Posted Successfully.",  "OK",null);
        }
        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            dismissProgressDialog();
            Log.d("data error", "" + content);
            try {
                JSONObject errorJson = new JSONObject(content);
                if (!errorJson.isNull("msg")) {
                    showAlert(errorJson.getString("msg"), "OK",null);
                } else {
                    showAlert("Something went wrong.",  "OK",null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Something went wrong.", "OK",null);
            }
        }
    }

    View.OnClickListener captureImageListener = new View.OnClickListener() {

        public void onClick(View v) {
            Intent intent = new Intent(NewReviewActivity.this, FilePicker.class);
            FilePickerAPI.setKey(LFSConfig.FILEPICKER_API_KEY);
            startActivityForResult(intent, FilePickerAPI.REQUEST_CODE_GETFILE);
        }
    };

    View.OnClickListener deleteCapturedImageListener = new View.OnClickListener() {

        public void onClick(View v) {
            addPhotoLL.setVisibility(View.VISIBLE);
            capturedImage.setVisibility(View.GONE);
            deleteCapturedImage.setVisibility(View.GONE);
            imgUrl = "";
            imgObj = null;
        }
    };

    // Dialog Listeners
    DialogInterface.OnClickListener selectImageDialogAction = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            Intent intent = new Intent(NewReviewActivity.this, FilePicker.class);
            FilePickerAPI.setKey(LFSConfig.FILEPICKER_API_KEY);
            startActivityForResult(intent, FilePickerAPI.REQUEST_CODE_GETFILE);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE) {
            if (resultCode != RESULT_OK) {
                showAlert("No Image Selected.", "SELECT IMAGE", selectImageDialogAction);
                addPhotoLL.setVisibility(View.VISIBLE);
                capturedImage.setVisibility(View.GONE);
                deleteCapturedImage.setVisibility(View.GONE);
                return;
            }
            addPhotoLL.setVisibility(View.GONE);
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
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    Picasso.with(getBaseContext()).load(imgUrl).fit()
                            .into(capturedImage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
