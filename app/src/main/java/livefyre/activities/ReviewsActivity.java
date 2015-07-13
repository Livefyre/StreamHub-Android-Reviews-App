package livefyre.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import livefyre.R;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import livefyre.models.ContentTypeEnum;
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
    Spinner activityTitleSpinner;
    private String selectedCategory;
    private SwipeRefreshLayout swipeView;
//    Bus mBus;
    ArrayList<String> newReviews;

    LinearLayout notification;
    TextView notifMsgTV, activityTitle, actionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        application = AppSingleton.getInstance().getApplication();
//        mBus = application.getBus();
        pullViews();
        setListenersToViews();
        buildToolBar();
        adminClintCall();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContentParser.ContentMap != null)
            if (!ContentParser.ContentMap.isEmpty()) {
                sortReviews(LFSAppConstants.MOVE_TO_VIEW_POINT);
                isReviewPosted();
                streamClintCall();
            }
    }

    void sortReviews(Boolean viewpoint) {

        if (!isNetworkAvailable()) {
            showToast("Network Not Available");
            return;
        } else
            dismissProgressDialog();
        char sortCase = activityTitleSpinner.getPrompt().toString().charAt(0);
        reviewCollectiontoBuild = new ArrayList<Content>();
        HashMap<String, Content> mainContent = ContentParser.ContentMap;
        if (mainContent != null)
            for (Content t : mainContent.values()) {
                if (t.getContentType() == ContentTypeEnum.PARENT
                        && t.getVisibility().equals("1")) {
                    reviewCollectiontoBuild.add(t);
                }
            }

        switch (sortCase) {
            case 'N':
                Collections.sort(reviewCollectiontoBuild,
                        new Comparator<Content>() {
                            @Override
                            public int compare(Content p2, Content p1) {
                                return Integer.parseInt(p1.getCreatedAt())
                                        - Integer.parseInt(p2.getCreatedAt());
                            }
                        });
                break;
            case 'O':
                Collections.sort(reviewCollectiontoBuild,
                        new Comparator<Content>() {
                            @Override
                            public int compare(Content p1, Content p2) {
                                return Integer.parseInt(p1.getCreatedAt())
                                        - Integer.parseInt(p2.getCreatedAt());
                            }
                        });
                break;
            case 'H':
                Collections.sort(reviewCollectiontoBuild,
                        new Comparator<Content>() {
                            @Override
                            public int compare(Content p2, Content p1) {
                                return Integer.parseInt(p1.getRating())
                                        - Integer.parseInt(p2.getRating());
                            }
                        });
                break;
            case 'L':
                Collections.sort(reviewCollectiontoBuild,
                        new Comparator<Content>() {
                            @Override
                            public int compare(Content p1, Content p2) {
                                return Integer.parseInt(p1.getRating())
                                        - Integer.parseInt(p2.getRating());
                            }
                        });
                break;
            case 'M':
                Collections.sort(reviewCollectiontoBuild,
                        new Comparator<Content>() {
                            @Override
                            public int compare(Content p1, Content p2) {
                                int t1 = 0, t2 = 0;
                                if (p1.getVote() != null) {
                                    t1 = p1.getVote().size();
                                }
                                if (p2.getVote() != null) {
                                    t2 = p2.getVote().size();
                                }
                                // return p1.getVote().size()- p2.getVote().size();
                                return t1 - t2;
                            }
                        });
                Collections.sort(reviewCollectiontoBuild,
                        new Comparator<Content>() {
                            @Override
                            public int compare(Content p2, Content p1) {
                                int t1 = 0, t2 = 0;
                                if (p1.getVote() != null) {
                                    t1 = p1.getHelpfulcount();
                                }
                                if (p2.getVote() != null) {
                                    t2 = p2.getHelpfulcount();
                                }
                                return t1 - t2;
                                // return p1.getVote().size()- p2.getVote().size();
                            }
                        });
                break;
            default:
                break;
        }
        // Move Own Review To Top.
        if (reviewCollectiontoBuild != null && !adminClintId.equals("NO")) {
            for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                if (reviewCollectiontoBuild.get(i).getAuthorId()
                        .equals(adminClintId)) {
                    Content temp = reviewCollectiontoBuild.get(i);
                    reviewCollectiontoBuild.remove(temp);
                    reviewCollectiontoBuild.add(0, temp);
                    break;
                }
            }
        }
        // Hide Notification
        notification.setVisibility(View.GONE);

        reviewListAdapter = null;
        reviewListAdapter = (ReviewListAdapter) reviewsRV.getAdapter();
        if (reviewListAdapter != null) {
            reviewListAdapter.updateContentResult(reviewCollectiontoBuild);
        } else {
            reviewListAdapter = new ReviewListAdapter(getApplicationContext(),
                    reviewCollectiontoBuild);

        }
//        Parcelable state = reviewsRV.onSaveInstanceState();
        reviewListAdapter.notifyDataSetChanged();
        reviewsRV.setAdapter(reviewListAdapter);
//        reviewsRV.onRestoreInstanceState(state);
        if (viewpoint)
            reviewsRV.scrollToPosition(0);
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
                    ReviewInDetailActivity.class);
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
            swipeView.setRefreshing(false);
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

                    Intent detailViewIntent = new Intent(ReviewsActivity.this, ReviewInDetailActivity.class);
                    detailViewIntent.putExtra(LFSAppConstants.ID, reviewCollectiontoBuild.get(position).getId());
                    startActivity(detailViewIntent);

                }

                @Override
                public void onLongClick(View view, int position) {
                }
            }));
            streamClintCall();
            isReviewPosted();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        newReviews = new ArrayList<>();
        swipeView.setEnabled(true);
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
        activityTitle.setVisibility(View.GONE);
        activityTitleSpinner.setVisibility(View.VISIBLE);
    }

    private void setListenersToViews() {

        postNewReviewIv.setOnClickListener(postNewReviewsListener);
        reviewsRV.setOnScrollListener(onScrollListener);
        activityTitle.setOnClickListener(activityTitleListenerHide);
        activityTitleSpinner.setOnItemSelectedListener(activityTitleSpinnerListener);
        actionTv.setOnClickListener(myReviewListener);
        swipeView.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                reviewListAdapter = null;
                reviewCollectiontoBuild.clear();
                reviewListAdapter = new ReviewListAdapter(getApplication(), reviewCollectiontoBuild);
                reviewsRV.setAdapter(reviewListAdapter);
                bootstrapClientCall();

                YoYo.with(Techniques.FadeIn)
                        .duration(700)
                        .playOn(findViewById(R.id.reviewsRV));
            }
        });
    }

    AdapterView.OnItemSelectedListener activityTitleSpinnerListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] helpfulCategories = getResources().getStringArray(R.array.helpful_categories);
            selectedCategory = helpfulCategories[position];

            if (selectedCategory.equals("Most Helpful")) {
                activityTitleSpinner.setPrompt("Most Helpful");
                sortReviews(LFSAppConstants.MOVE_TO_TOP);
            }
            if (selectedCategory.equals("Highest Rating")) {
                activityTitleSpinner.setPrompt("Highest Rating");
                sortReviews(LFSAppConstants.MOVE_TO_TOP);
            }
            if (selectedCategory.equals("Lowest Rating")) {
                activityTitleSpinner.setPrompt("Lowest Rating");
                sortReviews(LFSAppConstants.MOVE_TO_TOP);
            }
            if (selectedCategory.equals("Newest")) {
                activityTitleSpinner.setPrompt("Newest");
                sortReviews(LFSAppConstants.MOVE_TO_TOP);
            }
            if (selectedCategory.equals("Oldest")) {
                activityTitleSpinner.setPrompt("Oldest");
                sortReviews(LFSAppConstants.MOVE_TO_TOP);
            }
        }

        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };
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
        activityTitleSpinner = (Spinner) findViewById(R.id.activityTitleSpinner);
        actionTv = (TextView) findViewById(R.id.actionTv);
        notification = (LinearLayout) findViewById(R.id.notification);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
    }

    @Override
    public void onDataUpdate(HashSet<String> updates) {

        for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
            Content mContentBean = reviewCollectiontoBuild.get(i);
            if (mContentBean.getContentType() == ContentTypeEnum.DELETED) {
                reviewCollectiontoBuild.remove(mContentBean);
            }
        }
        HashMap<String, Content> mainContent = ContentParser.ContentMap;
        String authorId = application.getDataFromSharedPreferences(LFSAppConstants.ID);
        for (Content mContentBean : mainContent.values()) {
            if (mContentBean.getContentType() == ContentTypeEnum.PARENT) {
                if (mContentBean.getAuthorId().equals(authorId)) {
                    Boolean flag = true;
                    for (Content t : reviewCollectiontoBuild) {
                        if (t.getAuthorId().equals(authorId)) {
                            flag = false;
                        }
                    }
                    if (flag)
                        reviewCollectiontoBuild.add(0, mContentBean);
                    break;
                }
            }
        }
        reviewListAdapter.notifyDataSetChanged();
        ReviewInDetailActivity.notifyDatainDetail();

        int oldCount = 0;
        if (reviewCollectiontoBuild != null)
            oldCount = reviewCollectiontoBuild.size();

        List<Content> newList = new ArrayList();
        for (Content t : mainContent.values()) {
            if (t.getContentType() == ContentTypeEnum.PARENT
                    && t.getVisibility().equals("1")) {
                newList.add(t);
            }
        }
//        if (newReviews != null)
//            if (newReviews.size() > 0) {
//            if (newReviews.size() == 1) {
//                notifMsgTV.setText(newReviews.size() + " New Comment");
//
//            } else {
//                notifMsgTV.setText(newReviews.size() + " New Comments");
//            }
//            notification.setVisibility(View.VISIBLE);
//            YoYo.with(Techniques.DropOut)
//                    .duration(700)
//                    .playOn(findViewById(R.id.notification));
//
//        } else {
//            notification.setVisibility(View.GONE);
//        }
        isReviewPosted();
    }
//    public void onDataUpdate(HashSet<String> authorsSet, HashSet<String> statesSet, HashSet<String> annotationsSet, HashSet<String> updates) {
//
//        for (String stateBeanId : statesSet) {
//            Content stateBean = ContentParser.ContentMap.get(stateBeanId);
//            if (stateBean.getVisibility().equals("1")) {
//
//                if (isExistComment(stateBeanId)) continue;
//
//                if (adminClintId.equals(stateBean.getAuthorId())) {
//                    int flag = 0;
//                    for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
//                        Content content = reviewCollectiontoBuild.get(i);
//                        if (content.getId().equals(stateBean.getParentId())) {
//                            reviewCollectiontoBuild.add(i + 1, stateBean);
//                            reviewListAdapter.notifyItemInserted(i + 1);
//                            flag = 1;
//                            break;
//                        }
//                    }
//                    if (flag == 0) {
//                        reviewCollectiontoBuild.add(0, stateBean);
//                        reviewListAdapter.notifyItemInserted(0);
//                    }
//                } else {
//                    newReviews.add(0, stateBeanId);
//                }
//            } else {
//                if (!content.hasVisibleChildContents(stateBeanId)) {
//                    application.printLog(true, TAG, "Deleted Content");
//
//                    for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
//                        Content bean = reviewCollectiontoBuild.get(i);
//                        if (bean.getId().equals(stateBeanId)) {
//                            reviewCollectiontoBuild.remove(i);
//                            reviewListAdapter.notifyItemRemoved(i);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        reviewListAdapter.notifyDataSetChanged();
////        ReviewInDetailActivity.notifyDatainDetail();
//        if (updates.size() > 0) {
////            mBus.post(updates);
//            reviewListAdapter.notifyDataSetChanged();
//        }
//
//        if (newReviews != null)
//            if (newReviews.size() > 0) {
//                if (newReviews.size() == 1) {
//                    notifMsgTV.setText(newReviews.size() + " New Comment");
//
//                } else {
//                    notifMsgTV.setText(newReviews.size() + " New Comments");
//                }
//                notification.setVisibility(View.VISIBLE);
//                YoYo.with(Techniques.DropOut)
//                        .duration(700)
//                        .playOn(findViewById(R.id.notification));
//
//            } else {
//                notification.setVisibility(View.GONE);
//            }
//        isReviewPosted();
//    }
}
