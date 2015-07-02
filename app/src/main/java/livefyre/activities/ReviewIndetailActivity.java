package livefyre.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import livefyre.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import livefyre.BaseActivity;
import livefyre.adapters.ReviewInDetailAdapter;
import livefyre.models.Content;
import livefyre.parsers.ContentParser;

/**
 * Created by kvanadev5 on 18/06/15.
 */
public class ReviewIndetailActivity extends BaseActivity {
    ImageView image_header,mainReviewerImage;
    TextView titleForReviewInDetail;
    RatingBar ratingBarInDetailView;
    ReviewInDetailAdapter reviewInDetailAdapter;
    ContentParser content;
    Content selectedReviews;
    static String reviewId;
    List<Content> reviewCollectiontoBuild;
    RecyclerView recyclerView;
    static List<Content> childMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_indetail);
        pullViews();
        setListenersToViews();
    }

    private void pullViews() {
        image_header= (ImageView) findViewById(R.id.image_header);
        titleForReviewInDetail= (TextView) findViewById(R.id.titleForReviewInDetail);
        ratingBarInDetailView= (RatingBar) findViewById(R.id.ratingBarInDetailView);
        mainReviewerImage= (ImageView) findViewById(R.id.mainReviewerImage);
    }

    private void setListenersToViews() {
    }
    private void loadAllData() {
        reviewCollectiontoBuild = new ArrayList();
        Content mContentBean = ContentParser.ContentMap
                .get(reviewId);
        mContentBean.setNewReplyCount(0);

        reviewCollectiontoBuild.add(0, mContentBean);
//        childMap = ContentParser.getChildContentForReview(reviewId);

        if (childMap != null)
            for (Content content : childMap) {
                reviewCollectiontoBuild.add(content);
            }

        if (reviewInDetailAdapter != null) {
//            reviewInDetailAdapter.updateReviewInDetailAdapter(reviewCollectiontoBuild);
        }
    }
    void buildList() {
        loadAllData();
        reviewInDetailAdapter = null;
        if (reviewInDetailAdapter == null) {
//            reviewInDetailAdapter = new ReviewInDetailAdapter(ReviewIndetailActivity.this, getApplicationContext(), reviewCollectiontoBuild,notificationHandler, selectedReview.getId());
        }
        recyclerView.setAdapter(reviewInDetailAdapter);
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
                recyclerView.smoothScrollToPosition(position + 1);
            }
            if (resultCode == RESULT_CANCELED) {
                // Write your code if there's no result
                Log.d("A", "RESULT_CANCELED");
            }
        }
    }
}
