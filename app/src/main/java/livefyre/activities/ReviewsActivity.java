package livefyre.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livefyre.R;
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
import livefyre.ImagesCache.ImagesCache;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.LivefyreApplication;
import livefyre.adapters.ReviewListAdapter;
import livefyre.models.ContentBean;
import livefyre.models.ContentTypeEnum;
import livefyre.parsers.ContentParser;
import livefyre.parsers.ContentUpdateListener;
import livefyre.streamhub.AdminClient;
import livefyre.streamhub.BootstrapClient;
import livefyre.streamhub.StreamClient;

public class ReviewsActivity extends BaseActivity implements
        ContentUpdateListener {

	Button myReview, dropDownButton, notif;
	ImageButton postNewReviews;
	ImageView downArrow;
	ListView reviewListView;
	List<ContentBean> reviewCollectiontoBuild;
	ReviewListAdapter adapter;
	private LivefyreApplication application;
	public String ownReviewId;
	public String adminClintId = "No";
	ContentParser content;

	RelativeLayout mainNotification;

	TextView notifTV;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reviews_activity);

		init();
		adminClintCall();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ContentParser.ContentCollection != null)
			if (!ContentParser.ContentCollection.isEmpty()) {
				sortReviews(LFSAppConstants.MOVE_TO_VIEW_POINT);
				isReviewPosted();
				streamClintCall();
			}
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
			postNewReviews.setVisibility(View.GONE);
			myReview.setText("MY REVIEW");
			myReview.setVisibility(View.VISIBLE);
		} else {
			postNewReviews.setVisibility(View.VISIBLE);
			myReview.setVisibility(View.GONE);
		}
	}

	void init() {
		// applcation
		application = AppSingleton.getInstance().getApplication();

		// initialising SharedPreferences with dummy values to avoid problems-
		// with old data
		application.saveDataInSharedPreferences(LFSAppConstants.ID, "");
		application.saveDataInSharedPreferences(LFSAppConstants.ISMOD, "no");

		// Images Initialize Cache
		ImagesCache cache = ImagesCache.getInstance();
		cache.initializeCache();

		// pulling xml views
		reviewListView = (ListView) findViewById(R.id.mainActivityListView);
		reviewListView.setOnItemClickListener(mainActivityListViewListener);

		// For Log in user Review
		myReview = (Button) findViewById(R.id.myReview);
		myReview.setOnClickListener(myReviewListener);

		// Sorting Dropdown
		downArrow = (ImageView) findViewById(R.id.downArrow);
		downArrow.setOnClickListener(dropDownListener);
		dropDownButton = (Button) findViewById(R.id.dropDownButton);
		dropDownButton.setOnClickListener(dropDownListener);

		// to post review
		postNewReviews = (ImageButton) findViewById(R.id.postNewReviews);
		postNewReviews.setOnClickListener(postNewReviewsListener);

		// For Notification
		notif = (Button) findViewById(R.id.notif);
		notifTV = (TextView) findViewById(R.id.notifTV);
		mainNotification = (RelativeLayout) findViewById(R.id.mainNotification);

		// notif Animation
		notif.animate().translationY(notif.getWidth()).setDuration(500)
				.setInterpolator(new AccelerateDecelerateInterpolator());
		notif.setVisibility(View.VISIBLE);

	}

	OnItemClickListener mainActivityListViewListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			ContentBean contentBean = (ContentBean) reviewCollectiontoBuild
					.get(position);
			contentBean.setNewReplyCount(0);
			Intent detailViewIntent = new Intent(ReviewsActivity.this,
					ReviewInDetail.class);
			detailViewIntent.putExtra("id", contentBean.getId());
			startActivity(detailViewIntent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	};

	OnClickListener postNewReviewsListener = new OnClickListener() {

		public void onClick(View v) {

			Intent newPostIntent = new Intent(getApplicationContext(),
					NewReview.class);
			startActivity(newPostIntent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	};

	OnClickListener dropDownListener = new OnClickListener() {

		public void onClick(View v) {
			showDropDown();
		}
	};

	OnClickListener myReviewListener = new OnClickListener() {

		public void onClick(View v) {

			Intent detailViewIntent = new Intent(ReviewsActivity.this,
					ReviewInDetail.class);
			detailViewIntent.putExtra("id", ownReviewId);

			startActivity(detailViewIntent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	};

	void adminClintCall() {
		if (!isNetworkAvailable()) {
			showToast("Network Not Available");
			return;
		} else {
			showProgress();
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

	public class AdminCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject AdminClintJsonResponseObject) {
			JSONObject data;
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
			// Log.d("adminClintCall", "Fail");
			bootstrapClientCall();
		}

	}

	void bootstrapClientCall() {
		try {
			BootstrapClient.getInit( LFSConfig.SITE_ID,
					LFSConfig.ARTICLE_ID, new InitCallback());

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	private class InitCallback extends JsonHttpResponseHandler {

		public void onSuccess(String data) {
			try {
				buildReviewList(new JSONObject(data));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
		}

	}

	void buildReviewList(JSONObject data) {
		try {
			content = new ContentParser(data);
			content.getContentFromResponce(this);
			streamClintCall();
			sortReviews(LFSAppConstants.MOVE_TO_TOP);
			isReviewPosted();
		} catch (JSONException e) {
			e.printStackTrace();
		}
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

	@SuppressLint("ResourceAsColor")
	private void showDropDown() {
		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.drop_down_view);
		dialog.setCancelable(true);

		TextView mostHelpful = (TextView) dialog.findViewById(R.id.mostHelpful);
		TextView highestRating = (TextView) dialog
				.findViewById(R.id.highestRating);

		TextView lowestRating = (TextView) dialog
				.findViewById(R.id.lowestRating);

		TextView newest = (TextView) dialog.findViewById(R.id.newest);

		TextView oldest = (TextView) dialog.findViewById(R.id.oldest);

		TextView[] textViews = { mostHelpful, highestRating, lowestRating,
				newest, oldest };
		for (int i = 0; i < 5; i++) {
			if (dropDownButton.getText().toString()
					.equals(textViews[i].getText().toString())) {
				textViews[i].setTextColor(Color.parseColor("#0F98EC"));
			} else {
				textViews[i].setTextColor(Color.parseColor("#000000"));
			}
		}

		mostHelpful.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropDownButton.setText("Most Helpful");

				sortReviews(LFSAppConstants.MOVE_TO_TOP);

				dialog.dismiss();

			}
		});

		highestRating.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropDownButton.setText("Highest Rating");
				sortReviews(LFSAppConstants.MOVE_TO_TOP);
				dialog.dismiss();
			}
		});

		lowestRating.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropDownButton.setText("Lowest Rating");
				sortReviews(LFSAppConstants.MOVE_TO_TOP);
				dialog.dismiss();

			}
		});

		newest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropDownButton.setText("Newest");
				sortReviews(LFSAppConstants.MOVE_TO_TOP);
				dialog.dismiss();

			}
		});

		oldest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropDownButton.setText("Oldest");
				sortReviews(LFSAppConstants.MOVE_TO_TOP);
				dialog.dismiss();

			}
		});

		LinearLayout bottomViewOfDropDown = (LinearLayout) dialog
				.findViewById(R.id.bottomViewOfDropDown);
		bottomViewOfDropDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		LinearLayout rightViewOfDropDown = (LinearLayout) dialog
				.findViewById(R.id.rightViewOfDropDown);
		rightViewOfDropDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		LinearLayout leftViewOfDropDown = (LinearLayout) dialog
				.findViewById(R.id.leftViewOfDropDown);
		leftViewOfDropDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	void sortReviews(Boolean viewpoint) {

		if (!isNetworkAvailable()) {
			showToast("Network Not Available");
			return;
		} else
			dismissProgress();
		char sortCase = dropDownButton.getText().toString().charAt(0);
		reviewCollectiontoBuild = new ArrayList<ContentBean>();
		HashMap<String, ContentBean> mainContent = ContentParser.ContentCollection;
		if (mainContent != null)
			for (ContentBean t : mainContent.values()) {
				if (t.getContentType() == ContentTypeEnum.PARENT
						&& t.getVisibility().equals("1")) {
					reviewCollectiontoBuild.add(t);
				}
			}

		switch (sortCase) {
		case 'N':
			Collections.sort(reviewCollectiontoBuild,
					new Comparator<ContentBean>() {
						@Override
						public int compare(ContentBean p2, ContentBean p1) {
							return Integer.parseInt(p1.getCreatedAt())
									- Integer.parseInt(p2.getCreatedAt());
						}
					});
			break;
		case 'O':
			Collections.sort(reviewCollectiontoBuild,
					new Comparator<ContentBean>() {
						@Override
						public int compare(ContentBean p1, ContentBean p2) {
							return Integer.parseInt(p1.getCreatedAt())
									- Integer.parseInt(p2.getCreatedAt());
						}
					});
			break;
		case 'H':
			Collections.sort(reviewCollectiontoBuild,
					new Comparator<ContentBean>() {
						@Override
						public int compare(ContentBean p2, ContentBean p1) {
							return Integer.parseInt(p1.getRating())
									- Integer.parseInt(p2.getRating());
						}
					});
			break;
		case 'L':
			Collections.sort(reviewCollectiontoBuild,
					new Comparator<ContentBean>() {
						@Override
						public int compare(ContentBean p1, ContentBean p2) {
							return Integer.parseInt(p1.getRating())
									- Integer.parseInt(p2.getRating());
						}
					});
			break;
		case 'M':
			Collections.sort(reviewCollectiontoBuild,
					new Comparator<ContentBean>() {
						@Override
						public int compare(ContentBean p1, ContentBean p2) {
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
					new Comparator<ContentBean>() {
						@Override
						public int compare(ContentBean p2, ContentBean p1) {
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
					ContentBean temp = reviewCollectiontoBuild.get(i);
					reviewCollectiontoBuild.remove(temp);
					reviewCollectiontoBuild.add(0, temp);
					break;
				}
			}
		}

		// Hide Notification
		mainNotification.setVisibility(View.GONE);

		adapter = null;
		adapter = (ReviewListAdapter) reviewListView.getAdapter();
		if (adapter != null) {
			adapter.updateContentResult(reviewCollectiontoBuild);
		} else {
			adapter = new ReviewListAdapter(getApplicationContext(),
					reviewCollectiontoBuild);

		}
		Parcelable state = reviewListView.onSaveInstanceState();

		adapter.notifyDataSetChanged();
		reviewListView.setAdapter(adapter);
		reviewListView.onRestoreInstanceState(state);
		if (viewpoint)
			reviewListView.setSelectionAfterHeaderView();
	}

	public void onDataUpdate(HashSet<String> updates) {

		for (int i = 0; i < reviewCollectiontoBuild.size(); i++) {
			ContentBean mContentBean = reviewCollectiontoBuild.get(i);
			if (mContentBean.getContentType() == ContentTypeEnum.DELETED) {
				reviewCollectiontoBuild.remove(mContentBean);
			}
		}
		HashMap<String, ContentBean> mainContent = ContentParser.ContentCollection;
		String authorId = application
				.getDataFromSharedPreferences(LFSAppConstants.ID);
		for (ContentBean mContentBean : mainContent.values()) {
			if (mContentBean.getContentType() == ContentTypeEnum.PARENT) {
				if (mContentBean.getAuthorId().equals(authorId)) {
					Boolean flag = true;
					for (ContentBean t : reviewCollectiontoBuild) {
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
		adapter.notifyDataSetChanged();
		ReviewInDetail.notifyDatainDetail();

		int oldCount = 0;
		if (reviewCollectiontoBuild != null)
			oldCount = reviewCollectiontoBuild.size();

		List<ContentBean> newList = new ArrayList();
		for (ContentBean t : mainContent.values()) {
			if (t.getContentType() == ContentTypeEnum.PARENT
					&& t.getVisibility().equals("1")) {
				newList.add(t);
			}
		}
		if (newList.size() > 0) {
			if (newList.size() - oldCount > 0) {
				mainNotification.setVisibility(View.VISIBLE);
				Animation slide = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.slide_down);
				mainNotification.startAnimation(slide);
				if ((newList.size() - oldCount) == 1)
					notifTV.setText("" + (newList.size() - oldCount)
							+ " New Review ");
				else {
					notifTV.setText("" + (newList.size() - oldCount)
							+ " New Reviews ");
				}
			} else {
				mainNotification.setVisibility(View.GONE);
			}
		}
		notif.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropDownButton.setText("Newest");

				sortReviews(LFSAppConstants.MOVE_TO_TOP);
				Animation slide = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.slide_up);
				mainNotification.startAnimation(slide);
				mainNotification.setVisibility(View.GONE);
				reviewListView.smoothScrollToPosition(0);

			}
		});
		isReviewPosted();
	}
}