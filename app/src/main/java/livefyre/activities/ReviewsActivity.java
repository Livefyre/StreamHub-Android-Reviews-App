package livefyre.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import livefyre.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import livefyre.AppSingleton;
import livefyre.BaseActivity;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.LivefyreApplication;
import livefyre.adapters.ReviewListAdapter;
import livefyre.listeners.ContentUpdateListener;
import livefyre.models.Content;
import livefyre.parsers.ContentParser;
import livefyre.streamhub.AdminClient;
import livefyre.streamhub.BootstrapClient;
import livefyre.streamhub.StreamClient;

/**
 * Created by kvanadev5 on 16/06/15.
 */
public class ReviewsActivity extends BaseActivity implements ContentUpdateListener {
    public static final String TAG = ReviewsActivity.class.getSimpleName();

    ImageButton postNewReviewIv;
    RecyclerView reviewsRV;
    List<Content> reviewCollectiontoBuild;
    ReviewListAdapter reviewListAdapter;
    private LivefyreApplication application;
    public String ownReviewId;
    public String adminClintId = "No";
    ContentParser content;
    private Toolbar toolbar;
    Bus mBus;
    ArrayList<String> newReviews;

    LinearLayout notification;
    TextView notifMsgTV, activityTitle, actionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        application = AppSingleton.getInstance().getApplication();

        pullViews();
        setListenersToViews();
        buildToolBar();
        adminClintCall();

    }

    void isReviewPosted() {

        Boolean isGiven = false;
        if (reviewCollectiontoBuild != null)
            for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                if (reviewCollectiontoBuild.get(i).getAuthorId()
                        .equals(adminClintId)) {
                    isGiven = true;
                    ownReviewId = reviewCollectiontoBuild.get(i).getId();
                }
            }
        if (isGiven) {
            postNewReviewIv.setVisibility(View.GONE);
            actionTv.setText("MY REVIEW");
            actionTv.setVisibility(View.VISIBLE);
        } else {
            postNewReviewIv.setVisibility(View.VISIBLE);
            actionTv.setVisibility(View.GONE);
        }

    }

    Boolean isExistComment(String reviewId) {
        for (Content bean : reviewCollectiontoBuild) {
            if (bean.getId().equals(reviewId))
                return true;
        }
        return false;
    }

    View.OnClickListener myReviewListener = new View.OnClickListener() {

        public void onClick(View v) {
            Intent detailViewIntent = new Intent(ReviewsActivity.this,
                    ReviewIndetailActivity.class);
            detailViewIntent.putExtra("id", ownReviewId);
            startActivity(detailViewIntent);
//            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    };

    private void adminClintCall() {

        if (!isNetworkAvailable()) {
            showAlert("No connection available", "TRY AGAIN", tryAgain);
            return;
        } else {
            showProgressDialog();
        }
        try {
            AdminClient.authenticateUser(LFSConfig.USER_TOKEN,
                    LFSConfig.COLLECTION_ID, LFSConfig.ARTICLE_ID,
                    LFSConfig.SITE_ID,
                    new AdminCallback());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    void bootstrapClientCall() {

        try {
            BootstrapClient.getInit(LFSConfig.SITE_ID,
                    LFSConfig.ARTICLE_ID, new InitCallback());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private class InitCallback extends JsonHttpResponseHandler {
        public void onSuccess(String data) {
            application.printLog(false, TAG + "-InitCallback-onSuccess", data.toString());
            buildReviewsList(data);
//            swipeView.setRefreshing(false);
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            application.printLog(true, TAG + "-InitCallback-onFailure", error.toString());
        }
    }

    void buildReviewsList(String data) {
        try {
            content = new ContentParser(new JSONObject(data), getBaseContext());
            content.getContentFromResponse(this);
            reviewCollectiontoBuild = content.getReviews();
            reviewListAdapter = new ReviewListAdapter(this, reviewCollectiontoBuild);
            reviewsRV.setAdapter(reviewListAdapter);
            reviewsRV.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), reviewsRV, new ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    Intent detailViewIntent = new Intent(ReviewsActivity.this, ReviewIndetailActivity.class);
                    detailViewIntent.putExtra(LFSAppConstants.ID, reviewCollectiontoBuild.get(position).getId());
                    startActivity(detailViewIntent);

                }

                @Override
                public void onLongClick(View view, int position) {
                }
            }));
//            streamClintCall();
            isReviewPosted();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        newReviews = new ArrayList<>();
//        swipeView.setEnabled(true);
        dismissProgressDialog();
    }

    void streamClintCall() {
        try {
            StreamClient.pollStreamEndpoint(
                    LFSConfig.COLLECTION_ID, ContentParser.lastEvent,
                    new StreamCallBack());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class StreamCallBack extends AsyncHttpResponseHandler {

        public void onSuccess(String data) {
            if (data != null) {
                content.setStreamData(data);
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
        }
    }

    public class AdminCallback extends JsonHttpResponseHandler {

        public void onSuccess(JSONObject AdminClintJsonResponseObject) {
            JSONObject data;
//            application.printLog(true, TAG + "-AdminCallback-onSuccess", AdminClintJsonResponseObject.toString());
            try {
                data = AdminClintJsonResponseObject.getJSONObject("data");
                if (!data.isNull("permissions")) {
                    JSONObject permissions = data.getJSONObject("permissions");
                    if (!permissions.isNull("moderator_key"))
                        application.saveDataInSharedPreferences(
                                LFSAppConstants.ISMOD, "yes");
                    else {
                        application.saveDataInSharedPreferences(
                                LFSAppConstants.ISMOD, "no");
                    }
                } else {
                    application.saveDataInSharedPreferences(
                            LFSAppConstants.ISMOD, "no");
                }
                if (!data.isNull("profile")) {
                    JSONObject profile = data.getJSONObject("profile");

                    if (!profile.isNull("id")) {
                        application.saveDataInSharedPreferences(
                                LFSAppConstants.ID, profile.getString("id"));
                        adminClintId = profile.getString("id");
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            bootstrapClientCall();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
            application.printLog(true, TAG + "-AdminCallback-onFailure", error.toString());
            bootstrapClientCall();
        }
    }

    private void scrollToReviews(String mCommentBeanId) {
        for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
            Content mBean = reviewCollectiontoBuild.get(i);
            if (mBean.getId().equals(mCommentBeanId)) {
                reviewsRV.smoothScrollToPosition(i);
                break;
            }
        }
    }

    private void buildToolBar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        //disable title on toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView homeIcon = (ImageView) findViewById(R.id.activityIcon);
        homeIcon.setBackgroundResource(R.mipmap.livefyreflame);
        activityTitle.setText("Most Helpful");

    }

    private void setListenersToViews() {

        postNewReviewIv.setOnClickListener(postNewReviewsListener);
        reviewsRV.setOnScrollListener(onScrollListener);
        activityTitle.setOnClickListener(activityTitleListenerHide);
    }

    public RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        boolean hideToolBar = false;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (hideToolBar) {
                postNewReviewIv.setVisibility(View.GONE);
                getSupportActionBar().hide();
            } else {
                postNewReviewIv.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 2) {
                hideToolBar = true;
            } else if (dy < -1) {
                hideToolBar = false;
            }
        }
    };
    View.OnClickListener activityTitleListenerShow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            postNewReviewIv.setVisibility(View.VISIBLE);
            activityTitle.setOnClickListener(activityTitleListenerHide);

        }
    };
    View.OnClickListener activityTitleListenerHide = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                activityTitle.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                activityTitle.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
            }

            postNewReviewIv.setVisibility(View.GONE);


            activityTitle.setOnClickListener(activityTitleListenerShow);

        }
    };
    View.OnClickListener postNewReviewsListener = new View.OnClickListener() {

        public void onClick(View v) {
            Intent newPostIntent = new Intent(ReviewsActivity.this,
                    NewReviewActivity.class);
            startActivity(newPostIntent);
//                overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    };

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    DialogInterface.OnClickListener tryAgain = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            adminClintCall();
        }
    };

    private void pullViews() {
        reviewsRV = (RecyclerView) findViewById(R.id.reviewsRV);
        reviewsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postNewReviewIv = (ImageButton) findViewById(R.id.postNewReviewIv);
        activityTitle = (TextView) findViewById(R.id.activityTitle);
        actionTv = (TextView) findViewById(R.id.actionTv);
        notification = (LinearLayout) findViewById(R.id.notification);
    }

    @Override
    public void onDataUpdate(HashSet<String> authorsSet, HashSet<String> statesSet, HashSet<String> annotationsSet, HashSet<String> updates) {
        for (String stateBeanId : statesSet) {
            Content stateBean = ContentParser.ContentMap.get(stateBeanId);
            if (stateBean.getVisibility().equals("1")) {

                if (isExistComment(stateBeanId)) continue;

                if (adminClintId.equals(stateBean.getAuthorId())) {
                    int flag = 0;
                    for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                        Content content = reviewCollectiontoBuild.get(i);
                        if (content.getId().equals(stateBean.getParentId())) {
                            reviewCollectiontoBuild.add(i + 1, stateBean);
                            reviewListAdapter.notifyItemInserted(i + 1);
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        reviewCollectiontoBuild.add(0, stateBean);
                        reviewListAdapter.notifyItemInserted(0);
                    }
                } else {
                    newReviews.add(0, stateBeanId);
                }
            } else {
                if (!content.hasVisibleChildContents(stateBeanId)) {
                    application.printLog(true, TAG, "Deleted Content");

                    for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                        Content bean = reviewCollectiontoBuild.get(i);
                        if (bean.getId().equals(stateBeanId)) {
                            reviewCollectiontoBuild.remove(i);
                            reviewListAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }
        }
        if (updates.size() > 0) {
            mBus.post(updates);
            reviewListAdapter.notifyDataSetChanged();
        }

        if (newReviews != null)
            if (newReviews.size() > 0) {
                if (newReviews.size() == 1) {
                    notifMsgTV.setText(newReviews.size() + " New Comment");

                } else {
                    notifMsgTV.setText(newReviews.size() + " New Comments");
                }
                notification.setVisibility(View.VISIBLE);
//                YoYo.with(Techniques.DropOut)
//                        .duration(700)
//                        .playOn(findViewById(R.id.notification));

            } else {
                notification.setVisibility(View.GONE);
            }
        isReviewPosted();
    }
}
