package livefyre.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livefyre.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import livefyre.BaseActivity;
import livefyre.ImagesCache.DownloadImageTask;
import livefyre.ImagesCache.ImagesCache;
import livefyre.adapters.ReviewInDetailAdapter;
import livefyre.fadingactionbar.FadingActionBarHelper;
import livefyre.models.ContentBean;
import livefyre.parsers.ContentParser;


@SuppressLint("ResourceAsColor")
public class ReviewInDetail extends BaseActivity {

	ImagesCache cache = ImagesCache.getInstance();
	ContentBean selectedReview;
	TextView titleForReviewInDetail, reviewerDisplayName, reviewCewatedDate,
			reviewInDetailBody, isFeatured;
	ImageView reviewerImageINdetailView, reviewImageInDetail;
	RatingBar ratingBarInDetailView;
	Button backtoReviewActivityFromInDetailView;
	// ListView childsListView;
	RelativeLayout reviewInDetailMainLayout;
	static String reviewId;
	HashMap<String, ContentBean> childs;
	private Drawable mActionBarBackgroundDrawable;
	static ReviewInDetailAdapter adapter;
	static List<ContentBean> collectionToBuild;

	ListView listView;
	ImageView image_header;

	static List<ContentBean> childMap;

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

		FadingActionBarHelper helper = new FadingActionBarHelper()
				.actionBarBackground(
						getResources().getDrawable(R.color.livefyre))
				.headerLayout(R.layout.header)
				.contentLayout(R.layout.activity_listview);
		setContentView(helper.createView(this));
		helper.initActionBar(this);

		init();
		getDataFromIntent();
		buildList();

	}

	OnClickListener notificationHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			loadAllData();
			int position = 0;
			List<ContentBean> tempList = new ArrayList(collectionToBuild);
			if (tempList.size() > 0) {
				Collections.sort(tempList, new Comparator<ContentBean>() {
					@Override
					public int compare(ContentBean p2, ContentBean p1) {
						return Integer.parseInt(p1.getCreatedAt())
								- Integer.parseInt(p2.getCreatedAt());
					}
				});
				String latestContentId = tempList.get(0).getId();

				for (int i = 0; i < collectionToBuild.size(); i++) {
					ContentBean b = collectionToBuild.get(i);
					if (b.getId().equals(latestContentId)) {
						position = i;
					}
				}
			}
			listView.smoothScrollToPosition(position + 1);
		}
	};

	private void loadAllData() {
		collectionToBuild = new ArrayList();
		ContentBean mContentBean = ContentParser.ContentCollection
				.get(reviewId);
		mContentBean.setNewReplyCount(0);

		collectionToBuild.add(0, mContentBean);
		childMap = ContentParser.getChildContentForReview(reviewId);

		if (childMap != null)
			for (ContentBean content : childMap) {
				collectionToBuild.add(content);
			}

		if (adapter != null) {
			adapter.updateReviewInDetailAdapter(collectionToBuild);
		}
	}

	public static void notifyDatainDetail() {
		Log.d("Stream", "Stream detail");
		if (adapter != null)
			adapter.notifyDataSetChanged();

		if (collectionToBuild != null) {
			childMap = ContentParser.getChildContentForReview(reviewId);
			List<ContentBean> childTemp = new ArrayList<ContentBean>();
			if (childMap != null)
				for (ContentBean content : childMap) {
					childTemp.add(content);
				}

			int newReplyCount = childTemp.size()
					- (collectionToBuild.size() - 1);
			Log.d("New Count", "" + childTemp.size());
			Log.d("Old Count", "" + collectionToBuild.size());

			if (newReplyCount > 0) {
				ContentBean mContentBean = ContentParser.ContentCollection
						.get(reviewId);
				mContentBean.setNewReplyCount(newReplyCount);
			}
		}
	}

	void init() {
		// back action-app icon
		View appIcon = findViewById(android.R.id.home);
		appIcon.setClickable(true);
		appIcon.setOnClickListener(finishAct);

		// back action-title
		TextView actionbarTextView = (TextView) findViewById(Resources
				.getSystem().getIdentifier("action_bar_title", "id", "android"));
		actionbarTextView.setOnClickListener(finishAct);

		listView = (ListView) findViewById(android.R.id.list);
		reviewInDetailMainLayout = (RelativeLayout) findViewById(R.id.reviewInDetailMainLayout);
		image_header = (ImageView) findViewById(R.id.image_header);
		if (!isNetworkAvailable()) {
			showToast("Network Not Available");
			return;
		}
	}

	OnClickListener finishAct = new OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	OnClickListener backtoReviewActivityFromInDetailViewListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mainViewIntent = new Intent(ReviewInDetail.this,
					ReviewsActivity.class);
			startActivity(mainViewIntent);
		}
	};

	@SuppressLint("ResourceAsColor")
	void getDataFromIntent() {
		Intent fromReviewsActivity = getIntent();
		reviewId = fromReviewsActivity.getStringExtra("id");
		selectedReview = ContentParser.ContentCollection.get(reviewId);

		// actionbar text
		getActionBar().setTitle("  " + selectedReview.getTitle());
		String img = selectedReview.getOembedUrl();
		if (img != null) {
			if (img.length() > 0) {
				Bitmap bitmap = cache.getImageFromWarehouse(selectedReview
						.getOembedUrl());
				if (bitmap != null) {
					Log.d("Length bitmap", "" + bitmap.getByteCount());
					image_header.setImageBitmap(bitmap);

				} else {
					image_header.setImageBitmap(null);
					DownloadImageTask imgTask = new DownloadImageTask(cache,
							image_header);
					imgTask.execute(img);
				}
			} else {
				image_header.setImageResource(R.drawable.img_bac);
				listView.setBackgroundResource(R.drawable.img_bac);
			}
		} else {
			image_header.setImageResource(R.drawable.img_bac);
			listView.setBackgroundResource(R.drawable.img_bac);
		}
	}

	void buildList() {
		loadAllData();
		adapter = null;
		if (adapter == null) {
			adapter = new ReviewInDetailAdapter(ReviewInDetail.this, getApplicationContext(), collectionToBuild,notificationHandler, selectedReview.getId());
		}
		listView.setAdapter(adapter);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				Log.d("A", "RESULT_OK");
				loadAllData();

				int position = 0;
				List<ContentBean> tempList = new ArrayList(collectionToBuild);
				if (tempList.size() > 0) {
					Collections.sort(tempList, new Comparator<ContentBean>() {
						@Override
						public int compare(ContentBean p2, ContentBean p1) {
							return Integer.parseInt(p1.getCreatedAt())
									- Integer.parseInt(p2.getCreatedAt());
						}
					});
					String latestContentId = tempList.get(0).getId();

					for (int i = 0; i < collectionToBuild.size(); i++) {
						ContentBean b = collectionToBuild.get(i);
						if (b.getId().equals(latestContentId)) {

							position = i;
						}
					}
				}
				listView.smoothScrollToPosition(position + 1);
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
				Log.d("A", "RESULT_CANCELED");
			}
		}
	}// onActivityResult

}
