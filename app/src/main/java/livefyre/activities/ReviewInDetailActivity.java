package livefyre.activities;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import livefyre.AppSingleton;
import livefyre.BaseActivity;

import livefyre.LivefyreApplication;
import livefyre.R;
import livefyre.adapters.ReviewInDetailAdapter;
import livefyre.models.Attachments;
import livefyre.models.Content;

import livefyre.parsers.ContentParser;

/**
 * Created by kvanadev5 on 18/06/15.
 */

@SuppressLint("ResourceAsColor")
public class ReviewInDetailActivity extends BaseActivity {

    ImageView image_header;
    static ReviewInDetailAdapter reviewInDetailAdapter;

    Content selectedReviews;
    static String reviewId;
    static List<Content> reviewCollectiontoBuild;
    ListView commentsLV;
    static List<Content> childMap;
    private LivefyreApplication application;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_indetail);
//        application = AppSingleton.getInstance().getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pullViews();
        getDataFromIntent();
        buildList();
    }

    View.OnClickListener notificationHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loadAllData();
            int position = 0;
            List<Content> tempList = new ArrayList(reviewCollectiontoBuild);
            if (tempList.size() > 0) {
                Collections.sort(tempList, new Comparator<Content>() {
                    @Override
                    public int compare(Content p2, Content p1) {
                        return Integer.parseInt(p1.getCreatedAt())
                                - Integer.parseInt(p2.getCreatedAt());
                    }
                });
                String latestContentId = tempList.get(0).getId();

                for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                    Content b = reviewCollectiontoBuild.get(i);
                    if (b.getId().equals(latestContentId)) {
                        position = i;
                    }
                }
            }

            commentsLV.smoothScrollToPosition(position + 1);
        }
    };


    private void loadAllData() {
        reviewCollectiontoBuild = new ArrayList();
        Content mContentBean = ContentParser.ContentMap
                .get(reviewId);
        mContentBean.setNewReplyCount(0);

        reviewCollectiontoBuild.add(0, mContentBean);
        childMap = ContentParser.getChildForContent(reviewId);

        if (childMap != null)
            for (Content content : childMap) {
                reviewCollectiontoBuild.add(content);
            }

        if (reviewInDetailAdapter != null) {
            reviewInDetailAdapter.updateReviewInDetailAdapter(reviewCollectiontoBuild);
        }
    }

    public static void notifyDatainDetail() {
//        Log.d("Stream", "Stream detail");
        if (reviewInDetailAdapter != null)
            reviewInDetailAdapter.notifyDataSetChanged();
        if (reviewCollectiontoBuild != null) {
            childMap = ContentParser.getChildForContent(reviewId);
            List<Content> childTemp = new ArrayList<Content>();
            if (childMap != null)
                for (Content content : childMap) {
                    childTemp.add(content);
                }
            int newReplyCount = childTemp.size()
                    - (reviewCollectiontoBuild.size() - 1);
            Log.d("New Count", "" + childTemp.size());
            Log.d("Old Count", "" + reviewCollectiontoBuild.size());

            if (newReplyCount > 0) {
                Content mContentBean = ContentParser.ContentMap
                        .get(reviewId);
                mContentBean.setNewReplyCount(newReplyCount);
            }
        }
    }

    private void pullViews() {
        image_header = (ImageView) findViewById(R.id.image_header);
        commentsLV = (ListView) findViewById(R.id.commentsLV);

//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (!isNetworkAvailable()) {
            showToast("Network Not Available");
            return;
        }
    }

//    View.OnClickListener backtoReviewActivityFromInDetailViewListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            Intent mainViewIntent = new Intent(ReviewInDetailActivity.this,
//                    ReviewsActivity.class);
//            startActivity(mainViewIntent);
//        }
//    };

    @SuppressLint("ResourceAsColor")
    void getDataFromIntent() {
        Intent fromReviewsActivity = getIntent();
        reviewId = fromReviewsActivity.getStringExtra("id");
        selectedReviews = ContentParser.ContentMap.get(reviewId);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(selectedReviews.getTitle());
//        getActionBar().setTitle("  " + selectedReviews.getTitle());
        List<Attachments> img = selectedReviews.getAttachments();
        if (img != null) {
            if (img.size() > 0) {
                Attachments mAttachments = selectedReviews.getAttachments().get(0);

//                if (mAttachments.getType().equals("video")) {
//                    if (mAttachments.getThumbnail_url() != null) {
//                        if (mAttachments.getThumbnail_url().length() > 0) {
//                            image_header.setVisibility(View.VISIBLE);
//                            Picasso.with(getApplicationContext()).load(mAttachments.getThumbnail_url()).fit().into(image_header);
//                        } else {
//                            image_header.setImageResource(R.mipmap.img_bac);
//                        }
//                    } else {
//                        image_header.setImageResource(R.mipmap.img_bac);
//                    }
//                }
//                else {
                    if (mAttachments.getUrl() != null) {
                        if (mAttachments.getUrl().length() > 0) {
                            image_header.setVisibility(View.VISIBLE);
                            Picasso.with(getApplicationContext()).load(mAttachments.getUrl()).fit().into(image_header);
                        } else {
                            image_header.setImageResource(R.mipmap.img_bac);
                        }
                    } else {
                        image_header.setImageResource(R.mipmap.img_bac);
                    }
//                }
//            if (mAttachments.getUrl() != null) {
//                if (mAttachments.getUrl().length() > 0) {
//                    image_header.setVisibility(View.VISIBLE);
//                    Picasso.with(getApplicationContext()).load(mAttachments.getUrl()).fit().into(image_header);
//                } else {
//                    image_header.setVisibility(View.GONE);
//                }
//            } else {
//                image_header.setImageResource(R.mipmap.img_bac);
//            }
            } else {
                image_header.setImageResource(R.mipmap.img_bac);
            }
        } else {
            image_header.setImageResource(R.mipmap.img_bac);
        }
    }

    void buildList() {
        loadAllData();
        if (reviewInDetailAdapter == null) {
            reviewInDetailAdapter = new ReviewInDetailAdapter(this, getApplicationContext(), reviewCollectiontoBuild, notificationHandler, selectedReviews.getId());
        }
        commentsLV.setAdapter(reviewInDetailAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                Log.d("A", "RESULT_OK");
                loadAllData();

                int position = 0;
                List<Content> tempList = new ArrayList(reviewCollectiontoBuild);
                if (tempList.size() > 0) {
                    Collections.sort(tempList, new Comparator<Content>() {
                        @Override
                        public int compare(Content p2, Content p1) {
                            return Integer.parseInt(p1.getCreatedAt())
                                    - Integer.parseInt(p2.getCreatedAt());
                        }
                    });
                    String latestContentId = tempList.get(0).getId();

                    for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
                        Content b = reviewCollectiontoBuild.get(i);
                        if (b.getId().equals(latestContentId)) {

                            position = i;
                        }
                    }
                }
                commentsLV.smoothScrollToPosition(position + 1);
            }
            if (resultCode == RESULT_CANCELED) {
                // Write your code if there's no result
                Log.d("A", "RESULT_CANCELED");
            }
        }
    }// onActivityResult
}
//public class ReviewIndetailActivity extends BaseActivity {
//    ImageView image_header, mainReviewerImage;
//    TextView titleForReviewInDetail, mainReviewerDisplayName, isParentMod, mainReviewDate, mainReviewBody;
//    RatingBar ratingBarInDetailView;
//    ReviewInDetailAdapter reviewInDetailAdapter;
//    ContentParser content;
//    Content selectedReviews;
//    static String reviewId;
//    List<Content> reviewCollectiontoBuild;
//    RecyclerView recyclerView;
//    static List<Content> childMap;
//
//    //    @Override
////    protected void onResume() {
////        super.onResume();
////        populateData();
////    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review_indetail);
//
//        pullViews();
//        getDataFromIntent();
//        buildList();
//    }
//
//    View.OnClickListener notificationHandler = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            populateData();
//            int position = 0;
//            List<Content> tempList = new ArrayList(reviewCollectiontoBuild);
//            if (tempList.size() > 0) {
//                Collections.sort(tempList, new Comparator<Content>() {
//                    @Override
//                    public int compare(Content p2, Content p1) {
//                        return Integer.parseInt(p1.getCreatedAt())
//                                - Integer.parseInt(p2.getCreatedAt());
//                    }
//                });
//                String latestContentId = tempList.get(0).getId();
//
//                for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
//                    Content b = reviewCollectiontoBuild.get(i);
//                    if (b.getId().equals(latestContentId)) {
//                        position = i;
//                    }
//                }
//            }
//            recyclerView.smoothScrollToPosition(position + 1);
//        }
//    };
//
//    private void pullViews() {
//        image_header = (ImageView) findViewById(R.id.image_header);
//        titleForReviewInDetail = (TextView) findViewById(R.id.titleForReviewInDetail);
//        ratingBarInDetailView = (RatingBar) findViewById(R.id.ratingBarInDetailView);
//        mainReviewerImage = (ImageView) findViewById(R.id.mainReviewerImage);
//        recyclerView = (RecyclerView) findViewById(R.id.commentsRV);
//        mainReviewerDisplayName = (TextView) findViewById(R.id.mainReviewerDisplayName);
//        isParentMod = (TextView) findViewById(R.id.isParentMod);
//        mainReviewDate = (TextView) findViewById(R.id.mainReviewDate);
//        mainReviewBody = (TextView) findViewById(R.id.mainReviewBody);
//    }
//
//    private void populateData() {
//        reviewCollectiontoBuild = new ArrayList();
//        Content mContentBean = ContentParser.ContentMap
//                .get(reviewId);
//        mContentBean.setNewReplyCount(0);
//
//        reviewCollectiontoBuild.add(0, mContentBean);
//        childMap = ContentParser.getChildForContent(reviewId);
//
//        if (childMap != null)
//            for (Content content : childMap) {
//                reviewCollectiontoBuild.add(content);
//            }
//
//        if (reviewInDetailAdapter != null) {
//            reviewInDetailAdapter.updateReviewInDetailAdapter(reviewCollectiontoBuild);
//        }
//    }
//
//    //    public static void notifyDatainDetail() {
////        Log.d("Stream", "Stream detail");
////        if (reviewInDetailAdapter != null)
////            adapter.notifyDataSetChanged();
////
////        if (collectionToBuild != null) {
////            childMap = ContentParser.getChildContentForReview(reviewId);
////            List<ContentBean> childTemp = new ArrayList<ContentBean>();
////            if (childMap != null)
////                for (ContentBean content : childMap) {
////                    childTemp.add(content);
////                }
////
////            int newReplyCount = childTemp.size()
////                    - (collectionToBuild.size() - 1);
////            Log.d("New Count", "" + childTemp.size());
////            Log.d("Old Count", "" + collectionToBuild.size());
////
////            if (newReplyCount > 0) {
////                ContentBean mContentBean = ContentParser.ContentCollection
////                        .get(reviewId);
////                mContentBean.setNewReplyCount(newReplyCount);
////            }
////        }
////    }
//    void buildList() {
//        populateData();
////        reviewInDetailAdapter = null;
//        reviewInDetailAdapter = new ReviewInDetailAdapter(ReviewIndetailActivity.this, getApplicationContext(), reviewCollectiontoBuild, notificationHandler, selectedReviews.getId());
//        recyclerView.setAdapter(reviewInDetailAdapter);
//        reviewInDetailAdapter.notifyDataSetChanged();
//        ;
//    }
//
//    void getDataFromIntent() {
//        Intent intent = getIntent();
//        reviewId = intent.getStringExtra(LFSAppConstants.ID);
//        selectedReviews = ContentParser.ContentMap.get(reviewId);
//        // actionbar text
////        getActionBar().setTitle("  " + selectedReviews.getTitle());
//        List<Attachments> img = selectedReviews.getAttachments();
//        if (img != null) {
//            Attachments mAttachments = selectedReviews.getAttachments().get(0);
//            if (mAttachments.getUrl() != null) {
//                if (mAttachments.getUrl().length() > 0) {
//                    image_header.setVisibility(View.VISIBLE);
//                    Picasso.with(getApplicationContext()).load(mAttachments.getUrl()).fit().into(image_header);
//                } else {
//                    image_header.setVisibility(View.GONE);
//                }
//            } else {
//                image_header.setImageResource(R.mipmap.img_bac);
//            }
//        } else {
//            image_header.setImageResource(R.mipmap.img_bac);
//        }
//        titleForReviewInDetail.setText(selectedReviews.getTitle());
//        ratingBarInDetailView.setRating(Float.parseFloat(selectedReviews.getRating()) / 20);
//        if (selectedReviews.getAuthor().getAvatar().length() > 0) {
//            Picasso.with(getApplicationContext()).load(selectedReviews.getAuthor().getAvatar()).fit().transform(new RoundedTransformation(90, 0)).into(mainReviewerImage);
//        } else {
//        }
//        mainReviewerDisplayName.setText(selectedReviews.getAuthor().getDisplayName());
//        isParentMod.setText(selectedReviews.getIsModerator());
//        mainReviewDate.setText(LFUtils.getFormatedDate(selectedReviews.getCreatedAt(), LFSAppConstants.SHART));
//        mainReviewBody.setText(LFUtils.trimTrailingWhitespace(Html
//                        .fromHtml(selectedReviews.getBodyHtml())),
//                TextView.BufferType.SPANNABLE);
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                String result = data.getStringExtra("result");
//                Log.d("A", "RESULT_OK");
//                populateData();
//
//                int position = 0;
//                List<Content> tempList = new ArrayList(reviewCollectiontoBuild);
//                if (tempList.size() > 0) {
//                    Collections.sort(tempList, new Comparator<Content>() {
//                        @Override
//                        public int compare(Content p2, Content p1) {
//                            return Integer.parseInt(p1.getCreatedAt())
//                                    - Integer.parseInt(p2.getCreatedAt());
//                        }
//                    });
//                    String latestContentId = tempList.get(0).getId();
//
//                    for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
//                        Content b = reviewCollectiontoBuild.get(i);
//                        if (b.getId().equals(latestContentId)) {
//
//                            position = i;
//                        }
//                    }
//                }
//                recyclerView.smoothScrollToPosition(position + 1);
//            }
//            if (resultCode == RESULT_CANCELED) {
//                // Write your code if there's no result
//                Log.d("A", "RESULT_CANCELED");
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//}
